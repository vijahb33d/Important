/*
 * ActivityStatusListener.java
 * Created on 12/07/2007 by pavan.x.kuma
 *
 */
package com.telstra.olb.tegcbm.job.core;

/**
 * Activity Status Listener interface, which handles <code>ActivityStatusEvent</code> events.
 */
public interface ActivityStatusListener {
    /**
     * Activity status event handler.
     *
     * @param event acticity status event.
     */
    void handleActivityStatus(ActivityStatusEvent event);

}
