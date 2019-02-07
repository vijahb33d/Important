/*
 * Created on 28/11/2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.telstra.olb.tegcbm.job.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.Transformer;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author pavan.x.kuma
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class FileDataSource {	
	private static Log log = LogFactory.getLog(FileDataSource.class);
	
	private List dataFiles;
	
	private Transformer dataTransformer;

	/**
	 * @return Returns the dataFiles.
	 */
	public List getData() {
		List data = new ArrayList();
		if (dataFiles == null) {
			return data;
		}
		for (Iterator files = dataFiles.iterator(); files.hasNext();) {
			String fileName = (String) files.next();
			Object dataObject = dataTransformer != null ? dataTransformer.transform(fileName) : fileName;
			if (dataObject != null) {
				data.add(dataObject);
			} else {
				log.warn("unable to transform object from " + fileName);
			}
		}
		return data;
	}
	/**
	 * @param dataFiles The dataFiles to set.
	 */
	public void setDataFiles(String[] dataFiles) {
		this.dataFiles = Arrays.asList(dataFiles);
	}
	/**
	 * @return Returns the dataTransformer.
	 */
	public Transformer getDataTransformer() {
		return dataTransformer;
	}
	/**
	 * @param dataTransformer The dataTransformer to set.
	 */
	public void setDataTransformer(Transformer dataTransformer) {
		this.dataTransformer = dataTransformer;
	}
}
