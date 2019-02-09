/*
 * Created on 2/02/2010
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.telstra.olb.tegcbm.job.migration.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.Closure;
import org.apache.commons.collections.CollectionUtils;

import com.telstra.olb.tegcbm.job.core.ActivityStatus;
import com.telstra.olb.tegcbm.job.core.DefaultDataProvider;
import com.telstra.olb.tegcbm.job.core.IContext;
import com.telstra.olb.tegcbm.job.migration.dao.MigrationDAO;
import com.telstra.olb.tegcbm.job.migration.dao.MigrationDAOException;
import com.telstra.olb.tegcbm.job.migration.util.MigrationHelper;

/**
 * @author pavan.x.kuma
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MigrationStatusDataProvider extends DefaultDataProvider {
	public static final String INPUT_ARG_PROCESS_STATUS = "status";
	
	private MigrationDAO migrationDao;
	
	private List processActivityStatus;
	
	private boolean returnInGroups = true;

	/* (non-Javadoc)
	 * @see com.telstra.olb.tegcbm.job.core.DefaultDataProvider#processContext(com.telstra.olb.tegcbm.job.core.IContext)
	 */
	protected Collection processContext(IContext context) {
		String processActivityStatus = (String) context.getProperty(INPUT_ARG_PROCESS_STATUS);
        if (processActivityStatus != null) {
            List status = MigrationHelper.parse(processActivityStatus, ",");
            setProcessActivityStatus(status);
        }
        return null;
	}

	/* (non-Javadoc)
	 * @see com.telstra.olb.tegcbm.job.core.DefaultDataProvider#doGetData()
	 */
	protected Collection doGetData() {
		if (getProcessActivityStatus() == null) {
			return new ArrayList();
		}
		Collection inputCollection = new ArrayList();
		for (Iterator i = getProcessActivityStatus().iterator(); i.hasNext();) {
			String status = (String) i.next();
			try {
				List companiesByStatus = migrationDao.findCompanies(status);
				if (returnInGroups) {
				    Map statusCompanyMap = new HashMap();
				    statusCompanyMap.put(status,  companiesByStatus);
					inputCollection.add(statusCompanyMap);
				} else {
					inputCollection.addAll(companiesByStatus);
				}
			} catch (MigrationDAOException e) {
				
			}
		}
		return inputCollection != null ? inputCollection : new ArrayList();
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
	 * @return Returns the processActivityStatus.
	 */
	public List getProcessActivityStatus() {
		return processActivityStatus;
	}
	/**
	 * @param processActivityStatus The processActivityStatus to set.
	 */
	public void setProcessActivityStatus(List processActivityStatus) {
	    this.processActivityStatus = new ArrayList();
        CollectionUtils.forAllDo(processActivityStatus, new Closure() {
            public void execute(Object processActivityStatus) {
                ActivityStatus activityStatus = ActivityStatus.getType((String) processActivityStatus);
                if (activityStatus != null) {
                    MigrationStatusDataProvider.this.processActivityStatus.add(activityStatus.getName());
                }
            }
        });
	}
	/**
	 * @return Returns the returnCollection.
	 */
	public boolean isReturnInGroups() {
		return returnInGroups;
	}
	/**
	 * @param returnCollection The returnCollection to set.
	 */
	public void setReturnInGroups(boolean returnCollection) {
		this.returnInGroups = returnCollection;
	}
}
