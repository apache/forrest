/*
 * Copyright 1999-2004 The Apache Software Foundation or its licensors,
 * as applicable.
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
package org.apache.forrest.eclipse.actions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * 
 * Some handy utilities for working with plugins.
 */
public class Utilities {

	/**
	 * @param directory
	 * @return @throws
	 *         FileNotFoundException
	 */
	static public List getFileListing(File directory)
			throws FileNotFoundException {
		class JARFilter implements FilenameFilter {
			public boolean accept(File dir, String name) {
				return (name.endsWith(".jar"));
			}
		}

		List result = new ArrayList();
		File[] filesAndDirs = directory.listFiles();
		List filesDirs = Arrays.asList(filesAndDirs);
		Iterator filesIter = filesDirs.iterator();
		File file = null;

		while (filesIter.hasNext()) {
			file = (File) filesIter.next();

			if (!file.isFile()) {
				List deeperList = getFileListing(file);
				result.addAll(deeperList);
			} else {
				result.add(file);
			}
		}

		return result;
	}

	/**
	 * @param cmdString
	 */
	static public void RunExtCommand(final String cmdString) {
		Runnable r = new Runnable() {
			public void run() {
				Process p;
				try {
					System.out.println(cmdString);
					p = Runtime.getRuntime().exec(cmdString);

					BufferedReader br = new BufferedReader(
							new InputStreamReader(p.getInputStream()));
					String str;
					while ((str = br.readLine()) != null)
						System.out.println(str);

					p.waitFor();
				} catch (Exception e) {
					System.out.println(e);
				}
			}
		};
		r.run();
	}
}
