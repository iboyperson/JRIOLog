package com.iboy.jriolog;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public class ConfigHandler {
	private static Logger log = Logger.getLogger(ConfigHandler.class.getName());

	FileBasedConfigurationBuilder<FileBasedConfiguration> builder;
	private Configuration config;

	//Congig Vars
	private String team;
	private String ip;
	private int port;
	private String login;
	private String password;

	public ConfigHandler() throws ConfigurationException {
		log.setParent(JRIOLog.log);

		Parameters params = new Parameters();

		Configurations configs = new Configurations();
		String home = System.getProperty("user.home");
		File confDir = new File(home + "/JRIOLog");
		File propertiesFile = new File(confDir, "config.properties");
		boolean newFile = false;
		if (!confDir.exists()) {
			confDir.mkdirs();
		}
		if (!propertiesFile.exists()) {
			log.info("Configuration file does not exist. Creating new file.");
			try {
				newFile = propertiesFile.createNewFile();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}

		builder = new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
				.configure(params.fileBased()
						.setFile(propertiesFile));
		builder.setAutoSave(true);

		try {
			config = builder.getConfiguration();
			if (newFile) {
				log.info("Setting Stock Values");
				config.addProperty("team", "0000");
				config.addProperty("ip", "10.00.00.2");
				config.addProperty("ip.user-set", false);
				config.addProperty("port", 22);
				config.addProperty("login", "lvuser");
				config.addProperty("password", "");
			}
			team = config.getProperty("team").toString();
			ip = config.getProperty("ip").toString();
			port = Integer.parseInt(config.getProperty("port").toString());
			login = config.getProperty("login").toString();
			password = config.getProperties("password").toString();
		}
		catch (ConfigurationException e) {
			log.warning("Failed to load configuration. Exiting.");
		}
	}

	public void setTeam(String team) {
		//Set Team in configuration file
		config.setProperty("team",team);

		if (!Boolean.parseBoolean(config.getProperty("ip.user-set").toString())) {
			this.setIp(this.parseTeamToIp(team));
		}
	}


	public void setIp(String ip) {
		config.setProperty("ip", ip);
	}

	public void setIpUserSet(boolean userSet) {
		config.setProperty("ip.user-set", userSet);
	}

	public void setPort(int port) {
		config.setProperty("port", port);
	}

	public void setLogin(String login) {
		config.setProperty("login", login);
	}

	public void setPassword(String password) {
		config.setProperty("password", password);
	}

	public void reloadConfig() {
		team = config.getProperty("team").toString();
		ip = config.getProperty("ip").toString();
		port = Integer.parseInt(config.getProperty("port").toString());
		login = config.getProperty("login").toString();
		password = config.getProperties("password").toString();
	}

	public String getTeam() {
		return team;
	}

	public String getIp() {
		return ip;
	}

	public int getPort() {
		return port;
	}

	public String getLogin() {
		return login;
	}

	public String getPassword() {
		return password;
	}

	public Configuration getConfig() {
		return config;
	}


	private String parseTeamToIp(String team) {
		String edited = String.format("%04d", Integer.parseInt(team));
		String ip = "10";
		ip += "." + edited.substring(0,2);
		ip += "." + edited.substring(2,4);
		ip += ".2";

		return ip;
	}
}