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
