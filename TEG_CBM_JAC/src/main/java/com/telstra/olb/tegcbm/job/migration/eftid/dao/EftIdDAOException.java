/*
 * EftIdDAOException.java
 * Created on 20/07/2007 by pavan.x.kuma
 *
 */
package com.telstra.olb.tegcbm.job.migration.eftid.dao;


/**
 * EftId DAO Exception.
 * This exception is thrown if there is any problem accessing
 * the olb data store while performing updates to appid for a company.
 */
public class EftIdDAOException extends Exception {

    /**
     * 
     */
    public EftIdDAOException() {
        super();

    }

    /**
     * @param message exception message.
     */
    public EftIdDAOException(String message) {
        super(message);

    }

    /**
     * @param message exception message.
     * @param cause exception cause.
     */
    public EftIdDAOException(String message, Throwable cause) {
        super(message, cause);

    }

    /**
     * @param cause exception cause.
     */
    public EftIdDAOException(Throwable cause) {
        super(cause);

    }

}
