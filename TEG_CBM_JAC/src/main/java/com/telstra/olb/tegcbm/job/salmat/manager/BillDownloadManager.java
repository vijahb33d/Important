/*
 * Created on 25/08/2010
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.telstra.olb.tegcbm.job.salmat.manager;

import java.rmi.RemoteException;

import javax.activation.DataHandler;

import au.com.salmat.jiesws.jaxrpc.client.TelJIesRpcServiceFault;

import com.telstra.olb.tegcbm.billdownload.jms.BillDownloadEventNotification;

/**
 * @author arun.balasubramanian
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public interface BillDownloadManager {
	public DataHandler getDoc(String requestType, String billSysId,
			String accNo, String billNo, String issueDate, int pageLimit,
			int pageRange, String id, String pw) throws TelJIesRpcServiceFault,
			RemoteException;

	public void download(
			BillDownloadEventNotification eventNotification) throws Exception;
}