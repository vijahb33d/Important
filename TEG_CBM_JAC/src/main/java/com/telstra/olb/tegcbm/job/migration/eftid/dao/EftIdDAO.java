package com.telstra.olb.tegcbm.job.migration.eftid.dao;

/**
 * DAO Interface to update the EFT ID table.
 * 
 */
public interface EftIdDAO {

    /**
     * Updates the eft id for a company to corporate application.
     * @param companyCode company
     * @throws EftIdDAOException is thrown if the update cannot be performed.
     **/
    void updateApplicationId(String companyCode) throws EftIdDAOException;
}
