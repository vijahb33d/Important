/*
 * PdbDaoSpringJdbcImpl.java
 * Created on 16/07/2007 by pavan.x.kuma
 *
 */
package com.telstra.olb.tegcbm.job.migration.populate.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import com.telstra.olb.tegcbm.job.core.ActivityStatus;
import com.telstra.olb.tegcbm.job.migration.model.OLBCompanyMigration;
import com.telstra.olb.tegcbm.job.migration.populate.model.OwnCodes;
import com.telstra.olb.tegcbm.job.migration.util.MigrationHelper;

/**
 * Spring JDBC implementation of PdbDao object to retrieve the list of companies from PDB for the 
 * list of own codes for the given macro segment. 
 */
public class PdbDaoSpringJdbcImpl extends JdbcDaoSupport implements PdbDao {
    private static Log log = LogFactory.getLog(PdbDaoSpringJdbcImpl.class); 
    
    private static final String COMPANY_LIST_SQL = 
        "select cidn, value from pdb_customer_values_t "
        + "where attribute_id = 'ownership_code' and value in ";

    private static final String COL_COMPANY_CODE = "cidn";
    private static final String COL_OWN_CODE_VALUE = "value";

    /**
     * Gets the list of companies for the own codes.
     * 
     * @param ownCodes map of own codes with its segment.
     * @return list of OLBCompanyMigration objects.
     * @throws PopulateDataException upon exception
     * @see com.telstra.olb.tegcbm.job.migration.populate.dao.PdbDao#getCompanies(java.util.List)
     */
    public List getCompanies(final OwnCodes ownCodes) throws PopulateDataException {
        // IN Queries cannot be parameterized. 
        // So we need to construct the query statically for the list of own codes.
        if (log.isDebugEnabled()) {
            log.debug("getting list of companies for #" + ownCodes.size() + " owncodes.");
        }
        String sql = MigrationHelper.createInSQLQuery(COMPANY_LIST_SQL, ownCodes.getOwnCodes());
        List companyList = (List) getJdbcTemplate().query(sql, new RowMapper() {
            public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                String company = rs.getString(COL_COMPANY_CODE);
                String ownCode = rs.getString(COL_OWN_CODE_VALUE);
                String segment = (String) ownCodes.getSegment(ownCode);
                return createCompanyMigration(company, ownCode, segment);
            }
        });
        return companyList;
    }

    /**
     * creates a company migration object.
     * 
     * @param companyCode
     *            comapny id
     * @param ownCode
     *            own code
     * @param segment
     *            macrosegment
     * @return OLBCompanyMigration object.
     */
    private OLBCompanyMigration createCompanyMigration(String companyCode, String ownCode, String segment) {
        OLBCompanyMigration companyMigration = new OLBCompanyMigration();
        companyMigration.setCompanyCode(companyCode);
        companyMigration.setOwnCode(ownCode);
        companyMigration.setSegment(segment);
        companyMigration.setCreatedDate(new Date());
        companyMigration.setActivityStatus(ActivityStatus.STATUS_PENDING.getValue());
        return companyMigration;
    }
}
