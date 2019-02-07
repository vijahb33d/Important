
package com.telstra.olb.tegcbm.job.migration.pref.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import com.telstra.olb.tegcbm.job.migration.pref.model.UserAccountPreference;

/**
 * Concrete spring jdbc implemenation for <code>PDBPreferencesRollbackDAO</code> interface.
 * 
 * @author d274681
 */
public class PDBPreferencesRollbackDAOSpringJdbcImpl extends JdbcDaoSupport implements PDBPreferencesRollbackDAO {
	
    private static Log log = LogFactory.getLog(PDBPreferencesRollbackDAOSpringJdbcImpl.class);

    private static final String PREFERENCE_SPP = "SPP";

    private static final String USER_ACCNTS_BKUP_PREF_SQL = "select pref.ecsid, pref.account_number, pref.application_id, pref.attribute_id, "
            + "pref.value from PDB_USER_ACCT_PREF_VAL_BKUP_T pref where pref.cidn = ? order by pref.cidn";

    private static final String INSERT_SQL = "INSERT INTO PDB_USER_ACCOUNT_PREF_VALUES_T "
            + "(ECSID, ACCOUNT_NUMBER, APPLICATION_ID, ATTRIBUTE_ID, VALUE) VALUES (?,?,?,?,?)";

    private static final String UPDATE_SQL = "UPDATE PDB_USER_ACCOUNT_PREF_VALUES_T SET APPLICATION_ID = ? WHERE ECSID = ?";

    private static final String DELETE_SQL = "DELETE PDB_USER_ACCT_PREF_VAL_BKUP_T WHERE CIDN = ?";

    /**
     * Retrieves the User Account preferences from the PDB datastore backup table.
     * 
	 * @param cidn Company Id
	 * @return List of UserAccountPreferences
     * @throws PDBPreferencesRollbackDAOException upon pdb datastore related exception
     */
    public List getBackedUpUserAccountPreferencesByCIDN(String cidn) throws PDBPreferencesRollbackDAOException {
        if (log.isDebugEnabled()) { log.debug("Retrieving preferences from backup table for cidn:" + cidn);}
        try {
            return (List) getJdbcTemplate().query(USER_ACCNTS_BKUP_PREF_SQL, new Object[] { cidn }, new RowMapper() {
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    return new UserAccountPreference(rs.getString(1), rs.getString(2), rs.getString(3), rs.getString(4), rs.getString(5));
                }
            });
        } catch (DataAccessException e) {
            throw new PDBPreferencesRollbackDAOException("unable to get backed up preferences for cidn: " + cidn, e);
        }
    }
    /**
     * Removes the user account preferences from the PDB database.
     * 
     * @param userAccountPreferences list of user account preferences
     * @throws PDBPreferencesRollbackDAOException upon pdb datastore related exception
     */
    public void restoreUserPreferencesToPDB(List userAccountPreferences) throws PDBPreferencesRollbackDAOException {
        if (log.isDebugEnabled()) { log.debug("Restoring user preferences.");}
        try {
            for (Iterator it = userAccountPreferences.iterator(); it.hasNext();) {
                UserAccountPreference userAccountPreference = (UserAccountPreference) it.next();
                if (!userAccountPreference.getAttributeId().equals(PREFERENCE_SPP)) {
                    insertUserPreferenceToPDB(userAccountPreference);
                } else {
                    updateUserPreferenceToPDB(userAccountPreference);
                }
            }
        } catch (PDBPreferencesRollbackDAOException sqle) {
            throw new PDBPreferencesRollbackDAOException("Unable to restore backup preferences", sqle);
        }
    }
    /**
     * Restores the user notification preference application id.
     * 
     * @param userAccountPreference notification preference
	 * @throws PDBPreferencesRollbackDAOException upon sql datastore related exception
     */
    private void updateUserPreferenceToPDB(
    	final UserAccountPreference userAccountPreference) throws PDBPreferencesRollbackDAOException {

        if (log.isDebugEnabled()) { log.debug("Set appid:" + userAccountPreference.getAppId() + " for userid:" + userAccountPreference.getUserId());}

		try {
			getJdbcTemplate().update(UPDATE_SQL, new PreparedStatementSetter() {

				public void setValues(PreparedStatement updateStat) throws SQLException {
			        updateStat.setString(1, userAccountPreference.getAppId());
			        updateStat.setString(2, userAccountPreference.getUserId());
				}
				
			});
		} catch(DataAccessException sqle) {
            throw new PDBPreferencesRollbackDAOException("Unable to update application id for spp preference", sqle);
		}
    }
    /**
     * Restores the notification preference from backup table to main notification table.
     * 
     * @param userAccountPreference notification preference
	 * @throws PDBPreferencesRollbackDAOException upon sql datastore related exception
     */
    private void insertUserPreferenceToPDB(final UserAccountPreference userAccountPreference) throws PDBPreferencesRollbackDAOException {
        if (log.isDebugEnabled()) { log.debug("Inserting preference for userid:" + userAccountPreference.getUserId());}
    	try {
			getJdbcTemplate().update(INSERT_SQL, new PreparedStatementSetter() {
	
				public void setValues(PreparedStatement insertStat) throws SQLException {
			        insertStat.setString(1, userAccountPreference.getUserId());
			        insertStat.setString(2, userAccountPreference.getAccountNumber());
			        insertStat.setString(3, userAccountPreference.getAppId());
			        insertStat.setString(4, userAccountPreference.getAttributeId());
			        insertStat.setString(5, userAccountPreference.getValue());
				}
				
			});
		} catch(DataAccessException sqle) {
            throw new PDBPreferencesRollbackDAOException("Unable to insert preference for userid:" + userAccountPreference.getUserId(), sqle);
		}
    }
    /**
     * Creates the backup copy of the user account preferences data in the new backup table.
     * 
     * @param cidn Company Id
     * @throws PDBPreferencesRollbackDAOException upon pdb datastore related exception
     */
    public void removeBackedUpUserAccountPreference(final String cidn) throws PDBPreferencesRollbackDAOException {
        if (log.isDebugEnabled()) { log.debug("Remove records from backup table for cidn:" + cidn);}
    	try {
			getJdbcTemplate().update(DELETE_SQL, new PreparedStatementSetter() {
	
				public void setValues(PreparedStatement deleteStat) throws SQLException {
		            deleteStat.setString(1, cidn);
				}
				
			});
		} catch(DataAccessException sqle) {
            throw new PDBPreferencesRollbackDAOException("Unable to delete from backup table for cidn:" + cidn, sqle);
		}
    }
}
