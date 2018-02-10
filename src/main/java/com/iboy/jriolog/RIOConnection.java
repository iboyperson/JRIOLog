package com.iboy.jriolog;

import com.jcraft.jsch.*;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.logging.Logger;

public class RIOConnection {
    private static Logger log = Logger.getLogger(RIOConnection.class.getName());
    private static final String FILE_PATH = "/home/lvuser/FRC_UserProgram.log";

    private Session session;
    private InputStream input;

	private String mdnsIp;
    private String ip;
    private int port;
    private String login;
    private String password;

    private final JSch jsch = new JSch();

    public RIOConnection(ConfigHandler configHandler) {
	    mdnsIp = configHandler.getMdnsIp();
	    ip = configHandler.getIp();
	    port = configHandler.getPort();
	    login = configHandler.getLogin();
	    if (!configHandler.getPassword().isEmpty()) {
		    password = configHandler.getPassword();
	    }
    }
    public void newMdnsConnection() throws JSchException {
	    session = jsch.getSession(login, mdnsIp, port);
	    if (!password.isEmpty()) {
		    session.setPassword(password);
	    }
	    session.setConfig("StrictHostKeyChecking", "no");
    }

    public void newIpConnection() throws JSchException {
	    session = jsch.getSession(login, ip, port);
	    if (!password.isEmpty()) {
		    session.setPassword(password);
	    }
	    session.setConfig("StrictHostKeyChecking", "no");
    }

    public boolean connect() throws JSchException, IOException {
        log.info("[RIO] Attempting connection to: " + session.getHost() + ":" + session.getPort());
        try {
	        session.connect(5000);
		    Channel channel = session.openChannel("exec");
		    String command = "tail -f " + FILE_PATH;
		    ((ChannelExec)channel).setCommand(command);
		    input = channel.getInputStream();
		    channel.connect();
	        log.info("[RIO] Connection Successful");
		    return true;    
        }
        catch (Exception e) {
        	log.warning("[RIO] Connection Unsuccessful");
        	e.printStackTrace();
        	return false;
        }
    }

    public void disconnect() {
    	log.info("[RIO] Disconnecting");
    	session.disconnect();
    	log.info("[RIO] Disconnected");
    }

    public boolean isConnected() {
    	return session.isConnected();
    }

	public InputStream getInput() {
		return input;
	}
}
