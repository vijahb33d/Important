/*
 * Created on Feb 8, 2010
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.telstra.olb.tegcbm.job.migration.appPreferences.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import com.telstra.olb.tegcbm.job.migration.appPreferences.model.DefaultPreferences;

/**
 * @author hariharan.venkat
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SetDefaultAppPreferencesDaoSpringJdbcImpl extends JdbcDaoSupport implements SetDefaultAppPreferencesDao {

    private static Log log = LogFactory.getLog(SetDefaultAppPreferencesDaoSpringJdbcImpl.class);

    private static final String COL_OWN_CODE = "own_Code";
    private static final String COL_ID = "id";

    private static final String COL_CBM_ROLE_TYPE = "cbm_Role_Type";

    private static final String COL_DEFAULT_PREFERENCE = "default_Preference";

    private static final String ONLINE_BILL = "OnlineBill";

    private static final String ONLINE_BILL_PRO = "OnlineBillPro";
  
    private static final String ADMIN = "admin";
    
    private static final String UPDATE_EDX_BSL_COMPANY_PROFILES_SQL = "INSERT INTO EDX_DBA.EDX_BSL_CO_PROF_ATTRIBS "
            + "(ATTRID, ATTRKEY, ATTRVALUE) VALUES (?,?,?)";
    
    private static final String SELECT_EDX_BSL_COMPANY_PROFILES_SQL = "SELECT * FROM EDX_DBA.EDX_BSL_CO_PROF_ATTRIBS cpa" 
    																  +" WHERE cpa.ATTRID = (SELECT cp.ID FROM EDX_DBA.EDX_BSL_CMF_COMPANYPROFILE cp WHERE cp.NAME='";		

    private static final String ATTR_KEY = "ATTRKEY";
    
    private static final String ATTR_VALUE ="ATTRVALUE";
    
    /** 
     * Runs a query to obtain a list of preferences .
     * @param ownership Code ownCode
     * @return a list of preferences object.
     * @throws DefaultAppPreferencesException is thrown if there are any 
     * @see com.telstra.olb.tegcbm.job.migration.appPreferences.dao.SetDefaultAppPreferencesDao#getDefaultAppPreferences(String ownCode)
     */
    public List getDefaultAppPreferences(final String ownCode) throws DefaultAppPreferencesException {
    	  List preferencesList = null;
    	
    	if(ownCode !=null && ownCode!=""){
    	StringBuffer SELECT_PREFERENCES_SQL_PREFIX = new StringBuffer("SELECT  * FROM EDX_DBA.OLB_DEFAULT_ROLE_PREFERENCE WHERE OWN_CODE = '");
        SELECT_PREFERENCES_SQL_PREFIX.append(ownCode);
        SELECT_PREFERENCES_SQL_PREFIX.append("'");
        String sql = SELECT_PREFERENCES_SQL_PREFIX.toString();

        try {
            preferencesList = (List) getJdbcTemplate().query(sql, new RowMapper() {
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    String ownCode = rs.getString(COL_OWN_CODE);
                    String roleType = rs.getString(COL_CBM_ROLE_TYPE);
                    String defaultPreference = rs.getString(COL_DEFAULT_PREFERENCE);
                    return createDefaultPreferences(ownCode, roleType, defaultPreference);
                }
            });

            if (log.isDebugEnabled()) {
                log.debug("list of preferences for #" + preferencesList.size());
            }
        } catch (DataAccessException e) {
            throw new DefaultAppPreferencesException("unable to get preferences", e);
        }
    	}

        return preferencesList != null ? preferencesList : new ArrayList();
    }

    /** 
     * Runs a query to obtain profile Id for a company .
     * @param companyCode company Code
     * @return profile Id.
     * @throws DefaultAppPreferencesException is thrown if there are any 
     * @see com.telstra.olb.tegcbm.job.migration.appPreferences.dao.SetDefaultAppPreferencesDao#getProfileId(String companyCode)
     */
    public long getProfileId(final String companyCode) throws DefaultAppPreferencesException {
    	long profileId = 0;
    	List listProfile = new ArrayList();
    	if(companyCode !=null && companyCode!=""){
        StringBuffer SELECT_PROFILE_ID_SQL_PREFIX = new StringBuffer("SELECT  id FROM edx_bsl_cmf_companyprofile WHERE name = ?");
        
        String sql = SELECT_PROFILE_ID_SQL_PREFIX.toString();

        try {

            listProfile = (List) getJdbcTemplate().query(sql, 
														 new PreparedStatementSetter() {
																public void setValues(PreparedStatement psCompanyProfile) throws SQLException {
																	psCompanyProfile.setString(1,companyCode);
																}
									            		 } ,
														 new RowMapper() {
												                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
												                    long id = rs.getLong(COL_ID);
												                    return new Long(id);
												                }
											            });
 
            if (listProfile.size() > 1 ) {
            	log.error("There are " + listProfile.size() + " profiles in CBM for the company code: " + companyCode + 
            			  ". Reason: There may be problems with enrolments, check the corporatebill.log ");
                throw new DefaultAppPreferencesException("There are " + listProfile.size() + " profiles in CBM for the company code: " + companyCode);
			} else {
				Long profile = (Long) listProfile.get(0);
				profileId = profile.longValue();
			}
            
            if (log.isDebugEnabled()) {
                log.debug("list of preferences for #" + profileId);
            }
        } catch (DataAccessException e) {
            throw new DefaultAppPreferencesException("unable to get preferences", e);
        }
    	}

        return profileId;
    }

    /**
     * @param ownCode
     * @param roleType
     * @param defaultPreference
     * @return
     */
    protected Object createDefaultPreferences(String ownCode, String roleType, String defaultPreference) {
        // TODO Auto-generated method stub
        return new DefaultPreferences(ownCode, roleType, defaultPreference);
    }

    /**
     * This method is responsible for inserting the company preferences in the EDX table EDX_DBA.EDX_BSL_CO_PROF_ATTRIBS.
     * 
     * @param userAccountPreferences
     * @throws DefaultAppPreferencesException
     */
    public int updateCompanyProfileAttributes(final long profileId, final List preferencesList) throws DefaultAppPreferencesException {
        if (log.isDebugEnabled()) {
            log.debug("Inserting  preferences in CompanyProfileAttribs ");
        }

        try {
            getJdbcTemplate().batchUpdate(UPDATE_EDX_BSL_COMPANY_PROFILES_SQL, new BatchPreparedStatementSetter() {

                public void setValues(PreparedStatement insertStat, int index) throws SQLException {
                    DefaultPreferences defaultAppPreferences = (DefaultPreferences) preferencesList.get(index);
                    insertStat.setLong(1, profileId);
                    StringBuffer roleType=new StringBuffer("VP");
                    if(defaultAppPreferences.getCbmRoleType().equalsIgnoreCase("admin")){
                        roleType.append(defaultAppPreferences.getCbmRoleType());
                    }else{
                        roleType.append(defaultAppPreferences.getCbmRoleType());
                        //roleType.append("User");
                    }
                    String roleTypeString=roleType.toString();
                    insertStat.setString(2, roleTypeString);

                    if (defaultAppPreferences.getDefaultPreference().equalsIgnoreCase(ONLINE_BILL_PRO)) {
                        insertStat.setString(3, ONLINE_BILL_PRO);
                    } else {
                        insertStat.setString(3, ONLINE_BILL);
                    }
                   

                }

                public int getBatchSize() {
                    return preferencesList.size();
                }
            });
        } catch (DataAccessException e) {
            throw new DefaultAppPreferencesException("Unable to update preferences.", e);
        }
        return preferencesList.size();
    }

    /**
     * This method is responsible for updating the CBM company profile attributes.
     * @param cidn
     * @return
     * @throws DefaultAppPreferencesException
     */
	public Map getCompanyProfileAttributes(String cidn) throws DefaultAppPreferencesException {
		if (log.isDebugEnabled()) { log.debug("Retrieving preferences in CompanyProfileAttribs "); }
		final Map attributes = new HashMap();
		if(cidn != null) {
			String SQL = SELECT_EDX_BSL_COMPANY_PROFILES_SQL+ cidn+"')";
			try{
				getJdbcTemplate().query(SQL,new RowMapper(){
					public Object mapRow(ResultSet rs, int arg1) throws SQLException {
						String attrKey = rs.getString(ATTR_KEY);
						String attrValue = rs.getString(ATTR_VALUE);
						if(attrKey != null && attrValue != null){
							attributes.put(attrKey, attrValue);	
						}						
						return attrKey;
					}
				});
			}catch(Exception e){
				throw new DefaultAppPreferencesException(e);
			}
		}
		return attributes;
	}

}
