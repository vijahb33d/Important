package com.telstra.olb.tegcbm.job.migration.tbu.preferences.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.telstra.olb.tegcbm.job.core.ActivityException;
import com.telstra.olb.tegcbm.job.core.DefaultActivity;
import com.telstra.olb.tegcbm.job.migration.model.OLBTBUnmanagedCompanyMigration;
import com.telstra.olb.tegcbm.job.migration.populate.dao.PopulateDao;
import com.telstra.olb.tegcbm.job.migration.populate.dao.PopulateDataException;

public class GenerateReportActivity extends DefaultActivity{

	private PopulateDao populateDao;

	private String csvPath;
	
	/**
	 * This method will retrieve the successfully migrated companies by calling the 
	 * getMigratedTBUCompanies() method of the populateDao class and will generate a CSV report.
	 * 
	 * @param input
	 */
	protected void doExecute(Object input) throws ActivityException {
		List companies = new ArrayList();
		try {
			//Retrieving the list of successfully migrated companies.
			companies = populateDao.getMigratedTBUCompanies();
		} catch (PopulateDataException e) {
			throw new ActivityException("unable to retrieve migrated tbu companies from OLB_TBUNMANAGED_MIGRATION", e);
		}
		
		createCSV(companies, csvPath);
		
		try{
			populateDao.updateReported(companies);
		} catch (PopulateDataException e) {
			throw new ActivityException("unable to retrieve migrated tbu companies from OLB_TBUNMANAGED_MIGRATION", e);
		}
	}
	
	/**
	 * Method to create the CSV file with the successfully created company ids.
	 * The report will be generated at the following location /opt/WebSphere/AppServer/migration/cbm2bp_report
	 * 
	 * @param companies
	 * @param csvPath2
	 */
	private void createCSV(List companies, String csvPath)throws ActivityException {
		Iterator iter = companies.iterator();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		Date date = new Date();
		String filename = "Migrated-Customers-"+sdf.format(date)+".csv";
		//csvPath += filename;
		String str = date.toString()+"\nNumber of Companies migrated:"+companies.size()+"\n";
		while(iter.hasNext()){
			OLBTBUnmanagedCompanyMigration mig = (OLBTBUnmanagedCompanyMigration) iter.next();
			str = str + mig.toString()+"\n";
		}
		try {
			File file = new File(csvPath);
			if(!file.exists()){
				file.mkdirs();
			}
			file = new File(csvPath+filename);
			FileOutputStream fos = new FileOutputStream(file);
			log.debug("Writing contents in the CSV file :"+csvPath);
			fos.write(str.getBytes());
		} catch (FileNotFoundException e) {
			throw new ActivityException("unable to generate migration report ", e);
		} catch (IOException e) {
			throw new ActivityException("unable to generate migration report ", e);
		}
	}

	/**
	 * @return Returns the populateDao.
	 */
	public PopulateDao getPopulateDao() {
		return populateDao;
	}
	/**
	 * @param populateDao The populateDao to set.
	 */
	public void setPopulateDao(PopulateDao populateDao) {
		this.populateDao = populateDao;
	}
	/**
	 * @return Returns the csvPath.
	 */
	public String getCsvPath() {
		return csvPath;
	}
	/**
	 * @param csvPath The csvPath to set.
	 */
	public void setCsvPath(String csvPath) {
		this.csvPath = csvPath;
	}
}
