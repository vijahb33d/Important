/*
 */
package com.telstra.olb.tegcbm.job.bulkdownload.activity;

import java.io.File;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.telstra.olb.tegcbm.billdownload.model.OLBBillDownloadRequest;
import com.telstra.olb.tegcbm.billdownload.model.OLBBulkDownloadRequest;
import com.telstra.olb.tegcbm.job.bulkdownload.dao.BulkDownloadDAO;
import com.telstra.olb.tegcbm.job.core.Activity;
import com.telstra.olb.tegcbm.job.core.ActivityException;
import com.telstra.olb.tegcbm.job.core.IContext;
import com.telstra.olb.tegcbm.job.core.InputArgs;

/**
 */
public class BulkDownloadPurgeActivity implements Activity {
	
    protected transient Log log = LogFactory.getLog(getClass());

    BulkDownloadDAO bulkDownloadDao;
	private String billsPath;

    /**
     * @param setDefaultAppPreferencesDao The setDefaultAppPreferencesDao to set.
     */
    public void setBulkDownloadDao(BulkDownloadDAO bulkDownloadDao) {
        this.bulkDownloadDao = bulkDownloadDao;
    }
	/**
	 * @param billsPath The billsPath to set.
	 */
	public void setBillsPath(String billsPath) {
		this.billsPath = billsPath;
	}

    /**
     * @return Returns the name.
     */
    public final String getName() {
        return "bulkdownloadpurge";
    }

    /**
     * @return Returns the nextActivity.
     */
    public Activity getNextActivity() {
        return null;
    }

    public Iterator getData(IContext activityContext) {
    	return null;
    }

    public void execute(IContext activityContext) throws ActivityException {
    	InputArgs args = (InputArgs) activityContext;
    	if (args.getArgNames()==null || !args.getArgNames().contains("months")) {
    		throw new ActivityException("Bulk Download has not been invoked with the correct paramters");
    	}
    	doExecute(args.getArg("months"));
    }
    
    /**
     * @param input
     *            activity input
     * @throws ActivityException
     *             is thrown if the profile cannot be migrated.
     * @see com.telstra.olb.tegcbm.job.core.Activity#execute(IContext)
     */
    public void doExecute(String monthsStr) throws ActivityException {
        try {
        	int months = Integer.parseInt(monthsStr);
        	log.debug("Purging requests created before " + months + " months ago");
        	purgeRequests(months);
        } catch (Exception e) {
            throw new ActivityException("unable to purge bulk downloads", e);
        }
    }
    
	/**
	 * This method soft deletes bulk download requests older than x months.
	 */
	public void purgeRequests(int months) throws Exception {
		List requests = bulkDownloadDao.getBulkDownloadRequestsOlderThanXMonths(months);
        if (log.isDebugEnabled()) {
        	log.debug("Purging " + requests.size() + " requests");
        }
		for (Iterator iter = requests.iterator(); iter.hasNext();) {
			OLBBulkDownloadRequest request = (OLBBulkDownloadRequest) iter.next();
			removeFile(request);
			bulkDownloadDao.delete(request);
		}
		
		List billRequests = bulkDownloadDao.getBillDownloadRequestsOlderThanXMonths(months);
		if (log.isDebugEnabled()) {
        	log.debug("Purging " + requests.size() + " One-Off requests");
        }
		
		for (Iterator iter = billRequests.iterator(); iter.hasNext();) {
			OLBBillDownloadRequest request = (OLBBillDownloadRequest) iter.next();
			removeFile(request);
			bulkDownloadDao.delete(request);
		}
	}

	/**
	 * @param request
	 */
	private void removeFile(OLBBillDownloadRequest request) {
		String pdfFilePath = request.getBillFilePath();
		if (null != pdfFilePath && pdfFilePath.length() != 0) {
			File file = new File(pdfFilePath);
			file.delete();
        	if(log.isDebugEnabled()) log.debug("Deleted file: " + file.getName());
		} 
	}
	
	/**
	 * This method deletes the file located via the zipFilePath parameter
	 * @param zipFilePath
	 */
	private void removeFile(OLBBulkDownloadRequest request) {
		String zipFilePath = request.getZipFilePath();
		if (null != zipFilePath && zipFilePath.length() != 0) {
			File file = new File(zipFilePath);
			file.delete();
        	if(log.isDebugEnabled()) log.debug("Deleted file: " + file.getName());
		} else {
			//request not complete, therefore delete directory if it exists
			String userId = request.getUserId();
			String requestName = request.getName();
			if (null != userId && null != requestName) {
				MessageFormat dirFormat = new MessageFormat(this.billsPath);
				String[] dirArgs = {userId,requestName};
				String directory = dirFormat.format(dirArgs);
	
				File d = new File(directory.toString());
				deleteDirectory(d);
	        	if(log.isDebugEnabled()) log.debug("Deleted directory: " + d.getName());
			}
		}
	}
	/**
	 * @param d
	 */
	private void deleteDirectory(File path) {
		if( path.exists() ) {
			File[] files = path.listFiles();
			for(int i=0; i<files.length; i++) {
				if(files[i].isDirectory()) {
					deleteDirectory(files[i]);
				}
				else {
					files[i].delete();
				}
			}
			path.delete();
		}
	}
}
