package com.iboy.jriolog;

import com.jcraft.jsch.*;

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
        port = configHandler.getPort();
        login = configHandler.getLogin();
        password = configHandler.getPassword();
    }

    public boolean connect() throws JSchException {
        boolean isDone = false;
        int trys = 0;
        while (!isDone && trys < ips.size()) {
            session = jsch.getSession(login, ips.get(trys), port);
            if (!password.isEmpty()) {
                session.setPassword(password);
            }
            session.setConfig("StrictHostKeyChecking", "no");

            log.info("[RIO] Attempting connection to: " + session.getHost() + ":" + session.getPort());

            try {
                session.connect(5000);
                Channel channel = session.openChannel("exec");
                String command = "tail -f " + FILE_PATH;
                ((ChannelExec) channel).setCommand(command);
                input = channel.getInputStream();
                channel.connect();
                log.info("[RIO] Connection Successful");
                isDone = true;
            } catch (Exception e) {
                log.warning("[RIO] Connection Unsuccessful");
                trys++;
                //e.printStackTrace();
            }
        }

        return isDone;
    }

    public void disconnect() {
        log.info("[RIO] Disconnecting");
        session.disconnect();
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
