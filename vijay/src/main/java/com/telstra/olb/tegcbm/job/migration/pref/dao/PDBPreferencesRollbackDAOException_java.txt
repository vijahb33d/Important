package com.telstra.olb.tegcbm.job.migration.pref.dao;

/**
 * PDB Preferences Rollback DAO Exception.
 *
 * @author d274681
 */
public class PDBPreferencesRollbackDAOException extends Exception {

    /**
     * Default constructor.
     */
    public PDBPreferencesRollbackDAOException() {
        super();

    }

    /**
     * @param message exception message.
     */
    public PDBPreferencesRollbackDAOException(String message) {
        super(message);

    }

    /**
     * @param message exception message.
     * @param cause exception cause.
     */
    public PDBPreferencesRollbackDAOException(String message, Throwable cause) {
        super(message, cause);

    }

    /**
     * @param cause exception cause.
     */
    public PDBPreferencesRollbackDAOException(Throwable cause) {
        super(cause);

    }


}
