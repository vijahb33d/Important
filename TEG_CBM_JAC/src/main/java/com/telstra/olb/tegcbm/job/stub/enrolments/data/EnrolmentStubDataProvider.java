/*
 * Created on 2/02/2010
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.telstra.olb.tegcbm.job.stub.enrolments.data;

import java.util.Collection;

import com.telstra.olb.tegcbm.job.core.DefaultDataProvider;
import com.telstra.olb.tegcbm.job.core.FileDataSource;
import com.telstra.olb.tegcbm.job.core.IContext;

/**
 * @author pavan.x.kuma
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class EnrolmentStubDataProvider extends DefaultDataProvider {
	private static final String REGEX_DELIM_PATTERN = "[,;]";
	public static final String STUB_FILES = "stubFiles";
	
	private FileDataSource dataSource;

	/* (non-Javadoc)
	 * @see com.telstra.olb.tegcbm.job.core.DefaultDataProvider#processContext(com.telstra.olb.tegcbm.job.core.IContext)
	 */
	protected Collection processContext(IContext context) {
		String files = (String) context.getProperty(STUB_FILES);
		if (files != null) {
			String[] filesArray = files.split(REGEX_DELIM_PATTERN);
			dataSource.setDataFiles(filesArray);
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see com.telstra.olb.tegcbm.job.core.DefaultDataProvider#doGetData()
	 */
	protected Collection doGetData() {
		return dataSource.getData();
	}
}
