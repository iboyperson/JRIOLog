package com.iboy.jriolog;

import com.jcraft.jsch.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.logging.Logger;

public class RIOConnection {
    private static Logger log = Logger.getLogger(RIOConnection.class.getName());

    private Session session;
    private PrintStream ps;
    private InputStream input;
    private OutputStream output;

    private String filePath;

    private final JSch jsch = new JSch();

	public RIOConnection(String ip, int port, String login) throws JSchException {
		log.setParent(JRIOLog.log);

		filePath = "/home/lvuser/FRC_UserProgram.log";
		String command = "tail -f " + filePath;

		session = jsch.getSession(login, ip, port);
	}

    public RIOConnection(String ip, int port, String login, String password) throws JSchException {
	    log.setParent(JRIOLog.log);

        filePath = "/home/lvuser/FRC_UserProgram.log";
	    String command = "tail -f " + filePath;

	    session = jsch.getSession(login, ip, port);
	    session.setPassword(password);
    }

    public void connect() throws JSchException, IOException {
        log.info("[RIO] Connecting");
        session.connect();

	    Channel channel = session.openChannel("exec");
	    String command = "tail -f " + filePath;
	    ((ChannelExec)channel).setCommand(command);
	    output = channel.getOutputStream();
	    channel.connect();

	    log.info("[RIO] Connected");
    }

    public void disconnect() {
    	log.info("[RIO] Disconnecting");
    	session.disconnect();
    	log.info("[RIO] Disconnected");
    }

    public boolean isConnected() {
    	return session.isConnected();
    }

    public void setFilePath(String filePath) {
    	this.filePath = filePath;
    }

	public OutputStream getOutput() {
		return output;
	}
}
