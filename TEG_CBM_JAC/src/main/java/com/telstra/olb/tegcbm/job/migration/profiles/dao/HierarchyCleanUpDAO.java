/*
 * Created on Feb 3, 2010
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.telstra.olb.tegcbm.job.migration.profiles.dao;

import java.util.List;

/**
 * @author hariharan.venkat
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public interface HierarchyCleanUpDAO {

    /**
     * Finds the hierarchies for the passed cidn in the pdb database and returns as a List.
     *  
     * @param cidn Company cidn to search hierarchy for
     * @return List collection of hierarchies
     * @throws HierarchyCleanUpException is thrown if the hierarchies cannot be retrieved.
     */

    public List getHierarchies(String cidn) throws HierarchyCleanUpException;

    /**
     * Finds the hierarchies Nodes for the list of Hierarchies in the pdb database and returns as a List.
     *  
     * @param Hierarchy list to search hierarchy Node for
     * @return List collection of hierarchy Nodes
     * @throws HierarchyCleanUpException is thrown if the hierarchies cannot be retrieved.
     */

    public List getHierarchyNodes(final List hierarchyList) throws HierarchyCleanUpException;

    /**
     * Finds the Node User for the list of Hierarchy Nodes in the pdb database and returns as a List.
     *  
     * @param Hierarchy Node list to search hierarchy Node for
     * @return List collection of Node User
     * @throws HierarchyCleanUpException is thrown if the hierarchies cannot be retrieved.
     */

    public List getHierarchyNodeUser(final List hierarchyNodesList) throws HierarchyCleanUpException;

    /**
     * Removes the Hierarchies from the EDX CBM database.
     * 
     * @param hierarchyList list of hierarchy ids
     * @throws HierarchyDAOException thrown if there is problem working with EDX CBM datastore
     */
    public void deleteHierarchy(final List hierarchyList) throws HierarchyCleanUpException;

    /**
     * Removes the Hierarchy Nodes from the EDX CBM database.
     * 
     * @param hierarchyList list of hierarchy ids
     * @throws HierarchyDAOException thrown if there is problem working with EDX CBM datastore
     */
    public void deleteHierarchyNodes(final List hierarchyList) throws HierarchyCleanUpException;

    /**
     * Removes the Hierarchy Nodes user from the EDX CBM database.
     * 
     * @param hierarchyList list of hierarchy ids
     * @throws HierarchyDAOException thrown if there is problem working with EDX CBM datastore
     */
    public void deleteHierarchyNodeUser(final List hierarchyNodeList) throws HierarchyCleanUpException;

    /**
     * Removes the Hierarchy Nodes from the EDX CBM database.
     * 
     * @param hierarchyList list of hierarchy ids
     * @throws HierarchyDAOException thrown if there is problem working with EDX CBM datastore
     */
    public void deleteHierarchyUserRef(final List nodeUserList) throws HierarchyCleanUpException;
    
    /**
     * Removes the Account company ehtries available from the OLB_ACCOUNT_COMPANY_MAP table.
     * @param cidn
     */
    public void deleteAccountCompanyEntriesForCIDN(final String cidn) throws HierarchyCleanUpException ;

}
