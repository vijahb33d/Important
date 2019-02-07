/*
 * Created on 14/07/2010
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.telstra.olb.tegcbm.job.migration.panconversion.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.telstra.olb.tegcbm.job.core.Activity;
import com.telstra.olb.tegcbm.job.core.ActivityException;
import com.telstra.olb.tegcbm.job.core.IContext;
import com.telstra.olb.tegcbm.job.core.InputArgs;
import com.telstra.olb.tegcbm.job.migration.panconversion.dao.CreditCardDao;
import com.telstra.olb.tegcbm.job.migration.panconversion.dao.CreditCardDataException;
import com.telstra.olb.tegcbm.job.migration.panconversion.dao.SFTPConnectionException;

/**
 * @author arun.balasubramanian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class RetrieveCPANActivity implements Activity{

	private String name;
	private Log log = LogFactory.getLog(this.getClass());
	
	private final String CTL_FILE_EXTENSION="panconversion.file.ctl.extension";
	private final String DAT_FILE_EXTENSION="panconversion.file.dat.extension";
	private final String EOT_FILE_EXTENSION="panconversion.file.eot.extension";
	
	private final String SOURCE_SYSTEM_ID="panconversion.source.systemid";
	private final String SOURCE_SYSTEM_ID_LENGTH="panconversion.source.systemid.length";
	
	private final String DAT_FILENAME_LENGTH="panconversion.dat.filename.length"; 
	private final String RECORD_COUNT_LENGTH="panconversion.record.count.length";
	
	private final String BODY_CARD_ID_LENGTH="panconversion.file.body.cardId.length";
	private final String TPAN_LENGTH="panconversion.file.body.tpan.length";
	 
	private final int TIMESTAMP_LENGTH=14;
	private final String FILE_DELIMITER=".";
	private final String FILE_NAME_DELIMITER="_";
	private final String ASTERICK="*";
	private final String TPAN="TPAN";
	
	private Properties properties;
	
	private CreditCardDao creditCardDao;

	private ByteArrayOutputStream ctlStream = new ByteArrayOutputStream();
	private ByteArrayOutputStream datStream = new ByteArrayOutputStream();
	
	/**
	 * Method delegates the control to the doExecute method.
	 */
	public void execute(IContext activityContext) throws ActivityException {
		InputArgs args = (InputArgs) activityContext;
    	if (args.getArgNames()==null || !args.getArgNames().contains("ctlFile")) {
    		throw new ActivityException("Missing Input Parameters: ctlFile");
    	}
		doExecute(args.getArg("ctlFile"));
	}
	
	
	/**
	 * Method does all the logic
	 */
	protected void doExecute(String ctlFile) throws ActivityException {		
		String[] str = ctlFile.split("\\|");  
		String user = str[0];
		str = str[1].split("@");
		String password = str[0];
		str = str[1].split("#");
		String host = str[0];
		str = str[1].split(":");
		String portStr = str[0];
		int port = Integer.parseInt(portStr);
		int index = str[1].lastIndexOf("/");
		String location = str[1].substring(0,index+1);
		String filename = str[1].substring(index+1);	
		
		if(checkForFiles(host, user, password, port, location, filename)){
			try {
				getContents(host, user, password, port, location, filename);
			} catch (SFTPConnectionException e) {
				throw new ActivityException("Error occured during migration : "+e);
			} catch (ActivityException e) {
				throw e;
			} catch (CreditCardDataException e) {
				throw new ActivityException("Error occured while updating TPANs into the database : "+e);
			}
		} else {
			if(log.isDebugEnabled()){ log.debug("One or all of the files necessary for migration is not present in the location");}
		}		
	}
	
	
	
	
	
	/**
	 * Method retrieves the contents for CTL and DAT files and stores it as byteStreams.
	 * 
	 * @param host
	 * @param user
	 * @param password
	 * @param port
	 * @param location
	 * @param filename
	 * @throws SFTPConnectionException
	 * @throws ActivityException
	 * @throws CreditCardDataException
	 */
	private void getContents(String host, String user, String pwd, int port, String location, String filename) throws SFTPConnectionException, ActivityException, CreditCardDataException {
		ctlStream = PANConversionHelper.getContents(host, user, pwd, port, location, filename);
		String ctlContentStr = new String(ctlStream.toByteArray());
		StringBuffer ctlContent = new StringBuffer(ctlContentStr);
		
		int sourceIdLen = getPropertyInt(SOURCE_SYSTEM_ID_LENGTH);
		int datFilenameLen = getPropertyInt(DAT_FILENAME_LENGTH);
		String datFileName = ctlContent.substring(sourceIdLen,datFilenameLen+sourceIdLen).trim();
		log.info("DAT file provided by BillPay : " + datFileName);
		
		int countStart = sourceIdLen + datFilenameLen + TIMESTAMP_LENGTH;
		int countEnd = countStart + getPropertyInt(RECORD_COUNT_LENGTH);
		String recordCountStr = ctlContent.substring(countStart,countEnd).trim();
		long recordCount = Long.parseLong(recordCountStr);
		log.info("Total Records transformed by BillPay : " + recordCount);
		
		datStream = PANConversionHelper.getContents(host, user, pwd, port, location, datFileName);
		String datContentStr = new String(datStream.toByteArray());
		StringBuffer datContents = new StringBuffer(datContentStr);
		Map tpans = getTPANs(datContents);	
		
		if(tpans.size() == recordCount){
			creditCardDao.updateCPANsToTPANs(tpans);
		} else {
			throw new ActivityException("Record mismatch in Output Files");
		}
	}

	


	/**
	 * Method populates a Map with cardId as key and TPAN as value from the StringBuffer passed in.
	 * @param datContents
	 * @return
	 */
	private Map getTPANs(StringBuffer datContents){
		Map tpans = new HashMap();
		int count = datContents.length();
		
		int cardIdLength =  getPropertyInt(BODY_CARD_ID_LENGTH);
		int tpanLength = getPropertyInt(TPAN_LENGTH);
		
		int carIdIndex = 0;
		int tpanIndex = cardIdLength;
		int nextLine = tpanIndex + getPropertyInt(TPAN_LENGTH)+1;
		
		for(int i=0; i<count; i+=nextLine,tpanIndex+=nextLine,carIdIndex+=nextLine){
			String cardId = datContents.substring(carIdIndex, carIdIndex + cardIdLength);
			String tpan = datContents.substring(tpanIndex, tpanIndex + tpanLength);
			tpans.put(cardId.trim(), tpan.trim());
		}
		return tpans;
	}

	/**
	 * Method checks for the existence of all the three files necessary for migration.
	 * 
	 * @param host
	 * @param user
	 * @param pwd
	 * @param port
	 * @param location
	 * @param filename
	 * @return
	 */
	private boolean checkForFiles(String host, String user, String pwd, int port, String location, String filename) {
		String datFilename = getProperty(SOURCE_SYSTEM_ID)+FILE_NAME_DELIMITER+TPAN+ASTERICK+FILE_DELIMITER+getProperty(DAT_FILE_EXTENSION);
		String eotFilename = getProperty(SOURCE_SYSTEM_ID)+FILE_NAME_DELIMITER+TPAN+ASTERICK+FILE_DELIMITER+getProperty(EOT_FILE_EXTENSION);
		boolean flag = false;
		try {
			boolean flagCtl = PANConversionHelper.isFileExists(host, user, pwd, port, location, filename);
			boolean flagDat = PANConversionHelper.isFileExists(host, user, pwd, port, location, datFilename);
			boolean flagEot = PANConversionHelper.isFileExists(host, user, pwd, port, location, eotFilename);
			flag = flagCtl && flagDat && flagEot;
		} catch (SFTPConnectionException e) {
			if(log.isDebugEnabled()){ log.debug("Exception thrown in checkForFiles() :"+e);}
			flag = false;
		}
		return flag;
	}


	/**
	 * Method retrieves the contents of the file passed in as argument, if the file is present under the location.
	 * @param dir
	 * @param filename
	 * @return
	 * @throws Exception
	 */
	private String getContents(String dir, String filename) throws Exception{
		File file = new File(dir+filename);
		if(!file.exists()){
			throw new ActivityException("File not found:  "+dir+"\\"+filename);
		}		
		int len = (int)file.length();
		byte[] bytes = new byte[len];
		FileInputStream fis = new FileInputStream(file);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		while(fis.read(bytes)!=-1){
			bos.write(bytes);
		}
		bos.flush();
		String str = new String(bos.toByteArray());
		return str;
	}
	
	/**
	 * Method returns the property value for the argument from the properties.
	 * @param name
	 * @return
	 */
	public String getProperty(String name){
		return getProperties().getProperty(name);
	}
	
	/**
	 * Method returns the int property value for the argument from the properties.
	 * @param name
	 * @return
	 */
	public int getPropertyInt(String name){
		return Integer.parseInt(getProperty(name));
	}
	
	/* (non-Javadoc)
	 * @see com.telstra.olb.tegcbm.job.core.Activity#getNextActivity()
	 */
	public Activity getNextActivity() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.telstra.olb.tegcbm.job.core.Activity#getData(com.telstra.olb.tegcbm.job.core.IContext)
	 */
	public Iterator getData(IContext activityContext) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return Returns the properties.
	 */
	public Properties getProperties() {
		return properties;
	}
	/**
	 * @param properties The properties to set.
	 */
	public void setProperties(Properties properties) {
		this.properties = properties;
	}
	/**
	 * @return Returns the creditCardDao.
	 */
	public CreditCardDao getCreditCardDao() {
		return creditCardDao;
	}
	/**
	 * @param creditCardDao The creditCardDao to set.
	 */
	public void setCreditCardDao(CreditCardDao creditCardDao) {
		this.creditCardDao = creditCardDao;
	}
}
