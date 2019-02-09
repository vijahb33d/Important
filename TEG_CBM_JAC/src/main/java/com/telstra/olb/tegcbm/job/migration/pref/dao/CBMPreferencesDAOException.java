package com.telstra.olb.tegcbm.job.migration.pref.dao;

/**
 * CBM Preferences DAO Exception.
 *
 */
public class CBMPreferencesDAOException extends Exception {

    /**
     * Default constructor.
     */
    public CBMPreferencesDAOException() {
        super();

    }

    /**
     * @param message exception message.
     */
    public CBMPreferencesDAOException(String message) {
        super(message);

    }

    /**
     * @param message exception message.
     * @param cause exception cause.
     */
    public CBMPreferencesDAOException(String message, Throwable cause) {
        super(message, cause);

    }

    /**
     * @param cause exception cause.
     */
    public CBMPreferencesDAOException(Throwable cause) {
        super(cause);

    }

}
