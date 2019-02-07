package com.telstra.olb.tegcbm.job.migration.profiles.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import com.telstra.olb.tegcbm.job.migration.profiles.model.PDBAccountVO;

/**
 *Spring JDBC implementation of PDBProfilesDAO interface.
 * 
 */
public class PDBProfilesDAOSpringJdbcImpl extends JdbcDaoSupport implements PDBProfilesDAO {
    private static Log log = LogFactory.getLog(PDBProfilesDAOSpringJdbcImpl.class);

    private static final String COMPANY_ACCOUNTS_SQL = 
        "select account_number, attribute_id, value from PDB_AGREED_ACCNT_VALUES_T "
        + "where cidn = ?  order by account_number";
    
    private static final String CACHE_TIMEOUT_SQL = 
        "select value from PDB_CONTROL_T where attribute_id = 'customer_cache_time_out'";
    
    private static final String UPDATE_CACHE_TIMEOUT_SQL = 
        "update PDB_CONTROL_T set value = ? where attribute_id = 'customer_cache_time_out'";

    /**
     * retrieves tha list of accounts for the company.
     * 
     * @param cidn
     *            company
     * @return a list of account vo objects
     * @throws PDBProfilesDAOException
     *             is thrown if the account inofrmation cannot be retrieved.
     * @see com.telstra.olb.tegcbm.job.migration.profiles.dao.PDBProfilesDAO#getAccountsForCompany(java.lang.String)
     */
    public Collection getAccountsForCompany(String cidn) throws PDBProfilesDAOException {
        if (cidn == null) {
            return null;
        }
        try {
            return (Collection) getJdbcTemplate().query(COMPANY_ACCOUNTS_SQL, new Object[] { cidn }, new ResultSetExtractor() {

                public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
                    Map accounts = new HashMap();
                    PDBAccountVO pdbAccountVO = null;
                    while (rs.next()) {
                        String currentAccount = rs.getString(1);
                        if (!accounts.containsKey(currentAccount)) {
                            pdbAccountVO = new PDBAccountVO();
                            pdbAccountVO.setAccountNumber(currentAccount);
                            pdbAccountVO.getAttributes().put(rs.getString(2), rs.getString(3));
                            accounts.put(currentAccount, pdbAccountVO);
                        } else {
                            pdbAccountVO = (PDBAccountVO) (accounts.get(currentAccount));
                            pdbAccountVO.getAttributes().put(rs.getString(2), rs.getString(3));
                        }
                    }
                    return accounts.values();
                }
            });
        } catch (DataAccessException e) {
            throw new PDBProfilesDAOException("unable to retrieve accounts for Company: " + cidn, e);
        }
    }
    
    /**
	 * gets the current profile cache timeout. 
	 *
	 * @return timeout in hours.
	 * @throws PDBProfilesDAOException is thrown if the timeout value cannot be retrieved.
     * @see com.telstra.olb.tegcbm.job.migration.profiles.dao.PDBProfilesDAO#getProfileCacheTimeOut()
     */
    public long getProfileCacheTimeOut() throws PDBProfilesDAOException {
        try {
            String timeout = (String) getJdbcTemplate().queryForObject(CACHE_TIMEOUT_SQL, String.class);
            return Long.parseLong(timeout);
        } catch (DataAccessException e) {
            throw new PDBProfilesDAOException("unable to retrieve timeout information.", e);
        } catch (NumberFormatException e) {
            throw new PDBProfilesDAOException("invalid timeout value retrieved from PDB.", e);
        }
    }
    
    /**
 	 * 
 	 * Sets the profile cache timeout (in hrs) to control R&E updates.
 	 *
 	 * @param timeout timeout in hours
 	 * @throws PDBProfilesDAOException is thrown if the timeout values cannot be set.
     * @see com.telstra.olb.tegcbm.job.migration.profiles.dao.PDBProfilesDAO#setProfileCacheTimeOut(long)
     */
    public void setProfileCacheTimeOut(long timeout) throws PDBProfilesDAOException {
        try {
            int no = getJdbcTemplate().update(UPDATE_CACHE_TIMEOUT_SQL, new Object[] {"" + timeout});
            if (log.isDebugEnabled()) { log.debug("#"+ no + " rows updated to value: " + timeout); }
        }catch (DataAccessException e) {
            throw new PDBProfilesDAOException("unable to retrieve timeout information.", e);
        } 
    }
}
