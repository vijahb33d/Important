package com.telstra.olb.tegcbm.job.migration.profiles.dao;

/**
 * PDB Profiles DAO Exception.
 */
public class CBMProfilesDAOException extends Exception {

    /**
     * 
     */
    public CBMProfilesDAOException() {
        super();

    }
    /**
     * @param message exception message.
     */
    public CBMProfilesDAOException(String message) {
        super(message);

    }
    /**
     * @param message exception message.
     * @param cause exception cause.
     */
    public CBMProfilesDAOException(String message, Throwable cause) {
        super(message, cause);

    }
    /**
     * @param cause exception cause.
     */
    public CBMProfilesDAOException(Throwable cause) {
        super(cause);

    }
}
