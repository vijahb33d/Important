/*
 * Created on 28/11/2008
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.telstra.olb.tegcbm.job.stub.enrolments.data;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.apache.commons.collections.Transformer;

import com.telstra.olb.xml.marshall.XmlMarshaller;

/**
 * @author pavan.x.kuma
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class EnrolmentDataTransformer implements Transformer {
	private XmlMarshaller unmarshaller;

	/**
	 * @param source file.
	 * @return EnrolmentEventNotification object.
	 */
	public Object transform(Object source) {
		String file = (String) source;
		try {
			InputStream is = new BufferedInputStream(new FileInputStream(file));
			return is != null ? unmarshaller.unmarshall(is) : null;
		} catch (FileNotFoundException e) {
			throw new RuntimeException("File: " + file + " cannot be found.", e);
		} catch (Exception e) {
			throw new RuntimeException("unexpected Exception", e);
		}
	}
	/**
	 * @return Returns the unmarshaller.
	 */
	public XmlMarshaller getUnmarshaller() {
		return unmarshaller;
	}
	/**
	 * @param unmarshaller The unmarshaller to set.
	 */
	public void setUnmarshaller(XmlMarshaller unmarshaller) {
		this.unmarshaller = unmarshaller;
	}
}
