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

import com.telstra.olb.tegcbm.job.migration.profiles.dao.CBMProfilesDAOException;

/**
 * @author mahesh.prabhu
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CBMValidateDAOSpringJdbcImpl extends JdbcDaoSupport implements
		CBMValidateDAO {

	private static Log log = LogFactory
			.getLog(CBMValidateDAOSpringJdbcImpl.class);

	/* (non-Javadoc)
	 * @see com.telstra.olb.tegcbm.job.migration.validation.dao.CBMValidateDAO#getCustomerDetails(java.lang.String, java.lang.String)
	 */
	public ArrayList getCustomerDetails(String method, String customerCode)
			throws CBMProfilesDAOException {
		String query = Messages.getString("CBMValidateDAOSpringJdbcImpl."
				+ method);
		log.debug("Running query : " + query);
		ArrayList returned = new ArrayList();

		if (customerCode == null || method == null) {
			log
					.error("customerCode or method passed to CBMValidateDAO.getCustomerDetails(String method, String customerCode)) is null"); //$NON-NLS-1$
			return null;
		}
		try {

			List l = getJdbcTemplate().queryForList(query,
					new Object[] { customerCode });
			Object[] result = l.toArray();
			for (int i = 0; i < result.length; i++) {
				log.debug("CBM Result :: " + result[i].toString());
				returned.add(result[i].toString());
			}
		} catch (DataAccessException e) {
			throw new CBMProfilesDAOException(
					"unable to retrieve accounts for Company: " + customerCode, //$NON-NLS-1$
					e);
		}
		return returned;
	}

}
