package com.telstra.olb.tegcbm.job.bulkdownload.dao;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import net.sf.hibernate.expression.Expression;
import net.sf.hibernate.expression.Order;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.orm.hibernate.support.HibernateDaoSupport;

import com.telstra.olb.tegcbm.billdownload.model.OLBBillDownloadRequest;
import com.telstra.olb.tegcbm.billdownload.model.OLBBulkDownloadRequest;
import com.telstra.olb.tegcbm.bsl.core.TelstraCBMConstants;

/**
 * @author jeremy.russell
 * 
 */
public class BulkDownloadHibernateDAO extends HibernateDaoSupport
		implements BulkDownloadDAO {

	private final Log log = LogFactory.getLog(this.getClass());

	/**
	 * This method deletes in database the bulk download request passed as a parameter
	 */
	public void delete(OLBBulkDownloadRequest request) {
		request.setStatus(TelstraCBMConstants.DOWNLOAD_STATUS_REMOVED);
		request.setZipFilePath("");
		request.setName(""); //blank out name so it can be used again
		getHibernateTemplate().update(request);
    }

	public List getBulkDownloadRequestsOlderThanXMonths(int x) throws Exception {
        if (logger.isDebugEnabled()) {
        	log.debug("Retrieving BulkDownloadRequest objects older than " + x + " months");
        }
		Integer[] statuses = {new Integer(TelstraCBMConstants.DOWNLOAD_STATUS_PROCESSING),
				new Integer(TelstraCBMConstants.DOWNLOAD_STATUS_COMPLETE),
				new Integer(TelstraCBMConstants.DOWNLOAD_STATUS_ERROR)};
		Calendar cal = new GregorianCalendar();
		cal.add(Calendar.MONTH, -x);
		return getSession().createCriteria(OLBBulkDownloadRequest.class)
				.add(Expression.in("status", statuses))
				.add(Expression.lt("created",cal.getTime()))
				.list();
	}

	
	public List getBillDownloadRequestsOlderThanXMonths(int x) throws Exception {
		if (logger.isDebugEnabled()) {
        	log.debug("Retrieving BillDownloadRequest objects older than " + x + " months");
        }
		Integer[] statuses = {new Integer(TelstraCBMConstants.DOWNLOAD_STATUS_PROCESSING),
				new Integer(TelstraCBMConstants.DOWNLOAD_STATUS_COMPLETE),
				new Integer(TelstraCBMConstants.DOWNLOAD_STATUS_ERROR)};
		Calendar cal = new GregorianCalendar();
		cal.add(Calendar.MONTH, -x);
		return getSession().createCriteria(OLBBillDownloadRequest.class)
				.add(Expression.in("status", statuses))
				.add(Expression.lt("created",cal.getTime()))
				.list();
	}

	public void delete(OLBBillDownloadRequest request) {
		request.setBillFilePath("");
		request.setStatus(TelstraCBMConstants.DOWNLOAD_STATUS_REMOVED);
		getHibernateTemplate().update(request);
	}
}