/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.forrest;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import org.apache.felix.framework.util.Util;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;

public class Forrest {

  private static Framework sOsgiFramework;

  private List<Bundle> mBundleList = new ArrayList<Bundle>();

  public Forrest() {
    try {
      launchOsgiFramework();
      installBundles();
      startBundles();
      // System.exit(0);
    } catch (Exception ex) {
      System.err.println("Error launching framework: " + ex);
      System.exit(0);
    }
  }

  private void launchOsgiFramework() throws Exception {
    sOsgiFramework = getFrameworkFactory().newFramework(getForrestOsgiProperties());
    sOsgiFramework.start();
  }

  /**
   * This method is from Apache Felix:
   *   main/src/main/java/org/apache/felix/main/Main.java
   *
   * java.util.ServiceLoader is cleaner, but requires Java 6
   *
   * Simple method to parse META-INF/services file for framework factory.
   * Currently, it assumes the first non-commented line is the class name
   * of the framework factory implementation.
   * @return The created <tt>FrameworkFactory</tt> instance.
   * @throws Exception if any errors occur.
   **/
  private static FrameworkFactory getFrameworkFactory() throws Exception {
    URL url = Forrest.class.getClassLoader().getResource
      ("META-INF/services/org.osgi.framework.launch.FrameworkFactory");

    if (url != null) {
      BufferedReader br = new BufferedReader
        (new InputStreamReader(url.openStream()));

      try {
        for (String s = br.readLine(); s != null; s = br.readLine()) {
          s = s.trim();

          // Try to load first non-empty, non-commented line.
          if ((s.length() > 0) && (s.charAt(0) != '#')) {
            return (FrameworkFactory) Class.forName(s).newInstance();
          }
        }
      } finally {
        if (br != null) {
          br.close();
        }
      }
    }

    throw new Exception("Could not find framework factory.");
  }

  private void addShutdownHook() {
    Runtime.getRuntime().addShutdownHook(new Thread() {

        public void run() {
          System.out.println("Ok, bye!");

          try {
            if (null != sOsgiFramework) {
              sOsgiFramework.stop();
              sOsgiFramework.waitForStop(0);
            }
          } catch (Exception ex) {
            System.err.println("Error stopping framework: " + ex);
          }
        }

      });
  }

  /*
   * The following methods are attributed to
   *   OSGi in Action
   *   Richard S Hall, Karl Pauls, Stuart McCulloch, David Savage
   *   Manning Publications Co, 2011
   */
  private void installBundles() throws BundleException {
    // forrest.bundle.dir is defined in forrest.build.xml
    String bundleDir = System.getProperty("forrest.bundle.dir");
		
    System.out.println("Installing bundles from: " + bundleDir);

    File[] files = new File(bundleDir).listFiles();
    Arrays.sort(files);
    List<File> jars = new ArrayList<File>();

    for (int i = 0; i < files.length; i++) {
      if (files[i].getName().toLowerCase().endsWith(".jar")) {
        jars.add(files[i]);
      }
    }

    if (jars.isEmpty()) {
      System.out.println("No bundles to install");
    } else {
      addShutdownHook();
    }

    BundleContext context = sOsgiFramework.getBundleContext();

    for (int i = 0; i < jars.size(); i++) {
      System.out.println("Installing bundle: " + jars.get(i).getName());
      String path = jars.get(i).toURI().toString();
      Bundle bundle = context.installBundle(path);
      mBundleList.add(bundle);
    }
  }

  private void startBundles() throws BundleException {
    for (int i = 0; i < mBundleList.size(); i++) {
      if (!isFragment(mBundleList.get(i))) {
        System.out.println("Starting bundle: " + mBundleList.get(i).getSymbolicName());
        mBundleList.get(i).start();
      }
    }
  }

  private boolean isFragment(Bundle bundle) {
    return null != bundle.getHeaders().get(Constants.FRAGMENT_HOST);
  }

  /*
   * This method is based on Apache Felix launch code
   */
  private Properties getForrestOsgiProperties() {
    // Check the forrest.osgi.conf.file system property
    // and load the properties from that file.
    // The property is set in forrest.build.xml and passed
    // to the JVM as a system property.

    Properties props = new Properties();
    File config = new File(System.getProperty("forrest.osgi.conf.file"));
		
    System.out.println("Can read props from " + config.getName() + ": " + config.canRead());

    try {
      InputStream in = config.toURI().toURL().openStream();
      props.load(in);

      if (in != null) {
        in.close();
      }
    } catch (Exception e) {
      System.err.println("Oops: " + e);

      return null;
    }

    Enumeration<?> sysplist = System.getProperties().propertyNames();

    while (sysplist.hasMoreElements()) {
      String key = (String) sysplist.nextElement();

      if (key.startsWith("felix.")
          || key.startsWith("org.osgi.")
          || key.startsWith("forrest.") || key.startsWith("project.")) {
        System.out.println("Setting property '" + key + "' to '" + System.getProperty(key) + "'");
        props.setProperty(key, System.getProperty(key));
      }
    }

    Enumeration<?> configplist = props.propertyNames();

    while (configplist.hasMoreElements()) {
      String name = (String) configplist.nextElement();
      props.setProperty(name,
                        Util.substVars(props.getProperty(name), name, null, props));
    }

    return props;
  }

  public static void main(String[] args) {
    new Forrest();
  }

}
