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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

/**
 * Class for accessing properties in a properties file 
 * roughly compatible with Ant property files, where ${name}
 * is replaced with the value of the property 'name' if
 * declared beforehand.
 **/
public class AntProperties extends Properties {

  public AntProperties() {
    super();
  }

  public AntProperties(Properties arg0) {
    super(arg0);
  }


  public synchronized void load(InputStream arg0) throws IOException {
      // TODO Auto-generated method stub
      super.load(arg0);
  
      BufferedReader in = null;
      try {
         
          in = new BufferedReader(new InputStreamReader(arg0));
          
          String currentLine, name, value;
          int splitIndex;
           
          while((currentLine = in.readLine()) != null) {
              // # == comment
              if(!currentLine.startsWith("#")&&!(currentLine.trim().length()==0)){ 
                  splitIndex =  currentLine.indexOf('='); 
                  name = currentLine.substring(0, splitIndex).trim();
                  value = currentLine.substring(splitIndex+1).trim();
                  this.put(name,value);
              }    
          }
      }
      finally {
          if (in != null) {
              try {
                  in.close();
              }
              catch (IOException e) {
              }
          }
      }
  }
    
  public synchronized Object put(Object name, Object value) {
    //if the property is already there don't overwrite, as in Ant
    //properties defined first take precedence
    if(!super.containsKey(name)){
        Enumeration names = super.propertyNames();
        while( names.hasMoreElements() ) {
            String currentName = (String) names.nextElement();
            String valueToSearchFor = "${"+currentName+"}";
            String valueToReplaceWith = (String) super.get(currentName);
            value = StringUtils.replace(value.toString(), valueToSearchFor, valueToReplaceWith);
        }
        return super.put(name,value);
    }   
    
    return null;
  }

  public synchronized void putAll(Map arg0) {
    Set keys = arg0.keySet();
    Iterator i = keys.iterator();
    while(i.hasNext()) {
      String currentKey = i.next().toString();
      this.put(currentKey,arg0.get(currentKey));
    }

  }
  
  public synchronized Object setProperty(String name, String value) {
    // TODO Auto-generated method stub
    return this.put(name, value);
  }

}
