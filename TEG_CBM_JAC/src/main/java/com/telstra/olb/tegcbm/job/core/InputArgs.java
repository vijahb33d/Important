package com.telstra.olb.tegcbm.job.core;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Input Args mamages all the command line parameters. Commandline args are typically of the form
 * jobname [key=value]. Only the above mentioned format is supported. This class parses all the key value pairs
 * and provide methods to access the values based on keys.
 */
public class InputArgs implements IContext {
    private Map argsMap = new HashMap();
    private List args;
    /**
     * 
     * @param args command line migration args
     */
    public InputArgs(List args) {
        this.args = args;
        parseArgs(args);
    }
    
    /**
     * parses the list of args into a map of name value pairs.
     *
     * @param args list of args.
     */
    private void parseArgs(List args) {
        if (args != null) {
            Iterator iterator = args.iterator();
            while (iterator.hasNext()) {
                String argument = (String) iterator.next();
                parseArgument(argument);
            }
        }
    }

    /**
     * puts the args in the argsmap as name value pair. This method ignores all the standalone args. 
     *
     * @param argument in name=value format.
     */
    private void parseArgument(String argument) {
        StringTokenizer tokenizer = new StringTokenizer(argument, "=");
        String argumentKey = null;
        if (tokenizer.hasMoreTokens()) {
            argumentKey = tokenizer.nextToken();
        }
        String argumentValue = null;
        if (tokenizer.hasMoreTokens()) {
            argumentValue = tokenizer.nextToken();
        }
        if ((argumentKey!= null) && (argumentValue != null)) {
            argsMap.put(argumentKey, argumentValue);
        }
    }

    /**
     * @return Returns the args.
     */
    public List getArgs() {
        return args;
    }
    /**
     * gets the value of argument. 
     *
     * @param name name of the argument.
     * @return value of the argument.
     */
    public String getArg(String name) {
        return (String) argsMap.get(name);
    }
    /**
     * @param args The args to set.
     */
    public void setArgs(List args) {
        this.args = args;
    }
    /**
     * 
     * @return list of available arguments passed in from command line.
     */
    public Set getArgNames() {
    	return argsMap.keySet();
    }

	/* (non-Javadoc)
	 * @see com.telstra.olb.tegcbm.job.core.IContext#getInput()
	 */
	public Object getInput() {
		return null;
	}

	/* (non-Javadoc)
	 * @see com.telstra.olb.tegcbm.job.core.IContext#getProperty(java.lang.String)
	 */
	public Object getProperty(String name) {
		return getArg(name);
	}

	/* (non-Javadoc)
	 * @see com.telstra.olb.tegcbm.job.core.IContext#getPropertyNames()
	 */
	public Set getPropertyNames() {
		return getArgNames();
	}
}
