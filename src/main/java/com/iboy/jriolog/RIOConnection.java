package com.iboy.jriolog;

import com.jcraft.jsch.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.logging.Logger;

public class RIOConnection {
    private final String FILE_PATH = "/home/lvuser/FRC_UserProgram.log";
    private final JSch jsch = new JSch();

    private Logger log;
    private Session session;
    private InputStream input;
    private ArrayList<String> ips;
    private int port;
    private String login;
    private String password;

    public RIOConnection(ConfigHandler configHandler) {
        log = Logger.getLogger(RIOConnection.class.getName());

        ips = new ArrayList<String>();
        ips.add(configHandler.getMdnsIp());
        ips.add(configHandler.getIp());
        ips.add("172.22.11.2");
        port = configHandler.getPort();
        login = configHandler.getLogin();
        password = configHandler.getPassword();
    }

    public boolean connect() throws JSchException {
        for (String ip : ips) {
            session = jsch.getSession(login, ip, port);
            if (!password.isEmpty()) {
                session.setPassword(password);
            }
            session.setConfig("StrictHostKeyChecking", "no");

            log.info("[RIO] Attempting connection to: " + session.getHost() + ":" + session.getPort());

            try {
                session.connect(5000);
                if (session.isConnected()) {
                    Channel channel = session.openChannel("exec");
                    String command = "tail -f " + FILE_PATH;
                    ((ChannelExec) channel).setCommand(command);
                    input = channel.getInputStream();
                    channel.connect();
                    log.info("[RIO] Connection Successful");
                    return true;
                }
            }
            catch (JSchException e) {
                log.warning("[RIO] Connection Unsuccessful");
            }
            catch (IOException e) {
                log.warning("[RIO] Failed to get input stream");
            }
        }

        return false;
    }

    public void disconnect() {
        log.info("[RIO] Disconnecting");
        if(session!= null && session.isConnected()) {
            session.disconnect();
        }
        log.info("[RIO] Disconnected");
    }

    public boolean isConnected() {
        return session.isConnected();
    }

    public String getLogin() {
        return login;
    }

    public int getPort() {
        return port;
    }

    public InputStream getInput() {
        return input;
    }
}
