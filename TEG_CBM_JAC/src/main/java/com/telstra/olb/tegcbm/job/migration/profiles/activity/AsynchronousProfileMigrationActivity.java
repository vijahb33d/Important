/*
 * AsynchronousProfileMigrationActivity.java
 * Created on 13/08/2007 by pavan.x.kuma
 *
 */
package com.telstra.olb.tegcbm.job.migration.profiles.activity;

import com.telstra.olb.tegcbm.job.core.ActivityException;
import com.telstra.olb.tegcbm.job.core.AsynchronousActivity;
import com.telstra.olb.tegcbm.job.core.IContext;
import com.telstra.olb.tegcbm.job.migration.profiles.dao.PDBProfilesDAO;
import com.telstra.olb.tegcbm.job.migration.profiles.dao.PDBProfilesDAOException;

/**
 * 
 */

public class AsynchronousProfileMigrationActivity extends AsynchronousActivity {
    private PDBProfilesDAO profilesPdbDao;
    
    private long currentTimeout;
    
    private long cacheTimeOut;
    
    /**
     * @return Returns the cacheTimeOut.
     */
    public long getCacheTimeOut() {
        return cacheTimeOut;
    }
    /**
     * @param cacheTimeOut The cacheTimeOut to set.
     */
    public void setCacheTimeOut(long cacheTimeOut) {
        this.cacheTimeOut = cacheTimeOut;
    }
    /**
     * @return Returns the profilesPdbDao.
     */
    public PDBProfilesDAO getProfilesPdbDao() {
        return profilesPdbDao;
    }
    /**
     * @param profilesPdbDao The profilesPdbDao to set.
     */
    public void setProfilesPdbDao(PDBProfilesDAO profilesPdbDao) {
        this.profilesPdbDao = profilesPdbDao;
    }
    /**
     * sets the cache timeout to a a value to effective disable updates from R&E.
     * @param activityContext activity input
     * @throws ActivityException is thrown if the cache control cannot be updated
     * @see com.telstra.olb.tegcbm.job.core.DefaultActivity#preExecute(IContext)
     */
    protected void preExecute(IContext activityContext) throws ActivityException {
        super.preExecute(activityContext);
        if (log.isDebugEnabled()) { log.debug("extending the cache interval of profile management system to " + cacheTimeOut + " hrs."); }
        try {
            currentTimeout = profilesPdbDao.getProfileCacheTimeOut();
            profilesPdbDao.setProfileCacheTimeOut(cacheTimeOut);
        } catch (PDBProfilesDAOException e) {
            throw new ActivityException("unable to set the PDB cache timeout.", e);
        }
    }
    
    /**
     * reverts backs the cache control to the value that has been set before extending the value.
     * @param activityContext activity input.
     * @throws ActivityException is thrown if the cache control value cannot be reverted back.
     * @see com.telstra.olb.tegcbm.job.core.DefaultActivity#postExecute(IContext)
     */
    protected void postExecute(IContext activityContext) throws ActivityException {
        super.postExecute(activityContext);
        if (log.isDebugEnabled()) { log.debug("resetting the cache interval for profile management system to " + currentTimeout + " hrs."); }
        try {
            profilesPdbDao.setProfileCacheTimeOut(currentTimeout);
        } catch (PDBProfilesDAOException e) {
            throw new ActivityException("unable to set the PDB cache timeout.", e);
        }
    }
}
