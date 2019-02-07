package com.telstra.olb.tegcbm.job.migration.populate.dao;

import java.util.List;
import java.util.Map;

import com.telstra.olb.tegcbm.job.migration.populate.model.MigrationType;

/**
 * 
 */
public interface PopulateDao {
    /**
     * List of company codes for the given migration Type.
     *
     * @param migrationType SME|ENTERPRISE.
     * @return list of OLBCompanyMigration objects.
     * @throws PopulateDataException is thrown.
     */
    List getCompanies(MigrationType migrationType) throws PopulateDataException;
    
    /**
     * This method will be created as part of CR3134. This method is responsible for retrieving the TB Unmanaged companies. 
     * The list will consist of OLBTBUnmanageMigration objects.
     *
     * @param migrationType SME|ENTERPRISE.
     * @return list of OLBCompanyMigration objects.
     * @throws PopulateDataException is thrown.
     */
    List getTBUnmanagedCompanies(MigrationType migrationType) throws PopulateDataException;
    
    /** 
     * Runs a cross schema query to obtain a list of tbunmanaged company preferences based on the segment and account
     * information from OLB and Company information from PDB.
     * @param migrationType migration type
     * @return a list of OLBCompanyMigration object.
     * @throws PopulateDataException is thrown if there are any 
     */
    List getTBUnmanagedCompanyPreferences(final String cidn)throws PopulateDataException;
    
    /**
     * This method is responsible for inserting the user preferences in the PDB table PDB_DBA.PDB_USER_ACCOUNT_PREF_VALUES_T.
     * 
     * @param userAccountPreferences
     * @throws PopulateDataException
     */
    void insertUserAccountPreference(final List userAccountPreferences) throws PopulateDataException;
    
    /**
     * This method is responsible for inserting the CBM preferences records in the back-up table called EDX_DBA.EDX_BSL_USER_PROF_ATTRIBS_BKUP.
     * 
     * @param userAccountPreferences
     * @throws PopulateDataException
     */
    void backupUserAccountPreference(final List userAccountPreferences) throws PopulateDataException;
    
    /**
     * Removes the user account preferences from the EDX CBM database.
     * 
     * @param userAccountPreferences list of user notification preferences
     * @throws PopulateDataException thrown if there is problem working with EDX CBM datastore
     */
    void deleteUserAccountPreferences(final List userAccountPreferences) throws PopulateDataException;
    
    /**
     * Method returns the list of accounts for a company code
     * 
     * @param cidn
     * @return
     * @throws PopulateDataException
     */
    List getAccountNumbersForCompany(String cidn) throws PopulateDataException ;
    
    /** 
     * This method will retrieve the companies to be deleted from the OLB_ACCOUNT_COMPANY_MAP table. 
     * @param migrationType migration type
     * @return a list of OLBAccountCompanyMap object.
     * @throws PopulateDataException is thrown if there are any 
     */
    List getAccountCompanies(MigrationType migrationType) throws PopulateDataException;
    
    /**
     * This method will take a back-up of the companies to be deleted in the OLB_ACCOUNT_COMPANY_MAP_BKUP table. 
     * 
     * @param accountCompanies
     * @throws PopulateDataException
     */
    void backupAccountCompany(final List accountCompanies) throws PopulateDataException;
    
    /**
     * This method will delete the successfully migrated TBU companies from the OLB_ACCOUNT_COMPANY_MAP table. 
     * 
     * @param accountCompanies 
     * @throws PopulateDataException thrown if there is problem working with EDX CBM datastore
     */
    void deleteAccountCompany(final List accountCompanies) throws PopulateDataException;
    
    /** 
     * This method will retrieve the successfully migrated TBU companies from the database.
     * 
     * @return a list of OLBTBUnmanagedCompanyMigration object.
     * @throws PopulateDataException is thrown if there are any 
     */
    List getMigratedTBUCompanies() throws PopulateDataException;
    
    
    /**
     * Method updates the report field for Successfully migrated companies which are included in the report.
     * 
     * @param companies
     * @throws PopulateDataException
     */
    void updateReported(final List companies) throws PopulateDataException;
    
    /**
     * Method returns all the CIDNs from the TBUnmanaged migration table
     * 
     * @return
     * @throws PopulateDataException
     */
    public List getTBUnmanagedCIDNs() throws PopulateDataException;
    
    
    public List getCompanyList(MigrationType migrationType) throws PopulateDataException ;
    
    public Map getCompanyByCidn(final String cidn) throws PopulateDataException ;
}
