package com.telstra.olb.tegcbm.job.migration.pref.dao;

/**
 * CBM Preferences Rollback DAO Exception.
 *
 */
public class CBMPreferencesRollbackDAOException extends Exception {

    /**
     * Default constructor.
     */
    public CBMPreferencesRollbackDAOException() {
        super();

    }

    /**
     * @param message exception message.
     */
    public CBMPreferencesRollbackDAOException(String message) {
        super(message);

    }

    /**
     * @param message exception message.
     * @param cause exception cause.
     */
    public CBMPreferencesRollbackDAOException(String message, Throwable cause) {
        super(message, cause);

    }

    /**
     * @param cause exception cause.
     */
    public CBMPreferencesRollbackDAOException(Throwable cause) {
        super(cause);

    }

}
