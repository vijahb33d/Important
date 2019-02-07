/*
 * Created on 7/08/2007
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.telstra.olb.tegcbm.job.migration.helper;

import java.lang.reflect.Field;
import java.util.Enumeration;
import java.util.ResourceBundle;

import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author d274681
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class PropertiesSetterHelper {

	/**
	 * @param test
	 * @param applicationContext
	 * @param propertiesFile
	 * @throws NoSuchFieldException
	 * @throws IllegalAccessException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 */
	public static void setAllPropertiesFromSpringContext(Object test, ConfigurableApplicationContext applicationContext, String propertiesFile) throws IllegalArgumentException, SecurityException, IllegalAccessException, NoSuchFieldException  {
		ResourceBundle properties = ResourceBundle.getBundle(propertiesFile);
		setProperties(test, applicationContext, properties);
	}

	/**
	 * @param test
	 * @param applicationContext
	 * @param properties
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	private static void setProperties(Object test, ConfigurableApplicationContext applicationContext, ResourceBundle properties) throws IllegalAccessException, IllegalArgumentException, IllegalAccessException {
		Class klass = test.getClass();
		for (Enumeration enum = properties.getKeys(); enum.hasMoreElements(); ) {
			String key = (String)enum.nextElement();
			String value = properties.getString(key);
			Field field;
			try {
				field = klass.getDeclaredField(key);
				field.set(test, applicationContext.getBean(value));
			} catch (SecurityException e) {
				System.out.println("Security exception:");
				e.printStackTrace();
			} catch (NoSuchFieldException e) {
				System.out.println("No such field exception:");
				e.printStackTrace();
			}
		}
	}

}
