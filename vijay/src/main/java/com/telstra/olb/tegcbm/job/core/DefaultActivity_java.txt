/*
 * DefaultMigrationActvity.java
 * Created on 12/07/2007 by pavan.x.kuma
 *
 */
package com.telstra.olb.tegcbm.job.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


/**
 * Core Activity implemenation. It implements a chain of responsibility pattern. The activity get an iterator to 
 * total data collection that the activity needs to be executed upon and executes the chain for each data item.
 */
public abstract class DefaultActivity implements Activity {
	/**
	 * 
	 * 
	 */
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
	
    protected transient Log log = LogFactory.getLog(getClass());
    
    private String name;
    private List activityListeners;
    private Activity nextActivity;
    private boolean stopOnError = true;
    
    private IInputValidator inputValidator;
    private IDataProvider dataProvider;
 
    /**
     * Concrete implementations of this default activity template method will implement the <code>doExecute</code> method.
     * Concrete implementations should also notify the activity listeners about the status of the activities.
     * 
     * @param activityContext activity input
     * @throws ActivityException is thrown by concrete implementations.
     * @see com.telstra.olb.tegcbm.job.core.Activity#execute(IContext)
     */
    public final void execute(IContext activityContext) throws ActivityException {
        Iterator inputDataIterator = getData(activityContext);
        preExecute(activityContext);
        try {
	        while (inputDataIterator.hasNext()) {
	            Object inputData = inputDataIterator.next();
	            if (log.isDebugEnabled()) { log.debug("Input Data is "+inputData); }
	            
	            if (canProcess(inputData)) {
		            try {
		                notifyStatus(ActivityStatus.STATUS_PROCESSING, inputData);
		                doExecute(inputData);
		                notifyStatus(ActivityStatus.STATUS_COMPLETE, inputData);
		            } catch (Exception e) {
		                handleException(inputData, e);
		                continue;
		            }
	            } else {
	                if (log.isDebugEnabled()) { log.debug("activity: " + getName() + " skipped..."); }
	            }
	            // The next activity is chained for the current data unit that the activity is executing upon
	            // where as the preExecute and postExecute would work on the overall activity input, which may be 
	            // a collection of data units.
	            Activity nextActivity = getNextActivity();
	            if (nextActivity!= null) {
	                notifyStatus(ActivityStatus.STATUS_PENDING, inputData);
	                nextActivity.execute(toContext(inputData));
	            }
	        } 
        } finally {
            postExecute(activityContext);
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
    
    /**
     * pre execute processing hook. 
     * @param activityContext activity input.
     * @throws ActivityException is thrown by concrete implementations.
     */
    protected void preExecute(IContext activityContext) throws ActivityException {
    }
    /**
     * checks whether the unit of data is a valid data for this activity to execute upon.
     *
     * @param input data unit of activity input.
     * @return true if the data needs to be processed. by default, true is returned.
     * @throws ActivityException .
     */
    protected boolean canProcess(Object input) throws ActivityException {
        return inputValidator != null ? inputValidator.validateInput(input, this) : true;
    }
    /**
     * Default implementation of the activity. The input to this method is the unit of data the activity
     * needs to process.
     * @param input data unit of activity input.
     * @throws ActivityException is thrown by concrete implementations.
     */
    protected abstract void doExecute(Object input) throws ActivityException;
    /**
     * Post execute processing hook. 
     * @param activityContext activity input.
     * @throws ActivityException is thrown by concrete implementations.
     */
    protected void postExecute(IContext activityContext) throws ActivityException {
    }
    /**
     * handles any exception thrown. if the values stopOnError is thrown, then the activity is stopped
     * and the exception is rethrown as ActivityException.
     *
     * @param current current data
     * @param t exception
     * @throws ActivityException is thrown, all other exceptions are wrapped as Activity exception.
     */
    protected void handleException(Object current, Throwable t) throws ActivityException {
        notifyError(current, t);
        if (isStopOnError()) {
            if (log.isDebugEnabled()) { log.debug("the activity is configured to stop on error. stopping activity: " + getName()); }
            if (t instanceof ActivityException) {
                throw (ActivityException) t;
            } else {
                throw new ActivityException("unexpected exception.", t);
            }
        } else {
            if (log.isDebugEnabled()) { log.debug("an error occured for activity: " + getName() + ", but continuing processing of next data."); }
        }
    }
    /**
     * Template method that required implementation of <code>doGetData</code>. 
     * By default it returns an empty iterator.
     * @param activityContext activity input
     * @return Empty iterator.
     * @see com.telstra.olb.tegcbm.job.core.Activity#getData(IContext)
     */
    public final Iterator getData(IContext activityContext) {
    	IContext context = toContext(activityContext);
    	if (context.getInput() != null) {
    		List data = new ArrayList();
    		data.add(context.getInput());
    		return data.iterator();
    	}
    	Collection data = null;
    	if (dataProvider != null) {
    		data = dataProvider.getData(context);
    	} 
    	return data != null ? data.iterator() : (new ArrayList()).iterator();
    }
    
    /**
     * notifies the list of configured listeners about the status of the
     * activity.
     * 
     * @param status activity status
     * @param eventParams params for the activity status event.
     */
    protected void notifyStatus(ActivityStatus status, Object eventParams) {
        if ((activityListeners == null) || (activityListeners.isEmpty())) {
            return;
        }
        if (log.isDebugEnabled()) { log.debug("activity: " + getName() + ", notifying listeners: " + status); }
        ActivityStatusEvent event = new ActivityStatusEvent(this, status, eventParams);
        Iterator iterator = activityListeners.iterator();
        while (iterator.hasNext()) {
            ActivityStatusListener listener = (ActivityStatusListener) iterator.next();
            listener.handleActivityStatus(event);
        }
    }
    /**
     * notifies the list of configured listeners about the error status of the
     * activity.
     *
     * @param eventParams params for the activity status event.
     * @param t error.
     */
    protected void notifyError(Object eventParams, Throwable t) {
        notifyStatus(ActivityStatus.STATUS_ERROR, new Object[] {eventParams, t});
    }

    /**
     * @return Returns the name.
     */
    public final String getName() {
        return name;
    }
    /**
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }
    /**
     * @return Returns the jobListeners.
     */
    public List getActivityListeners() {
        return activityListeners;
    }
    /**
     * @param activityListeners The activityListeners to set.
     */
    public void setActivityListeners(List activityListeners) {
        this.activityListeners = activityListeners;
    }
    
    /**
     * @return Returns the nextActivity.
     */
    public Activity getNextActivity() {
        return nextActivity;
    }
    /**
     * @param nextActivity The nextActivity to set.
     */
    public void setNextActivity(Activity nextActivity) {
        this.nextActivity = nextActivity;
    }
    /**
     * @return Returns the stopOnError.
     */
    public boolean isStopOnError() {
        return stopOnError;
    }
    /**
     * @param stopOnError The stopOnError to set.
     */
    public void setStopOnError(boolean stopOnError) {
        this.stopOnError = stopOnError;
    }
	/**
	 * @return Returns the dataProvider.
	 */
	public IDataProvider getDataProvider() {
		return dataProvider;
	}
	/**
	 * @param dataProvider The dataProvider to set.
	 */
	public void setDataProvider(IDataProvider dataProvider) {
		this.dataProvider = dataProvider;
	}
	/**
	 * @return Returns the inputValidator.
	 */
	public IInputValidator getInputValidator() {
		return inputValidator;
	}
	/**
	 * @param inputValidator The inputValidator to set.
	 */
	public void setInputValidator(IInputValidator inputValidator) {
		this.inputValidator = inputValidator;
	}
}
