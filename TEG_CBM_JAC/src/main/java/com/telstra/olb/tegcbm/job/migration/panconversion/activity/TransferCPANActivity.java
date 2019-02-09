/*
 * Created on 7/07/2010
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.telstra.olb.tegcbm.job.migration.panconversion.activity;

import java.io.ByteArrayInputStream;
import java.security.GeneralSecurityException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
public class TransferCPANActivity implements Activity{
	private String name;
	private Log log = LogFactory.getLog(this.getClass());
	
	private final String CTL_FILE_EXTENSION="panconversion.file.ctl.extension";
	private final String DAT_FILE_EXTENSION="panconversion.file.dat.extension";
	private final String EOT_FILE_EXTENSION="panconversion.file.eot.extension";
	private final String FILENAME_TIMESTAMP_FORMAT="panconversion.file.timestamp.format";
		
	private final String SOURCE_SYSTEM_ID="panconversion.source.systemid";
	private final String SOURCE_SYSTEM_ID_LENGTH="panconversion.source.systemid.length";
	
	private final String TIMESTAMP="panconversion.file.timestamp.format";
	
	private final String BODY_CARD_ID_LENGTH="panconversion.file.body.cardId.length";
	private final String BODY_FILLER_LENGTH="panconversion.file.body.filler.length";
	
	private final String CPAN_LENGTH="panconversion.file.body.cpan.length";
	//private final String TPAN_LENGTH="panconversion.file.body.tpan.length";
	
	private final String DAT_FILENAME_LENGTH="panconversion.dat.filename.length";
	
	private final String RECORD_COUNT_LENGTH="panconversion.record.count.length";
	
	private final String SFTP_HOST="panconversion.sftp.host";
	private final String SFTP_USER="panconversion.sftp.user";
	private final String SFTP_PASSWORD="panconversion.sftp.password";
	private final String SFTP_PORT="panconversion.sftp.port";
	private final String SFTP_LOCATION="panconversion.sftp.location";
	
	private final String FILE_DELIMITER=".";
	private final String PIPE_DELIMITER="|";
	private final String FILE_NAME_DELIMITER="_";
	private final String CPAN="CPAN";
	
	private SimpleDateFormat sdf = null;
	
	private Properties properties;

	private CreditCardDao creditCardDao;
	
	/**
	 * Default Constructor
	 */
	public TransferCPANActivity(){
		
	}
	
	
	public final void execute(IContext activityContext) throws ActivityException {
		InputArgs args = (InputArgs) activityContext;
    	if (args.getArgNames()==null || !args.getArgNames().contains("path")) {
    		throw new ActivityException("Missing Input Parameters: path");
    	}
		doExecute(args.getArg("path"));
	}

	/**
	 * Method does all the logic
	 */
	protected void doExecute(Object input) throws ActivityException {
		if(log.isDebugEnabled()){log.debug("Entering TransferCPANActivity.doExecute");}
		sdf = new SimpleDateFormat(getProperty(TIMESTAMP));
		Map cpans = new HashMap();
		Map failedCPans = new HashMap();
		try {
			if(log.isDebugEnabled()){log.debug("Retrieving CPANs from OLB database");}
			cpans = creditCardDao.getCreditCardNumbers();
			if(log.isDebugEnabled()){log.debug("Retrieved CPANs from OLB database count :"+cpans.size());}
		} catch (CreditCardDataException e) {
			throw new ActivityException("Error occured while retrieving CPANs from OLB database",e);
		}
				
		failedCPans = decryptCPANs(cpans);
		List cardIds = new ArrayList(); 
		cardIds.addAll(failedCPans.keySet());
		Iterator iter = cardIds.iterator();
		log.info("FAILED credit cards count: "+cardIds.size() +"\n List of Cardids : \n");
		while(iter.hasNext()){
			log.info((String)iter.next());
		}
		
		String timeStamp = sdf.format(new Date());
		String ctlFileName = getFileName(CTL_FILE_EXTENSION, timeStamp);
		String datFileName = getFileName(DAT_FILE_EXTENSION, timeStamp);
		String eotFileName = getFileName(EOT_FILE_EXTENSION, timeStamp);
		
		ByteArrayInputStream ctlStream = generateCTLFileContent(cpans.size(), datFileName, timeStamp);
		ByteArrayInputStream contentStream = generateFileContent(cpans);
		ByteArrayInputStream eotStream = new ByteArrayInputStream(new byte[0]);
		
		String path = (String)input;
		String[] str = path.split("\\|");  
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
		
		/*String host = getProperty(SFTP_HOST);
		int port = getPropertyInt(SFTP_PORT);
		String user = getProperty(SFTP_USER);
		String password = getProperty(SFTP_PASSWORD);
		String location = getProperty(SFTP_LOCATION);*/
		
		try {
			log.info("Transferring streams through SFTP to host:"+host+":"+location);
			PANConversionHelper.transferStreamSFTP(ctlStream,host, user, password, port, location, ctlFileName);
			log.info("Transferred CTL file");
			PANConversionHelper.transferStreamSFTP(contentStream,host, user, password, port, location, datFileName);
			log.info("Transferred DAT file");
			PANConversionHelper.transferStreamSFTP(eotStream,host, user, password, port, location, eotFileName);
			log.info("Transferred EOT file");
			if(log.isDebugEnabled()){log.debug("Transfer complete");}
		} catch (SFTPConnectionException e) {
			throw new ActivityException("Error occured while transferring the streams through SFTP to Host:"+host,e);
		}		
		if(log.isDebugEnabled()){log.debug("Exiting TransferCPANActivity.doExecute");}
	}
	
	/**
	 * Method decrypts the CPANs and stores it in the map passed in.
	 * @param cpans
	 * @throws GeneralSecurityException
	 */
	private Map decryptCPANs(Map cpans) {
		Map failedCpans = new HashMap();
		List cardIds = new ArrayList();
		cardIds.addAll(cpans.keySet());
		Iterator iter = cardIds.iterator();
		while(iter.hasNext()){
			String cardId = (String)iter.next();
			byte[] bytes = (byte[])cpans.get(cardId);
			String cpan = null;
			try {
				cpan = PANConversionHelper.decrypt(bytes);
				cpans.put(cardId,cpan);
			} catch (GeneralSecurityException e) {
				cpans.remove(cardId);		
				failedCpans.put(cardId, cpan);
			}			
		}
		return failedCpans;
	}


	/**
	 * Method generates the file name.
	 * @return
	 */
	private String getFileName(String fileExtnProp, String timeStamp) {
		StringBuffer filename = new StringBuffer();
		filename.append(getProperty(SOURCE_SYSTEM_ID));
		filename.append(FILE_NAME_DELIMITER);
		filename.append(CPAN);
		filename.append(FILE_NAME_DELIMITER);		
		filename.append(timeStamp);
		filename.append(FILE_DELIMITER);
		filename.append(getProperty(fileExtnProp));
		return filename.toString();
	}

	/**
	 * Method generates the file content.
	 * @param cpans
	 */
	private ByteArrayInputStream generateFileContent(Map cpans) {
		if(log.isDebugEnabled()){log.debug("Entering TransferCPANActivity.generateFileContent");}
		StringBuffer fileContent = new StringBuffer();
		//fileContent.append(createHeader());
		
		List cardIds = new ArrayList();
		cardIds.addAll(cpans.keySet());
		Iterator iter = cardIds.iterator();
		while(iter.hasNext()){
			String cardId = (String)iter.next();
			String cpan = (String)cpans.get(cardId);
			fileContent.append(createRecords(cardId, cpan));
		}
		
		//fileContent.append(createTrailer(cpans.size()));
		
		ByteArrayInputStream bis = new ByteArrayInputStream(fileContent.toString().getBytes());
		if(log.isDebugEnabled()){log.debug("Exiting TransferCPANActivity.generateFileContent:\n"+fileContent.toString());}
		return bis;
	}

	/**
	 * Method creates the Control file for migration.
	 * @return
	 */
	private ByteArrayInputStream generateCTLFileContent(int recordCount, String datFilename, String timeStamp) {
		if(log.isDebugEnabled()){log.debug("Entering TransferCPANActivity.generateCTLFileContent");}
		
		StringBuffer sb = new StringBuffer();
		
		int countBefore = sb.length();
		sb.append(recordCount);
		int countAfter = sb.length();
		int fillerLength = getPropertyInt(RECORD_COUNT_LENGTH);
		if((countAfter - countBefore) < fillerLength){
			filler(sb,fillerLength - (countAfter - countBefore));
		}
		
		sb.append(PIPE_DELIMITER);
		sb.append(timeStamp.substring(0,8));
		sb.append(PIPE_DELIMITER);
		
		sb.append(datFilename);
		int len = getPropertyInt(DAT_FILENAME_LENGTH);
		if(datFilename.length() < len){
			filler(sb, len-datFilename.length());
		}
		
		sb.append(PIPE_DELIMITER);
		
		sb.append(timeStamp.substring(8));	
		
		sb.append("\n");
		if(log.isDebugEnabled()){log.debug("Exiting TransferCPANActivity.generateCTLFileContent:\n"+sb.toString());}
		return new ByteArrayInputStream(sb.toString().getBytes());
	}
	
	/**
	 * Method creates the DT records for each cpan.
	 * @param cardId
	 * @param cpan
	 * @return
	 */
	public String createRecords(String cardId, String cpan){
		if(log.isDebugEnabled()){log.debug("Entering TransferCPANActivity.createRecords( "+cardId+", "+cpan+" )");}
		StringBuffer sb = new StringBuffer();

		sb.append(cardId);
		int len = getPropertyInt(BODY_CARD_ID_LENGTH);
		if(cardId.length() < len){
			filler(sb, len-cardId.length());
		}
		
		sb.append(cpan);
		int cpanLength = getPropertyInt(CPAN_LENGTH);
		if(cpan.length() < cpanLength){
			filler(sb, cpanLength-cpan.length());
		}
		
		sb.append("\n");
		if(log.isDebugEnabled()){log.debug("Exiting TransferCPANActivity.createRecords( "+cardId+", "+cpan+" ) \n"+sb.toString());}
		return sb.toString();
	}
	
	/**
	 * Method fills the len number of spaces at the end of the StringBuffer object passed. 
	 * @param sb
	 * @param len
	 */
	private void filler(StringBuffer sb, int len){
		for(int i=0;i<len;sb.append(" "),i++);
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
    public final String getName() {
        return name;
    }
    /**
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

	
}
