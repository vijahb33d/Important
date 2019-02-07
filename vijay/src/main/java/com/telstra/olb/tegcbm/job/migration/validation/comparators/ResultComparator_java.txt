/*
 * Created on 26/11/2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.telstra.olb.tegcbm.job.migration.validation.comparators;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author mahesh.prabhu
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class ResultComparator implements Comparator {
	
	 protected transient Log log = LogFactory.getLog(getClass());

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Object o1, Object o2) {
		ArrayList list1;
		ArrayList list2;
		if(o1 instanceof ArrayList && o2 instanceof ArrayList) {
			list1 = (ArrayList) o1;
			list2 = (ArrayList) o2;
			
			Iterator i1 = list1.iterator();
			Iterator i2 = list2.iterator();
			
			StringBuffer s1 = new StringBuffer();
			while(i1.hasNext()){
			 s1.append((String)i1.next());
			}
			StringBuffer s2 = new StringBuffer();
			while(i2.hasNext()){
			 s2.append((String)i2.next());
			}
			
			log.debug("HashCode of first comparable result  : " + s1.toString().hashCode());
			log.debug("HashCode of second comparable result : " + s2.toString().hashCode());
						
			if(s1.toString().hashCode() == s2.toString().hashCode()) {
				return 0;
			} else {
				return -1;
			}
		
		} else {
			log.error("Objects passed not instances of ArrayList");
		}
		return -1;
	}

	/**
	 * @param result
	 */
	public boolean compareResultSets(String companyCode, Object[] result) {
		boolean matched = false;
		log.debug("Comparing the objects for Company with company code : " + companyCode);
		int match = compare(result[0], result[1]);
		
		switch (match) {
		case 0:
			log.debug("Objects for Company with companyCode : " +  companyCode + " matched.");
			matched = true;
			break;
		case 1:
			log.debug("Objects for Company with companyCode : " +  companyCode + " DID NOT match.");
			break;
		case -1:
			log.debug("Objects for Company with companyCode : " +  companyCode + " DID NOT match.");
			break;
		}
		return matched;
	}

}
