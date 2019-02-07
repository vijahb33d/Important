/*
 * MigrationDAO.java
 * Created on 13/07/2007 by pavan.x.kuma
 *
 */
package com.telstra.olb.tegcbm.job.migration.dao;

import java.util.List;

import com.telstra.olb.tegcbm.job.migration.model.OLBCompanyMigration;
import com.telstra.olb.tegcbm.job.migration.model.OLBMigrationActivity;
import com.telstra.olb.tegcbm.job.migration.model.OLBMigrationStatus;
import com.telstra.olb.tegcbm.job.migration.model.PDBUserAccountPreference;
import com.telstra.olb.tegcbm.job.migration.model.CBMUserPreferenceBackup;

/**
 * This interface defines all the functionality for the data migration data store access.
 *  
 */
public interface MigrationDAO {
    
    /**
     * Returns the migration activity data object for the given activity name from the data store.
     *
     * @param name migration activity name.
     * @return migration activity data object
     * @throws MigrationDAOException is thrown if the activity object cannot be found or more than one object
     * with the same name exists.
     */
    OLBMigrationActivity findMigrationActivity(String name) throws MigrationDAOException;
    
    /**
     * Returns the migration status data object for the given status name from the data store.
     *
     * @param activityName migration activity name.
     * @return migration status data object
     * @throws MigrationDAOException is thrown if the status object cannot be found or more than one object
     * with the same name exists.
     */
    OLBMigrationStatus findMigrationStatus(String activityName) throws MigrationDAOException;
    
    /*
     * Audit operations
     */
    
    /**
     * Adds an entry into audit data store for the company. 
     *
     * @param company company
     * @param activity activity name
     * @param status status name
     * @param comments comments.
     * @throws MigrationDAOException is thrown if the audit entry cannot be added.
     */
    void audit(String company, String activity, String status, String comments) throws MigrationDAOException;
    
    /**
     * Adds an entry into audit data store for the company. 
     *
     * @param companyCode company code
     * @param activity activity name
     * @param status status name
     * @param comments comments
     * @throws MigrationDAOException is thrown if the audit entry cannot be added.
     */
    void auditTBU(String companyCode, String activity, String status, String comments) throws MigrationDAOException;
    
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
    List findMigrationAudit(String companyCode, String activity, String activityStatus) throws MigrationDAOException;
    
    /**
     * Gets all the audit items for the company. 
     *
     * @param companyCode company
     * @return list of audit items for teh company
     * @throws MigrationDAOException is thrown if the audit items cannot be retrieved.
     */ 
    List findMigrationAudit(String companyCode) throws MigrationDAOException;
    
    /*
     * Company Migration operations
     */
    
    /**
     * Saves the list of company migration objects that are to be migrated.
     *
     * @param olbCompanyMigrationList list of companies to be migrated.
     * @return no of companies migration objects added.
     * @throws MigrationDAOException is thrown if the list cannot be saved.
     */
    int insertCompanyMigration(List olbCompanyMigrationList) throws MigrationDAOException;
    
    /**
     * This method is responsible for populating the OLB_TBUnmanaged_Migration table. 
     * All the data related to TBUnmanaged Companies will be inserted using hibernate.
     * 
     * @param olbTBUnmanagedCompanyMigrationList list of tbunmanaged companies to be migrated.
     * @return no of companies persisted to the database.
     * @throws MigrationDAOException  is thrown if the list cannot be saved.
     */
    int insertTBUnmanagedCompanyMigration(List olbTBUnmanagedCompanyMigrationList) throws MigrationDAOException ;
    
    /**
     * Method updates the PDBUserAccountPreference object in the data store
     * 
     * @param preference
     * @throws MigrationDAOException
     */
    void updateUserAccountPreference(PDBUserAccountPreference preference) throws MigrationDAOException;
    
    /**
     * Method returns the OLBPDBUserAccountPreference object corresponds to the input params
     * 
     * @param userId
     * @param accountNumber
     * @param attributeId
     * @return
     * @throws MigrationDAOException
     */
    PDBUserAccountPreference getPDBUserAccountPreference(String userId, String accountNumber, String attributeId) throws MigrationDAOException; 
    
    /**
     * Method returns the tb unmanaged companies yet to be migrated
     * 
     * @return
     * @throws MigrationDAOException
     */
    List getTBUCompaniesToBeMigrated()throws MigrationDAOException;
	
    /**
     * Method updates the backup table with the OLBUserProfileAttribute object
     * @param attribute
     * @throws MigrationDAOException
     */
    void updateUserProfileAttribute(CBMUserPreferenceBackup attribute) throws MigrationDAOException;
	
    /**
     * Updates the company migration object in the data store.
     *
     * @param companyMigration company migration.
     * @throws MigrationDAOException is thrown if the company migration object cannot be updated.
     */
    void updateCompanyMigration(OLBCompanyMigration companyMigration) throws MigrationDAOException;
    
    /**
     * Returns a list of company for a given status.
     *
     * @param activityStatus migration status
     * @return list of companies
     * @throws MigrationDAOException is thrown if the list cannot be retrieved.
     */
    List findCompanies(String activityStatus) throws MigrationDAOException; 
    
    /**
     * Returns the details for company identified by given company code.
     * 
     * @param companyCode company.
     * @return company object
     * @throws MigrationDAOException is thrown if the object cannot be retrieved or there is more than one details available.
     */
    OLBCompanyMigration findCompany(String companyCode) throws MigrationDAOException;
    
    /**
     * Returns a list of companies that has the status as pending or error.
     *
     * @return list of companies
     * @throws MigrationDAOException is thrown if the list cannot be retrieved.
     */
    List findCompaniesToMigrate() throws MigrationDAOException;
    /**
     * Returns a list of companies that has the specified statuses.
     * @param activityStatus list of activity status as strings.
     * @return list of compaies.
     * @throws MigrationDAOException is thrown if the list cannot be retrieved.
     */
    List findCompanies(List activityStatus) throws MigrationDAOException; 
    /**
     * Returns a list of companies for a particular segment with specified statuses.
     * @param segments list of segments
     * @param activityStatus list of activity status as strings.
     * @return list of companies.
     * @throws MigrationDAOException is thrown if the list cannot be retrieved.
     */
    List findCompaniesBySegment(List segments, List activityStatus) throws MigrationDAOException;
}
