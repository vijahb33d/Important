/*
 * OLBServiesDAO.java
 * Created on 16/07/2007 by pavan.x.kuma
 *
 */
package com.telstra.olb.tegcbm.job.migration.populate.dao;

import com.telstra.olb.tegcbm.job.migration.populate.model.MigrationType;
import com.telstra.olb.tegcbm.job.migration.populate.model.OwnCodes;

/**
 * Data Access Interface to get Own Codes for a Migration Type.
 */
public interface OlbDao {

    /**
     * Retrieves a list of own codes and its segments from the data store for given migration type. 
     * 
     * @param migrationType migration type (either ENTERPRISE, SME).
     * @return OwnCodes Wrapper object that contains own codes and its corresponding segments. 
     * @throws PopulateDataException is thrown if there is any issues with retrieval.
     */
    OwnCodes getOwnCodes(MigrationType migrationType) throws PopulateDataException;
}
