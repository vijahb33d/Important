/*
 * Created on 20/01/2010
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.telstra.olb.tegcbm.job.migration.appPreferences.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.telstra.olb.tegcbm.job.core.ActivityException;
import com.telstra.olb.tegcbm.job.core.DefaultActivity;
import com.telstra.olb.tegcbm.job.migration.appPreferences.dao.DefaultAppPreferencesException;
import com.telstra.olb.tegcbm.job.migration.appPreferences.dao.SetDefaultAppPreferencesDao;
import com.telstra.olb.tegcbm.job.migration.appPreferences.model.DefaultPreferences;
import com.telstra.olb.tegcbm.job.migration.model.OLBCompanyMigration;

/**
 * @author pavan.x.kuma
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class SetDefaultAppPreferencesActivity extends DefaultActivity {

    SetDefaultAppPreferencesDao setDefaultAppPreferencesDao;

    private static final String ONLINE_BILL = "OnlineBill";

    private static final String ONLINE_BILL_PRO = "OnlineBillPro";
    
    /**
     * @return Returns the setDefaultAppPreferencesDao.
     */
    public SetDefaultAppPreferencesDao getSetDefaultAppPreferencesDao() {
        return setDefaultAppPreferencesDao;
    }

    /**
     * @param setDefaultAppPreferencesDao The setDefaultAppPreferencesDao to set.
     */
    public void setSetDefaultAppPreferencesDao(SetDefaultAppPreferencesDao setDefaultAppPreferencesDao) {
        this.setDefaultAppPreferencesDao = setDefaultAppPreferencesDao;
    }

    /* (non-Javadoc)
     * @see com.telstra.olb.tegcbm.job.core.DefaultActivity#doExecute(java.lang.Object)
     */
    protected void doExecute(Object input) throws ActivityException {
        OLBCompanyMigration current = (OLBCompanyMigration) input;
        String ownCode = current.getOwnCode();
        String companyCode = current.getCompanyCode();
		List preferencesList = getDefaultAppPreferences(ownCode);
		Map newAttributes = getAttributesMap(preferencesList);
		List newPreferenceList = new ArrayList(); 
        long profileId = getProfileId(companyCode);

        try {
        	Map existingAttributes = setDefaultAppPreferencesDao.getCompanyProfileAttributes(companyCode);
        	
        	//AB: Reason for passing existingAttribs as the first parameter is just not to lose the defaultAppPreferences if already available.
        	boolean isAttributesModified = matchAttributes(existingAttributes, newAttributes, preferencesList, newPreferenceList);
        	if (log.isDebugEnabled()) {log.debug("Is the Preference Attributes already present in the Attributes table : " + !isAttributesModified);}
        	if(isAttributesModified)
        	{
        		int added = setDefaultAppPreferencesDao.updateCompanyProfileAttributes(profileId, newPreferenceList);
                log.info("company profiles Attributes: " + added);
        	}

        } catch (DefaultAppPreferencesException e) {

            throw new ActivityException("unable to set default application preferences for companies.", e);
        }

    }
    
    
    /**
	 * Match the new and existing attributes and copy existing 
	 * attributes not available in new attributes. Also populates the newPreferences list with the new preferences to be added. 
	 * 
	 * @param newAttributes New attributes
	 * @param existingAttributes Existing attributes
     * @param preferencesList
     * @param newPreferenceList
	 */
	private boolean matchAttributes(Map newAttributes, Map existingAttributes, List preferencesList, List newPreferenceList) {
		boolean isAdded = false;
		
		Iterator existingKeyItr = existingAttributes.keySet().iterator();
		while (existingKeyItr.hasNext()) {
			String key = (String)existingKeyItr.next();
			if (!newAttributes.containsKey(key)) {
				isAdded = true;
				newAttributes.put(key, existingAttributes.get(key));
				newPreferenceList.add(getDefaultPreferencesByRoleType(preferencesList, key));
			}
		}
		return isAdded;
	}
	
	/**
	 * Returns the Default App preferences from the PreferenceList by roletype
	 * @param preferencesList
	 * @param key
	 * @return
	 */
	private DefaultPreferences getDefaultPreferencesByRoleType(List preferencesList, String key){
		DefaultPreferences preference = null;
		Iterator iter = preferencesList.iterator();
		while(iter.hasNext()){
			DefaultPreferences defPref = (DefaultPreferences)iter.next();
			if(key.equalsIgnoreCase("VP"+defPref.getCbmRoleType())){
				return defPref;
			}
		}
		return preference;
	}
	
	/**
	 * Method is responsible for converting the list of preferences generated to a Map.
	 * 
	 * @param preferencesList
	 * @return
	 */
	private Map getAttributesMap(List preferencesList){
		Map attributes = new HashMap();
		Iterator iter = preferencesList.iterator();
        while(iter.hasNext()){
        	DefaultPreferences defaultAppPreferences = (DefaultPreferences)iter.next();
            StringBuffer roleType=new StringBuffer("VP");
            if(defaultAppPreferences.getCbmRoleType().equalsIgnoreCase("admin")){
                roleType.append(defaultAppPreferences.getCbmRoleType());
            }else{
                roleType.append(defaultAppPreferences.getCbmRoleType());
               //roleType.append("User");
            }
            String roleTypeString=roleType.toString();
            if (defaultAppPreferences.getDefaultPreference().equalsIgnoreCase(ONLINE_BILL_PRO)) {
            	attributes.put(roleTypeString, ONLINE_BILL_PRO);
            } else {
            	attributes.put(roleTypeString, ONLINE_BILL);
            }
            if (log.isDebugEnabled()) {
                log.debug("Preferences Attributes Map : " + attributes);
            }
        }
        return attributes;
	}

    /**
     * @param companyCode
     * @return profileId 
     * @throws ActivityException
     *             is thrown if the profile Id for the company cannot be
     *             retrieved.
     */
    private long getProfileId(String companyCode) throws ActivityException {
        try {
            long profileId = setDefaultAppPreferencesDao.getProfileId(companyCode);
            if (log.isDebugEnabled()) {
                log.debug("profile Id  Fetched : " + profileId);
            }
            return profileId;

        } catch (DefaultAppPreferencesException e) {
            throw new ActivityException("unable to get profile id  for company: " + companyCode, e);
        }

    }

    /**
     * @param ownCode
     * @return List of Preferences for a company
     * @throws ActivityException
     *             is thrown if the profile Id for the company cannot be
     *             retrieved.
     */
    private List getDefaultAppPreferences(String ownCode) throws ActivityException {
        try {
            List preferencesList = setDefaultAppPreferencesDao.getDefaultAppPreferences(ownCode);
            if (log.isDebugEnabled()) {
                log.debug("Preferences Fetched : " + preferencesList);
            }
            return preferencesList;
        } catch (DefaultAppPreferencesException e) {
            throw new ActivityException("unable to get preferences for given own code: " + ownCode, e);
        }

    }

}
