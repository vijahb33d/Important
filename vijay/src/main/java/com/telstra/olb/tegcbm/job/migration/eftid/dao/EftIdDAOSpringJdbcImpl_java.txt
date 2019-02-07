package com.telstra.olb.tegcbm.job.migration.eftid.dao;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.support.JdbcDaoSupport;


/**
 * @author daniel.fajerman
 *
 */
public class EftIdDAOSpringJdbcImpl extends JdbcDaoSupport implements EftIdDAO {
	
    private static final String UPDATE_EFTID_SQL = "update OLB_EFT_ID_CIDN_T set APP_ID = ? where CIDN = ?";
    
    private static Log log = LogFactory.getLog(EftIdDAOSpringJdbcImpl.class);
    
    private int applicationId;
    
    /**
     * @return Returns the applicationId.
     */
    public int getApplicationId() {
        return applicationId;
    }
    /**
     * @param applicationId The applicationId to set.
     */
    public void setApplicationId(int applicationId) {
        this.applicationId = applicationId;
    }
    /**
     * Updates the eft id for a company to corporate application.
     * 
     * @param companyCode company
     * @throws EftIdDAOException is thrown if the update cannot be performed.
     * @see com.telstra.olb.tegcbm.job.migration.eftid.dao.EftIdDAO#updateApplicationId(java.lang.String)
     */
    public void updateApplicationId(final String companyCode) throws EftIdDAOException {
		try {
            if (log.isDebugEnabled()) { log.debug("Updating application id to " + getApplicationId() + " for company code:" + companyCode);}

            getJdbcTemplate().update(UPDATE_EFTID_SQL, new PreparedStatementSetter() {

				public void setValues(PreparedStatement updateStat) throws SQLException {
				    updateStat.setInt(1, getApplicationId());
				    updateStat.setString(2, companyCode);
				}
				
			});
		} catch(DataAccessException sqle) {
			throw new EftIdDAOException("Problem updating eftid in OLB database for company:" + companyCode, sqle);
		}
    }
}
