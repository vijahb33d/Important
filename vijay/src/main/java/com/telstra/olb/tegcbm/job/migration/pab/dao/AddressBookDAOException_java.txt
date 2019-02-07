/*
 * AddressBookDaoException.java
 * Created on 20/07/2007 by pavan.x.kuma
 *
 */
package com.telstra.olb.tegcbm.job.migration.pab.dao;

/**
 * This class wraps any exception thrown as part of address book
 * data migration.
 */
public class AddressBookDAOException extends Exception {

    /**
     * Default constructor.
     */
    public AddressBookDAOException() {
        super();

    }

    /**
     * @param message exception message.
     */
    public AddressBookDAOException(String message) {
        super(message);

    }

    /**
     * @param message exception message.
     * @param cause exception cause.
     */
    public AddressBookDAOException(String message, Throwable cause) {
        super(message, cause);

    }

    /**
     * @param cause exception cause.
     */
    public AddressBookDAOException(Throwable cause) {
        super(cause);

    }

}
