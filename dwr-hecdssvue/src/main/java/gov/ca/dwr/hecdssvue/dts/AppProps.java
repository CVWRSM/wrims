/*
 * Water Resource Integrated Modeling System (WRIMS) Copyright (c) 2024.
 *
 * WRIMS 2 is copyrighted by the State of California Department of Water Resources.
 * It is licensed under the Eclipse Public License, Version 1.0.
 * See Eclipse Public License for more details.
 */

package gov.ca.dwr.hecdssvue.dts;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Properties for the application. These properties are stored in the
 * app.props file in the [user.dir]/.calsim directory. These directories
 * & files are created if they do not exist.
 *
 * @author Nicky Sandhu
 * @version $Id: AppProps.java,v 1.1.2.3 2000/12/20 20:02:06 amunevar Exp $
 */
public class AppProps {
    public static Properties props = new Properties();

    public static void initializeProps() {
        File appPropsDir = checkForPropsDir();
        File appPropsFile = new File(appPropsDir.getPath() + File.separator + "app.props");
        if (!appPropsFile.exists()) {
            // then look in jar file for defaults & save to props file
            try {
                props.load(Object.class.getResourceAsStream("/calsim/app/app.props"));
                save();
            } catch (IOException ioe) {
                ioe.printStackTrace(System.err);
            }
        } else { // read from app.props file
            try {
                // always load defaults and then user customized properties
                props.load(new FileInputStream(appPropsFile));
                save();
            } catch (IOException ioe) {
                ioe.printStackTrace(System.err);
            }
        }
    }

    /**
     *
     */
    public static File checkForPropsDir() {
        String appDir = "data";
        File appPropsDir = new File(appDir);
        if (!appPropsDir.exists()) {
            // create this directory...
            appPropsDir.mkdir();
        }
        return appPropsDir;
    }

    /**
     *
     */
    public static void save() {
        // look for .calsim directory in user.home & a file called app.props
        File appPropsDir = checkForPropsDir();
        File appPropsFile = new File(appPropsDir.getPath() + File.separator + "app.props");
        // then look in jar file for defaults & save to props file
        try {
            props.store(new FileOutputStream(appPropsFile), "application user properties");
        } catch (IOException ioe) {
            ioe.printStackTrace(System.err);
        }
    }

    static {
        initializeProps();
    }

    /**
     *
     */
    public static String getProperty(String key) {
        return props.getProperty(key);
    }
}
