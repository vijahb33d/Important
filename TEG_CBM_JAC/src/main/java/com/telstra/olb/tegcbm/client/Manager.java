package com.telstra.olb.tegcbm.client;

import java.util.List;

/**
 * @author daniel.fajerman
 *
 */
public interface Manager {
    /**
     * exceutes the manager implementation.
     * @param args input args
     * @return 0 for success, 1 for failure and 2 for invalid args.
     */
    ReturnStatus execute(List args);
    
    /**
     * Usage Message specific to the implementation.
     * @return String usage message
     */
    String usage();
}
