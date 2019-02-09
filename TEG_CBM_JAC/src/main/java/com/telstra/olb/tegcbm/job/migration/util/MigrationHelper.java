/*
 * PopulateActivityHelper.java
 * Created on 15/08/2007 by pavan.x.kuma
 *
 */
package com.telstra.olb.tegcbm.job.migration.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 *  
 */

public final class MigrationHelper {
    private static Log log = LogFactory.getLog(MigrationHelper.class);
    /**
     * 
     *  
     */
    private MigrationHelper() {

    }

    /**
     * Creates an IN SQL query for the collection of values.
     *
     * @param sql sql query with in clause
     * @param inValues the values for in clause
     * @return SQL query.
     */
    public static String createInSQLQuery(String sql, Collection inValues) {
        if (log.isDebugEnabled()) { log.debug("creating an IN SQL query for " + inValues.size() + " values."); }
        StringBuffer inSql = new StringBuffer(sql);
        boolean first = true;
        inSql.append("(");
        for (Iterator i = inValues.iterator(); i.hasNext();) {
            if (!first) {
                inSql.append(",");
            }
	        inSql.append("'").append(i.next()).append("'");
            if (first) {
                first = false;
            }
        }
        inSql.append(")");
        return inSql.toString();
    }

    /**
     * Parses a delimiter seperated values into a list.
     * 
     * @param argument
     *            categories seperated by comma
     * @param delimiter
     *            delimiter
     * 
     * @return list of categories.
     */
    public static List parse(String argument, String delimiter) {
        List argumentList = new ArrayList();
        if (argument != null) {
            StringTokenizer tokenizer = new StringTokenizer(argument, delimiter);
            while (tokenizer.hasMoreTokens()) {
                argumentList.add(tokenizer.nextToken());
            }
        }
        return argumentList;
    }
}
