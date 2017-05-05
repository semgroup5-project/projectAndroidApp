package com.marcos.emanuel.projectapp;

import android.app.Activity;
import android.os.AsyncTask;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.ByteArrayOutputStream;
import java.util.Properties;

/**
 * @author Emanuel on 05/05/2017.
 */

public class Ssh extends Activity {
    private String command, username, password, hostname = "host goes here";
    private int PORT = 80085, ip;

    public Ssh(int ip, String command, String password){
        this.ip = ip;
        this.command = command;
        this.password = password;
        ExecuteRemoteCommand ex = new ExecuteRemoteCommand();
        ex.execute();
    }


    private class ExecuteRemoteCommand extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            {
                try {

                    JSch jsch = new JSch();
                    Session session = jsch.getSession(username, hostname, PORT);
                    session.setPassword(password);

                    // Avoid asking for key confirmation
                    Properties prop = new Properties();
                    prop.put("StrictHostKeyChecking", "no");
                    session.setConfig(prop);

                    session.connect();

                    // SSH Channel
                    ChannelExec channelssh = (ChannelExec)
                            session.openChannel("exec");
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    channelssh.setOutputStream(baos);

                    // Execute command
                    channelssh.setCommand("lsusb > /home/pi/test.txt");
                    channelssh.connect();
                    channelssh.disconnect();

                    return baos.toString();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
            return null;
        }
    }

 }
