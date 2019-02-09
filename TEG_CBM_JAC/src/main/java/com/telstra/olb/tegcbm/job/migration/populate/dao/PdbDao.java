/*
 * PDBServicesDAO.java
 * Created on 16/07/2007 by pavan.x.kuma
 *
 */
package com.telstra.olb.tegcbm.job.migration.populate.dao;

import java.util.List;

import com.telstra.olb.tegcbm.job.migration.populate.model.OwnCodes;

/**
 * PdbDao object to retrieve the list of companies from PDB for the list of own codes
 * for a given macro segment.
 */
public interface PdbDao {

    /**
     * List of company codes for the given ownership codes.
     *
     * @param ownCodes map of own codes with their segments.
     * @return list of OLBCompanyMigration objects.
     * @throws PopulateDataException is thrown.
     */
    List getCompanies(OwnCodes ownCodes) throws PopulateDataException;

}
