package com.marcos.emanuel.projectapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private UDPSender udpSender;
    private String command, ip;
    private SharedPreferences toSend;
    private SharedPreferences.Editor editor;
    private Button stop;
    private boolean stop_ = false;
    private Ssh ssh;
    private EditText IP, input;
    private TextView  shell;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        stop = (Button) findViewById(R.id.Stop);
        stop.setBackgroundColor(Color.GREEN);
        udpSender = new UDPSender();
        toSend = getSharedPreferences("myPreferences",0);

        IP = (EditText) findViewById(R.id.IPedit) ;
        input = (EditText) findViewById(R.id.sshText);
        shell = (TextView) findViewById(R.id.shell);
        shell.setMovementMethod(new ScrollingMovementMethod());
    }
    public void onClick(View v) {
        editor = toSend.edit();
        ip = IP.getText().toString().trim();
        editor.putString("IP", ip);
        switch (v.getId()) {
            case R.id.Stop:
                editor.putString("Function","stop");
                if (stop_) {
                    stop_ = false;
                    stop.setBackgroundColor(Color.GREEN);
                    stop.setText("Stop");
                }else {
                    stop_ = true;
                    stop.setBackgroundColor(Color.RED);
                    stop.setText("Stopped!");
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
                ssh = new Ssh(ip, command, "Insert password here");    // Define password for the server here
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

}
