/*
 * Created on 2/02/2010
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.telstra.olb.tegcbm.job.migration.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.telstra.olb.tegcbm.job.core.DefaultDataProvider;
import com.telstra.olb.tegcbm.job.core.IContext;
import com.telstra.olb.tegcbm.job.migration.populate.model.MigrationType;
import com.telstra.olb.tegcbm.job.migration.util.MigrationHelper;

/**
 * @author pavan.x.kuma
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class MigrationTypeDataProvider extends DefaultDataProvider {
	public static final String INPUT_ARG_CATEGORIES = "migrationType";
    
    private List migrationTypes;
    /**
     * checks for the categories argument in command line args, if exist, then a
     * list of those categories are returned for processing. The cmd line args
     * is specified as [categories=(list of categories seperated by commas)]
     * 
     * @param context
     *            command line arguments wrapper.
     * @return Iterator to list of categories.
     * @see com.telstra.olb.tegcbm.job.core.DefaultActivity#processInputArgs(com.telstra.olb.tegcbm.job.migration.MigrationArgs)
     */
	protected Collection processContext(IContext context) {
		String categories = (String) context.getProperty(INPUT_ARG_CATEGORIES);
		List migrationTypes = null;
        if (categories != null) {
        	migrationTypes = new ArrayList();
            Iterator types = MigrationHelper.parse(categories, ",").iterator();
            while (types.hasNext()) {
                String type = (String) types.next();
                MigrationType migrationType = MigrationType.getType(type.toUpperCase());
                migrationTypes.add(migrationType);
            }
        }
        return migrationTypes;
	}

	/**
     * returns the configured list of categories to be processed.
     * 
     * @return iterator to preconfigured list of catgories.
     * @see com.telstra.olb.tegcbm.job.core.DefaultActivity#doGetData(java.lang.Object)
     */
	protected Collection doGetData() {
		return migrationTypes != null ? migrationTypes : new ArrayList();
	}
    
    /**
     * @param macroSegments
     *            The categories to set.
     */
    public void setMigrationTypes(List macroSegments) {
        this.migrationTypes = macroSegments;
    }

}
