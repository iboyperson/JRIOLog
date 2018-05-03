package com.iboy.jriolog;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public class ConfigHandler {
    private Logger log;

    private FileBasedConfigurationBuilder<FileBasedConfiguration> builder;
    private Configuration config;

    //Congig Vars
    private String team;
    private String mdnsIp;
    private String ip;
    private int port;
    private String login;
    private String password;

    public ConfigHandler() {
        log = Logger.getLogger(ConfigHandler.class.getName());

        Parameters params = new Parameters();

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
            } catch (IOException e) {
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
                log.info("Configurations not found in file. Setting default values");
                config.addProperty("team", "0000");
                config.addProperty("ip", "10.00.00.2");
                config.addProperty("ip.mdns", "roboRIO-0000-FRC.local");
                config.addProperty("port", 22);
                config.addProperty("login", "lvuser");
                config.addProperty("password", "");
            }
        } catch (ConfigurationException e) {
            log.warning("Failed to load configuration. Exiting.");
            e.printStackTrace();
        }
        this.loadConfig();
    }

    public void loadConfig() {
        team = config.getProperty("team").toString();
        ip = config.getProperty("ip").toString();
        mdnsIp = config.getProperty("ip.mdns").toString();
        port = Integer.parseInt(config.getProperty("port").toString());
        login = config.getProperty("login").toString();
        password = config.getProperty("password").toString();
        log.info("Configs loaded successfully");
    }

    public String getTeam() {
        return team;
    }

    public void setTeam(String team) {
        //Set Team in configuration file
        config.setProperty("team", team);

        this.setIp(this.parseTeamToIp(team));
        this.setMdnsIp("roboRIO-" + team + "-FRC.local");
    }

    public String getMdnsIp() {
        return mdnsIp;
    }

    public void setMdnsIp(String mdnsIp) {
        config.setProperty("ip.mdns", mdnsIp);
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        config.setProperty("ip", ip);
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        config.setProperty("port", port);
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        config.setProperty("login", login);
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        config.setProperty("password", password);
    }

    public Configuration getConfig() {
        return config;
    }

    private String parseTeamToIp(String team) {
        String edited = String.format("%04d", Integer.parseInt(team));
        String ip = "10";
        ip += "." + edited.substring(0, 2);
        ip += "." + edited.substring(2, 4);
        ip += ".2";

        return ip;
    }
}
