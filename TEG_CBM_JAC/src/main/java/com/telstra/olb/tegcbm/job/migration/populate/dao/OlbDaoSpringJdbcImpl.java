/*
 * OlbDaoSpringJdbcImpl.java
 * Created on 16/07/2007 by pavan.x.kuma
 *
 */
package com.telstra.olb.tegcbm.job.migration.populate.dao;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import com.telstra.olb.tegcbm.job.migration.populate.model.MigrationType;
import com.telstra.olb.tegcbm.job.migration.populate.model.OwnCodes;
import com.telstra.olb.tegcbm.job.migration.util.MigrationHelper;

/**
 *  Spring JDBC Implementation of OlbDao Data Access Interface. 
 */
public class OlbDaoSpringJdbcImpl extends JdbcDaoSupport implements OlbDao {
    private static Log log = LogFactory.getLog(OlbDaoSpringJdbcImpl.class); 
    
    private static final String OWN_CODE_SEGMENTS_SQL = "select own_code, segment from olb_category_t where segment in ";

    private static final String COL_OWN_CODE = "own_code";

    private static final String COL_SEGMENT = "segment";

    /**
     * Retrieves the list of owncodes for the list of segments defined for the migration type (ENTERPRISE, SME) 
     * from olb services OLB_CATEGORY_T.
     * <p>
     * The table below shows the unique combinations of values.
     * SME customers have a Segment of SmallBusiness or SME
     * E&G customer have a Segment of International or Key-Corporate
     *  <br/>
     *  Segment	        Macro Segment	OLB Category
     *  <br/>
     *  International	ENTERPRISE	    Corporate<br/>	  
     *  Key-Corporate	ENTERPRISE	    Corporate<br/>  
     *  Key-Corporate	SME	            Corporate<br/>	  
     *  SmallBusiness	ENTERPRISE	    ConsSme<br/>      
     *  SmallBusiness	SMALL BUS	    ConsSme<br/>     	
     *  SmallBusiness	SME	            ConsSme<br/>  
     *  SME	            ENTERPRISE	    ConsSme<br/>        
     *  SME	            SME	            ConsSme<br/>
     * </p>
     * <p>
     * The list of segments are registered with the migration type enum.
     * @see com.telstra.olb.tegcbm.job.migration.populate.model.MigrationType#getSegments()
     * </p>
     * 
     * @param migrationType migration type either ENTERPRISE or SME
     * @return OwnCodes object that has owncode and its segment information.
     * @throws PopulateDataException is thrown if there is any data access exceptions.
     * @see com.telstra.olb.tegcbm.job.migration.populate.dao.OlbDao#getOwnCodesAndSegments(java.util.List)
     */       
    public OwnCodes getOwnCodes(MigrationType migrationType) throws PopulateDataException {
        if (migrationType == null) {            
            throw new PopulateDataException("migartion type cannot be null.");
        }
        try {
            if (log.isDebugEnabled()) {
                log.debug("getting own codes for migration type: " + migrationType);
            }
            String sql = MigrationHelper.createInSQLQuery(OWN_CODE_SEGMENTS_SQL, migrationType.getSegments());
            return (OwnCodes) getJdbcTemplate().query(sql, new ResultSetExtractor() {

                public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
                    OwnCodes ownCodes = new OwnCodes();
                    while (rs.next()) {
                        ownCodes.put(rs.getString(COL_OWN_CODE), rs.getString(COL_SEGMENT));
                    }
                    return ownCodes;
                }
            });
        } catch (DataAccessException e) {
            throw new PopulateDataException("unable to retrieve own codes and segments for " + migrationType, e);
        }
    }
}
