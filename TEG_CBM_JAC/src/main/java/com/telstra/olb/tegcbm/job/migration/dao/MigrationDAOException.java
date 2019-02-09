/*
 * MigrationDaoException.java
 * Created on 18/07/2007 by pavan.x.kuma
 *
 */
package com.telstra.olb.tegcbm.job.migration.dao;

/**
 * Migration DAO Exception.
 * This exception is thrown if there is any problem accessing
 * the data migration data store.
 * 
 */
public class MigrationDAOException extends Exception {

    /**
     * 
     */
    public MigrationDAOException() {
        super();

    }

    /**
     * @param message exception message.
     */
    public MigrationDAOException(String message) {
        super(message);

    }

    /**
     * @param message exception message.
     * @param cause exception cause.
     */
    public MigrationDAOException(String message, Throwable cause) {
        super(message, cause);

    }

    /**
     * @param cause exception cause.
     */
    public MigrationDAOException(Throwable cause) {
        super(cause);

    }

}
