/*
 * Created on 2/02/2010
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.telstra.olb.tegcbm.job.migration.eftid.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import com.telstra.olb.tegcbm.job.core.ActivityStatus;
import com.telstra.olb.tegcbm.job.core.DefaultDataProvider;
import com.telstra.olb.tegcbm.job.core.IContext;
import com.telstra.olb.tegcbm.job.migration.dao.MigrationDAO;
import com.telstra.olb.tegcbm.job.migration.dao.MigrationDAOException;
import com.telstra.olb.tegcbm.job.migration.model.OLBCompanyMigration;
import com.telstra.olb.tegcbm.job.migration.util.MigrationHelper;

/**
 * @author pavan.x.kuma
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class EFTIDRollbackActivityDataProvider extends DefaultDataProvider {
	
	public static final String INPUT_ARG_COMPANY_LIST = "companyList";
	
	private MigrationDAO migrationDao;

	private String macroSegment = "SME";
	
	/**
     * Gets the companies that has been specified as command line arguments by the property companyList.
     * 
     * @param context input args
     * @return an iterator of the OLBCompanyMigration objects for the list specified.
     * @see com.telstra.olb.tegcbm.job.core.DefaultActivity#processInputArgs(com.telstra.olb.tegcbm.job.core.InputArgs)
     */
    protected Collection processContext(IContext context) {
        String companyListStr = (String) context.getProperty(INPUT_ARG_COMPANY_LIST);
        if (companyListStr == null) {
            return null;
        }
        List companies = MigrationHelper.parse(companyListStr, ",");
        Collection companyList = doGetData();
        Collection filteredList = CollectionUtils.select(companyList, getFilter(companies));
        return filteredList;
    }
	/**
	 * @param companies
	 * @return
	 */
	protected Predicate getFilter(final List companies) {
		return new Predicate() { 
        	public boolean evaluate(Object company) {
                OLBCompanyMigration companyMigration = (OLBCompanyMigration) company;
                return companies.contains(companyMigration.getCompanyCode()) && companyMigration.getSegment().equals(getMacroSegment());
            }
        };
	}
	/**
	 * Returns the list of companies that needs to be rolled back.
	 * 
	 * @return Iterator iterator to the list of companies.
	 */
    protected Collection doGetData() {
		List companies = new ArrayList();
        try {
            companies = migrationDao.findCompanies(ActivityStatus.STATUS_ERROR.getName());
        } catch (MigrationDAOException e) {
            log.error(e.getMessage(), e);
            companies = new ArrayList();
        }
        return companies;
	}

	/**
	 * @return Returns the migrationDao.
	 */
	public MigrationDAO getMigrationDao() {
		return migrationDao;
	}
	/**
	 * @param migrationDao The migrationDao to set.
	 */
	public void setMigrationDao(MigrationDAO migrationDao) {
		this.migrationDao = migrationDao;
	}
	
	/**
     * @return Returns the macroSegment.
     */
    public String getMacroSegment() {
        return macroSegment;
    }
    /**
     * @param macroSegment The macroSegment to set.
     */
    public void setMacroSegment(String macroSegment) {
        this.macroSegment = macroSegment;
    }
}
