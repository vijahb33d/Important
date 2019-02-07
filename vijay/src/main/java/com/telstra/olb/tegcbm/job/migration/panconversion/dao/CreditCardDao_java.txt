/*
 * Created on 7/07/2010
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.telstra.olb.tegcbm.job.migration.panconversion.dao;

import java.util.List;
import java.util.Map;

/**
 * @author arun.balasubramanian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface CreditCardDao {
	
	/**
	 * Method returns the list of credit card numbers from the database
	 * 
	 * @return
	 * @throws CreditCardDataException
	 */
	Map getCreditCardNumbers() throws CreditCardDataException;
	
	/**
	 * Method inserts the TPANs replacing CPANs in the database against cardId.
	 * 
	 * @param tpans
	 * @throws CreditCardDataException
	 */
	void updateCPANsToTPANs(Map tpans) throws CreditCardDataException;
}
