/*
 * Created on Feb 10, 2004
 */
package org.apache.forrest.forrestbot.webapp.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.apache.forrest.forrestbot.webapp.Constants;
import org.apache.forrest.forrestbot.webapp.Config;
import org.apache.forrest.forrestbot.webapp.dto.ProjectDTO;
import org.apache.log4j.Logger;

public class Project {
	protected ProjectDTO dto;
	private static Logger log = Logger.getLogger(Project.class);

	private String logfile = null;

	public Project() {
		this(new ProjectDTO());
	}
	public Project(ProjectDTO dto) {
		this.dto = dto;
	}
	public ProjectDTO asDTO() {
		return dto;
	}

	public void loadData() {
		dto.setLastBuilt(getLastBuilt());
		dto.setUrl(getUrl());
		dto.setLogUrl(getLogUrl());
		dto.setStatus(getStatus());
		dto.setLogged(isLogged());
	}

	private boolean isLogged() {
		return new File(getLogFile()).isFile();
	}
	
	private String getLogFile() {
		if (logfile == null) {
			logfile = Config.getProperty("logs-dir") + "/" + dto.getName() + ".log";
		}
		return logfile;
	}

	private Date getLastBuilt() {
		File f = new File(getLogFile());
		long lm = f.lastModified();
		if (lm == 0)
			return null;
		else
			return new Date(lm);
	}

	private String getUrl() {
		return Config.getProperty("build-url") + "/" + dto.getName() + "/";
	}

	private String getLogUrl() {
		return Config.getProperty("logs-url") + "/" + dto.getName() + ".log";
	}

	private int getStatus() {
		RandomAccessFile f;
		try {
			f = new RandomAccessFile(getLogFile(), "r");
		} catch (FileNotFoundException e) {
			log.debug("couldn't find log file for: " + getLogFile());
			return Constants.STATUS_UNKNOWN;
		}

		byte[] checkSuccess = new byte[Constants.BUILD_SUCCESS_STRING.length()];
		try {
			f.seek((int) f.length() - checkSuccess.length - 2);
			f.read(checkSuccess, 0, checkSuccess.length);
		} catch (IOException e1) {
			log.debug("couldn't find seek in log file: " + f.toString());
			return Constants.STATUS_UNKNOWN;
		}

		if (Constants.BUILD_SUCCESS_STRING.equals(new String(checkSuccess))) {
			return Constants.STATUS_SUCCESS;
		} else if (getLastBuilt().getTime() > (new Date()).getTime() - 60 * 1000) {
			// if date is in last minute, consider it still running
			return Constants.STATUS_RUNNING;
		} else {

			return Constants.STATUS_FAILED;
		}
	}

	public static Collection getAllProjects() {
		/* based on available directories
		File f = new File(build_dir);
		File[] possibleSites = f.listFiles();
		ArrayList sites = new ArrayList();
		for (int i = 0; i < possibleSites.length; i++) {
			if (possibleSites[i].isDirectory()) {
				sites.add(possibleSites[i].getName());
			}
		}
		*/
		/* based on config files */
		Collection sites = new ArrayList();
		File f = new File(Config.getProperty("config-dir"));
		File[] possibleSites = f.listFiles();
		for (int i = 0; i < possibleSites.length; i++) {
			if (possibleSites[i].isFile()) {
				String name = possibleSites[i].getName();
				if (name.endsWith(".xml")) {
					ProjectDTO projectDTO = new ProjectDTO();
					projectDTO.setName(name.substring(0, name.length() - 4));
					(new Project(projectDTO)).loadData();
					sites.add(projectDTO);
				}
			}
		}
		return sites;
	}
}
