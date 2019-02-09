/*
 * Created on 26/08/2010
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.telstra.olb.tegcbm.job.salmat.activity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.jms.TextMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jms.core.JmsTemplate102;

import com.telstra.olb.tegcbm.job.core.Activity;
import com.telstra.olb.tegcbm.job.core.ActivityException;
import com.telstra.olb.tegcbm.job.core.IContext;

/**
 * @author arun.balasubramanian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SalmatClientActivity implements Activity{

	protected transient Log log = LogFactory.getLog(getClass());
	
	private static final class DataItem implements IContext {
		private Object input;
		
		private Map properties = new HashMap();
		/**
		 * 
		 * @param input input.
		 */
		private DataItem(Object input) {
			this.input = input;
		}
		/* (non-Javadoc)
		 * @see com.telstra.olb.tegcbm.job.core.IContext#getInput()
		 */
		public Object getInput() {
			return input;
		}

		/* (non-Javadoc)
		 * @see com.telstra.olb.tegcbm.job.core.IContext#getProperty(java.lang.String)
		 */
		public Object getProperty(String name) {
			return properties.get(name);
		}

		/* (non-Javadoc)
		 * @see com.telstra.olb.tegcbm.job.core.IContext#getPropertyNames()
		 */
		public Set getPropertyNames() {
			return properties.keySet();
		}
		
		public void setProperty(String name, Object property) {
			properties.put(name, property);
		}	
	}
	
	private JmsTemplate102 jmsTemplate;

	private String queueName;
	
	private int pollInterval;
	
	private Activity nextActivity;
	
	private String name;
	
	
	public void execute(IContext activityContext) throws ActivityException {
		while(true){
			TextMessage message = (TextMessage) jmsTemplate.receive(queueName);
			if(log.isDebugEnabled()){log.debug("Message Received: "+message);}
			if(message != null){
				Activity nextActivity = getNextActivity();
	            if (nextActivity!= null) {
	                nextActivity.execute(toContext(message));
	            }
			}
			try {
				Thread.sleep(pollInterval);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	 /**
     * 
     * @param activityInput input
     * @return activity context.
     */
    protected IContext toContext(Object activityInput) {
    	IContext context = null;
    	if (activityInput instanceof IContext) {
    		context = (IContext) activityInput;
    	} else {
    		context = new DataItem(activityInput);
    	}
    	return context;
    }
	
	/* (non-Javadoc)
	 * @see com.telstra.olb.tegcbm.job.core.Activity#getNextActivity()
	 */
	public Activity getNextActivity() {
		return this.nextActivity;
	}

	/* (non-Javadoc)
	 * @see com.telstra.olb.tegcbm.job.core.Activity#getData(com.telstra.olb.tegcbm.job.core.IContext)
	 */
	public Iterator getData(IContext activityContext) {
		// TODO Auto-generated method stub
		return null;
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
	/**
	 * @return Returns the jmsTemplate.
	 */
	public JmsTemplate102 getJmsTemplate() {
		return jmsTemplate;
	}
	/**
	 * @param jmsTemplate The jmsTemplate to set.
	 */
	public void setJmsTemplate(JmsTemplate102 jmsTemplate) {
		this.jmsTemplate = jmsTemplate;
	}
	/**
	 * @return Returns the pollInterval.
	 */
	public int getPollInterval() {
		return pollInterval;
	}
	/**
	 * @param pollInterval The pollInterval to set.
	 */
	public void setPollInterval(int pollInterval) {
		this.pollInterval = pollInterval;
	}
	/**
	 * @param nextActivity The nextActivity to set.
	 */
	public void setNextActivity(Activity nextActivity) {
		this.nextActivity = nextActivity;
	}
	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return name;
	}
	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}
}
