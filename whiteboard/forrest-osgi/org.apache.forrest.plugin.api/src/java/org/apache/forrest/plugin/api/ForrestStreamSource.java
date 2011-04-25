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
package org.apache.forrest.plugin.api;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import javax.xml.transform.stream.StreamResult;

public class ForrestStreamSource implements ForrestSource {

  private InputStream mRawInputStream;
  private String mInternalString;

  public ForrestStreamSource(InputStream input) {
    mRawInputStream = input;
  }

  // @Override
  public InputStream getInputStream() {
    return mRawInputStream;
  }

  // @Override
  public String getSourceAsString() {
    if (null != mRawInputStream) {
      StringBuilder inputString = new StringBuilder();
      BufferedReader br = new BufferedReader(new InputStreamReader(mRawInputStream));

      try {
        String line = br.readLine();

        while (null != line) {
          inputString.append(line);
          line = br.readLine();
        }

        return inputString.toString();
      } catch (IOException ioe) {
        System.out.println("Problem accessing the resource: " + ioe);
      }
    }

    return new String();
  }

  // @Override
  public String getInternalRepresentationAsString() {
    if (null != mInternalString) {
      return mInternalString;
    }

    return new String();
  }

  // @Override
  public void setInternalRepresentation(String internal) {
    mInternalString = internal;
  }

}
