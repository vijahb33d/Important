/*
 * Created on 31/08/2010
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.telstra.olb.tegcbm.job.salmat.manager;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URL;
import java.rmi.RemoteException;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.UnexpectedRollbackException;

import au.com.salmat.jiesws.jaxrpc.client.TelJIesRpcServiceFault;

import com.telstra.olb.tegcbm.billdownload.dao.BillDownloadDAO;
import com.telstra.olb.tegcbm.billdownload.jms.BillDownloadEventNotification;
import com.telstra.olb.tegcbm.billdownload.model.OLBBillDownloadRequest;
import com.telstra.olb.tegcbm.bsl.core.TelstraCBMConstants;
import com.telstra.olb.tegcbm.job.salmat.jms.SalmatMessageSender;
import com.telstra.olb.xml.marshall.XmlMarshaller;

/**
 * @author arun.balasubramanian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class BillDownloadManagerStub implements BillDownloadManager{
	private Log log = LogFactory.getLog(this.getClass());
	
	private final String REQUEST_TYPE_BULK = "Type2";
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
	
	private XmlMarshaller marshaller;
	private String stubFilePath;
	SalmatPdfVOResponses responses;
	
	/** 
	 * Method retrieves the Bill details related to notification, downloads it from salmat and writes it in the configured location. 
	 */
	public void download(BillDownloadEventNotification eventNotification) throws Exception {
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
			billContent = getDoc(REQUEST_TYPE_BULK,billSysId,request.getAccountNumber(), billNumber, issueDate,PAGE_LIMIT, PAGE_RANGE, id, pw);
		} catch(TelJIesRpcServiceFault e){
			if (log.isDebugEnabled()) log.debug("Service Fault received: "+e);
			if(e.getFaultID().equals(TelstraCBMConstants.SALMAT_ERROR_CODE_001)){
				request.setComments(TelstraCBMConstants.BILL_DOWNLOAD_COMMENT_NOT_FOUND);
			} else if(e.getFaultID().equals(TelstraCBMConstants.SALMAT_ERROR_CODE_008)){
				request.setComments(TelstraCBMConstants.BILL_DOWNLOAD_COMMENT_TOO_LARGE);
			} else { 
				request.setComments(TelstraCBMConstants.BILL_DOWNLOAD_COMMENT_FAILED);
			}
			successful = false;
		} catch(RemoteException re){
			if (log.isDebugEnabled()) log.debug("Remote exception occured: "+re);
			request.setComments(TelstraCBMConstants.BILL_DOWNLOAD_COMMENT_FAILED);
			successful = false;
		}
		try {
			if(billContent == null){ throw new Exception("Stub Data not Found");}
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
		if(eventNotification.getParentRecordId() != ONE_OFF_DOWNLOAD_BULK_ID){
			jmsSender.sendMesage(eventNotification);
		}
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
	 * To get a matching PDF document.
	 * 
	 * @param request DocumentPdfVORequest
	 * @return DocumentPdfVO the value object contains byte array of PDF file.
	 * @throws IntegrationCCBException General IntegrationCCBException 
     */
	public DataHandler getDoc(String requestType, String billSysId, String accNo, String billNo, String issueDate, int pageLimit, int pageRange, String id, String pw) throws TelJIesRpcServiceFault, RemoteException {

    	List salmatPdfVOList = new ArrayList();
    	
		SalmatPdfVOResponse response = null;
		try {
			response = (SalmatPdfVOResponse) getStubObject(accNo, billNo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(response != null) {
			if(response.getExceptionCode() != null && !response.getExceptionCode().equals("")){
				if(response.getExceptionCode().equals("001")){
					throw new TelJIesRpcServiceFault("001",response.getBillingAccountCode()+"-"+response.getBillNumber()+"-----"+"Document Not Found");
				} else if(response.getExceptionCode().equals("002")){
					throw new TelJIesRpcServiceFault("002","Bad id/pw");
				} else if(response.getExceptionCode().equals("003")){
					throw new TelJIesRpcServiceFault("003",response.getBillingAccountCode()+"-"+response.getBillNumber()+"-----"+"Invalid Page Limit");
				} else if(response.getExceptionCode().equals("004")){
					throw new TelJIesRpcServiceFault("004",response.getBillingAccountCode()+"-"+response.getBillNumber()+"-----"+"Invalid Page Range");
				} else if(response.getExceptionCode().equals("005")){
					throw new TelJIesRpcServiceFault("005",response.getBillingAccountCode()+"-"+response.getBillNumber()+"-----"+"Total Page exceeds Page Limit.");
				} else if(response.getExceptionCode().equals("006")){
					throw new TelJIesRpcServiceFault("006",response.getBillingAccountCode()+"-"+response.getBillNumber()+"-----"+"Fragment not found: /frags/d01/telstra/generate/inv_detail/lg1l630");
				} else if(response.getExceptionCode().equals("007")){
					throw new TelJIesRpcServiceFault("007",response.getBillingAccountCode()+"-"+response.getBillNumber()+"-----"+"Could be anything here");
				} else if(response.getExceptionCode().equals("008")){
					throw new TelJIesRpcServiceFault("008",response.getBillingAccountCode()+"-"+response.getBillNumber()+"-----"+"System Page Limit Exceeded");
				} else if(response.getExceptionCode().equals("009")){
					throw new TelJIesRpcServiceFault("009",response.getBillingAccountCode()+"-"+response.getBillNumber()+"-----"+"Validation Failed: Bill ID KENAN invalid");
				} else if(response.getExceptionCode().equals("010")){
					throw new UnexpectedRollbackException("Salmat - Connection timeout");
				}
			} else {
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				if(response.getFileLocation() != null && !response.getFileLocation().equals("")) {
					if (log.isDebugEnabled()) log.debug("File location is not NULL, getting the DataHandler for the file:"+response.getFileLocation());
					byte[] bytes = new byte[10240];
					URL url = this.getClass().getClassLoader().getResource(response.getFileLocation());
					File file = new File(url.getPath());
					DataHandler handler = new DataHandler(new FileDataSource(file));
					response.setData(handler);					
				}
			}
			return response.getData();
		}
		return null;
    }
	
	
	/**
	 * @param request
	 * @return
	 * @throws Exception
	 */
	private SalmatPdfVOResponse getStubObject(String accNo, String billNo) throws Exception {
		if(responses == null) {
			loadData();
		}
		Iterator iter = responses.getSalmatResponses().iterator();
		SalmatPdfVOResponse response = null;
		while(iter.hasNext()) {
			 response = (SalmatPdfVOResponse) iter.next();
			 if(response.getBillingAccountCode().equals(accNo)
			 	&& response.getBillNumber().equals(billNo)){
			 	return response;
			 }
		}
		return null;
	}

	private void loadData() throws Exception {
		URL url = this.getClass().getClassLoader().getResource(stubFilePath);
		File file = new File(url.getPath());
		responses = (SalmatPdfVOResponses) marshaller.unmarshall(new FileInputStream(file));
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
	/**
	 * @return Returns the marshaller.
	 */
	public XmlMarshaller getMarshaller() {
		return marshaller;
	}
	/**
	 * @param marshaller The marshaller to set.
	 */
	public void setMarshaller(XmlMarshaller marshaller) {
		this.marshaller = marshaller;
	}

	
	/**
	 * @return Returns the stubFilePath.
	 */
	public String getStubFilePath() {
		return stubFilePath;
	}
	/**
	 * @param stubFilePath The stubFilePath to set.
	 */
	public void setStubFilePath(String stubFilePath) {
		this.stubFilePath = stubFilePath;
	}
}

