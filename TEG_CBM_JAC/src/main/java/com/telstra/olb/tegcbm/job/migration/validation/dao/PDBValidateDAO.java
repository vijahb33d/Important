/*
 * Created on 14/11/2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.telstra.olb.tegcbm.job.migration.validation.dao;

import java.util.ArrayList;

import com.telstra.olb.tegcbm.job.migration.profiles.dao.PDBProfilesDAOException;

/**
 * @author mahesh.prabhu
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface PDBValidateDAO {
	
	public ArrayList getCustomerDetails(String method, String customerCode) throws PDBProfilesDAOException;

}
