/*
 * Created on 25/08/2010
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.telstra.olb.tegcbm.job.salmat.jms;

import javax.jms.TextMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jms.core.JmsTemplate102;

import com.telstra.olb.tegcbm.billdownload.jms.BillDownloadEventNotification;
import com.telstra.olb.tegcbm.job.salmat.manager.BillDownloadManager;
import com.telstra.olb.xml.marshall.XmlMarshaller;

/**
 * @author arun.balasubramanian
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class SalmatMessageReceiver {
	private Log log = LogFactory.getLog(this.getClass());

	private JmsTemplate102 JmsTemplate;

	private XmlMarshaller marshaller;

	private BillDownloadManager billDownloadManager;

	private String queueName;

	public void processMessage(TextMessage msg) {
		//TextMessage msg = (TextMessage) JmsTemplate.receive(queueName);
		BillDownloadEventNotification event = null;

		try {
			if (msg != null) {
				event = (BillDownloadEventNotification) marshaller
						.unmarshall(msg.getText());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (event != null) {
			try {
				billDownloadManager.download(event);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * @return Returns the JmsTemplate.
	 */
	public JmsTemplate102 getJmsTemplate() {
		return JmsTemplate;
	}

	/**
	 * @param JmsTemplate
	 *            The JmsTemplate to set.
	 */
	public void setJmsTemplate(JmsTemplate102 JmsTemplate) {
		this.JmsTemplate = JmsTemplate;
	}

	/**
	 * @return Returns the marshaller.
	 */
	public XmlMarshaller getMarshaller() {
		return marshaller;
	}

	/**
	 * @param marshaller
	 *            The marshaller to set.
	 */
	public void setMarshaller(XmlMarshaller marshaller) {
		this.marshaller = marshaller;
	}

	/**
	 * @return Returns the queueName.
	 */
	public String getQueueName() {
		return queueName;
	}

	/**
	 * @param queueName
	 *            The queueName to set.
	 */
	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}

	/**
	 * @return Returns the billDownloadManager.
	 */
	public BillDownloadManager getBillDownloadManager() {
		return billDownloadManager;
	}

	/**
	 * @param billDownloadManager
	 *            The billDownloadManager to set.
	 */
	public void setBillDownloadManager(BillDownloadManager billDownloadManager) {
		this.billDownloadManager = billDownloadManager;
	}
}