package com.telstra.olb.tegcbm.job.migration.pref.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import com.telstra.olb.tegcbm.job.migration.pref.model.UserAccountPreference;

/**
 * Spring jdbc implementation for <code>PDBPreferencesDAO</code> interface.
 * 
 * @author d274681
 */
public class PDBPreferencesDAOSpringJdbcImpl extends JdbcDaoSupport implements PDBPreferencesDAO {
    private static Log log = LogFactory.getLog(PDBPreferencesDAOSpringJdbcImpl.class);

    private static final String USER_ACCOUNTS_PREF_SQL = 
        "select pref.ecsid, pref.account_number, pref.application_id, pref.attribute_id, pref.value"
        +" from PDB_USER_ACCOUNT_PREF_VALUES_T pref where pref.attribute_id !='SPP' AND pref.ecsid in ( select user_val.ecsid from"
        +" PDB_ECS_USER_VALUES_T user_val where user_val.ecsid like ? )";
    private static final String INSERT_SQL = "INSERT INTO PDB_USER_ACCT_PREF_VAL_BKUP_T "
            + "(ECSID, ACCOUNT_NUMBER, APPLICATION_ID, ATTRIBUTE_ID, VALUE, CREATED_DATE, CIDN) VALUES (?,?,?,?,?,?,?)";

    private static final String UPDATE_SQL = "UPDATE PDB_USER_ACCOUNT_PREF_VALUES_T SET VALUE = ? WHERE ECSID = ? AND ACCOUNT_NUMBER = ? AND ATTRIBUTE_ID = 'SPP'";

    private static final String DELETE_SQL = "DELETE PDB_USER_ACCOUNT_PREF_VALUES_T WHERE ECSID = ? AND ATTRIBUTE_ID IN ('BPR','LPR','BAN')";


    /**
     * Retrieves the User Account preferences from the PDB datastore.
     * 
	 * @param cidn Company cidn
	 * @return list of user notification preferences
     * @throws PDBPreferencesDAOException thrown if there is problem working with PDB datastore
     */
    public List getUserAccountPreferencesByCIDN(String cidn) throws PDBPreferencesDAOException {
        if (log.isDebugEnabled()) { log.debug("Retrieving preferences for cidn:" + cidn);}
        try {
            return (List) getJdbcTemplate().query(USER_ACCOUNTS_PREF_SQL, new Object[] { "E" + cidn + "%" }, new RowMapper() {
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return new UserAccountPreference(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5));
                }
            });
        } catch (DataAccessException e) {
            throw new PDBPreferencesDAOException("unable to retrieve accounts for User Id: " + cidn, e);
        }
    }
    /**
     * Creates the backup copy of the user account preferences data in the new backup table.
     * 
     * @param cidn Company cidn
     * @param userAccountPreferences list of user notification preferences
     * @throws PDBPreferencesDAOException thrown if there is problem working with PDB datastore
     */
    public void backupUserAccountPreference(final String cidn, final List userAccountPreferences) throws PDBPreferencesDAOException {
        if (log.isDebugEnabled()) { log.debug("Backing up preferences for cidn:" + cidn);}
	    try {    
	        getJdbcTemplate().batchUpdate(INSERT_SQL, new BatchPreparedStatementSetter() {
	
				public void setValues(PreparedStatement insertStat, int index) throws SQLException {
	                UserAccountPreference userAccountPreference = 
	                	(UserAccountPreference) userAccountPreferences.get(index);
	                insertStat.setString(1, userAccountPreference.getUserId());
	                insertStat.setString(2, userAccountPreference.getAccountNumber());
	                insertStat.setString(3, userAccountPreference.getAppId());
	                insertStat.setString(4, userAccountPreference.getAttributeId());
	                insertStat.setString(5, userAccountPreference.getValue());
	                insertStat.setDate(6, new java.sql.Date(Calendar.getInstance().getTimeInMillis()));
	                insertStat.setString(7, cidn);
				}
	
				public int getBatchSize() {
					// TODO Auto-generated method stub
					return userAccountPreferences.size();
				}
			});
	    } catch (DataAccessException e) {
	        throw new PDBPreferencesDAOException("Unable to update preferences.", e);
	    }
    }
    /**
     * Updates the application id for the user.
     * 
     * @param sppPreferences list of user notifictaion preferences
     * @throws PDBPreferencesDAOException thrown if there is problem working with PDB datastore
     */
    public void processSPP(final List sppPreferences) throws PDBPreferencesDAOException {
        if (log.isDebugEnabled()) { log.debug("Updating application ids");}
        try {
            getJdbcTemplate().batchUpdate(UPDATE_SQL, new BatchPreparedStatementSetter() {

				public void setValues(PreparedStatement updateStat, int index) throws SQLException {
	                UserAccountPreference userAccountPreference = 
	                	(UserAccountPreference) sppPreferences.get(index);
	                updateStat.setString(1, "email");
	                updateStat.setString(2, userAccountPreference.getUserId());
	                updateStat.setString(3, userAccountPreference.getAccountNumber());
				}

				public int getBatchSize() {
					// TODO Auto-generated method stub
					return sppPreferences.size();
				}
			});
        } catch (DataAccessException e) {
            throw new PDBPreferencesDAOException("Unable to update preferences.", e);
        }
    }
    /**
     * Removes the user account preferences from the PDB database.
     * 
     * @param userAccountPreferences list of user notification preferences
     * @throws PDBPreferencesDAOException thrown if there is problem working with PDB datastore
     */
    public void deleteUserAccountPreferences(final List userAccountPreferences) throws PDBPreferencesDAOException {
        if (log.isDebugEnabled()) { log.debug("Removing user preferences");}
	    try {
	    	getJdbcTemplate().batchUpdate(DELETE_SQL, new BatchPreparedStatementSetter() {
	
				public void setValues(PreparedStatement deleteStat, int index) throws SQLException {
	                UserAccountPreference userAccountPreference = 
	                	(UserAccountPreference) userAccountPreferences.get(index);
	                deleteStat.setString(1, userAccountPreference.getUserId());
				}
	
				public int getBatchSize() {
					// TODO Auto-generated method stub
					return userAccountPreferences.size();
				}
			});
	    } catch (DataAccessException e) {
	        throw new PDBPreferencesDAOException("Unable to update preferences.", e);
	    }
    }
}
