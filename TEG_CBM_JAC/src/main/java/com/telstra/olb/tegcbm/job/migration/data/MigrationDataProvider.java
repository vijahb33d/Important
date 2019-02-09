/*
 * Created on 29/01/2010
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.telstra.olb.tegcbm.job.migration.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.Closure;
import org.apache.commons.collections.CollectionUtils;

import com.telstra.olb.tegcbm.job.core.ActivityStatus;
import com.telstra.olb.tegcbm.job.core.DefaultDataProvider;
import com.telstra.olb.tegcbm.job.core.IContext;
import com.telstra.olb.tegcbm.job.migration.dao.MigrationDAO;
import com.telstra.olb.tegcbm.job.migration.dao.MigrationDAOException;
import com.telstra.olb.tegcbm.job.migration.model.OLBCompanyMigration;
import com.telstra.olb.tegcbm.job.migration.populate.model.MigrationType;
import com.telstra.olb.tegcbm.job.migration.util.MigrationHelper;

/**
 * @author pavan.x.kuma
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MigrationDataProvider extends DefaultDataProvider {
	public static final String INPUT_ARG_COMPANY_LIST = "companyList";
    public static final String INPUT_ARG_PROCESS_STATUS = "status";
    public static final String INPUT_ARG_MIGRATION_SIZE = "migrationSize";
    public static final String INPUT_ARG_SEGMENT = "segment";
    
    private MigrationDAO migrationDao;
    
    private List processActivityStatus = new ArrayList();
    private int migrationSize = 0;
    private List segments;
    
	/**
     * Input is from command line arguments. Process command line arguments.
     * 
     * @param context
     *            command line arguments.
     * @return data iterator.
     */
    protected Collection processContext(IContext context) {
        List companies = processInputArgCompanyList(context);
        if (companies != null) {
            return companies;
        }
        processInputArgStatus(context);
        processInputArgMigrationSize(context);
        processInputArgSegment(context);
        return null;
    }

	/**
	 * gets the list of companies specified by the input parameter companyList.
	 * @return list of companies to migrate or null if  parameter companyList is not specified.
     * @param context input arguments.
     */
    private List processInputArgCompanyList(IContext context) {
        String companyListStr = (String) context.getProperty(INPUT_ARG_COMPANY_LIST);
        if (companyListStr != null) {
            List companies = getCompanies(companyListStr);
            if (log.isDebugEnabled()) {
            	log.debug("migrating companies: " + companies.size());
            }
            return companies;
        }
        return null;
    }
    
    /**
     * @param companyListStr list of companies to be retrieved seperated by commas.
     * @return list of companies to migrate.
     */
    private List getCompanies(String companyListStr) {
        List companies = MigrationHelper.parse(companyListStr, ",");
        List companyList = new ArrayList();
        for(Iterator i = companies.iterator(); i.hasNext();) {
            String company = (String) i.next();
            try {
                OLBCompanyMigration companyMigration = migrationDao.findCompany(company);
                int migrationStatus = companyMigration.getActivityStatus();
                if ((migrationStatus != ActivityStatus.STATUS_COMPLETE.getValue())
                        && (migrationStatus != ActivityStatus.STATUS_PROCESSING.getValue())) {
                    companyList.add(companyMigration);
                }
            } catch (MigrationDAOException e) {
                log.error("unable to retrieve company details for company: " + company, e);
            }
        }
        return companyList;
    }
    /**
     * sets the company retrieval filter as the one based on activity status. this is specified by the input parameter status.
     * @param context input arguments.
     */
    private void processInputArgStatus(IContext context) {
        String processActivityStatus = (String) context.getProperty(INPUT_ARG_PROCESS_STATUS);
        if (processActivityStatus != null) {
            List status = MigrationHelper.parse(processActivityStatus, ",");
            setProcessActivityStatus(status);
        }
    }
    /**
     * sets the max companies to be migrated by this execution. This is specified by the input parameter migration size.
     * @param context input arguments.
     */
    private void processInputArgMigrationSize(IContext context) {
        String noOfCompanies = (String) context.getProperty(INPUT_ARG_MIGRATION_SIZE);
        try {
            setMigrationSize(Integer.parseInt(noOfCompanies));
        } catch (NumberFormatException e) {
            if (log.isDebugEnabled()) {
                log.debug("unable to set the migration size. please check the property.");
            }
        }
    }
    /**
     * sets the company retrieval filter as the one based on segment type. this is specified by the input parameter segment.
     * @param context input arguments.
     */
    private void processInputArgSegment(IContext context) {
        String segment = (String) context.getProperty(INPUT_ARG_SEGMENT);
        if (segment!= null) {
            setSegment(segment.toUpperCase());
        } else {
            if (log.isDebugEnabled()) {
                log.debug("invalid segment. segment value should be either SME or ENTERPRISE.");
            }
        }
    }
    
    /**
     * @param status processActivityStatus to set.
     */
    public void setProcessActivityStatus(List status) {
        if (status != null) {
            CollectionUtils.forAllDo(status, new Closure() {
                public void execute(Object processActivityStatus) {
                    ActivityStatus activityStatus = ActivityStatus.getType((String) processActivityStatus);
                    if (activityStatus != null) {
                        MigrationDataProvider.this.processActivityStatus.add(activityStatus.getName());
                    }
                }
            });
        }
    }
    
    /**
     * @param migrationSize migrationSize to set.
     */
    public void setMigrationSize(int migrationSize) {
        this.migrationSize = migrationSize;
    }
    
    /**
     * @param segment list of segments to set.
     */
    public void setSegment(String segment) {
        MigrationType type = MigrationType.getType(segment);
        if (type != null) {
            this.segments = type.getSegments();
        }
    }
    
    /**
	 * gets the list of companies, that needs to be enrolled.
	 * @return iterator to the list of companies.
	 * @see com.telstra.olb.tegcbm.job.core.DefaultActivity#doGetData()
	 */
	protected Collection doGetData() {
        List companyList = null;
        try {
            if (segments != null) {
                companyList = migrationDao.findCompaniesBySegment(segments, processActivityStatus);
            } else if (!processActivityStatus.isEmpty()) {
                companyList = migrationDao.findCompanies(processActivityStatus);
            } else {
                companyList = migrationDao.findCompaniesToMigrate();
            }
        } catch (MigrationDAOException e) {
            log.error(e.getMessage(), e);
            companyList = new ArrayList();
        }
        return migrationSize > 0 ? companyList.subList(0, Math.min(migrationSize, companyList.size())) : companyList;
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
}
