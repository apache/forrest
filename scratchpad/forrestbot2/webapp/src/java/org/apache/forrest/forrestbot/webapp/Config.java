/*
 * Created on Oct 21, 2003
 */
package org.apache.forrest.forrestbot.webapp;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

/**
 * A singleton that takes care of any configuration
 */
public class Config {
	private static Logger log = Logger.getLogger(Config.class);
	private static Properties p = new Properties();

	// singleton holder
	private static final class SingletonHolder {
		static final Config a = new Config();
	}
	// get the singleton instance
	public static Config getInstance() {
		return SingletonHolder.a;
	}

	// private constructor to prevent construction
	private Config() {
			configureLog4j();
			configureProperties();
			validateProperties();
	}

	private static void configureLog4j() {
		Properties log4j = new Properties();
		try {
			log4j.load(Config.class.getClassLoader().getResourceAsStream("log4j.properties"));
		} catch (IOException e1) {
			log.warn("can't load log4j.properties", e1);
		}
		PropertyConfigurator.configure(log4j);
	}

	private static void configureProperties() {
		log.info("loading settings.properties");
		try {
			p.load(
			Config.class.getClassLoader().getResourceAsStream("settings.properties"));
		} catch (IOException e) {
			log.error("can't load settings.properties", e);
		}
	}

	public static void validateProperties() {
		String [] requiredProperties = { "forrest-exec", "config-dir", "build-dir", "logs-dir", "build-url", "refreshrate", "debug-exec" };
		String [] filesToCheck = { "forrest-exec" };
		String [] directoriesToCheck = { "config-dir", "build-dir", "logs-dir" };
		
		for (int i = 0; i < requiredProperties.length; i++) {
			if (getProperty(requiredProperties[i]) == null) {
				log.error("Property " + requiredProperties[i] + " is required.");
			}
		}
		for (int i = 0; i < filesToCheck.length; i++) {
			File f = new File(getProperty(filesToCheck[i]));
			if (!f.isFile()) {
				log.error("Property " + filesToCheck[i] + " must reference a file.  Current value: " + f.toString());
			}
		}
		for (int i = 0; i < directoriesToCheck.length; i++) {
			File f = new File(getProperty(directoriesToCheck[i]));
			if (!f.isDirectory()) {
				log.error("Property " + directoriesToCheck[i] + " must reference a directory.  Current value: " + f.toString());
			}
		}
	}

	public static String getProperty(String arg0, String arg1) {
		return p.getProperty(arg0, arg1);
	}

	public static String getProperty(String arg0) {
		return p.getProperty(arg0);
	}

}
