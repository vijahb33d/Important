/*
 * Created on Feb 8, 2010
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.telstra.olb.tegcbm.job.migration.appPreferences.dao;

import java.util.List;
import java.util.Map;

/**
 * @author hariharan.venkat
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface SetDefaultAppPreferencesDao {

    /**
     * Finds the profile id  for the passed cidn in the edx database.
     *  
     * @param cidn Company cidn to search hierarchy for
     * @return profile Id
     * @throws DefaultAppPreferencesException is thrown if the profile Id  cannot be retrieved.
     */

    public long getProfileId(String companyCode) throws DefaultAppPreferencesException;

    /**
     * Finds the list of Preferences  for the passed owncode in the edx database and returns as a List.
     *  
     * @param  ownership code to search preferences for
     * @return List of preferences
     * @throws DefaultAppPreferencesException is thrown if the List cannot be retrieved.
     */

    public List getDefaultAppPreferences(String ownCode) throws DefaultAppPreferencesException;

    /**
     * This method is responsible for retrieving the CBM company profile attributes from the database.
     * 
     * @param profileId,applicationPreferences
     * @throws DefaultAppPreferencesException
     */

    public int updateCompanyProfileAttributes(final long profileId, final List preferencesList) throws DefaultAppPreferencesException;
    
    /**
     * This method is responsible for updating the CBM company profile attributes.
     * @param cidn
     * @return
     * @throws DefaultAppPreferencesException
     */
    public Map getCompanyProfileAttributes(final String cidn) throws DefaultAppPreferencesException;
    
    

}
