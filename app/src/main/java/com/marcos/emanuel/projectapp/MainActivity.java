package com.marcos.emanuel.projectapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {
    private UDPSender udpSender;
    private String command;
    private int ip;
    private SharedPreferences toSend;
    private SharedPreferences.Editor editor;
    private Button stop;
    private boolean stop_ = false;
    private Ssh ssh;
    private EditText IP, input;



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


    }
    public void onClick(View v) {
        editor = toSend.edit();
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
                break;
            case R.id.Overtaker:
                editor.putString("Function","overtake");
                break;
            case R.id.Parker:
                editor.putString("Function","parker");
                break;
            case R.id.Connect:
                ip = Integer.valueOf(IP.getText().toString().trim());
                command = input.getText().toString().trim();
                ssh = new Ssh(ip, command, "password");

                break;
            default:
                throw new RuntimeException("Button ID unknown");
        }
        startService(v);
       // stopService(v);  //not sure if needed
    }

    public void startService(View view) {
        startService(new Intent(getBaseContext(), UDPSender.class));
    }

    public void stopService(View view) {
        stopService(new Intent(getBaseContext(), UDPSender.class));
    }

}
