/*
 * CompositeActivity.java
 * Created on 18/07/2007 by pavan.x.kuma
 *
 */
package com.telstra.olb.tegcbm.job.core;

import java.util.List;

/**
 * Interface that extends Activity to allow child Activity objects.
 */
public interface CompositeActivity extends Activity {
    /**
     * @return a list of composed child activities.
     */
    List getChildActivities();
}
