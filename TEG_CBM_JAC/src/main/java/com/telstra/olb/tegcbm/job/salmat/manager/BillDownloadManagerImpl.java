/*
 * Created on 25/08/2010
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.telstra.olb.tegcbm.job.salmat.manager;

import java.io.File;
import java.io.FileOutputStream;
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.activation.DataHandler;
import javax.xml.rpc.Stub;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import au.com.salmat.jiesws.jaxrpc.client.TelJIesRpcServiceFault;
import au.com.salmat.jiesws.jaxrpc.client.TelJIesRpcServiceIF;
import au.com.salmat.jiesws.jaxrpc.client.TelJIesRpcService_Impl;

import com.accenture.util.StopWatch;
import com.telstra.olb.tegcbm.billdownload.dao.BillDownloadDAO;
import com.telstra.olb.tegcbm.billdownload.jms.BillDownloadEventNotification;
import com.telstra.olb.tegcbm.billdownload.model.OLBBillDownloadRequest;
import com.telstra.olb.tegcbm.billdownload.model.OLBBillDownloadTime;
import com.telstra.olb.tegcbm.bsl.core.TelstraCBMConstants;
import com.telstra.olb.tegcbm.job.salmat.jms.SalmatMessageSender;

/**
 * @author arun.balasubramanian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class BillDownloadManagerImpl implements BillDownloadManager{
	private Log log = LogFactory.getLog(this.getClass());
	
	private final String REQUEST_TYPE_BULK = "Type2";
	private final String REQUEST_TYPE_ONEOFF = "Type1";
	private final String BILL_SYS_ID_FLEXCAB = "FLEX";
	private final String BILL_SYS_ID_MICA = "MICA";
	private  final int ONE_OFF_DOWNLOAD_BULK_ID = 0;
	private final int PAGE_LIMIT = 0;
	private final int PAGE_RANGE = 0;
	
	private String keyStore;
	private String keyStoreType;
	private String keyStorePassword;
	
	private String trustStore;
	private String trustStoreType;
	private String trustStorePassword;
	
	private String endpointAddress1;
	private String endpointAddress2;
	private String username;
	private String password;
	private String id;
	private String pw;
	private String billsPath;
	
	private SalmatMessageSender jmsSender;
	private BillDownloadDAO dao;
	
	
	/** 
	 * Method retrieves the Bill details related to notification, downloads it from salmat and writes it in the configured location. 
	 */
	public void download(BillDownloadEventNotification eventNotification) throws Exception {
		StopWatch timer = new StopWatch();
		timer.start();
		
		OLBBillDownloadRequest request = null;
		boolean successful = false;
		//update status to processing
		request = dao.getBillDownloadRequest(eventNotification.getRecordId());
		if (null == request) {
			throw new Exception("BillDownloadRequest cannot be found: " + eventNotification.getRecordId());
		}
		request.setStatus(TelstraCBMConstants.DOWNLOAD_STATUS_PROCESSING);
		dao.update(request);
		
		DataHandler billContent = null;
		String extension = ".pdf";
		successful = true;
		String billSysId = getBillSysId(request.getDdn());
		
		String billNumber = "";
		if(billSysId.equals(BILL_SYS_ID_FLEXCAB)){
			billNumber = request.getBillNumber();
		} else if(billSysId.equals(BILL_SYS_ID_MICA)){
			billNumber = getMICABillNumber(request.getBillNumber());			
		}
		
		DateFormat df = new SimpleDateFormat("yyyyMMdd");
		String issueDate = df.format(request.getBillIssueDate());
		if (log.isDebugEnabled()) log.debug("Calling webservice client");
		try {
			String requestType =  REQUEST_TYPE_BULK;
			if(eventNotification.getParentRecordId() ==  ONE_OFF_DOWNLOAD_BULK_ID){
				requestType = REQUEST_TYPE_ONEOFF;
			}
			billContent = getDoc(requestType,billSysId,request.getAccountNumber(), billNumber, issueDate,PAGE_LIMIT, PAGE_RANGE, id, pw);
		} catch(TelJIesRpcServiceFault e){
			if (log.isDebugEnabled()) log.debug("Service Fault received: ",e);			
			if(e.getFaultID().equals(TelstraCBMConstants.SALMAT_ERROR_CODE_001)){
				request.setComments(TelstraCBMConstants.BILL_DOWNLOAD_COMMENT_NOT_FOUND);
			} else if(e.getFaultID().equals(TelstraCBMConstants.SALMAT_ERROR_CODE_008)){
				request.setComments(TelstraCBMConstants.BILL_DOWNLOAD_COMMENT_TOO_LARGE);
			} else { 
				request.setComments(TelstraCBMConstants.BILL_DOWNLOAD_COMMENT_FAILED);
			}
			successful = false;
		} catch(RemoteException re){
			if (log.isDebugEnabled()) log.debug("Remote exception occured: ",re);
			request.setComments(TelstraCBMConstants.BILL_DOWNLOAD_COMMENT_FAILED);
			successful = false;
		}
		try {
			eventNotification.setSuccessful(false);
			if (successful) {
				//save file to file system
				MessageFormat pathFormat = new MessageFormat(this.billsPath);
				String[] pathArgs = {eventNotification.getUserId(),eventNotification.getBulkReqestName()};
				StringBuffer path = new StringBuffer(pathFormat.format(pathArgs));
		
				path.append(request.getAccountNumber()).append("_")
						.append(request.getBillNumber()).append("_")
						.append(df.format(request.getBillIssueDate())).append(extension);
				if (log.isDebugEnabled()) log.debug("Writing PDF to the location: "+path.toString());
				File file = new File(path.toString());
				if(!file.exists()){ file.createNewFile();}
				FileOutputStream fos = new FileOutputStream(file);
				billContent.writeTo(fos);
				fos.close();
				
				//update status to complete
				request.setBillFilePath(path.toString());
				request.setStatus(TelstraCBMConstants.DOWNLOAD_STATUS_COMPLETE);
				dao.update(request);
				dao.flushToDatabase();
			
				eventNotification.setSuccessful(true);
			} else {
				if (log.isDebugEnabled()) log.debug("Saving failed bill status to database");
				request.setStatus(TelstraCBMConstants.DOWNLOAD_STATUS_ERROR);
				dao.update(request);
				dao.flushToDatabase();
			}
		} catch (Exception e) {
			log.error("Exception caught while saving bill download request to file system: ", e);
			if (log.isDebugEnabled()) log.debug("Cleaning up bill download request");
			//get fresh copy of request
			request = dao.getBillDownloadRequest(eventNotification.getRecordId());
			request.setComments(TelstraCBMConstants.BILL_DOWNLOAD_COMMENT_FAILED);
			request.setStatus(TelstraCBMConstants.DOWNLOAD_STATUS_ERROR);
			dao.update(request);
			dao.flushToDatabase();
		}
		
		timer.stop();
		OLBBillDownloadTime time = new OLBBillDownloadTime();
		time.setCreated(new Date());
		time.setGeneratorTypeId(getGeneratorType(request.getDownloadFormat()));
		time.setElapsedSeconds((float)timer.getElapsedTime()/(float)1000);
		time.setReference(request.getId()+"");
		if(log.isDebugEnabled()) { log.debug("Recording response time: " + time.getElapsedSeconds()); }
		getDao().recordTime(time);
		
		if(eventNotification.getParentRecordId() != ONE_OFF_DOWNLOAD_BULK_ID){
			jmsSender.sendMesage(eventNotification);
		}
	}
	
	private int getGeneratorType(String format) throws Exception {
		if (format.equals(TelstraCBMConstants.BILL_DOWNLOAD_FORMAT_SUMMARY_PDF)) {
			return TelstraCBMConstants.BILL_DOWNLOAD_FORMAT_SUMMARY_PDF_ID;
		} else if (format.equals(TelstraCBMConstants.BILL_DOWNLOAD_FORMAT_DETAILED_PDF)) {
			return TelstraCBMConstants.BILL_DOWNLOAD_FORMAT_DETAILED_PDF_ID;
		}
		throw new Exception("Download format not supported: " + format);
	}
	
	
	/**
	 * @param billNumber
	 * @param accountType
	 * @return
	 */
	private String getMICABillNumber(String billNumber) {
		StringBuffer sb = new StringBuffer(billNumber);
		sb.insert(1,"000000");
		billNumber = sb.toString();
		return billNumber;
	}

	/**
	 * Method sets the BillSysID depending on the accountType
	 * 
	 * @param accountType
	 * @return
	 * @throws Exception
	 */
	private String getBillSysId(String accountType) throws Exception {
		String delimiter = "_";
		String[] str = accountType.split(delimiter);
		accountType = str[0];
		if (accountType.equals(TelstraCBMConstants.SOURCE_SYSTEM_FLEXCAB)) {
			return BILL_SYS_ID_FLEXCAB;
		} else if (accountType.equals(TelstraCBMConstants.SOURCE_SYSTEM_MNET)) {
			return BILL_SYS_ID_MICA;
		}
		else throw new Exception("Unsupported account type: " + accountType);
	}

	/** 
	 * Method does call Salmat and returns the DataHandler object.
	 */
	public DataHandler getDoc(String requestType, String billSysId, String accNo, String billNo, String issueDate, int pageLimit, int pageRange, String id, String pw) throws TelJIesRpcServiceFault, RemoteException {
		if(log.isDebugEnabled()){log.debug("Entering getDoc()"+accNo+" : "+billNo+" : "+issueDate);}
		
		System.setProperty("javax.net.ssl.keyStore", keyStore);
        System.setProperty("javax.net.ssl.keyStoreType", keyStoreType);
        System.setProperty("javax.net.ssl.keyStorePassword", keyStorePassword);
        System.setProperty("javax.net.ssl.trustStore", trustStore);
        System.setProperty("javax.net.ssl.trustStoreType", trustStoreType);
        System.setProperty("javax.net.ssl.trustStorePassword", trustStorePassword);
		
		TelJIesRpcService_Impl locator = new TelJIesRpcService_Impl();
		Stub stub = (Stub)locator.getTelJIesRpcServiceIFPort();

		if(requestType.equals(REQUEST_TYPE_BULK)){
			stub._setProperty(javax.xml.rpc.Stub.ENDPOINT_ADDRESS_PROPERTY,endpointAddress2);
		} else {
			stub._setProperty(javax.xml.rpc.Stub.ENDPOINT_ADDRESS_PROPERTY,endpointAddress1);
		}
	    stub._setProperty(javax.xml.rpc.Stub.USERNAME_PROPERTY, username);
	    stub._setProperty(javax.xml.rpc.Stub.PASSWORD_PROPERTY, password);
	    TelJIesRpcServiceIF wsAdapter = (TelJIesRpcServiceIF)stub;
	  	  
		DataHandler handler = wsAdapter.getDoc(requestType, billSysId, accNo, billNo, issueDate, pageLimit, pageRange, id, pw);
		if(log.isDebugEnabled()){log.debug("Exiting getDoc() with : "+handler);}
		return handler;
	}

	/**
	 * @return Returns the password.
	 */
	public String getPassword() {
		return password;
	}
	/**
	 * @param password The password to set.
	 */
	public void setPassword(String password) {
		this.password = password;
	}
	/**
	 * @return Returns the username.
	 */
	public String getUsername() {
		return username;
	}
	/**
	 * @param username The username to set.
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	
	/**
	 * @return Returns the dao.
	 */
	public BillDownloadDAO getDao() {
		return dao;
	}
	/**
	 * @param dao The dao to set.
	 */
	public void setDao(BillDownloadDAO dao) {
		this.dao = dao;
	}
	/**
	 * @return Returns the billsPath.
	 */
	public String getBillsPath() {
		return billsPath;
	}
	/**
	 * @param billsPath The billsPath to set.
	 */
	public void setBillsPath(String billsPath) {
		this.billsPath = billsPath;
	}
	/**
	 * @return Returns the id.
	 */
	public String getId() {
		return id;
	}
	/**
	 * @param id The id to set.
	 */
	public void setId(String id) {
		this.id = id;
	}
	/**
	 * @return Returns the pw.
	 */
	public String getPw() {
		return pw;
	}
	/**
	 * @param pw The pw to set.
	 */
	public void setPw(String pw) {
		this.pw = pw;
	}
	/**
	 * @return Returns the jmsSender.
	 */
	public SalmatMessageSender getJmsSender() {
		return jmsSender;
	}
	/**
	 * @param jmsSender The jmsSender to set.
	 */
	public void setJmsSender(SalmatMessageSender jmsSender) {
		this.jmsSender = jmsSender;
	}
	/**
	 * @return Returns the endpointAddress1.
	 */
	public String getEndpointAddress1() {
		return endpointAddress1;
	}
	/**
	 * @param endpointAddress1 The endpointAddress1 to set.
	 */
	public void setEndpointAddress1(String endpointAddress1) {
		this.endpointAddress1 = endpointAddress1;
	}
	/**
	 * @return Returns the endpointAddress2.
	 */
	public String getEndpointAddress2() {
		return endpointAddress2;
	}
	/**
	 * @param endpointAddress2 The endpointAddress2 to set.
	 */
	public void setEndpointAddress2(String endpointAddress2) {
		this.endpointAddress2 = endpointAddress2;
	}
	/**
	 * @return Returns the keyStore.
	 */
	public String getKeyStore() {
		return keyStore;
	}
	/**
	 * @param keyStore The keyStore to set.
	 */
	public void setKeyStore(String keyStore) {
		this.keyStore = keyStore;
	}
	/**
	 * @return Returns the keyStorePassword.
	 */
	public String getKeyStorePassword() {
		return keyStorePassword;
	}
	/**
	 * @param keyStorePassword The keyStorePassword to set.
	 */
	public void setKeyStorePassword(String keyStorePassword) {
		this.keyStorePassword = keyStorePassword;
	}
	/**
	 * @return Returns the keyStoreType.
	 */
	public String getKeyStoreType() {
		return keyStoreType;
	}
	/**
	 * @param keyStoreType The keyStoreType to set.
	 */
	public void setKeyStoreType(String keyStoreType) {
		this.keyStoreType = keyStoreType;
	}
	/**
	 * @return Returns the trustStore.
	 */
	public String getTrustStore() {
		return trustStore;
	}
	/**
	 * @param trustStore The trustStore to set.
	 */
	public void setTrustStore(String trustStore) {
		this.trustStore = trustStore;
	}
	/**
	 * @return Returns the trustStorePassword.
	 */
	public String getTrustStorePassword() {
		return trustStorePassword;
	}
	/**
	 * @param trustStorePassword The trustStorePassword to set.
	 */
	public void setTrustStorePassword(String trustStorePassword) {
		this.trustStorePassword = trustStorePassword;
	}
	/**
	 * @return Returns the trustStoreType.
	 */
	public String getTrustStoreType() {
		return trustStoreType;
	}
	/**
	 * @param trustStoreType The trustStoreType to set.
	 */
	public void setTrustStoreType(String trustStoreType) {
		this.trustStoreType = trustStoreType;
	}
}
