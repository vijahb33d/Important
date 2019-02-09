package com.telstra.olb.tegcbm.job.bulkdownload.dao;

import java.util.List;

import com.telstra.olb.tegcbm.billdownload.model.OLBBillDownloadRequest;
import com.telstra.olb.tegcbm.billdownload.model.OLBBulkDownloadRequest;

/**
 * @author jeremy.russell
 */
public interface BulkDownloadDAO {
	public List getBulkDownloadRequestsOlderThanXMonths(int months) throws Exception;
	public void delete(OLBBulkDownloadRequest request);
	
	public List getBillDownloadRequestsOlderThanXMonths(int months) throws Exception;
	public void delete(OLBBillDownloadRequest request);	
}
