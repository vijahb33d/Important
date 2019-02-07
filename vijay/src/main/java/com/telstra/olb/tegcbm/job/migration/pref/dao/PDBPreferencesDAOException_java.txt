package com.telstra.olb.tegcbm.job.migration.pref.dao;

/**
 * PDB Preferences DAO Exception.
 * 
 * @author d274681
 *
 */
public class PDBPreferencesDAOException extends Exception {

    /**
     * Default constructor.
     */
    public PDBPreferencesDAOException() {
        super();
    }

    /**
     * @param message exception message.
     */
    public PDBPreferencesDAOException(String message) {
        super(message);

    }

    /**
     * @param message exception message.
     * @param cause exception cause.
     */
    public PDBPreferencesDAOException(String message, Throwable cause) {
        super(message, cause);

    }

    /**
     * @param cause exception cause.
     */
    public PDBPreferencesDAOException(Throwable cause) {
        super(cause);

    }


}
