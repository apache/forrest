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
package org.apache.forrest.dispatcher;

import java.io.File;

import org.apache.forrest.dispatcher.helper.DispatcherPropertiesHelper;
import org.apache.forrest.dispatcher.helper.StructurerHelperStAX;

public class Cli {
    private static DispatcherPropertiesHelper propertiesHelper;

    /**
     * @param args
     */
    public static void main(String[] args) {
        System.out.println("--- Dispatcher CLI ---");
        final String home;
        if (args.length == 0) {
            // no home directory specified so use the current working directory
            home = System.getProperty("user.dir");
        } else {
            home  = args[0];
        }
        System.out.println("home: "+home);
        propertiesHelper = new DispatcherPropertiesHelper(home);
        String sourceUrl;
        try {
            sourceUrl = propertiesHelper.getMasterStructurerUrl().replace("file:///", "");
            System.out.println("sourceUrl "+sourceUrl);
            StructurerHelperStAX helper = new StructurerHelperStAX(home);
            File result = helper.execute(sourceUrl, "html");
            System.out.println("********  END *********");
            System.out.println("Structurer");
            System.out.println("result:  " + result.getAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
