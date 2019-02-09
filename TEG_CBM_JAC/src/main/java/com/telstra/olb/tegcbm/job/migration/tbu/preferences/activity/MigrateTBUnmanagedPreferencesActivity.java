package com.telstra.olb.tegcbm.job.migration.tbu.preferences.activity;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.edocs.common.api.bsl.IBusinessServices;
import com.edocs.common.bsl.core.BusinessServicesBeanFactory;
import com.telstra.olb.tegcbm.job.core.ActivityException;
import com.telstra.olb.tegcbm.job.core.DefaultActivity;
import com.telstra.olb.tegcbm.job.migration.model.CBMUserPreferenceBackup;
import com.telstra.olb.tegcbm.job.migration.model.OLBEcsUser;
import com.telstra.olb.tegcbm.job.migration.model.OLBTBUnmanagedCompanyPreference;
import com.telstra.olb.tegcbm.job.migration.model.PDBUserAccountPreference;
import com.telstra.olb.tegcbm.job.migration.populate.dao.PopulateDao;
import com.telstra.olb.tegcbm.job.migration.populate.dao.PopulateDataException;

public class MigrateTBUnmanagedPreferencesActivity extends DefaultActivity {

	private static final String BL_APPLICATION_ID = "BL";
	private static final String PREFERENCE_EMAIL = "email";
	private static final String INSERT_LIST = "insertList";
	private static final String SPP_LIST = "sppList";
	private static final String BACKUP_LIST = "backupList";
	
	private PopulateDao populateDao;
	
	private PopulateDao populateDaoPDB;
	
	private Properties attributeProps;
	
	/**
	 * This method will migrate the CBM preferences from CBM tables to PDB tables. 
	 * The Asynchronous activity will call the execute method of its parent class(Default Activity) 
	 * which internal will make a call to the doExecute of the MigrateTBUnmanagedPreferencesActivity 
	 * class for each company retrieved in the doGetData() method.
	 *  
	 * @Param Object
	 */
	public void doExecute(Object input) throws ActivityException {
		//Retrieve the preferences for the CIDN
		OLBTBUnmanagedCompanyPreference preferenceInput =  (OLBTBUnmanagedCompanyPreference) input;
		OLBTBUnmanagedCompanyPreference preference = retrievePreferences(preferenceInput.getCompany().getCompanyCode());
		if(preference != null) {
			Map lists = createPDBPreferences(preference);
			migrate(lists);
		} 
	}
	
	/**
	 * This method will perform the following steps:
	 *  •	Take the back-up of the preferences data in the EDX_DBA.EDX_BSL_USER_PROF_ATTRIBS_BKUP table by calling the backupUserPreferences(List backupList) method.
	 * 	•	Insert the preferences in the PDB_DBA.PDB_USER_ACCOUNT_PREF_VALUES_T table by calling the insertUserPreferencesInToPDB(insertList).
	 * 	•	Remove the preferences from the EDX_DBA.EDX_BSL_USER_PROF_ATTRIBS table by calling the deleteUserPreferencesInCBM(backupList).
	 * @param Map lists
	 * @throws ActivityException
	 */
	private void migrate(Map lists) throws ActivityException {
		List insertList = (List)lists.get(INSERT_LIST);
		List backupList = (List)lists.get(BACKUP_LIST);
		backupUserPreferences(backupList);
		insertUserPreferencesInToPDB(insertList);
		deleteUserPreferencesInCBM(backupList);
	}

	/**
	 * This method is responsible for removing the CBM preferences. 
	 * @param backupList
	 * @throws ActivityException
	 */
	private void deleteUserPreferencesInCBM(List backupList) throws ActivityException {
		try {
			populateDao.deleteUserAccountPreferences(backupList);
		} catch (PopulateDataException e1) {
			log.debug("Error occured while deleting the preferences",e1);
			throw new ActivityException("Error occured while deleting the preferences",e1);
		}		
	}

	/**
	 * This method is responsible for inserting the preferences in the PDB tables. 
	 * @param insertList
	 * @throws ActivityException
	 */
	private void insertUserPreferencesInToPDB(List insertList) throws ActivityException {
		try {
			populateDaoPDB.insertUserAccountPreference(insertList);
		} catch (PopulateDataException e) {
			log.debug("Error occured while updating preferences",e);
			throw new ActivityException("Error occured while updating preferences",e);
		}		
	}

	/**
	 * This method is responsible for backing-up the CBM preferences. 
	 * @param backupList
	 * @throws ActivityException
	 */
	private void backupUserPreferences(List backupList) throws ActivityException {
		try {
			populateDao.backupUserAccountPreference(backupList);
		} catch (PopulateDataException e) {
			log.debug("Error occured while taking the backup of  preferences",e);
			throw new ActivityException("Error occured while taking the backup of  preferences",e);
		}
	}
	
	/**
	 * This method is responsible for creating the objects which will be used while inserting the records in the PDB tables.
	 * 	The objects will be stored in a MAP. The following objects will be stored in the Map,
	 *	•	List of objects to be inserted in the PDB, the objects will be of type PDBUserAccountPreference.
	 *  •	List of objects to be backed-up in CBM, the objects will be of type CBMUserPreferenceBackup.
	 * 
	 * @param preference
	 */
	private Map createPDBPreferences(OLBTBUnmanagedCompanyPreference preference) {
		List insertList = new ArrayList();
		List backupList = new ArrayList();
		Map returnMap = new HashMap();
		returnMap.put(INSERT_LIST, insertList);
		returnMap.put(BACKUP_LIST, backupList);
		
		Iterator userIter = preference.getEcsUsers().keySet().iterator();
		String cidn = preference.getCompany().getCompanyCode();
		//Iterating through users belong to the Company
		while(userIter.hasNext()){
			String userId = (String)userIter.next();
			OLBEcsUser user = (OLBEcsUser) preference.getEcsUser(userId);
			Map prefMap = (Map)user.getPreferences();
			Iterator prefIter = prefMap.keySet().iterator();
			//Iterating through the preferences set for each user
			while(prefIter.hasNext()){
				String prefId = (String)prefIter.next();
				String prefValue = (String)prefMap.get(prefId);
				Iterator accountsIter = user.getAccounts().iterator();
				//Iterating through the accounts in the company for updating preferences in PDB
				if(!prefValue.equalsIgnoreCase("false")){
					while(accountsIter.hasNext()){
						String accNum = (String)accountsIter.next();
						
						//Creating PDBUserPreference object matching the PDB_User_Account_Pref_T table
						PDBUserAccountPreference uaPref = createPDBUserAccountPreference(accNum, userId, prefId, PREFERENCE_EMAIL, BL_APPLICATION_ID);
						insertList.add(uaPref);
					}
				}
				int attrId = user.getAttrId();
				//Creating backup preference objects(matching backup tables) for backup of migrated preferences. 
				CBMUserPreferenceBackup attribute = createCBMUserPreferenceBackup(attrId, prefId, prefValue, cidn);
				backupList.add(attribute);
			}
		}
		return returnMap;
	}

	/**
	 * This method will create a CBMUserPreferenceBackup object which will be used to insert the records in the back-up table.
	 * 
	 * @param attrId
	 * @param prefId
	 * @param prefValue
	 * @param cidn
	 */
	private CBMUserPreferenceBackup createCBMUserPreferenceBackup(int attrId, String prefId, String prefValue, String cidn) {
		CBMUserPreferenceBackup attribute = new CBMUserPreferenceBackup();
		attribute.setAttrId(attrId);
		attribute.setAttrKey(prefId);
		attribute.setAttrVal(prefValue);
		attribute.setCompanyCode(cidn);	
		attribute.setCreatedDate(new Date());
		return attribute;
	}

	/**
	 * This method will create a PDBUserAccountPreference object which will be used to insert the records in the PDB preference table.
	 *  
	 * @param accNum
	 * @param userId
	 * @param prefId
	 * @param prefValue
	 * @param bl_application_id2
	 */
	private PDBUserAccountPreference createPDBUserAccountPreference(String accNum, String userId, String prefId, String prefValue, String bl_application_id2) {
		PDBUserAccountPreference uaPref = new PDBUserAccountPreference();
		uaPref.setAccountNumber(accNum);
		uaPref.setUserId(userId);
		uaPref.setAttributeId(attributeProps.getProperty(prefId));
		uaPref.setValue(prefValue);
		uaPref.setApplicationId(BL_APPLICATION_ID);
		return uaPref;
	}
    
    /**
     * This method is responsible for retrieving the user preferences for the TB Unmanaged customers which are going to be migrated to PDB.
     * 
     * @param cidn
     * @return
     * @throws ActivityException
     */
    protected OLBTBUnmanagedCompanyPreference retrievePreferences(String cidn) throws ActivityException {
    	try {
    		//Retrieving all the TBU company preferences from the database
			List tbuCompanyPreferences = new ArrayList();
			tbuCompanyPreferences = populateDao.getTBUnmanagedCompanyPreferences(cidn);
			
			Iterator iterator = tbuCompanyPreferences.iterator();
			while(iterator.hasNext()) {
				OLBTBUnmanagedCompanyPreference pref = (OLBTBUnmanagedCompanyPreference) iterator.next();
					//Retrieving accounts in a company from the OLB_ACCOUNT_COMPANY_MAP.
					List accounts = populateDao.getAccountNumbersForCompany(pref.getCompany().getCompanyCode());
					
					if(accounts != null) {
						pref.getAccountList().addAll(accounts);
					}
			}
			
			//Grouping user preferences based on CIDN
			tbuCompanyPreferences = groupUserPreferencesForCIDN(tbuCompanyPreferences); 
			if(tbuCompanyPreferences.isEmpty()){
				return null;
			}
			return (OLBTBUnmanagedCompanyPreference) tbuCompanyPreferences.get(0);
		} catch (Exception e) {
			log.error("Error occured with CIDN:"+cidn+" while retrieving preferences");
			throw new ActivityException("Error occured with CIDN:"+cidn+" while retrieving preferences");
		}
    }
	
    /**
     * Method restructures the preferences data in such a way that it helps the data insertion.
     * Method does the Grouping of preferences based on CIDN 
     * 
     * This method is called from the doGetData() method. 
     * This method is responsible for grouping the preferences objects with the user objects and grouping the users objects with the company
     * 
	 * @param tbuCompanyPreferences
	 * @return
	 */
	private List groupUserPreferencesForCIDN(List tbuCompanyPreferences) {
		Map prefMap = new HashMap();
		Iterator iter = tbuCompanyPreferences.iterator();
		//Iterating through each Preference object fetched from the database.
		while(iter.hasNext()){
			OLBTBUnmanagedCompanyPreference pref = (OLBTBUnmanagedCompanyPreference) iter.next();
			String cidn = pref.getCompany().getCompanyCode();

			//Checking whether the CIDN is already present in the Map, 
			//i.e. Populating preferences in a map with Key as CIDN 
			//and Values as preference object containing all the user preferences.
			if(prefMap.containsKey(cidn)) {
				OLBTBUnmanagedCompanyPreference cPref = (OLBTBUnmanagedCompanyPreference) prefMap.get(cidn);
				OLBEcsUser user = (OLBEcsUser) cPref.getEcsUser(pref.getUserId());
				//Checking whether the user object is already created for the current user,
				//Looks for appending the preferences in the User object if already created,
				//Else creates a user object with the current preference and sets it in corresponding CIDN preference object.
				if(user == null){
					String userId = pref.getUserId();
					int attrId = pref.getAttrId();
					List accounts = pref.getAccountList();
					user = new OLBEcsUser(userId, attrId, accounts);
				}
				user.getPreferences().put(pref.getPreference(), pref.getPreferenceValue());
				cPref.getEcsUsers().put(user.getUserId(), user);
				
				prefMap.put(cidn, cPref);
			} else {
				//User created for the first time with current preferences and set in the 
				//corresponding company preference object.
				OLBEcsUser user = new OLBEcsUser(pref.getUserId(), pref.getAttrId(), pref.getAccountList());
				user.getPreferences().put(pref.getPreference(), pref.getPreferenceValue());
				Map users = new HashMap();
				users.put(user.getUserId(), user);
				pref.setEcsUsers(users);
				
				prefMap.put(cidn, pref);
	 		}				
	 	}
		List prefs = new ArrayList();
		prefs.addAll(prefMap.values());
	 	return prefs;
 	}
	
	/**
	 * Method returns the BusinessServices object
	 * @return
	 */
	private IBusinessServices getBusinessServices() {
		return (IBusinessServices)BusinessServicesBeanFactory.getBean("businessServices");
	}
	
	/**
	 * @return Returns the populateDao.
	 */
	public PopulateDao getPopulateDao() {
		return populateDao;
	}
	/**
	 * @param populateDao The populateDao to set.
	 */
	public void setPopulateDao(PopulateDao populateDao) {
		this.populateDao = populateDao;
	}
	/**
	 * @return Returns the attributeProps.
	 */
	public Properties getAttributeProps() {
		return attributeProps;
	}
	/**
	 * @param attributeProps The attributeProps to set.
	 */
	public void setAttributeProps(Properties attributeProps) {
		this.attributeProps = attributeProps;
	}
	
	/**
	 * @return Returns the populateDaoPDB.
	 */
	public PopulateDao getPopulateDaoPDB() {
		return populateDaoPDB;
	}
	/**
	 * @param populateDaoPDB The populateDaoPDB to set.
	 */
	public void setPopulateDaoPDB(PopulateDao populateDaoPDB) {
		this.populateDaoPDB = populateDaoPDB;
	}
}
