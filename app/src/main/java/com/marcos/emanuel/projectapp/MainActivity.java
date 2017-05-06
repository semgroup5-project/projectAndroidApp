package com.marcos.emanuel.projectapp;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

public class MainActivity extends AppCompatActivity {
    private String command, ip;
    private SharedPreferences toSend;
    private SharedPreferences.Editor editor;
    private Button stop;
    private boolean stop_ = false;
    private Ssh ssh;
    private EditText IP, input;
    private TextView  shell, received;
    private  UdpServerThread udpServerThread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        stop = (Button) findViewById(R.id.Stop);
        stop.setBackgroundColor(Color.GREEN);
        toSend = getSharedPreferences("myPreferences",0);
        IP = (EditText) findViewById(R.id.IPedit) ;
        input = (EditText) findViewById(R.id.sshText);
        shell = (TextView) findViewById(R.id.shell);
        received = (TextView) findViewById(R.id.received);
        shell.setMovementMethod(new ScrollingMovementMethod());
    }

    public void onClick(View v) {

        editor = toSend.edit();
        ip = IP.getText().toString().trim();
        editor.putString("IP", ip);
        switch (v.getId()) {
            case R.id.Stop:

                if (stop_) {
                    stop_ = false;
                    stop.setBackgroundColor(Color.GREEN);
                    stop.setText("Stop");
                    editor.putString("Function","move");
                }else {
                    stop_ = true;
                    stop.setBackgroundColor(Color.RED);
                    stop.setText("Stopped!");
                    editor.putString("Function","stop");
                }
                startService(v);
                break;
            case R.id.Overtaker:
                editor.putString("Function","overtake");
                startService(v);
                break;
            case R.id.Parker:
                editor.putString("Function","parker");
                startService(v);
                break;
            case R.id.Connect:
                command = input.getText().toString().trim();
                ssh = new Ssh(ip, command, "password goes here");    // Define password for the server here
                try{Thread.sleep(500);}catch(Exception ee){}  //small delay
                String returned = ssh.getOutput();
                shell.setText(returned );
                break;
            default:
                throw new RuntimeException("Button ID unknown");
        }
        editor.commit();
        stopService(v);  //not sure if needed
    }

    public void startService(View view) {
        startService(new Intent(getBaseContext(), UDPSender.class));
    }

    public void stopService(View view) {
        stopService(new Intent(getBaseContext(), UDPSender.class));
    }

    @Override
    protected void onStart() {
        udpServerThread = new UdpServerThread(11111);
        udpServerThread.start();
        super.onStart();
    }

    @Override
    protected void onStop() {
        if(udpServerThread != null){
            udpServerThread.setRunning(false);
            udpServerThread = null;
        }

        super.onStop();
    }

    private void updateState(final String state){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
               received.setText(state);
            }
        });
    }

    private void updatePrompt(final String prompt){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
             // write here the code to update image display
            }
        });
    }

    private class UdpServerThread extends Thread{

        int serverPort;
        DatagramSocket socket;

        boolean running;

        public UdpServerThread(int serverPort) {
            super();
            this.serverPort = serverPort;
        }

        public void setRunning(boolean running){
            this.running = running;
        }

        @Override
        public void run() {

            running = true;
            String TAG = MainActivity.class.getSimpleName();
            try {
                updateState("Starting UDP Server");
                socket = new DatagramSocket(serverPort);

                updateState("UDP Server is running");
                Log.e(TAG, "UDP Server is running");

                while(running){
                    byte[] buf = new byte[1500];

                    // receive request
                    DatagramPacket packet = new DatagramPacket(buf, buf.length);
                    socket.receive(packet);     //this code block the program flow

                   String lText = new String(buf, 0, packet.getLength());
                    updateState(lText);
                }

                Log.e(TAG, "UDP Server ended");

            } catch (SocketException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if(socket != null){
                    socket.close();
                    Log.e(TAG, "socket.close()");
                }
            }
        }
    }


}
