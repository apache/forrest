/*
* Copyright 2002-2004 The Apache Software Foundation
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
/*
 * Created on Feb 10, 2004
 */
package org.apache.forrest.forrestbot.webapp.dto;

import java.util.Date;

public class ProjectDTO {
	String name;
	int status;
	Date lastBuilt;
	String logUrl;
	String url;
	boolean isLogged;
	boolean deployable;
	boolean buildable;

	/**
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * @param string
	 */
	public void setName(String string) {
		name = string;
	}

	/**
	 * @param string
	 */
	public void setStatus(int i) {
		status = i;
	}

	/**
	 * @return
	 */
	public Date getLastBuilt() {
		return lastBuilt;
	}

	/**
	 * @param date
	 */
	public void setLastBuilt(Date date) {
		lastBuilt = date;
	}

	/**
	 * @return
	 */
	public String getLogUrl() {
		return logUrl;
	}

	/**
	 * @return
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @param string
	 */
	public void setLogUrl(String string) {
		logUrl = string;
	}

	/**
	 * @param string
	 */
	public void setUrl(String string) {
		url = string;
	}

	/**
	 * @return
	 */
	public boolean isLogged() {
		return isLogged;
	}

	/**
	 * @param b
	 */
	public void setLogged(boolean b) {
		isLogged = b;
	}

	/**
	 * @return
	 */
	public boolean isBuildable() {
		return buildable;
	}

	/**
	 * @return
	 */
	public boolean isDeployable() {
		return deployable;
	}

	/**
	 * @param b
	 */
	public void setBuildable(boolean b) {
		buildable = b;
	}

	/**
	 * @param b
	 */
	public void setDeployable(boolean b) {
		deployable = b;
	}

}
