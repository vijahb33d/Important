package com.telstra.olb.tegcbm.job.migration.model;

import java.util.Date;

/**
 * Hibernate class for OLB_TBUNMANAGED_MIGRATION table.
 */
public class OLBTBUnmanagedCompanyMigration extends OLBCompanyMigration {

	private String macroSegment;
	
	private int report;
	
	/**
	 * Default constructor
	 */
	public OLBTBUnmanagedCompanyMigration(){
		
	}
	
	/**
	 * Constructor
	 * 
	 * @param cidn
	 * @param ownCode
	 * @param segment
	 * @param mSegment
	 * @param status
	 * @param createdDate
	 * @param updatedDate
	 */
	public OLBTBUnmanagedCompanyMigration(String cidn, String ownCode, String segment, String mSegment, int status, Date createdDate, Date updatedDate){
		this.setCompanyCode(cidn);
		this.setOwnCode(ownCode);
		this.setSegment(segment);
		this.macroSegment = mSegment;
		this.setActivityStatus(status);
		this.setCreatedDate(createdDate);
		this.setUpdatedDate(updatedDate);
	}
	
	/**
	 * @return Returns the macroSegment.
	 */
	public String getMacroSegment() {
		return macroSegment;
	}
	/**
	 * @param macroSegment The macroSegment to set.
	 */
	public void setMacroSegment(String macroSegment) {
		this.macroSegment = macroSegment;
	}
	
	/**
	 * Methord overrides the toString method of Object
	 * @return Returns the companyCode
	 */
	public String toString(){
		return this.getCompanyCode();
	}
	/**
	 * @return Returns the report.
	 */
	public int getReport() {
		return report;
	}
	/**
	 * @param report The report to set.
	 */
	public void setReport(int report) {
		this.report = report;
	}
}
