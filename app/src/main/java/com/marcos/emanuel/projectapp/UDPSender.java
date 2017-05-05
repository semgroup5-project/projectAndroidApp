package com.marcos.emanuel.projectapp;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * @author Emanuel on 04/05/2017.
 */

public class UDPSender extends Service{
    public static final String IP = "WHAT IS THE IP???";
    public static final int PORT = 80085;

    int mStartMode;
    IBinder mBinder;
    boolean mAllowRebind;
    private Context context;
    private SharedPreferences toSend;
    private String function;

    @Override
    public void onCreate() {
        toSend = getSharedPreferences("myPreferences",0);
        function = toSend.getString("Function", "0");
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        System.out.println("got here");
        try {

            byte[] message = "LAWL,LAWL,LAWL".getBytes();


            InetAddress address = InetAddress.getByName(IP);

            DatagramPacket packet = new DatagramPacket(message, message.length,
                    address, PORT);

            // Create a datagram socket, send the packet through it, close it.
            DatagramSocket dsocket = new DatagramSocket();
            dsocket.send(packet);
            dsocket.close();
            Toast.makeText(context, "Pedido enviado",
                    Toast.LENGTH_LONG).show();
            System.out.println("Sent");
        } catch (Exception e) {
            System.err.println(e);
        }

        return mStartMode;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    String encodedNetString(String plainInput){

        //return a NetString in the form of [len]":"[string]","
        return  Integer.toString(plainInput.length()) + ":" + plainInput + ",";
    }



}
