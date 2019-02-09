/*
 * Created on 20/01/2010
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.telstra.olb.tegcbm.job.migration.reporting.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.telstra.olb.tegcbm.job.core.ActivityException;
import com.telstra.olb.tegcbm.job.core.DefaultActivity;
import com.telstra.olb.tegcbm.job.core.DefaultManagerImpl;
import com.telstra.olb.tegcbm.job.migration.model.OLBCompanyMigration;

/**
 * @author pavan.x.kuma
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class GenerateReportActivity extends DefaultActivity {
	 private static Log log = LogFactory.getLog(GenerateReportActivity.class);
	private String csvPath;
	
	SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
	
	protected void doExecute(Object input) throws ActivityException {
		Map companies = (Map) input;
		createCSV(companies);
		log.debug("companies" +companies);
	}
	
	/**
	 * Method to create the CSV file with the successfully created company ids.
	 * The report will be generated at the following location /opt/WebSphere/AppServer/migration/cbm2bp_report
	 * 
	 * @param companies
	 * @param csvPath2
	 */
	private void createCSV(Map companies)throws ActivityException {
		Date date = new Date();
		String filename = getFileName(date);
		StringBuffer contents = new StringBuffer("Report Date,");
		contents.append(dateFormatter.format(date));
		for (Iterator iter = companies.keySet().iterator();iter.hasNext();){
		    String status = (String) iter.next();
		    contents.append("\n").append("Migration Status Report for Status," + status).append("\n");
		    Collection companiesForStatus = (Collection) companies.get(status);
		    contents.append("Number of Companies,").append(companiesForStatus.size()).append("\n").append("\n");
            processCollection(companiesForStatus, contents);
            contents.append("\n");
		}
		if (log.isDebugEnabled()) {
			log.debug("cvspath*******" +csvPath);
		}
		 
		writeToFile(filename, contents.toString());
	}

	/**
     * @param companiesForStatus
     * @param contents
     */
    private void processCollection(Collection companiesForStatus, StringBuffer contents) {
        if (companiesForStatus == null) {
            return;
        }
        contents.append("Migrated cidn(s):,").append("\n");
        for (Iterator i = companiesForStatus.iterator(); i.hasNext(); ) {
            OLBCompanyMigration mig = (OLBCompanyMigration)  i.next();
            contents.append(mig.getCompanyCode()).append("\n");
        }
    }

    /**
	 * @param date
	 * @return
	 */
	private String getFileName(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		String filename = "Migrated-Customers-" + sdf.format(date) + ".csv";
		return filename;
	}

	/**
	 * @param filename
	 * @param contents
	 * @throws ActivityException
	 */
	private void writeToFile(String filename, String contents) throws ActivityException {
		FileOutputStream fos = null;
		try {
			File outputFile = getOutputFile(filename);
			fos = new FileOutputStream(outputFile);
			if (log.isDebugEnabled()) {
				log.debug("Writing contents in the CSV file :" + outputFile.getAbsolutePath());
			}
			fos.write(contents.getBytes());
		} catch (FileNotFoundException e) {
			throw new ActivityException("unable to generate migration report ", e);
		} catch (IOException e) {
			throw new ActivityException("unable to generate migration report ", e);
		} finally {
			close(fos);
		}
	}

	/**
	 * @param filename
	 * @return
	 */
	private File getOutputFile(String filename) {
		File file = new File(csvPath);
		if(!file.exists()){
			file.mkdirs();
		}
		File outputFile = new File(file, filename);
		return outputFile;
	}

	/**
	 * @param fos
	 * @throws IOException
	 */
	private void close(FileOutputStream fos) {
		if (fos != null) {
			try {
				fos.flush();
				fos.close();
			} catch (IOException e) {
				// do nothing
			}
		}
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
