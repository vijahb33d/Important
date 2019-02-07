/*
 * Created on 2/02/2010
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.telstra.olb.tegcbm.job.migration.validation.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

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
public class MigrationValidationDataProvider extends DefaultDataProvider {
	private static final String INPUT_ARG_COMPANY_LIST = "companyList";

	private static final String INPUT_ARG_COMPANY_SIZE = "companySize";

	private static final String INPUT_ARG_GET_COMPANIES_SEEDED = "seed";
	
	private int companySize;
	
	private int totalCompanies;
	
	private MigrationDAO migrationDao;
	
	/* (non-Javadoc)
	 * @see com.telstra.olb.tegcbm.job.core.DefaultDataProvider#processContext(com.telstra.olb.tegcbm.job.core.IContext)
	 */
	protected Collection processContext(IContext context) {
		String companyListStr = (String) context.getProperty(INPUT_ARG_COMPANY_LIST);
		String companySizeStr = (String) context.getProperty(INPUT_ARG_COMPANY_SIZE);
		if (companySizeStr != null) {
			setCompanySize(Integer.parseInt(companySizeStr));
		}
		if (companyListStr != null) {
			List companies = getCompanies(companyListStr);
			totalCompanies = companies.size();
			
			log.info("Number of companies to be validated : " + totalCompanies);
			
			return companies;
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see com.telstra.olb.tegcbm.job.core.DefaultDataProvider#doGetData()
	 */
	protected Collection doGetData() {
		return new ArrayList();
	}
	
	/**
	 * @param companyListStr
	 *            list of companies to be retrieved seperated by commas.
	 * @return list of companies to migrate.
	 */
	private List getCompanies(String companyListStr) {
		List companies = MigrationHelper.parse(companyListStr, ",");
		List companyList = new ArrayList();
		for (Iterator i = companies.iterator(); i.hasNext();) {
			String company = (String) i.next();
			if (INPUT_ARG_GET_COMPANIES_SEEDED.equalsIgnoreCase(company)) {
				companyList = findCompanies();
			} else {
				OLBCompanyMigration companyMigration = findCompany(company);
				if (companyMigration != null) {
					companyList.add(companyMigration);
				}
			}
		}
		return companyList;
	}

	/**
	 * @param company
	 * @return
	 */
	private OLBCompanyMigration findCompany(String company) {
		OLBCompanyMigration companyMigration = null;
		try {
			companyMigration = migrationDao.findCompany(company);
		} catch (MigrationDAOException e) {
			log.error("unable to retrieve company details for company: "
					+ company, e);
		}
		return companyMigration;
	}

	/**
	 * @return
	 */
	private List findCompanies() {
		List companyList = new ArrayList();
		try {
			companyList = migrationDao
					.findCompanies(ActivityStatus.STATUS_COMPLETE.getName());
			Random r = new Random();
			int seed = r.nextInt(companyList.size() - companySize);
			companyList = companyList.subList(seed, (seed + companySize));
		} catch (MigrationDAOException e) {
			log
					.error(
							"unable to retrieve company list for companies with completed migration status ",
							e);
		}
		return companyList;
	}
	
	/**
	 * @param seedNumber
	 *            The seedNumber to set.
	 */
	public void setCompanySize(int seedNumber) {
		this.companySize = seedNumber;
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
	 * @return Returns the totalCompanies.
	 */
	public int getTotalCompanies() {
		return totalCompanies;
	}
	/**
	 * @param totalCompanies The totalCompanies to set.
	 */
	public void setTotalCompanies(int totalCompanies) {
		this.totalCompanies = totalCompanies;
	}
	/**
	 * @return Returns the companySize.
	 */
	public int getCompanySize() {
		return companySize;
	}
}
