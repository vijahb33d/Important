/*
 * Created on 12/05/2009
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.telstra.olb.tegcbm.job.migration.model;

/**
 * @author arun.balasubramanian
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class OLBAccountCompanyMap {

	private String accountNumber;

	private String companyCode;

	private int accountTypeId;

	private int relationId;

	/**
	 * @param accountNumber
	 * @param companyCode
	 * @param accTypeId
	 * @param relationId
	 */
	public OLBAccountCompanyMap(String accountNumber, String companyCode,
			int accTypeId, int relationId) {
		this.accountNumber = accountNumber;
		this.companyCode = companyCode;
		this.accountTypeId = accTypeId;
		this.relationId = relationId;
	}

	/**
	 * @return Returns the accountNumber.
	 */
	public String getAccountNumber() {
		return accountNumber;
	}

	/**
	 * @param accountNumber
	 *            The accountNumber to set.
	 */
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}

	/**
	 * @return Returns the accountTypeId.
	 */
	public int getAccountTypeId() {
		return accountTypeId;
	}

	/**
	 * @param accountTypeId
	 *            The accountTypeId to set.
	 */
	public void setAccountTypeId(int accountTypeId) {
		this.accountTypeId = accountTypeId;
	}

	/**
	 * @return Returns the companyCode.
	 */
	public String getCompanyCode() {
		return companyCode;
	}

	/**
	 * @param companyCode
	 *            The companyCode to set.
	 */
	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}

	/**
	 * @return Returns the relationId.
	 */
	public int getRelationId() {
		return relationId;
	}

	/**
	 * @param relationId
	 *            The relationId to set.
	 */
	public void setRelationId(int relationId) {
		this.relationId = relationId;
	}
}
