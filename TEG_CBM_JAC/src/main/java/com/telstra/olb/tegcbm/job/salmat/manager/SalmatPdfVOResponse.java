/*
 * Created on 9/04/2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.telstra.olb.tegcbm.job.salmat.manager;

import javax.activation.DataHandler;





public class SalmatPdfVOResponse{

	/*Below four variables will only be used in stubs*/
    private String billingAccountCode;
    private String billNumber;
    private String fileLocation;
    private String exceptionCode;
    
    private DataHandler data;
    
    public SalmatPdfVOResponse(){    	
    }
    
    public SalmatPdfVOResponse(String billingAccountCode, DataHandler bytes){
    	this.billingAccountCode=billingAccountCode;
    	this.data=bytes;    	    	
    }
    
    /**
     * @return Returns the accountNumber.
     */
    public String getBillingAccountCode() {
        return billingAccountCode;
    }
    /**
     * @param accountNumber The accountNumber to set.
     */
    public void setBillingAccountCode(String accountNumber) {
        this.billingAccountCode = accountNumber;
    }
    /**
     * @return Returns the documentNumber.
     */
    public String getBillNumber() {
        return billNumber;
    }
    /**
     * @param documentNumber The documentNumber to set.
     */
    public void setBillNumber(String documentNumber) {
        this.billNumber = documentNumber;
    }
	
	/**
	 * @return Returns the fileLocation.
	 */
	public String getFileLocation() {
		return fileLocation;
	}
	/**
	 * @param fileLocation The fileLocation to set.
	 */
	public void setFileLocation(String fileLocation) {
		this.fileLocation = fileLocation;
	}
	/**
	 * @return Returns the exceptionCode.
	 */
	public String getExceptionCode() {
		return exceptionCode;
	}
	/**
	 * @param exceptionCode The exceptionCode to set.
	 */
	public void setExceptionCode(String exceptionCode) {
		this.exceptionCode = exceptionCode;
	}
	/**
	 * @return Returns the data.
	 */
	public DataHandler getData() {
		return data;
	}
	/**
	 * @param data The data to set.
	 */
	public void setData(DataHandler data) {
		this.data = data;
	}
}
