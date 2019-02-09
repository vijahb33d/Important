package com.telstra.olb.tegcbm.client;

import java.io.ObjectStreamException;
import java.util.HashMap;
import java.util.Map;

import com.telstra.olb.types.AbstractEnum;

/**
 * Defines the types of jobs available to run and associated application
 * contexts.
 * 
 * @author daniel.fajerman
 */
public final class JobType extends AbstractEnum {

    private static final String CLASSPATH_PREFIX = "com/telstra/olb/tegcbm/job/migration/config/";
    private static final String DL_CLASSPATH_PREFIX = "com/telstra/olb/tegcbm/job/bulkdownload/config/";
    private static final String PAN_CLASSPATH_PREFIX = "com/telstra/olb/tegcbm/job/migration/panconversion/config/";
    private static final String SALMAT_CLASSPATH_PREFIX = "com/telstra/olb/tegcbm/job/salmat/config/"; 
    private static final String STUBCONFIG_PREFIX = "com/telstra/olb/tegcbm/job/stub/config/";
    // Core Configurations
    private static final String APP_CONTEXT_PARENT         = CLASSPATH_PREFIX + "teg-cbm-jac-applicationContext.xml";
    private static final String APP_CONTEXT_MIGRATION_CORE = CLASSPATH_PREFIX + "companyMigrationContext.xml";
    private static final String APP_CONTEXT_POPULATE       = CLASSPATH_PREFIX + "teg-cbm-jac-populateDataContext.xml";
    //  Standard Jobs - Job Specific Configurations
    private static final String APP_CONTEXT_MIGRATION      = CLASSPATH_PREFIX + "teg-cbm-jac-migrationContext.xml";
    private static final String APP_CONTEXT_ROLLBACK       = CLASSPATH_PREFIX + "teg-cbm-jac-rollbackContext.xml";
    private static final String APP_CONTEXT_TBU_MIGRATION  = CLASSPATH_PREFIX + "teg-cbm-jac-tbuMigrateContext.xml";
    private static final String APP_CONTEXT_TBU_ENROL  = CLASSPATH_PREFIX + "teg-cbm-jac-tbuEnrolContext.xml";
    //  Job Types for Test Enrolment - Job Specific Configurations
    private static final String APP_CONTEXT_ENROL 	       = CLASSPATH_PREFIX + "teg-cbm-jac-enrolActivityContext.xml";
    private static final String APP_CONTEXT_STUB_ENROL	   = STUBCONFIG_PREFIX + "teg-cbm-jac-enrolmentStubContext.xml";
    //  JobType added for validating migration runs - Job Specific Configurations
    private static final String APP_CONTEXT_VALIDATE 	   = CLASSPATH_PREFIX + "teg-cbm-jac-validationContext.xml";
    //  JobType added for bulk download purge
    private static final String APP_CONTEXT_BULK_DL_PURGE  = DL_CLASSPATH_PREFIX + "teg-cbm-jac-bulkDownloadPurgeContext.xml";
    // Job type added for PAN Conversion migration.
    private static final String APP_CONTEXT_PANCONVERSION = PAN_CLASSPATH_PREFIX + "teg-cbm-jac-panConversionContext.xml";
    
    private static final String APP_CONTEXT_SALMAT = SALMAT_CLASSPATH_PREFIX + "salmatAppContext.xml";
    
    // Standard Jobs
    public static final JobType POPULATE = new JobType("populate", new String[] { APP_CONTEXT_PARENT, APP_CONTEXT_MIGRATION_CORE, APP_CONTEXT_POPULATE });
    public static final JobType MIGRATION = new JobType("migrate", new String[] { APP_CONTEXT_PARENT, APP_CONTEXT_MIGRATION_CORE, APP_CONTEXT_MIGRATION });
    public static final JobType ROLLBACK = new JobType("rollback", new String[] { APP_CONTEXT_PARENT, APP_CONTEXT_MIGRATION_CORE, APP_CONTEXT_ROLLBACK });
    public static final JobType TBU_MIGRATE  = new JobType("populatePreferences", new String[] { APP_CONTEXT_PARENT, APP_CONTEXT_MIGRATION_CORE, APP_CONTEXT_TBU_MIGRATION });
    public static final JobType TBU_ENROL  = new JobType("enrolTBU", new String[] { APP_CONTEXT_PARENT, APP_CONTEXT_MIGRATION_CORE, APP_CONTEXT_TBU_ENROL });
    public static final JobType TBU_ENROL_REPORT  = new JobType("enrolTBUReport", new String[] { APP_CONTEXT_PARENT, APP_CONTEXT_MIGRATION_CORE, APP_CONTEXT_TBU_ENROL });
    // Job Types for Test Enrolment
    public static final JobType CBA_ENROL = new JobType("cbaEnrol", new String[] { APP_CONTEXT_PARENT, APP_CONTEXT_MIGRATION_CORE, APP_CONTEXT_ENROL });
    public static final JobType CBM_ENROL = new JobType("cbmEnrol", new String[] { APP_CONTEXT_PARENT, APP_CONTEXT_MIGRATION_CORE, APP_CONTEXT_ENROL });
    public static final JobType STUB_ENROL = new JobType("stubEnrol", new String[] {APP_CONTEXT_STUB_ENROL});
    // JobType added for validating migration runs.
    public static final JobType VALIDATE  = new JobType("validate", new String[] { APP_CONTEXT_PARENT, APP_CONTEXT_MIGRATION_CORE, APP_CONTEXT_VALIDATE });
    //  JobType added for bulk download purge
    public static final JobType BULK_DOWNLOAD_PURGE  = new JobType("bulkdownloadpurge", new String[] { APP_CONTEXT_BULK_DL_PURGE });
    // Job type added for PAN Conversion migration.
    public static final JobType TRANSFER_CPAN = new JobType("transferCPAN", new String[] {APP_CONTEXT_PARENT, APP_CONTEXT_PANCONVERSION});
    public static final JobType RETRIEVE_TPAN = new JobType("retrieveTPAN", new String[] {APP_CONTEXT_PARENT, APP_CONTEXT_PANCONVERSION});
    // Job type added for downloading detailed pdfs from salmat    
    public static final JobType SALMAT_CLIENT = new JobType("salmatClient", new String[] {APP_CONTEXT_PARENT, APP_CONTEXT_SALMAT});
    
    private static int nextOrdinal = 0;

    private static final AbstractEnum[] TYPES = { POPULATE, 
    											MIGRATION, 
												ROLLBACK, 
												CBM_ENROL,  
												CBA_ENROL, 
												VALIDATE, 
												STUB_ENROL, 
												TBU_MIGRATE, 
												TBU_ENROL, 
												TBU_ENROL_REPORT,
												BULK_DOWNLOAD_PURGE,
												TRANSFER_CPAN,
												RETRIEVE_TPAN,
												SALMAT_CLIENT};

    private static Map nameMap = new HashMap();

    private static Map valueMap = new HashMap();

    private String[] appContextFilenames;

    /**
     * Constructor which initialises attributes.
     * 
     * @param name
     *            of type
     * @param appContextFilenames
     *            application context names
     */
    private JobType(String name, String[] appContextFilenames) {
        super(name, nextOrdinal++);
        this.appContextFilenames = appContextFilenames;
    }

    /**
     * get type from name.
     * 
     * @param name
     *            to get
     * @return instance of BillfileFormatType
     */
    public static JobType getType(String name) {
        if (nameMap.isEmpty()) {
            map(TYPES, nameMap, valueMap);
        }
        return (JobType) getType(nameMap, name);
    }

    /**
     * get type by value.
     * 
     * @param value
     *            to get
     * @return instance of BillfileFormatType
     */
    public static JobType getType(int value) {
        if (nameMap.isEmpty()) {
            map(TYPES, nameMap, valueMap);
        }
        return (JobType) getType(valueMap, value);
    }

    /**
     * Allows this class to resolve the object read from the stream before it is
     * returned to the caller.
     * 
     * @return instance of type
     * @throws ObjectStreamException -
     */
    private Object readResolve() throws ObjectStreamException {
        return TYPES[getValue()];
    }

    /**
     * Method returns the application context. *
     * 
     * @return Returns the appContextFilenames.
     */
    public String[] getAppContextFilenames() {
        return appContextFilenames;
    }
}
