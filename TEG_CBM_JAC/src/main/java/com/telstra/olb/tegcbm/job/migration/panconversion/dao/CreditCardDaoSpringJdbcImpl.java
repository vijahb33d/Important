/*
 * Created on 7/07/2010
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.telstra.olb.tegcbm.job.migration.panconversion.dao;

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
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

/**
 * @author arun.balasubramanian
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class CreditCardDaoSpringJdbcImpl extends JdbcDaoSupport implements CreditCardDao{
	private Log log = LogFactory.getLog(this.getClass());

	public static final String SELECT_CC_NUMS_SQL = "SELECT cct.card_id,cct.cc_number FROM BILLPAY_DBA.CREDIT_CARD_T cct";
	public static final String UPDATE_CC_NUMS_SQL = "UPDATE BILLPAY_DBA.CREDIT_CARD_T_TPAN SET cc_number=? WHERE card_id=?";
	
	/**
	 * Method returns all the CPANs in the database as a map with card_id as key and cc_number as value.
	 * @return Map cpans
	 */
	public Map getCreditCardNumbers() throws CreditCardDataException {
		if (log.isDebugEnabled()) { log.debug("Retrieving CPANS from OLB Database");}
		final Map cpans = new HashMap();
		try{
			getJdbcTemplate().query(SELECT_CC_NUMS_SQL, new RowMapper() {
	            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
	            	String cardId = rs.getString("card_id");
	            	byte[] bytes = rs.getBytes("cc_number");
	            	//String cpan = bytes.toString();
	            	cpans.put(cardId, bytes);
	            	return null;
	            }
			});
		}
		catch (Exception e) {
			throw new CreditCardDataException("Exception Occured while retrieving Creditcard numbers",e);
		}
		return cpans;
	}

	/**
	 * Method updates all the CPANs with TPANs passed as Map.
	 * @param Map tpans
	 */
	public void updateCPANsToTPANs(final Map tpans) throws CreditCardDataException {
		if (log.isDebugEnabled()) { log.debug("Updating CPANs with TPANs from BillPay");}
		final List cardIds = new ArrayList();
		cardIds.addAll(tpans.keySet());
		  try {    
	        getJdbcTemplate().batchUpdate(UPDATE_CC_NUMS_SQL, new BatchPreparedStatementSetter() {
	
				public void setValues(PreparedStatement insertStat, int index) throws SQLException {
					String cardId = (String)cardIds.get(index);
					insertStat.setString(1, (String)tpans.get(cardId));
	                insertStat.setString(2, cardId);
				}
	
				public int getBatchSize() {
					return tpans.size();
				}
			});
	    } catch (DataAccessException e) {
	    	throw new CreditCardDataException("Exception Occured while inserting TPANs",e);
	    }
	}

}
