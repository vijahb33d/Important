/*
 * Created on 16/07/2007
 *
 */
package com.telstra.olb.tegcbm.job.migration.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Session;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.orm.hibernate.HibernateCallback;
import org.springframework.orm.hibernate.support.HibernateDaoSupport;

import com.telstra.olb.tegcbm.job.core.ActivityStatus;
import com.telstra.olb.tegcbm.job.migration.model.OLBCompanyMigration;
import com.telstra.olb.tegcbm.job.migration.model.OLBMigrationActivity;
import com.telstra.olb.tegcbm.job.migration.model.OLBMigrationAudit;
import com.telstra.olb.tegcbm.job.migration.model.OLBMigrationStatus;
import com.telstra.olb.tegcbm.job.migration.model.OLBTBUMigrationAudit;
import com.telstra.olb.tegcbm.job.migration.model.PDBUserAccountPreference;
import com.telstra.olb.tegcbm.job.migration.model.CBMUserPreferenceBackup;
import com.telstra.olb.tegcbm.job.migration.util.MigrationHelper;

/**
 * This class is the hibernate implementation of <code>MigrationDAO</code> interface. It 
 * provides the concrete implementation for all the data migration data store access.
 * 
 * @author <a href="mailto:Deepak.Bawa@team.telstra.com.au">Deepak Bawa</a>
 */
public class MigrationDAOHibernateImpl extends HibernateDaoSupport implements MigrationDAO {
    private static Log log = LogFactory.getLog(MigrationDAOHibernateImpl.class);
    
    private static final int DEFAULT_INPUT_BATCH_SIZE = 100;
    private int inputBatchSize = DEFAULT_INPUT_BATCH_SIZE;
    
    /**
     * Returns the batch size for processing.
     * 
     * @return Returns the inputBatchSize.
     */
    public int getInputBatchSize() {
        return inputBatchSize;
    }
    /**
     * Sets the batch size for processing.
     * 
     * @param inputBatchSize The inputBatchSize to set.
     */
    public void setInputBatchSize(int inputBatchSize) {
        this.inputBatchSize = inputBatchSize;
    }
    /**
     * Returns a list of company for a given status.
     *
     * @param activityStatus migration status
     * @return list of companies
     * @throws MigrationDAOException is thrown if the list cannot be retrieved.
     */
    public List findCompanies(String activityStatus) throws MigrationDAOException {
        if (log.isDebugEnabled()) { log.debug("Retrieving companies with the status:" + activityStatus);}
        List status = new ArrayList();
        status.add(activityStatus);
        return findCompanies(status);
    }
    /**
     * Returns a list of companies that has the status as pending.
     *
     * @return list of companies
     * @throws MigrationDAOException is thrown if the list cannot be retrieved.
     */
    public List findCompaniesToMigrate() throws MigrationDAOException {
        if (log.isDebugEnabled()) { log.debug("Retrieving companies that can be migrated");}
        List status = new ArrayList();
        status.add(ActivityStatus.STATUS_PENDING.getName());
        status.add(ActivityStatus.STATUS_ERROR.getName());
        return findCompanies(status);
    }
    
    /**
     * Returns a list of companies that has the specified statuses.
     * @param activityStatus list of activity status as strings.
     * @return list of compaies.
     * @throws MigrationDAOException is thrown if the list cannot be retrieved.
     */
    public List findCompanies(List activityStatus) throws MigrationDAOException {
        String hql = "select company from OLBCompanyMigration as company, OLBMigrationStatus as status " + 
        		"where company.activityStatus = status.id and status.status in ";
	    String sql = MigrationHelper.createInSQLQuery(hql, activityStatus);
	    return getHibernateTemplate().find(sql);
    }
    
    /**
     * Returns a list of companies for a particular segment with specified statuses.
     * @param segments list of segments
     * @param activityStatus list of activity status as strings.
     * @return list of companies.
     * @throws MigrationDAOException is thrown if the list cannot be retrieved.
     */
    public List findCompaniesBySegment(List segments, List activityStatus) throws MigrationDAOException {
        String hql = "select company from OLBCompanyMigration as company, OLBMigrationStatus as status " + 
				"where company.activityStatus = status.id and status.status in ";
        List status = null;
        if ((activityStatus == null) || (activityStatus.isEmpty())) {
            status = new ArrayList();
            status.add(ActivityStatus.STATUS_PENDING.getName());
            status.add(ActivityStatus.STATUS_ERROR.getName());
        } else {
            status = activityStatus;
        }
        String sql = MigrationHelper.createInSQLQuery(hql, activityStatus);
        if ((segments != null) && (!segments.isEmpty())) {
            sql += " and company.segment in ";
        	sql = MigrationHelper.createInSQLQuery(sql, segments);
        }
        return getHibernateTemplate().find(sql);
    }
    
    
    /**
     * Method returns the OLBPDBUserAccountPreference object corresponds to the input params
     * 
     * @param userId
     * @param accountNumber
     * @param attributeId
     * @return
     * @throws MigrationDAOException
     */
    public PDBUserAccountPreference getPDBUserAccountPreference(String userId, String accountNumber, String attributeId) throws MigrationDAOException {
    	String hql = "select from OLBPDBUserAccountPreference as pref where pref.userId = ? AND pref.accountNumber = ? AND pref.attributeId = ?";
    	List preferences = getHibernateTemplate().find(hql,new Object[]{userId, accountNumber, attributeId});
    	if(preferences == null || preferences.isEmpty()){
    		throw new MigrationDAOException("Invalid search params for getPDBUserAccountPreference Userid :"+ userId +" accountNumber :"+accountNumber+" attribute:"+attributeId);
    	}
    	return (PDBUserAccountPreference)preferences.get(0);
    }
    
    
    
    
    /**
     * Returns the details for company identified by given company code.
     * 
     * @param companyCode company.
     * @return company object
     * @throws MigrationDAOException is thrown if the object cannot be retrieved or there is more than one details available.
     */
    public OLBCompanyMigration findCompany(String companyCode) throws MigrationDAOException {
        String hql = "select from OLBCompanyMigration as company where company.companyCode = ?";
        List companyList = getHibernateTemplate().find(hql, new Object[] {companyCode});
        if ((companyList.isEmpty()) || (companyList.size() > 1)) {
            throw new MigrationDAOException("invalid search params, either no company details or more than one details exist for company: "
                    + companyCode);
        }
        return (OLBCompanyMigration) companyList.get(0);
    }    
    /**
     * Saves the list of company migration objects that are to be migrated.
     *
     * @param olbCompanyMigrationList list of companies to be migrated.
     * @return no of companies persisted to the database.
     * @throws MigrationDAOException is thrown if the list cannot be saved.
     */
    public int insertCompanyMigration(List olbCompanyMigrationList) throws MigrationDAOException {
        List existingCompanies = getHibernateTemplate().find("select from OLBCompanyMigration");
        Collection distinctCompanies = CollectionUtils.subtract(olbCompanyMigrationList, existingCompanies);
        if (log.isDebugEnabled()) { log.debug("inserting {" + distinctCompanies.size() + "}from{"+olbCompanyMigrationList.size()+"} to migrate.");}
        insertCollection(distinctCompanies);
        return distinctCompanies.size();
    }
    
    /**
     * This method is responsible for populating the OLB_TBUnmanaged_Migration table. 
     * All the data related to TBUnmanaged Companies will be inserted using hibernate.
     * 
     * @param olbTBUnmanagedCompanyMigrationList list of tbunmanaged companies to be migrated.
     * @return no of companies persisted to the database.
     * @throws MigrationDAOException  is thrown if the list cannot be saved.
     */
    public int insertTBUnmanagedCompanyMigration(List olbTBUnmanagedCompanyMigrationList) throws MigrationDAOException {
    	 List existingCompanies = getHibernateTemplate().find("select from OLBTBUnmanagedCompanyMigration");
         Collection distinctCompanies = CollectionUtils.subtract(olbTBUnmanagedCompanyMigrationList, existingCompanies);
         if (log.isDebugEnabled()) { log.debug("inserting TBUnmanaged companies {" + distinctCompanies.size() + "}from{"+olbTBUnmanagedCompanyMigrationList.size()+"} to migrate.");}
         insertCollection(distinctCompanies);
         return distinctCompanies.size();
    }
    
    /**
     * Updates the company migration object in the data store.
     *
     * @param companyMigration company migration.
     * @throws MigrationDAOException is thrown if the company migration object cannot be updated.
     */
    public void updateCompanyMigration(OLBCompanyMigration companyMigration) throws MigrationDAOException {
        try {
            companyMigration.setUpdatedDate(new Date());
            if (log.isDebugEnabled()) { log.debug("Updating company migration details for company:" + companyMigration.getCompanyCode());}
            getHibernateTemplate().update(companyMigration);
        } catch (DataAccessException e) {
            throw new MigrationDAOException("unable to update the status of company: " + companyMigration.getCompanyCode(), e);
        }
    }
    
    /**
     * Method updates the PDBUserAccountPreference object in the data store
     * 
     * @param preference
     * @throws MigrationDAOException
     */
    public void updateUserAccountPreference(PDBUserAccountPreference preference) throws MigrationDAOException {
    	try {
            if (log.isDebugEnabled()) { log.debug("Updating Preference details for user:" + preference.getUserId() + "  accountNumber :"+preference.getAccountNumber());}
            getHibernateTemplate().update(preference);
        } catch (DataAccessException e) {
            throw new MigrationDAOException("unable to update the preference details of user: " + preference.getUserId() + "  accountNumber :"+preference.getAccountNumber(), e);
        }
    }
    
    /**
     * Method updates the backup table with the OLBUserProfileAttribute object
     * @param attribute
     * @throws MigrationDAOException
     */
    public void updateUserProfileAttribute(CBMUserPreferenceBackup attribute) throws MigrationDAOException {
    	try {
            if (log.isDebugEnabled()) { log.debug("Taking backup of preferences for company:" + attribute.getCompanyCode());}
            getHibernateTemplate().update(attribute);
        } catch (DataAccessException e) {
            throw new MigrationDAOException("unable to take backup of preference details for the company: " + attribute.getCompanyCode(), e);
        }
    }
    
    /**
     * Method returns the tb unmanaged companies yet to be migrated
     * 
     * @return
     * @throws MigrationDAOException
     */
    public List getTBUCompaniesToBeMigrated()throws MigrationDAOException{
    	String hql = "select from OLBTBUnmanagedCompanyMigration tbm where tbm.activityStatus="+ActivityStatus.STATUS_COMPLETE.getValue();
    	try{
    		return getHibernateTemplate().find(hql);
    	}catch (DataAccessException e) {
            throw new MigrationDAOException("unable to retrieve the tbunmanaged companies from OLB_TBUMANAGED_MIGRATION");
        }
    }
    
    
    /**
     * Returns the list of companies for the activity which have the status in audit table
     * as passed from the audit data store.
     *
     * @param companyCode Company Code
     * @param activity activity
     * @param activityStatus status of the activity.
     * @return a list of records that match the criteria.
     * @throws MigrationDAOException is thrown if the list could not be retrieved.
     */
    public List findMigrationAudit(String companyCode, String activity, String activityStatus) throws MigrationDAOException {
        String hql = "select audit from OLBMigrationAudit as audit, OLBMigrationActivity as migrationActivity," +
			" OLBMigrationStatus as migrationStatus where audit.activity.id = migrationActivity.id" +
	        " and audit.activityStatus.id = migrationStatus.id and migrationActivity.activityName = ?" +
	        " and migrationStatus.status = ? and audit.companyCode = ?";
        try {
            if (log.isDebugEnabled()) { log.debug("Audit for activity: " + activity + " with status: " + activityStatus+" for comp: "+companyCode);}
            return getHibernateTemplate().find(hql, new Object[] { activity, activityStatus, companyCode });
        } catch (DataAccessException e) {
            throw new MigrationDAOException("unable to retrieve audit history for: " + activity + "{" + activityStatus + "}", e);
        }
    }
    /**
     * Gets all the audit items for the company. 
     *
     * @param companyCode company
     * @return list of audit items for the company
     * @throws MigrationDAOException is thrown if the audit items cannot be retrieved.
     */ 
    public List findMigrationAudit(String companyCode) throws MigrationDAOException {
        String hql = "select from OLBMigrationAudit as audit where audit.companyCode = ? ";
        try {
            if (log.isDebugEnabled()) { log.debug("Returning audit details for company:" + companyCode);}
            return getHibernateTemplate().find(hql, new Object[] {companyCode});
        } catch (DataAccessException e) {
            throw new MigrationDAOException("unable to retrieve audit history for company: " + companyCode, e);
        }
    }
    /**
     * Adds an entry into audit data store for the company. 
     *
     * @param companyCode company code
     * @param activity activity name
     * @param status status name
     * @param comments comments
     * @throws MigrationDAOException is thrown if the audit entry cannot be added.
     */
    public void auditTBU(String companyCode, String activity, String status, String comments) throws MigrationDAOException {
        OLBMigrationActivity migrationActivity = findMigrationActivity(activity);
        OLBMigrationStatus migrationStatus = findMigrationStatus(status);
        OLBTBUMigrationAudit audit = createTBUMigrationAudit(companyCode, migrationActivity, migrationStatus, comments);
        try {	
            getHibernateTemplate().save(audit);
    	} catch (DataAccessException e) {
    	    throw new MigrationDAOException("unable to add audit details for company: " + companyCode, e);
    	}
    }
    /**
     * Adds an entry into audit data store for the company. 
     *
     * @param companyCode company code
     * @param activity activity name
     * @param status status name
     * @param comments comments
     * @throws MigrationDAOException is thrown if the audit entry cannot be added.
     */
    public void audit(String companyCode, String activity, String status, String comments) throws MigrationDAOException {
        OLBMigrationActivity migrationActivity = findMigrationActivity(activity);
        OLBMigrationStatus migrationStatus = findMigrationStatus(status);
        OLBMigrationAudit audit = createMigrationAudit(companyCode, migrationActivity, migrationStatus, comments);
        try {	
            getHibernateTemplate().save(audit);
    	} catch (DataAccessException e) {
    	    throw new MigrationDAOException("unable to add audit details for company: " + companyCode, e);
    	}
    }
    /**
     * Creates a OLBMigrationAudit Object.
     *
     * @param companyCode company
     * @param migrationActivity migration activity
     * @param migrationStatus migration activity status
     * @param comments comment.
     * @return OLBMigrationAudit Object.
     */
    private OLBMigrationAudit createMigrationAudit(String companyCode, OLBMigrationActivity migrationActivity,
            OLBMigrationStatus migrationStatus, String comments) {
        OLBMigrationAudit audit = new OLBMigrationAudit();
        audit.setCompanyCode(companyCode);
        audit.setActivity(migrationActivity);
        audit.setActivityStatus(migrationStatus);
        audit.setCreatedDate(new Date());
        audit.setComments(comments);
        return audit;
    }
    /**
     * Creates a OLBMigrationAudit Object.
     *
     * @param companyCode company
     * @param migrationActivity migration activity
     * @param migrationStatus migration activity status
     * @param comments comment.
     * @return OLBMigrationAudit Object.
     */
    private OLBTBUMigrationAudit createTBUMigrationAudit(String companyCode, OLBMigrationActivity migrationActivity,
            OLBMigrationStatus migrationStatus, String comments) {
        OLBTBUMigrationAudit audit = new OLBTBUMigrationAudit();
        audit.setCompanyCode(companyCode);
        audit.setActivity(migrationActivity);
        audit.setActivityStatus(migrationStatus);
        audit.setCreatedDate(new Date());
        audit.setComments(comments);
        return audit;
    }
    /**
     * Returns the migration status data object for the given status name from the data store.
     *
     * @param activityName migration activity name.
     * @return migration status data object
     * @throws MigrationDAOException is thrown if the status object cannot be found or more than one object
     * with the same name exists.
     */
    public OLBMigrationActivity findMigrationActivity(String activityName) throws MigrationDAOException {
        String sql = "select from OLBMigrationActivity as migrationActivity where migrationActivity.activityName = ?";
        List activity = getHibernateTemplate().find(sql, new Object[] { activityName });
        if ((activity.isEmpty()) || (activity.size() > 1)) {
            throw new MigrationDAOException("unable to find or duplicate activity definitions in database for " + activityName);
        }
        return (OLBMigrationActivity) activity.get(0);
    }
    /**
     * Returns the migration status data object for the given name from the OLB_MIGRATION_STATUS table.
     *
     * @param name migration status name.
     * @return migration status data object
     * @throws MigrationDAOException is thrown if the status object cannot be found or more than one object
     * with the same name exists.
     */
    public OLBMigrationStatus findMigrationStatus(String name) throws MigrationDAOException {
        String sql = "select from OLBMigrationStatus as migrationStatus where migrationStatus.status = ?";
        List activity = getHibernateTemplate().find(sql, new Object[] { name });
        if ((activity.isEmpty()) || (activity.size() > 1)) {
            throw new MigrationDAOException("unable to find or duplicate status definitions in database for " + name);
        }
        return (OLBMigrationStatus) activity.get(0);
    }
    
    /**
     * Inserts a collection in a batch.
     *
     * @param collection collection to insert.
     * @throws MigrationDAOException is thrown if the collection cannot be inserted.
     */
    private void insertCollection(final Collection collection) throws MigrationDAOException {
        try {
            getHibernateTemplate().execute(new HibernateCallback() {
                public Object doInHibernate(Session session) throws HibernateException, SQLException {
                    int batchElement = 0;
                    for (Iterator iterator = collection.iterator();iterator.hasNext(); batchElement++) {
                        session.save(iterator.next());
                        if (batchElement%getInputBatchSize() == 0) {
                            session.flush();
                            session.clear();
                        }
                    }
                    return null;
                }
            });
        } catch (DataAccessException e) {
            throw new MigrationDAOException("unable to insert collection: " + collection.size(), e);
        }
    }
    
}
