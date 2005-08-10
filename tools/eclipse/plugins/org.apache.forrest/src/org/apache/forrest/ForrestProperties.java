package org.apache.forrest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.forrest.eclipse.ForrestPlugin;
import org.apache.forrest.eclipse.preference.ForrestPreferences;
import org.apache.log4j.Logger;

/**
 * A representation of te forrest properties.
 * 
 */
public class ForrestProperties {
    /** Path to the root of the Forrest project */
    String projectPath;

    /**
     * Logger for this class
     */
    private static final Logger logger = Logger
            .getLogger(ForrestProperties.class);

    /**
     * Create a new instance for the supplied projectPath.
     * 
     * @param projectPath -
     *            path to the root of the forrest proejct
     */
    public ForrestProperties(String path) {
        super();
        projectPath = path;
    }

    /**
     * Retrieves the value of a property in the forrest.properties file
     * 
     * @param path -
     *            path to the forrest.properties file
     * @param property -
     *            property to get the value from
     * 
     */
    public String getProperty(String property) {
        File defaultFile = new File(getDefaultPropertiesPath());
        Properties defaultProps = new Properties();
        try {
            defaultProps.load(new FileInputStream(defaultFile));
        } catch (FileNotFoundException e) {
            logger.error("Unable to load default forrest properties", e);
        } catch (IOException e) {
            logger.error("Unable to load default forrest properties", e);
        }

        File projectFile = new File(getProjectPropertiesPath());
        Properties projectProps = new Properties(defaultProps);
        try {
            projectProps.load(new FileInputStream(projectFile));
        } catch (FileNotFoundException e) {
            logger.error("Unable to load project forrest properties", e);
        } catch (IOException e) {
            logger.error("Unable to load project forrest properties", e);
        }
        return projectProps.getProperty(property);
    }

    /**
     * Get the path to the projects forrest properties file.
     * 
     * @return path to file.
     */
    private String getProjectPropertiesPath() {
        return projectPath + File.separator + "forrest.properties";
    }

    /**
     * Get the path to the default forrest properties file.
     * 
     * @return path to file.
     */
    private String getDefaultPropertiesPath() {
        String forrestHome = ForrestPlugin.getDefault().getPluginPreferences()
                .getString(ForrestPreferences.FORREST_HOME);
        return forrestHome + File.separator + "main" + File.separator + "webapp" + File.separator + "default-forrest.properties";
    }

}
