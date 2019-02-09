package com.telstra.olb.tegcbm.job.migration.profiles.dao;

/**
 * PDB Profiles DAO Exception.
 */
public class PDBProfilesDAOException extends Exception {

    /**
     * 
     */
    public PDBProfilesDAOException() {
        super();

    }
    /**
     * @param message exception message.
     */
    public PDBProfilesDAOException(String message) {
        super(message);

    }
    /**
     * @param message exception message.
     * @param cause exception cause.
     */
    public PDBProfilesDAOException(String message, Throwable cause) {
        super(message, cause);

    }
    /**
     * @param cause exception cause.
     */
    public PDBProfilesDAOException(Throwable cause) {
        super(cause);

    }
}
