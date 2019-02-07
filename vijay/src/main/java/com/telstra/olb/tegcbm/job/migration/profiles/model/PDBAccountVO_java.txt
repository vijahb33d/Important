package com.telstra.olb.tegcbm.job.migration.profiles.model;

import java.util.HashMap;
import java.util.Map;


/**
 * Value object for PDB Account attributes.
 * 
 * @author d274681
 */
public class PDBAccountVO {

	private String accountNumber;
	private Map attributes;
	
	/**
	 * Constructor.
	 */
	public PDBAccountVO() {
		this.attributes = new HashMap();
	}

	/**
	 * @return Returns the accountNumber.
	 */
	public String getAccountNumber() {
		return accountNumber;
	}
	/**
	 * @param accountNumber The accountNumber to set.
	 */
	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}
	/**
	 * @return Returns the attributes.
	 */
	public Map getAttributes() {
		return attributes;
	}
	/**
	 * @param attributes The attributes to set.
	 */
	public void setAttributes(Map attributes) {
		this.attributes = attributes;
	}
}
