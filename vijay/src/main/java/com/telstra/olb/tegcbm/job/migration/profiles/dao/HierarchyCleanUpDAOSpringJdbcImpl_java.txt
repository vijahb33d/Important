/*
 * Created on Feb 3, 2010
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.telstra.olb.tegcbm.job.migration.profiles.dao;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.support.JdbcDaoSupport;

import com.telstra.olb.tegcbm.job.migration.pref.dao.CBMPreferencesRollbackDAOException;
import com.telstra.olb.tegcbm.job.migration.profiles.model.Hierarchy;
import com.telstra.olb.tegcbm.job.migration.profiles.model.HierarchyNode;
import com.telstra.olb.tegcbm.job.migration.profiles.model.HierarchyNodeUser;

/**
 * @author hariharan.venkat
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class HierarchyCleanUpDAOSpringJdbcImpl extends JdbcDaoSupport implements HierarchyCleanUpDAO {

    private static Log log = LogFactory.getLog(HierarchyCleanUpDAOSpringJdbcImpl.class);

    private static final String COL_ID = "id";

    private static final String COL_HTYPE = "htype";

    private static final String COL_NAME = "name";

    private static final String COL_HIERARCHY_ID = "hierarchyid";

    private static final String COL_NODE_ID = "nodeid";

    private static final String COL_USER_ID = "userid";

    private static final String SELECT_HIERARCHY_SQL = "SELECT  h.* FROM  edx_bsl_cmf_companyprofile cp,edx_bsl_cmf_company c,edx_hier_hierarchy h"
            + " WHERE  h.companyid = c.id AND h.deletedat = 0 AND c.profileid = cp.id AND cp.name = ?";

    private static final String SELECT_HIERARCHY_NODE_SQL_PREFIX = "SELECT  hn.* FROM edx_hier_hnode hn " + "WHERE hn.hierarchyid in (";

    private static final String DELETE_HIERARCHY_SQL_PREFIX = "UPDATE edx_hier_hierarchy h SET h.deletedat =? ,h.modifiedat=? WHERE h.id in (";

    private static final String DELETE_HIERARCHY_SQL_SUFFIX = ") AND h.deletedat = 0";

    private static final String DELETE_HIERARCHY_NODE_SQL_PREFIX = "UPDATE edx_hier_hnode hn SET hn.deletedat =?,hn.modifiedat=? WHERE hn.hierarchyid in (";

    private static final String HIERARCHY_NODE_SQL_SUFFIX = ") AND hn.deletedat = 0 ";

    private static final String SELECT_HIERARCHY_NODE_USER_SQL = "SELECT  nu.* FROM edx_hier_node_user nu WHERE nu.nodeid in (";

    private static final String DELETE_HIERARCHY_NODE_USER_SQL = "delete edx_hier_node_user WHERE nodeid in (";

    private static final String DELETE_HIERARCHY_USER_REF_SQL = "delete edx_hier_user_ref WHERE id in (";
    
    private static final String DELETE_ACCOUNT_COMPANY_MAP_SQL = "DELETE EDX_DBA.OLB_ACCOUNT_COMPANY_MAP WHERE COMPANY_ID = ?";

    private static final String DELIMITER = ",";

    /** 
     * Runs a query to obtain a list of hierarchies based on the segment and account
     * information from OLB and Company information from PDB.
     * @param cidn cidn
     * @return a list of hierarchies object.
     * @throws HierarchyCleanUpException is thrown if there are any 
     * @see com.telstra.olb.tegcbm.job.migration.populate.dao.HierarchyCleanUpDAO#getHierarchies(String cidn)
     */
    public List getHierarchies(final String cidn) throws HierarchyCleanUpException {
        String sql = SELECT_HIERARCHY_SQL; // + cidn + "'";
        List hierarchiesList = null;

        try {
            hierarchiesList = (List) getJdbcTemplate().query(sql, 
            												 new PreparedStatementSetter() {
																	public void setValues(PreparedStatement psRetrieveHierarchyInfo) throws SQLException {
																		psRetrieveHierarchyInfo.setString(1,cidn);
																	}
            												 } ,
															 new RowMapper() {
													                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
													                    long id = rs.getLong(COL_ID);
													                    long htype = rs.getLong(COL_HTYPE);
													                    String name = rs.getString(COL_NAME);
													                    return createHierarchy(id, htype, name);
													                }
												            });

            if (log.isDebugEnabled()) {
                log.debug("list of hierarchies for #" + hierarchiesList.size());
            }
        } catch (DataAccessException e) {
            throw new HierarchyCleanUpException("unable to get hierarchies", e);
        }

        return hierarchiesList != null ? hierarchiesList : new ArrayList();
    }

    /**
     * @param id
     * @param htype
     * @param name
     * @return
     */
    protected Object createHierarchy(long id, long htype, String name) {
        // TODO Auto-generated method stub
        return new Hierarchy(id, htype, name);
    }

    public List getHierarchyNodes(List hierarchyList) throws HierarchyCleanUpException {
        String parameters="";
        String sql = "";
        if(hierarchyList!=null){
         parameters = getQueryParameters(hierarchyList);
        }
        
        List hierarchyNodeList = null;
        if (parameters!=null && !parameters.equals("")) {
            sql = SELECT_HIERARCHY_NODE_SQL_PREFIX + parameters + HIERARCHY_NODE_SQL_SUFFIX;

            try {
                hierarchyNodeList = (List) getJdbcTemplate().query(sql, new RowMapper() {
                    public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                        long hid = rs.getLong(COL_HIERARCHY_ID);
                        long nodeid = rs.getLong(COL_NODE_ID);
                        return createHierarchyNode(hid, nodeid);
                    }
                });

                if (log.isDebugEnabled()) {
                    log.debug("list of hierarchies Nodes for #" + hierarchyNodeList.size());
                }
            } catch (DataAccessException e) {
                throw new HierarchyCleanUpException("unable to get hierarchies", e);
            }
        }

        return hierarchyNodeList != null ? hierarchyNodeList : new ArrayList();
    }

    public List getHierarchyNodeUser(List hierarchyNodesList) throws HierarchyCleanUpException {
        String sql="";
        List nodeUserList = null;
        if(hierarchyNodesList!=null && !hierarchyNodesList.isEmpty()){
         sql = getHierarchyNodeUserSQL(hierarchyNodesList);
        }
       
        
        try {
            if(sql!=null &&!sql.equals("")){
            nodeUserList = (List) getJdbcTemplate().query(sql, new RowMapper() {
                public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
                    long userId = rs.getLong(COL_USER_ID);
                    long nodeid = rs.getLong(COL_NODE_ID);
                    return createHierarchyNodeUser(nodeid, userId);
                }
            });
            }

            if (log.isDebugEnabled()) {
                log.debug("list of hierarchies Node User  for #" + ((nodeUserList != null) ? nodeUserList.size():0));
            }
        } catch (DataAccessException e) {
            throw new HierarchyCleanUpException("unable to get hierarchies Node User List", e);
        }
        
        return nodeUserList != null ? nodeUserList : new ArrayList();
    }

    /**
     * @param nodeid
     * @param userId
     * @return
     */
    protected Object createHierarchyNodeUser(long nodeid, long userId) {

        return new HierarchyNodeUser(nodeid, userId);
    }

    /**
     * @param hid
     * @param nodeid
     * @return
     */
    protected Object createHierarchyNode(long hid, long nodeid) {
        return new HierarchyNode(hid, nodeid);
    }

    /**
     * @param hierarchyList
     * @return
     */
    private String getHierarchyNodeUserSQL(List hierarchyNodeList) {
        log.debug("inside node user sql");
        String hierarchyNodeIdStr = "";
        boolean isFirst = true;
        if (hierarchyNodeList != null && !hierarchyNodeList.isEmpty()) {
            Iterator iter = hierarchyNodeList.iterator();
            while (iter.hasNext()) {
                HierarchyNode hierarchyNode = (HierarchyNode) iter.next();
                long hierarchyNodeId = hierarchyNode.getNodeId();
                if(isFirst) {
                    hierarchyNodeIdStr = "" + hierarchyNodeId;
                    isFirst = false;
                } else {
                    hierarchyNodeIdStr += DELIMITER + hierarchyNodeId ;
                }
                
            }

        }
        if (!hierarchyNodeIdStr.equals("")) {
            log.debug("inside node user sql" +hierarchyNodeIdStr);
            return SELECT_HIERARCHY_NODE_USER_SQL + hierarchyNodeIdStr + ")";
        }
        return null;
    }

    /**
     * @param hierarchyNodeList
     * @return
     */
    private String getHierarchyNodeUserDeleteSQL(List hierarchyNodeList) {
        String hierarchyNodeIdStr = "";
        boolean isFirst = true;
        if (hierarchyNodeList != null && !hierarchyNodeList.isEmpty()) {
            Iterator iter = hierarchyNodeList.iterator();
            while (iter.hasNext()) {
                HierarchyNode hierarchyNode = (HierarchyNode) iter.next();
                long hierarchyNodeId = hierarchyNode.getNodeId();
                if(isFirst) {
                    hierarchyNodeIdStr = "" + hierarchyNodeId;
                    isFirst = false;
                } else {
                    hierarchyNodeIdStr += DELIMITER + hierarchyNodeId ;
                }
                
            }

        }
        if (!hierarchyNodeIdStr.equals("")) {
            return DELETE_HIERARCHY_NODE_USER_SQL + hierarchyNodeIdStr + " )";
        }
        return null;
    }

    private String getQueryParameters(List hierarchyList) {
        String hierarchyIdStr = "";
        boolean isFirst = true;
        if (hierarchyList != null && !hierarchyList.isEmpty()) {
            Iterator iter = hierarchyList.iterator();
            while (iter.hasNext()) {
                Hierarchy hierarchy = (Hierarchy) iter.next();
                long hierarchyId = hierarchy.getId();
                if(isFirst) {
                    hierarchyIdStr = "" + hierarchyId;
                    isFirst = false;
                } else {
                    hierarchyIdStr += DELIMITER + hierarchyId ;
                }
            }
            return hierarchyIdStr;
        }

        return null;
    }

    /**
     * Removes the Hierarchies from the EDX CBM database.
     * 
     * @param hierarchyList list of hierarchy ids
     * @throws HierarchyDAOException thrown if there is problem working with EDX CBM datastore
     */
    public void deleteHierarchy(final List hierarchyList) throws HierarchyCleanUpException {
        String parameters="";
        if(hierarchyList!=null){
         parameters = getQueryParameters(hierarchyList);
        }
        String sql = "";
        if (parameters != null && !parameters.equals("")) {
            sql = DELETE_HIERARCHY_SQL_PREFIX + parameters + DELETE_HIERARCHY_SQL_SUFFIX;

            try {
                getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter() {

                    public void setValues(PreparedStatement deleteStat, int index) throws SQLException {
                        Hierarchy hierarchy = (Hierarchy) hierarchyList.get(index);
                        deleteStat.setLong(1, new Date().getTime());
                        deleteStat.setLong(2, new Date().getTime());

                    }

                    public int getBatchSize() {
                        return hierarchyList.size();
                    }
                });
            } catch (DataAccessException e) {
                throw new HierarchyCleanUpException("Unable to clean hierarchies.", e);
            }
        }
    }

    /**
     * Removes the Account company ehtries available from the OLB_ACCOUNT_COMPANY_MAP table.
     * @param cidn
     */
    public void deleteAccountCompanyEntriesForCIDN(final String cidn) throws HierarchyCleanUpException {
    	if (log.isDebugEnabled()) { log.debug("Remove records from cbm user preferences table for cidn:" + cidn);}
    	try {
			getJdbcTemplate().update(DELETE_ACCOUNT_COMPANY_MAP_SQL, new PreparedStatementSetter() {
	
				public void setValues(PreparedStatement deleteStat) throws SQLException {
		            deleteStat.setString(1, cidn);
				}
				
			});
    	} catch (DataAccessException e) {
            throw new HierarchyCleanUpException("Unable to clean Account company Map.", e);
        }
    }
    
    /**
     * Removes the Hierarchy Nodes from the EDX CBM database.
     * 
     * @param hierarchyList list of hierarchy ids
     * @throws HierarchyDAOException thrown if there is problem working with EDX CBM datastore
     */
    public void deleteHierarchyNodes(final List hierarchyList) throws HierarchyCleanUpException {
        String sql = "";
        String parameters="";
        if(hierarchyList!=null && !hierarchyList.isEmpty()){
         parameters = getQueryParameters(hierarchyList);
        }
        
        if (!parameters.equals("") && parameters!=null) {
            sql = DELETE_HIERARCHY_NODE_SQL_PREFIX + parameters + HIERARCHY_NODE_SQL_SUFFIX;

            try {
                getJdbcTemplate().batchUpdate(sql, new BatchPreparedStatementSetter() {

                    public void setValues(PreparedStatement deleteStat, int index) throws SQLException {
                        Hierarchy hierarchy = (Hierarchy) hierarchyList.get(index);
                        deleteStat.setLong(1, new Date().getTime());
                        deleteStat.setLong(2, new Date().getTime());

                    }

                    public int getBatchSize() {
                        return hierarchyList.size();
                    }
                });
            } catch (DataAccessException e) {
                throw new HierarchyCleanUpException("Unable to clean hierarchy Nodes.", e);
            }
        }
    }

    /**
     * Removes the Hierarchy Nodes from the EDX CBM database.
     * 
     * @param hierarchyList list of hierarchy ids
     * @throws HierarchyDAOException thrown if there is problem working with EDX CBM datastore
     */
    public void deleteHierarchyNodeUser(final List hierarchyNodeList) throws HierarchyCleanUpException {

        String sql ="";
        if(hierarchyNodeList!=null && !hierarchyNodeList.isEmpty()){
            sql=getHierarchyNodeUserDeleteSQL(hierarchyNodeList);
        }
        log.debug("deleteHierarchyNodeUser" +sql);

        try {
            if(sql != null && !sql.equals("") ){
            getJdbcTemplate().execute(sql);
            }
        } catch (DataAccessException e) {
            throw new HierarchyCleanUpException("Unable to clean hierarchy Nodes.", e);
        }

    }

    /**
     * Removes the Hierarchy Nodes from the EDX CBM database.
     * 
     * @param hierarchyList list of hierarchy ids
     * @throws HierarchyDAOException thrown if there is problem working with EDX CBM datastore
     */
    public void deleteHierarchyUserRef(final List nodeUserList) throws HierarchyCleanUpException {

        String sql = "";
        if(nodeUserList!=null && !nodeUserList.isEmpty()){
            sql=getHierarchyUserRefDeleteSQL(nodeUserList);
        }
        log.debug("deleteHierarchyUserRef" +sql);

        try {
            if(sql != null && !sql.equals("") ){
            getJdbcTemplate().execute(sql);
            }
        } catch (DataAccessException e) {
            throw new HierarchyCleanUpException("Unable to clean hierarchy Nodes.", e);
        }

    }

    /**
     * @param nodeUserList
     * @return
     */
    private String getHierarchyUserRefDeleteSQL(List nodeUserList) {
        String nodeUserIdStr = "";
        boolean isFirst = true;
        if (nodeUserList != null && !nodeUserList.isEmpty()) {
            Iterator iter = nodeUserList.iterator();
            while (iter.hasNext()) {
                HierarchyNodeUser hierarchyNodeUser = (HierarchyNodeUser) iter.next();
                long userId = hierarchyNodeUser.getUserId();
                if(isFirst) {
                    nodeUserIdStr = "" + userId;
                    isFirst = false;
                } else {
                    nodeUserIdStr += DELIMITER + userId ;
                }
                
            }

        }
        if (!nodeUserIdStr.equals("")) {
            return DELETE_HIERARCHY_USER_REF_SQL + nodeUserIdStr + " )";
        }
        return null;
    }

}
