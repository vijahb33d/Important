package com.telstra.olb.tegcbm.job.migration.pref.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

/**
 * Spring implementation for <code>CBMPreferencesDAO</code> interface.
 * 
 * @author d274681
 */
public class CBMPreferencesRollbackDAOSpringJdbcImpl extends JdbcDaoSupport implements
		CBMPreferencesRollbackDAO {

	private static Log log = LogFactory.getLog(CBMPreferencesRollbackDAOSpringJdbcImpl.class);

    private static final String DELETE_SQL = 
        "DELETE EDX_BSL_USER_PROF_ATTRIBS WHERE ATTRID IN (SELECT u.USERPROFILEID " +
        "FROM EDX_BSL_UMF_USER u WHERE u.COMPANYID IN (SELECT ID FROM EDX_BSL_CMF_COMPANY " +
        "WHERE PROFILEID IN (SELECT ID FROM EDX_BSL_CMF_COMPANYPROFILE WHERE NAME = ?)))";

    private static final String DELETE_PDN_SQL = "DELETE EDX_DBA.PAYMENT_DUE_NOTIFICATION pdn WHERE pdn.PAYER_ID LIKE ?";
    
	/**
	 * Removes user notification preferences for all the users for a company from the CBM data store.
	 * 
	 * @param cidn Company CIDN.
     * @throws CBMPreferencesRollbackDAOException thrown if there is problem working with CBM data store
	 */
	public void removeUserPreferences(final String cidn) throws CBMPreferencesRollbackDAOException {
        if (log.isDebugEnabled()) { log.debug("Remove records from cbm user preferences table for cidn:" + cidn);}
    	try {
			getJdbcTemplate().update(DELETE_SQL, new PreparedStatementSetter() {
	
				public void setValues(PreparedStatement deleteStat) throws SQLException {
		            deleteStat.setString(1, cidn);
				}
				
			});
			
			getJdbcTemplate().update(DELETE_PDN_SQL,  new PreparedStatementSetter(){
				public void setValues(PreparedStatement deleteStat) throws SQLException {
		            deleteStat.setString(1, "%"+cidn+"%");
				}
			});
		} catch(DataAccessException sqle) {
            throw new CBMPreferencesRollbackDAOException("Unable to delete notifications from user attributes table for cidn:" + cidn, sqle);
		}
	}
}
