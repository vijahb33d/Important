/*
 * Created on 23/11/2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.telstra.olb.tegcbm.job.migration.populate.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import com.telstra.olb.tegcbm.job.core.ActivityStatus;
import com.telstra.olb.tegcbm.job.migration.model.OLBAccountCompanyMap;
import com.telstra.olb.tegcbm.job.migration.model.OLBCompanyMigration;
import com.telstra.olb.tegcbm.job.migration.model.PDBUserAccountPreference;
import com.telstra.olb.tegcbm.job.migration.model.OLBTBUnmanagedCompanyMigration;
import com.telstra.olb.tegcbm.job.migration.model.OLBTBUnmanagedCompanyPreference;
import com.telstra.olb.tegcbm.job.migration.model.CBMUserPreferenceBackup;
import com.telstra.olb.tegcbm.job.migration.populate.model.MigrationType;

/**
 * @author pavan.x.kuma
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PopulateDaoSpringJdbcImpl extends JdbcDaoSupport implements PopulateDao {
    private static Log log = LogFactory.getLog(PopulateDaoSpringJdbcImpl.class);
    
    private static final String BL_APP_ID = "BL";
    private static final String COL_CIDN = "cidn";
    private static final String COL_OWN_CODE = "own_code";
    private static final String COL_SEGMENT = "segment";
    private static final String COL_MACRO_SEGMENT = "macro_segment";
    private static final String COL_COMPANY_ID = "company_id";
    private static final String COL_COMPANY_CODE = "company_code";
    private static final String COL_ACCOUNT_NUMBER = "account_number";
    private static final String COL_ACCOUNT_TYPE_ID = "account_type_id";
    private static final String COL_RELATION_ID = "relation_id";
    private static final String TBU_SEGMENT = "SmallBusiness";
    private static final String TBU_MACRO_SEGMENT = "SME";
    private static final String COL_USER_ID = "userid";
    private static final String COL_ATTR_KEY = "attrkey";
    private static final String COL_ATTR_VALUE = "attrvalue";
    private static final String COL_CREATED_DATE = "created_date";
    private static final String COL_ACTIVITY_STATUS = "activity_status_id";
    private static final String COL_ATTR_ID = "attrId";
    private static final String COL_ACCT_NUMBER = "ACCOUNT_NUMBER";
    private static final String UPDATE_SQL_PDB_USER_PREFERENCES = "UPDATE PDB_DBA.PDB_USER_ACCOUNT_PREF_VALUES_T SET APPLICATION_ID = ? WHERE ECSID = ? AND ACCOUNT_NUMBER = ? AND ATTRIBUTE_ID = 'SPP'";
    private static final String INSERT_SQL_PDB_USER_PREFERENCES = "INSERT INTO PDB_DBA.PDB_USER_ACCOUNT_PREF_VALUES_T "
        + "(ECSID, ACCOUNT_NUMBER, APPLICATION_ID, ATTRIBUTE_ID, VALUE) VALUES (?,?,?,?,?)";
    private static final String DELETE_SQL_PDB_USER_PREFERENCES = "DELETE FROM PDB_DBA.PDB_USER_ACCOUNT_PREF_VALUES_T WHERE ECSID = ? AND ACCOUNT_NUMBER = ? AND "
        + "APPLICATION_ID = ? AND ATTRIBUTE_ID = ?";
    private static final String INSERT_SQL_USER_PROFILE_BKUP = "INSERT INTO EDX_DBA.EDX_BSL_USER_PROF_ATTRIBS_BKUP "
        + "(ATTRID, ATTRVALUE, ATTRKEY, CIDN, CREATED_DATE) VALUES (?,?,?,?,?)";
    private static final String DELETE_SQL_USER_PROFILE = "DELETE FROM EDX_DBA.EDX_BSL_USER_PROF_ATTRIBS WHERE ATTRID = ? AND ATTRKEY = ?";
    private static final String INSERT_SQL_ACM_BKUP = "INSERT INTO EDX_DBA.OLB_ACCOUNT_COMPANY_MAP_BKUP VALUES(?,?,?,?)";
    private static final String DELETE_SQL_ACM = "DELETE FROM EDX_DBA.OLB_ACCOUNT_COMPANY_MAP WHERE ACCOUNT_NUMBER = ? AND COMPANY_ID = ?";
    private static final String SELECT_SQL_CIDN = "SELECT COMPANY_CODE FROM EDX_DBA.OLB_TBUNMANAGED_MIGRATION tbm WHERE tbm.activity_status_id not in (2,3)";
    
	
    private Map sqls = new HashMap();
    /** 
     * Runs a cross schema query to obtain a list of companies based on the segment and account
     * information from OLB and Company information from PDB.
     * @param migrationType migration type
     * @return a list of OLBCompanyMigration object.
     * @throws PopulateDataException is thrown if there are any 
     * @see com.telstra.olb.tegcbm.job.migration.populate.dao.PopulateDao#getCompanies(com.telstra.olb.tegcbm.job.migration.populate.model.MigrationType)
     */
    public List getCompanies(MigrationType migrationType) throws PopulateDataException {
        String sql = (String) sqls.get(migrationType.getName() + ".SQL");
        List companyList = null;
        if (sql != null) {
            try {
                companyList = (List) getJdbcTemplate().query(sql, new RowMapper() {
	                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
	                    String company = rs.getString(COL_CIDN);
	                    String ownCode = rs.getString(COL_OWN_CODE);
	                    String segment = rs.getString(COL_SEGMENT);
	                    return createCompanyMigration(company, ownCode, segment);
	                }
	            });
	            if (log.isDebugEnabled()) {
	                log.debug("list of companies for #" + companyList.size()
	                        + " for " + migrationType);
	            }
            } catch (DataAccessException e) {
                throw new PopulateDataException("unable to get companies for " + migrationType, e);
            }
        } else {
            log.warn("SQL for " + migrationType.getName()
                    + " not set. Set the property " + migrationType.getName()
                    + ".sql");
        }
        return companyList != null ? companyList : new ArrayList();
    }
    
    /**
     * Method returns the companies from OLB_COMPANY_MIGRATION table for which the status is not 1 and 4.
     */
    public List getCompanyList(MigrationType migrationType) throws PopulateDataException {
        String sql = (String) sqls.get(migrationType.getName() + ".SQL");
        List companyList = null;
        if (sql != null) {
            try {
                companyList = (List) getJdbcTemplate().query(sql, new RowMapper() {
	                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
	                    String company = rs.getString(COL_COMPANY_CODE);
	                    String ownCode = rs.getString(COL_OWN_CODE);
	                    String segment = rs.getString(COL_SEGMENT);
	                    return createCompanyMigration(company, ownCode, segment);
	                }
	            });
	            if (log.isDebugEnabled()) {
	                log.debug("list of companies for #" + companyList.size()
	                        + " for " + migrationType);
	            }
            } catch (DataAccessException e) {
                throw new PopulateDataException("unable to get companies for " + migrationType, e);
            }
        } else {
            log.warn("SQL for " + migrationType.getName()
                    + " not set. Set the property " + migrationType.getName()
                    + ".sql");
        }
        return companyList != null ? companyList : new ArrayList();
    }
    
    /**
     * creates a company migration object.
     * 
     * @param companyCode
     *            comapny id
     * @param ownCode
     *            own code
     * @param segment
     *            macrosegment
     * @return OLBCompanyMigration object.
     */
    private OLBCompanyMigration createCompanyMigration(String companyCode, String ownCode, String segment) {
        OLBCompanyMigration companyMigration = new OLBCompanyMigration();
        companyMigration.setCompanyCode(companyCode);
        companyMigration.setOwnCode(ownCode);
        companyMigration.setSegment(segment);
        companyMigration.setCreatedDate(new Date());
        companyMigration.setActivityStatus(ActivityStatus.STATUS_PENDING.getValue());
        return companyMigration;
    }

    
    /**
     * creates a company migration object.
     * 
     * @param companyCode
     *            comapny id
     * @param ownCode
     *            own code
     * @param segment
     *            macrosegment
     * @param activityStatus
     *            activity status
     * @return OLBCompanyMigration object.
     */
    
    private OLBCompanyMigration createCompanyMigration(String companyCode, String ownCode, String segment,int activityStatus) {
        OLBCompanyMigration companyMigration = new OLBCompanyMigration();
        companyMigration.setCompanyCode(companyCode);
        companyMigration.setOwnCode(ownCode);
        companyMigration.setSegment(segment);
        companyMigration.setCreatedDate(new Date());
        companyMigration.setActivityStatus(activityStatus);
        return companyMigration;
    }

    
    /**
     * Method returns all the CIDNs from the TBUnmanaged migration table
     * 
     * @return
     * @throws PopulateDataException
     */
    public List getTBUnmanagedCIDNs() throws PopulateDataException {
    	List companyList = null;
            try {
                companyList = (List) getJdbcTemplate().query(SELECT_SQL_CIDN, new RowMapper() {
	                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
	                    String company = rs.getString(COL_COMPANY_CODE);
	                    OLBTBUnmanagedCompanyMigration comp = createTBUnmanagedCompanyMigration(company);
	                    return createTBUnmanagedCompanyPreference(comp, null, null, null, 0);
	                }
	            });
	            if (log.isDebugEnabled()) {
	                log.debug("list of companies for #" + companyList.size()
	                        + " to be migrated");
	            }
            } catch (DataAccessException e) {
                throw new PopulateDataException("unable to get companies from TBUnmanaged Migration table");
            }
    	return companyList != null ? companyList : new ArrayList();
    }
    
    /** 
     * This method will be created as part of CR3134. This method is responsible for retrieving the TB Unmanaged companies. 
     * The list will consist of OLBTBUnmanageMigration objects.
     * 
     * @param migrationType migration type
     * @return a list of OLBCompanyMigration object.
     * @throws PopulateDataException is thrown if there are any 
     */
    public List getTBUnmanagedCompanies(MigrationType migrationType) throws PopulateDataException {
        String sql = (String) sqls.get(migrationType.getName() + ".SQL");
        List companyList = null;
        if (sql != null) {
            try {
                companyList = (List) getJdbcTemplate().query(sql, new RowMapper() {
	                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
	                    String company = rs.getString(COL_CIDN);
	                    String ownCode = rs.getString(COL_OWN_CODE);
	                    return createTBUnmanagedCompanyMigration(company, ownCode);
	                }
	            });
	            if (log.isDebugEnabled()) {
	                log.debug("list of companies for #" + companyList.size()
	                        + " for " + migrationType);
	            }
            } catch (DataAccessException e) {
                throw new PopulateDataException("unable to get companies for " + migrationType, e);
            }
        } else {
            log.warn("SQL for " + migrationType.getName()
                    + " not set. Set the property " + migrationType.getName()
                    + ".sql");
        }
        return companyList != null ? companyList : new ArrayList();
    }
    
    /** 
     * This method will retrieve the successfully migrated TBU companies from the database.
     * 
     * @return a list of OLBTBUnmanagedCompanyMigration object.
     * @throws PopulateDataException is thrown if there are any 
     */
    public List getMigratedTBUCompanies() throws PopulateDataException {
    	String sql = "SELECT * FROM EDX_DBA.olb_tbunmanaged_migration WHERE activity_status_id = 3 AND report = 0";
    	List accounts = null;
    	try{
    		accounts = getJdbcTemplate().query(sql, new RowMapper() {
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    String cidn = rs.getString(COL_COMPANY_CODE);
                    String ownCode = rs.getString(COL_OWN_CODE);
                    String segment = rs.getString(COL_SEGMENT);
                    String mSegment = rs.getString(COL_MACRO_SEGMENT);
                    int status = rs.getInt(COL_ACTIVITY_STATUS);
                    Date date = rs.getDate(COL_CREATED_DATE);
                    return new OLBTBUnmanagedCompanyMigration(cidn, ownCode, segment, mSegment, status, date, null);
                }
            });
    	} catch (DataAccessException e) {
    		throw new PopulateDataException("unable to get migrated tbu companies", e);
    	}
    	return accounts;
    }
    
    
    /**
     * Method updates the report field for Successfully migrated companies which are included in the report.
     * 
     * @param companies
     * @throws PopulateDataException
     */
    public void updateReported(final List companies) throws PopulateDataException {
    	String sql = "UPDATE OLB_TBUNMANAGED_MIGRATION SET report = 1 WHERE company_code = ? ";
    	try {    
	        getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter() {
	
				public void setValues(PreparedStatement updateStat, int index) throws SQLException {
					OLBTBUnmanagedCompanyMigration company = 
	                	(OLBTBUnmanagedCompanyMigration) companies.get(index);
	                updateStat.setString(1, company.getCompanyCode());
				}
	
				public int getBatchSize() {
					return companies.size();
				}
			});
	    } catch (DataAccessException e) {
	        throw new PopulateDataException("Unable to update preferences.", e);
	    }
    }
    
    
    /** 
     * Runs a cross schema query to obtain a list of tbunmanaged company preferences based on the segment and account
     * information from OLB and Company information from PDB.
     * @param migrationType migration type
     * @return a list of OLBCompanyMigration object.
     * @throws PopulateDataException is thrown if there are any 
     */
    public List getTBUnmanagedCompanyPreferences(final String cidn)throws PopulateDataException {
    	String sql = (String) sqls.get(MigrationType.SME.getName() + ".SQL");
        List companyList = null;
        if (sql != null && cidn != null) {
        	sql = sql + "'E"+cidn+"S%'";
        	log.debug("SQL statement for retrieving Preferences:  " +sql);
            try {
                companyList = (List) getJdbcTemplate().query(sql, new RowMapper() {
	                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
	                    //String cidn        = rs.getString(COL_CIDN);
	                    String userId      = rs.getString(COL_USER_ID);
	                    String attrKey     = rs.getString(COL_ATTR_KEY);
	                    String attrValue   = rs.getString(COL_ATTR_VALUE); 
	                    //String segment     = rs.getString(COL_SEGMENT);
	                    //String macroSeg    = rs.getString(COL_MACRO_SEGMENT);
	                    //Date createdDate   = rs.getDate(COL_CREATED_DATE);
	                    //String ownCode     = rs.getString(COL_OWN_CODE);
	                    //int status         = rs.getInt(COL_ACTIVITY_STATUS);
	                    int attrId         = rs.getInt(COL_ATTR_ID);
	                    OLBTBUnmanagedCompanyMigration company = createTBUnmanagedCompanyMigration(cidn);
	                    return createTBUnmanagedCompanyPreference(company, userId, attrKey, attrValue, attrId);
	                }
	            });
	            if (log.isDebugEnabled()) {
	                log.debug("list of companies for #" + companyList.size()
	                        + " to be migrated");
	            }
            } catch (DataAccessException e) {
                throw new PopulateDataException("unable to get preferences for " + cidn, e);
            }
        } 
        return companyList != null ? companyList : new ArrayList();
    }
    
    /** 
     * This method will retrieve the companies to be deleted from the OLB_ACCOUNT_COMPANY_MAP table. 
     * @param migrationType migration type
     * @return a list of OLBAccountCompanyMap object.
     * @throws PopulateDataException is thrown if there are any 
     */
    public List getAccountCompanies(MigrationType migrationType) throws PopulateDataException {
        String sql = (String) sqls.get(migrationType.getName() + ".SQL");
        List companyList = null;
        if (sql != null) {
            try {
                companyList = (List) getJdbcTemplate().query(sql, new RowMapper() {
	                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
	                    String company = rs.getString(COL_COMPANY_ID);
	                    String accountNumber = rs.getString(COL_ACCOUNT_NUMBER);
	                    int accTypeId = rs.getInt(COL_ACCOUNT_TYPE_ID);
	                    int relationId = rs.getInt(COL_RELATION_ID);
	                    return new OLBAccountCompanyMap(accountNumber, company, accTypeId, relationId);
	                }
	            });
	            if (log.isDebugEnabled()) {
	                log.debug("list of companies for #" + companyList.size()
	                        + " for which migration of preferences is done");
	            }
            } catch (DataAccessException e) {
                throw new PopulateDataException("unable to get companies from OLB_ACCOUNT_COMPANY_MAP", e);
            }
        } else {
            log.warn("SQL for " + migrationType.getName()
                    + " not set. Set the property " + migrationType.getName()
                    + ".sql");
        }
        return companyList != null ? companyList : new ArrayList();
    }
    
    
    /**
     * Method returns the list of accounts for a company code
     * 
     * @param cidn
     * @return
     * @throws PopulateDataException
     */
    public List getAccountNumbersForCompany(String cidn) throws PopulateDataException {
    	String sql = "SELECT ACCOUNT_NUMBER FROM OLB_ACCOUNT_COMPANY_MAP WHERE COMPANY_ID ='"+cidn+"'";
    	List accounts = null;
    	try{
    		accounts = getJdbcTemplate().query(sql, new RowMapper() {
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    String accNum = rs.getString(COL_ACCT_NUMBER);
                    return accNum;
                }
            });
    	} catch (DataAccessException e) {
    		throw new PopulateDataException("unable to get accounts for  companyCode:"+cidn, e);
    	}
    	return accounts;
    }
    
    /**
     * This method will take a back-up of the companies to be deleted in the OLB_ACCOUNT_COMPANY_MAP_BKUP table. 
     * 
     * @param accountCompanies
     * @throws PopulateDataException
     */
    public void backupAccountCompany(final List accountCompanies) throws PopulateDataException {
        if (log.isDebugEnabled()) { log.debug("Inserting account company map in the backup table");}
	    try {    
	        getJdbcTemplate().batchUpdate(INSERT_SQL_ACM_BKUP, new BatchPreparedStatementSetter() {
	
				public void setValues(PreparedStatement insertStat, int index) throws SQLException {
					OLBAccountCompanyMap accountCompany = 
	                	(OLBAccountCompanyMap) accountCompanies.get(index);
	                insertStat.setString(1, accountCompany.getAccountNumber());
	                insertStat.setString(2, accountCompany.getCompanyCode());
	                insertStat.setInt(3, accountCompany.getAccountTypeId());
	                insertStat.setInt(4, accountCompany.getRelationId());
				}
	
				public int getBatchSize() {
					return accountCompanies.size();
				}
			});
	    } catch (DataAccessException e) {
	        throw new PopulateDataException("Unable to backup account company record.", e);
	    }
    }
    
    /**
     * This method will delete the successfully migrated TBU companies from the OLB_ACCOUNT_COMPANY_MAP table. 
     * 
     * @param accountCompanies 
     * @throws PopulateDataException thrown if there is problem working with EDX CBM datastore
     */
    public void deleteAccountCompany(final List accountCompanies) throws PopulateDataException {
        if (log.isDebugEnabled()) { log.debug("Removing account company record");}
	    try {
	    	getJdbcTemplate().batchUpdate(DELETE_SQL_ACM, new BatchPreparedStatementSetter() {
	
				public void setValues(PreparedStatement deleteStat, int index) throws SQLException {
					OLBAccountCompanyMap accountCompany = 
	                	(OLBAccountCompanyMap) accountCompanies.get(index);
	                deleteStat.setString(1, accountCompany.getAccountNumber());
	                deleteStat.setString(2, accountCompany.getCompanyCode());
				}
	
				public int getBatchSize() {
					return accountCompanies.size();
				}
			});
	    } catch (DataAccessException e) {
	        throw new PopulateDataException("Unable to Delete account company record.", e);
	    }
    }
    
    
    /**
     * This method is responsible for inserting the user preferences in the PDB table PDB_DBA.PDB_USER_ACCOUNT_PREF_VALUES_T.
     * 
     * @param userAccountPreferences
     * @throws PopulateDataException
     */
    public void insertUserAccountPreference(final List userAccountPreferences) throws PopulateDataException {
        if (log.isDebugEnabled()) { log.debug("Inserting User preferences in OLBPDBUserAccountPreference");}
        deleteUserAccountPreference(userAccountPreferences);
	    try {    
	        getJdbcTemplate().batchUpdate(INSERT_SQL_PDB_USER_PREFERENCES, new BatchPreparedStatementSetter() {
	
				public void setValues(PreparedStatement insertStat, int index) throws SQLException {
					PDBUserAccountPreference userAccountPreference = 
	                	(PDBUserAccountPreference) userAccountPreferences.get(index);
	                insertStat.setString(1, userAccountPreference.getUserId());
	                insertStat.setString(2, userAccountPreference.getAccountNumber());
	                insertStat.setString(3, BL_APP_ID);
	                insertStat.setString(4, userAccountPreference.getAttributeId());
	                insertStat.setString(5, userAccountPreference.getValue());
				}
	
				public int getBatchSize() {
					return userAccountPreferences.size();
				}
			});
	    } catch (DataAccessException e) {
	        throw new PopulateDataException("Unable to update preferences.", e);
	    }
    }
    
    /**
     * This method is responsible for inserting the user preferences in the PDB table PDB_DBA.PDB_USER_ACCOUNT_PREF_VALUES_T.
     * 
     * @param userAccountPreferences
     * @throws PopulateDataException
     */
    public void deleteUserAccountPreference(final List userAccountPreferences) throws PopulateDataException {
        if (log.isDebugEnabled()) { log.debug("Deleting matching User preferences from OLBPDBUserAccountPreference");}
	    try {    
	        getJdbcTemplate().batchUpdate(DELETE_SQL_PDB_USER_PREFERENCES, new BatchPreparedStatementSetter() {
	
				public void setValues(PreparedStatement deleteStat, int index) throws SQLException {
					PDBUserAccountPreference userAccountPreference = 
	                	(PDBUserAccountPreference) userAccountPreferences.get(index);
	                deleteStat.setString(1, userAccountPreference.getUserId());
	                deleteStat.setString(2, userAccountPreference.getAccountNumber());
	                deleteStat.setString(3, BL_APP_ID);
	                deleteStat.setString(4, userAccountPreference.getAttributeId());
				}
	
				public int getBatchSize() {
					return userAccountPreferences.size();
				}
			});
	    } catch (DataAccessException e) {
	        throw new PopulateDataException("Unable to delete preferences.", e);
	    }
    }
    
    /**
     * This method is responsible for inserting the CBM preferences records in the back-up table called EDX_DBA.EDX_BSL_USER_PROF_ATTRIBS_BKUP.
     * 
     * @param userAccountPreferences
     * @throws PopulateDataException
     */
    public void backupUserAccountPreference(final List userAccountPreferences) throws PopulateDataException {
        if (log.isDebugEnabled()) { log.debug("Inserting User preferences in OLBPDBUserAccountPreference");}
	    try {    
	        getJdbcTemplate().batchUpdate(INSERT_SQL_USER_PROFILE_BKUP, new BatchPreparedStatementSetter() {
	
				public void setValues(PreparedStatement insertStat, int index) throws SQLException {
					CBMUserPreferenceBackup attribute = 
	                	(CBMUserPreferenceBackup) userAccountPreferences.get(index);
	                insertStat.setInt(1, attribute.getAttrId());
	                insertStat.setString(2, attribute.getAttrVal());
	                insertStat.setString(3, attribute.getAttrKey());
	                insertStat.setString(4, attribute.getCompanyCode());
	                Date createdDate = attribute.getCreatedDate();
	                java.sql.Date dateCreated = new java.sql.Date(createdDate.getTime());
	                insertStat.setDate(5, dateCreated);
				}
	
				public int getBatchSize() {
					return userAccountPreferences.size();
				}
			});
	    } catch (DataAccessException e) {
	        throw new PopulateDataException("Unable to update preferences.", e);
	    }
    }
    
    /**
     * This method is responsible for deleting the CBM preferences records from the EDX_DBA.EDX_BSL_USER_PROF_ATTRIBS table.
     * 
     * @param userAccountPreferences list of user notification preferences
     * @throws PopulateDataException thrown if there is problem working with EDX CBM datastore
     */
    public void deleteUserAccountPreferences(final List userAccountPreferences) throws PopulateDataException {
        if (log.isDebugEnabled()) { log.debug("Removing user preferences");}
	    try {
	    	getJdbcTemplate().batchUpdate(DELETE_SQL_USER_PROFILE, new BatchPreparedStatementSetter() {
	
				public void setValues(PreparedStatement deleteStat, int index) throws SQLException {
					CBMUserPreferenceBackup attributes = 
	                	(CBMUserPreferenceBackup) userAccountPreferences.get(index);
	                deleteStat.setInt(1, attributes.getAttrId());
	                deleteStat.setString(2, attributes.getAttrKey());
				}
	
				public int getBatchSize() {
					return userAccountPreferences.size();
				}
			});
	    } catch (DataAccessException e) {
	        throw new PopulateDataException("Unable to Delete preferences.", e);
	    }
    }
    
    /**
     * creates a company migration object.
     * 
     * @param companyCode
     * @param ownCode
     * @param segment
     * @param macroSegment
     * @param created
     * @param status
     * @return
     */
    private OLBTBUnmanagedCompanyMigration createTBUnmanagedCompanyMigration(String companyCode) {
    	OLBTBUnmanagedCompanyMigration companyMigration = new OLBTBUnmanagedCompanyMigration();
        companyMigration.setCompanyCode(companyCode);
//        companyMigration.setOwnCode(ownCode);
//        companyMigration.setSegment(segment);
//        companyMigration.setMacroSegment(macroSegment);
//        companyMigration.setCreatedDate(created);
//        companyMigration.setActivityStatus(status);
        return companyMigration;
    }
    
    /**
     * creates a company migration object.
     * 
     * @param companyCode
     *            comapny id
     * @param ownCode
     *            own code
     * @param segment
     *            macrosegment
     * @return OLBCompanyMigration object.
     */
    private OLBTBUnmanagedCompanyMigration createTBUnmanagedCompanyMigration(String companyCode, String ownCode) {
    	OLBTBUnmanagedCompanyMigration companyMigration = new OLBTBUnmanagedCompanyMigration();
        companyMigration.setCompanyCode(companyCode);
        //companyMigration.setOwnCode(ownCode);
        companyMigration.setSegment(TBU_SEGMENT);
        companyMigration.setMacroSegment(TBU_MACRO_SEGMENT);
        companyMigration.setCreatedDate(new Date());
        companyMigration.setActivityStatus(ActivityStatus.STATUS_PENDING.getValue());
        return companyMigration;
    }
    
    /**
     * creates a tb unmanaged preference object.
     * 
     * @param companyCode
     * @param userId
     * @param attrKey
     * @param attrValue
     * @return
     */
    private OLBTBUnmanagedCompanyPreference createTBUnmanagedCompanyPreference(OLBTBUnmanagedCompanyMigration company, String userId, String attrKey, String attrValue, int attrId) {
    	OLBTBUnmanagedCompanyPreference companyPreference = new OLBTBUnmanagedCompanyPreference();
    	companyPreference.setCompany(company);
    	companyPreference.setUserId(userId);
    	companyPreference.setPreference(attrKey);
    	companyPreference.setPreferenceValue(attrValue);
    	companyPreference.setAttrId(attrId);
    	return companyPreference;
    }
    
    
    public Map getCompanyByCidn(final String cidn) throws PopulateDataException{
    	List companyList=null;
    	Map companyCidnMap=new HashMap();    	
    	OLBCompanyMigration olbCompanyMigration = new OLBCompanyMigration();
    	if(cidn!=null && cidn !=""){      		
    	String sql="select * from OLB_COMPANY_MIGRATION WHERE COMPANY_CODE IN ("+cidn+") order by COMPANY_CODE";    	
         
              try {
              	companyList = (List) getJdbcTemplate().query(sql, new RowMapper() {
  	                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
  	                	String company = rs.getString(COL_COMPANY_CODE);
  	                    String ownCode = rs.getString(COL_OWN_CODE);
  	                    String segment = rs.getString(COL_SEGMENT);
  	                    int activityStatus=rs.getInt(COL_ACTIVITY_STATUS);
  	                    return createCompanyMigration(company, ownCode, segment,activityStatus);
  	                    
  	                }
  	            });
  	            if (log.isDebugEnabled()) {
  	                log.debug("list of companies for #" + companyList.size()
  	                        + " for " + cidn);}
  	                if(!companyList.equals(null)){
  	                	Iterator companyListIterator =companyList.iterator();
  	                	while(companyListIterator.hasNext()){
  	                		 olbCompanyMigration=(OLBCompanyMigration)companyListIterator.next();
  	                		companyCidnMap.put(olbCompanyMigration.getCompanyCode(),olbCompanyMigration);
  	                	 	                		
  	                		
  	                	}
  	                	
  	                }
  	              if (log.isDebugEnabled()) {
  	                log.debug("list of companies for #" + companyCidnMap.size());}
  	                
  	              
  	           
              } catch (DataAccessException e) {
                  throw new PopulateDataException("Invalid CIdn provided" + cidn, e);
              }
         
    	}
          return companyCidnMap != null ? companyCidnMap : new HashMap();
    	
    	
    	
    	
    	
    }
    
    /**
     * @return Returns the sqls.
     */
    public Map getSqls() {
        return sqls;
    }
    /**
     * @param sqls The sqls to set.
     */
    public void setSqls(Map sqls) {
        this.sqls = sqls;
    }
}
