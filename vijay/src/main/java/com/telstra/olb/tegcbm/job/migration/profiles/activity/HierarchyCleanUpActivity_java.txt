/*
 * Created on 20/01/2010
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.telstra.olb.tegcbm.job.migration.profiles.activity;

import java.util.List;

import com.telstra.olb.tegcbm.job.core.ActivityException;
import com.telstra.olb.tegcbm.job.core.DefaultActivity;
import com.telstra.olb.tegcbm.job.core.IContext;
import com.telstra.olb.tegcbm.job.migration.model.OLBCompanyMigration;
import com.telstra.olb.tegcbm.job.migration.profiles.dao.HierarchyCleanUpDAO;
import com.telstra.olb.tegcbm.job.migration.profiles.dao.HierarchyCleanUpException;

/**
 * @author pavan.x.kuma
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class HierarchyCleanUpActivity extends DefaultActivity {

    /**
     * @return Returns the hierarchyCleanUpDao.
     */
    public HierarchyCleanUpDAO getHierarchyCleanUpDao() {
        return hierarchyCleanUpDao;
    }

    /**
     * @param hierarchyCleanUpDao
     *            The hierarchyCleanUpDao to set.
     */
    public void setHierarchyCleanUpDao(HierarchyCleanUpDAO hierarchyCleanUpDao) {
        this.hierarchyCleanUpDao = hierarchyCleanUpDao;
    }

    private HierarchyCleanUpDAO hierarchyCleanUpDao;

    /**
     * execute cleanup of hierarchies.
     * 
     * @param input
     *            activity input
     * @throws ActivityException
     *             is thrown if the profile cannot be migrated.
     * @see com.telstra.olb.tegcbm.job.core.Activity#execute(IContext)
     */
    protected void doExecute(Object input) throws ActivityException {

        OLBCompanyMigration current = (OLBCompanyMigration) input;
        String companyCode = current.getCompanyCode();
        List hierarchies = getHierarchies(companyCode);
        List hierarchyNodes = getHierarchyNodes(hierarchies);
        List nodeUserList = getHierarchyNodeUser(hierarchyNodes);
        if (log.isDebugEnabled()) {
	        log.debug("size of the node user list " + nodeUserList.size());
	        log.debug("size of the hierarchy node list " + hierarchyNodes.size());
	        log.debug("size of the hierarchy list " + hierarchies.size());
        }
        if (hierarchies == null) {
            log.info("no hierarchies found for the company: " + companyCode + " to clean");
        }
        try {
            
            hierarchyCleanUpDao.deleteHierarchyNodeUser(hierarchyNodes);
            hierarchyCleanUpDao.deleteHierarchyUserRef(nodeUserList);
            hierarchyCleanUpDao.deleteHierarchyNodes(hierarchies);
            hierarchyCleanUpDao.deleteHierarchy(hierarchies);
        } catch (HierarchyCleanUpException e) {
            throw new ActivityException("unable to delete hierarchhies for migration.", e);
        }

    }

    /**
     * @param hierarchyNodes
     * @return
     */
    private List getHierarchyNodeUser(List hierarchyNodes) throws ActivityException {
        try {
            List nodeUserList = hierarchyCleanUpDao.getHierarchyNodeUser(hierarchyNodes);
            if (log.isDebugEnabled()) { log.debug("Hierarchy Nodes Fetched : " + nodeUserList + " Size : " + nodeUserList.size());}
            return nodeUserList;

        } catch (HierarchyCleanUpException e) {
            throw new ActivityException("unable to get hierarchy Node Users for hierarchy");
        }
    }

    /**
     * Fetches the list of hierarchies for a company.
     * 
     * @param companyCode
     *            company code
     * @return collection of hierarchies to be cleanedUp.
     * @throws ActivityException
     *             is thrown if the hierarchies for the company cannot be
     *             retrieved.
     */
    private List getHierarchies(String companyCode) throws ActivityException {
        try {
            List hierarchies = hierarchyCleanUpDao.getHierarchies(companyCode);
            if (log.isDebugEnabled()) { log.debug("Hierarchies Fetched : " + hierarchies + " Size : " + hierarchies.size());}
            return hierarchies;

        } catch (HierarchyCleanUpException e) {
            throw new ActivityException("unable to get hierarchies for company: " + companyCode, e);
        }
    }

    /**
     * Fetches the list of hierarchy Nodes for a hierarchy.
     * 
     * @param list
     *            of Hierarchies company code
     * @return collection of hierarchy Nodes to be cleanedUp.
     * @throws ActivityException
     *             is thrown if the hierarchies nodes cannot be retrieved.
     */
    private List getHierarchyNodes(List hierarchiesList) throws ActivityException {
        try {
            List hierarchyNodeList = hierarchyCleanUpDao.getHierarchyNodes(hierarchiesList);
            if (log.isDebugEnabled()) { log.debug("Hierarchy Nodes Fetched : " + hierarchyNodeList + " Size : " + hierarchyNodeList.size());}
            return hierarchyNodeList;

        } catch (HierarchyCleanUpException e) {
            throw new ActivityException("unable to get hierarchy Nodes for hierarchy");
        }
    }

}
