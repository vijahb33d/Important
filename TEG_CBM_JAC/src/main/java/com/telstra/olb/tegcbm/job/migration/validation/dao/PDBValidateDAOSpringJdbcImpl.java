/*
 * Created on 14/11/2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.telstra.olb.tegcbm.job.migration.validation.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import com.telstra.olb.tegcbm.job.migration.profiles.dao.PDBProfilesDAOException;

/**
 * @author mahesh.prabhu
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PDBValidateDAOSpringJdbcImpl extends JdbcDaoSupport implements
		PDBValidateDAO {
	private static Log log = LogFactory
			.getLog(PDBValidateDAOSpringJdbcImpl.class);


	/* (non-Javadoc)
	 * @see com.telstra.olb.tegcbm.job.migration.validation.dao.PDBValidateDAO#getCustomerDetails(java.lang.String, java.lang.String)
	 */
	public ArrayList getCustomerDetails(String method, String customerCode) throws PDBProfilesDAOException {
		String query = Messages.getString("PDBValidateDAOSpringJdbcImpl."+method);
		log.debug("Running query : " + query);
		ArrayList returned = new ArrayList();
		
		if (customerCode == null || method == null) {
			log
					.error("customerCode or method passed to PDBValidateDAO.getCustomerDetails(String method, String customerCode)) is null"); //$NON-NLS-1$
			return null;
		}
		try {
					List l = getJdbcTemplate().queryForList(query, new Object[] { customerCode });
					Object[] result = l.toArray();
					for (int i = 0; i < result.length; i++) {
						log.debug("PDB Result :: " + result[i].toString());
						returned.add(result[i].toString());
					}
		} catch (DataAccessException e) {
			throw new PDBProfilesDAOException(
					"unable to retrieve accounts for Company: " + customerCode, //$NON-NLS-1$
					e);
		}
		return returned;
	}
	

}
