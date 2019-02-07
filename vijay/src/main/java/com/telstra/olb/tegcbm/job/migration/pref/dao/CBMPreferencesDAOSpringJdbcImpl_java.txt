package com.telstra.olb.tegcbm.job.migration.pref.dao;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import com.telstra.olb.tegcbm.job.migration.pref.model.UserNotification;

/**
 * Spring implementation for <code>CBMPreferencesDAO</code> interface.
 * 
 * @author d274681
 */
public class CBMPreferencesDAOSpringJdbcImpl extends JdbcDaoSupport implements
		CBMPreferencesDAO {
    private static Log log = LogFactory.getLog(CBMPreferencesDAOSpringJdbcImpl.class);
	
    // Preference Values - Constants for user preferences that match the values
    // in the database
    
    private static final String PREF_BAN = "BAN";
    private static final String PREF_BPR = "BPR";

    private static final String CBM_BAN_PREFERENCE      = "newStmtAvailableEmail";
    private static final String CBM_BPR_PREFERENCE      = "paymentDueXDaysEmail";
    private static final String CBM_BPR_DAYS_PREFERENCE = "numDaysBeforeDueDate";
    private static final String DEFAULT_NUMBER_OF_DAYS 	= "2"; 
    private static final String TRUE = "true";

    private static final String INSERT_SQL = "INSERT INTO EDX_BSL_USER_PROF_ATTRIBS (ATTRID, ATTRVALUE, ATTRKEY) VALUES (?,?,?)";
    private static final String INSERT_SQL_PAYMENT_DUE = 
    	"INSERT INTO PAYMENT_DUE_NOTIFICATION (PAYER_ID, NUM_DAYS_BEFORE_DUE_DATE, LAST_PROCESSED_TIME) VALUES (?,?,?)";

    private static final String SELECT_SQL = 
        "SELECT u.USERPROFILEID FROM EDX_BSL_UMF_USER u, EDX_BSL_AUTH_SECPROFILE s " +
        "where u.SECURITYPROFILEID = s.PROFILEID and s.USERID = ?";

	/**
	 * Reads the user notification settings and stores them in the CBM database.
	 * 
	 * @param userNotification User's notification settings.
     * @throws CBMPreferencesDAOException thrown if there is problem working with CBM data store
	 */
	public void updateUserPreferences(UserNotification userNotification) throws CBMPreferencesDAOException {
	    // Retrieve the user profile id for user
    	String userProfileId = (String)getJdbcTemplate().queryForObject(SELECT_SQL, 
    		new Object[] { userNotification.getEcsId() }, String.class);

        if (userNotification.getNotificationTypes().contains(PREF_BAN)) {
            // store the BAN preference to database
        	insertPreference(userProfileId, TRUE, CBM_BAN_PREFERENCE);
        }
        if (userNotification.getNotificationTypes().contains(PREF_BPR)) {
            // store the BPR preference to database
        	insertPreference(userProfileId, TRUE, CBM_BPR_PREFERENCE);
            // store the BPR days to database
        	insertPreference(userProfileId, DEFAULT_NUMBER_OF_DAYS, CBM_BPR_DAYS_PREFERENCE);
        	insertPreferenceIntoPaymentDueNotif(userNotification.getEcsId(), DEFAULT_NUMBER_OF_DAYS);
        }
	}
	
	/**
	 * Stores the preference to data store.
	 * 
	 * @param userId User id
	 * @param preferenceValue preference setting
	 * @param preferenceKey preference name
     * @throws CBMPreferencesDAOException thrown if there is problem working with CBM data store
	 */
	private void insertPreference(final String userId, final String preferenceValue, final String preferenceKey) 
		throws CBMPreferencesDAOException {

        if (log.isDebugEnabled()) { log.debug("Inserting preference:" + preferenceKey + " with value:" + preferenceValue + " for userId:" + userId);}

		try {
			getJdbcTemplate().update(INSERT_SQL, new PreparedStatementSetter() {

				public void setValues(PreparedStatement insertStat) throws SQLException {
			        insertStat.setString(1, userId);
			        insertStat.setString(2, preferenceValue);
			        insertStat.setString(3, preferenceKey);
				}
				
			});
		} catch(DataAccessException sqle) {
			throw new CBMPreferencesDAOException("Problem inserting preference for userid:" + userId, sqle);
		}
	}
	
	/**
	 * Stores the preference to data store.
	 * 
	 * @param userId User id
	 * @param numOfDays the default number of days before the notification is sent
     * @throws CBMPreferencesDAOException thrown if there is problem working with CBM data store
	 */
	private void insertPreferenceIntoPaymentDueNotif(final String userId, final String numOfDays) 
		throws CBMPreferencesDAOException {

        if (log.isDebugEnabled()) { log.debug("Inserting into PAYMENT_DUE_NOTIFICATION for ECSID: " + userId);}

		try {
			getJdbcTemplate().update(INSERT_SQL_PAYMENT_DUE, new PreparedStatementSetter() {

				public void setValues(PreparedStatement insertStat) throws SQLException {
			        insertStat.setString(1, userId);
			        insertStat.setString(2, numOfDays);
			        insertStat.setDate(3, new Date(System.currentTimeMillis()));					
				}
				
			});
		} catch(DataAccessException sqle) {
			throw new CBMPreferencesDAOException("Problem inserting preference into PAYMENT_DUE_NOTIFICATION for ECSID:" + userId, sqle);
		}
	}
}
