/*
 * Copyright 1999-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.forrest.conf;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.avalon.framework.thread.ThreadSafe;
import org.apache.cocoon.components.modules.input.AbstractJXPathModule;
import org.apache.cocoon.components.modules.input.InputModule;
import org.apache.excalibur.source.Source;
import org.apache.excalibur.source.SourceResolver;

/**
 * @date 15-apr-2004
 **/
public class ForrestConfModule {
  /**
   * Input module for accessing properties in a properties file 
   * roughly compatible with Ant property files, where ${name}
   * is replaced with the value of the property 'name' if
   * declared beforehand.
   * 
  */
  public class AntPropertiesModule extends AbstractJXPathModule 
  implements InputModule, Serviceable, Initializable, ThreadSafe {
      
      private SourceResolver m_resolver;      private Properties forrestProperties;;
      
      public void service(ServiceManager manager) throws ServiceException {
          m_resolver = (SourceResolver) manager.lookup(SourceResolver.ROLE);
      }

      public void initialize() throws Exception{
          // get forrest properties
      
          Source source = null;
          InputStream in = null;
          try {
              source = m_resolver.resolveURI("forrest.properties");
              
              if(source.exists()) {
              
                in = source.getInputStream();
              
                forrestProperties = new AntProperties();
                forrestProperties.load(in);
              }
              else{
                forrestProperties = new Properties();
              }
          }
          catch (IOException e) {
              throw new Exception("Cannot load forrest.properties file.");
          }
          finally {
              if (source != null) {
                  m_resolver.release(source);
              }
              if (in != null) {
                  try {
                      in.close();
                  }
                  catch (IOException e) {
                  }
              }
          }
          
          // get location of forrest home
          
          String forrestHome;
          
          try{
             forrestHome = System.getenv("FORREST_HOME");
             getLogger().debug("gotten forrestHome from FORREST_HOME, value:"+forrestHome);
          }
          catch(Exception e){
            //probably using a JDK with deprecated getEnv
            forrestHome = System.getProperty("forrest.home",".");
            getLogger().debug("gotten forrestHome from forrest.home, value:"+forrestHome);
          }                       
                  
          forrestProperties.put("rawforresthome",forrestHome); 
          forrestProperties.put("forresthome",m_resolver.resolveURI(forrestHome).getURI());                                           
      }
       
      
      protected Object getContextObject(Configuration modeConf, Map objectModel)
          throws ConfigurationException {
          
          return forrestProperties;
      }

  }

}
