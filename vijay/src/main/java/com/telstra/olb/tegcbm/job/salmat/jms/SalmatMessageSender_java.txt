/*
 * Created on 25/08/2010
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.telstra.olb.tegcbm.job.salmat.jms;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import com.telstra.olb.tegcbm.billdownload.jms.BillDownloadEventNotification;
import com.telstra.olb.xml.marshall.XmlMarshaller;

/**
 * @author arun.balasubramanian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SalmatMessageSender {
	private JmsTemplate JmsTemplate;
	private XmlMarshaller marshaller;
	private String queueName;	
	
	public void sendMesage(BillDownloadEventNotification event)throws Exception{
		String message = null;
		try {
			message = (String)marshaller.marshall(event);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		final String msg = message; 
		JmsTemplate.send(queueName, new MessageCreator() {
		      public Message createMessage(Session session) throws JMSException {
		        return session.createTextMessage(msg);
		      }
		});
	}

	/**
	 * @return Returns the JmsTemplate.
	 */
	public JmsTemplate getJmsTemplate() {
		return JmsTemplate;
	}
	/**
	 * @param JmsTemplate The JmsTemplate to set.
	 */
	public void setJmsTemplate(JmsTemplate JmsTemplate) {
		this.JmsTemplate = JmsTemplate;
	}
	/**
	 * @return Returns the marshaller.
	 */
	public XmlMarshaller getMarshaller() {
		return marshaller;
	}
	/**
	 * @param marshaller The marshaller to set.
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
	 * @param queueName The queueName to set.
	 */
	public void setQueueName(String queueName) {
		this.queueName = queueName;
	}
}
