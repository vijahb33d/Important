package com.telstra.olb.tegcbm.client;

import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * JobRunnerClient.java is the main entry point class for the CBM JAC which accepts
 * the job type and arguments used to commence the migration process.
 */
public final class JobRunnerClient {
    private static Log log = LogFactory.getLog(JobRunnerClient.class);

    /**
     * Reads the command line arguments. It returns an error status if either
     * no arguments are passed in or the first argument passed in is not a valid
     * job type.If the first argument is a valid job type it then kicks off the
     * bill extraction process.
     * 
     * On error this script will exit with the following statuses: 
     * <ul>
     * <li>0 - normal 
     * <li>1 - error with params 
     * <li>2 - error during execution
     * </ul>
     * 
     * @param args the arguments (job name)
     */
    public static void main(String[] args) {
        if (args.length == 0) {
            usage("Usage error - please provide a job name to execute the data migration client.");
            System.exit(ReturnStatus.INVALID_ARGS.getValue());
        }

        String jobName = args[0];
        if (JobType.getType(jobName) == null) {
            usage("Usage error - '" + jobName + "' is not a valid job name.");
            System.exit(ReturnStatus.INVALID_ARGS.getValue());
        }

        new JobRunnerClient(jobName, args);
    }

    /**
     * Prints out instructions on how to use the bill extractor from the unix
     * command line.
     * 
     * @param error
     *            explain why the client was unable to complete sucessfully
     */
    private static void usage(String error) {
        String usage = "\n" + error + "\n\n" + "JobRunnerClient is a CBM batch job runner\n"
                + "It does so according to the job type specified. \n\n"
                + "Usage: java -Djava.ext.dirs=[LIB] -jar -cp [PROPERTIES] [JAR FILE] [JOBTYPE] [arg2...argN]\n\n"
                + "JOBTYPE is the type of job to be executed (i.e. for migration: populate, migrate, rollback, validate).\n"
                + "arg2-argN are the optional arguments specific to the requriements of the job type implementation.\n"
                + "LIB is the path to the lib/ folder containing all the libary jars.\n\n"
                + "PROPERTIES is the path to the properties/ folder containing the properties file for job runner client.\n\n"
                + "A wrapper shell run script 'runClient.sh' can be found in the scripts folder which hides this complexity. ";
        
        log.error(usage);
        System.err.println(usage);
    }

    /**
     * Loads the spring context file specific to the job, before retrieving the 
     * appropriate manager bean which matches the job type property supplied. 
     * It then executes the manager process.
     * 
     * @param jobName
     *            the job to process
     * @param args
     *            Only if the job type is 'accountlist' then the DDN and list of
     *            accounts to be processed will be passed in as arguments
     */
    private JobRunnerClient(String jobName, String[] args) {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext(JobType.getType(jobName).getAppContextFilenames());

        // get the instance of manager from the context
        String managerName = "cbmjac." + jobName + ".manager";
        Manager manager = (Manager) applicationContext.getBean(managerName);
        if (log.isDebugEnabled()) { log.debug("calling manager: " + managerName + " [" + manager.getClass().getName() + "]"); }
        
        ReturnStatus status = manager.execute(Arrays.asList(args));
        if (status == ReturnStatus.INVALID_ARGS) {
            log.error("manager: " +  managerName + ", usage: " + manager.usage());
            System.err.println("manager: " +  managerName + ", usage: " + manager.usage());
        }
        System.exit(status.getValue());
    }
}
