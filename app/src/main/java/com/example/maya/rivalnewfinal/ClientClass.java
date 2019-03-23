package com.example.maya.rivalnewfinal;

/**
 * Created by Maya on 20/03/2016.
 */

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.Buffer;

import android.content.Context;
import android.util.Log;

public class ClientClass {
    private static ClientClass myRefrence;
    private String serverMSG;
//public static final String serverIP = "192.168.1.206";
// public static final String serverIP = "10.0.0.19";
 public static final String serverIP = "79.180.24.71";

    public static String isConnected ="none";
    public static final int serverPort = 1323;
    private static OnMessageReceived mMessageListener = null;
    private static Socket socket;
    private Boolean mRun = false;
    static PrintWriter out;
    static BufferedReader in;
    //mlml ,m
    public ClientClass(OnMessageReceived listener, Context context) {

        mMessageListener = listener;

        try {
            //here you must put your computer's IP address.
            InetAddress serverAddr = InetAddress.getByName(serverIP);

            Log.e("TCP Client", "C: Connecting...");

            //create a socket to make the connection with the server
            if (socket == null) {
                socket = new Socket(serverAddr, serverPort);
                isConnected="true";
            }
            //send the message to the server
            if (out == null)
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
            //receive the message which the server sends back
            if (in == null)
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            myRefrence = this;
        } catch (Exception e) {
            isConnected = "false";
        }
    }

    /**
     * Sends the message entered by client to the server
     *
     * @param message text entered by client
     */
    public void sendMessage(String message) {
        if (out != null && !out.checkError()) {
            out.println(message);
            out.flush();
        }
    }

    public void stopClient() {
        mRun = false;
        try {
            socket.close();
            isConnected="false";
        } catch (IOException e) {
            e.printStackTrace();
        }

        // socket = null;
    }

    public void run() {
        mRun = true;
        try {
            //in this while the client listens for the messages sent by the server
            while (mRun) {
                Log.e("client:","Waiting");
                serverMSG = in.readLine();
                Log.e("client:","recieved");
                if (serverMSG != null && mMessageListener != null) {
                    //call the method messageReceived from MyActivity class

                    mMessageListener.messageReceived(serverMSG);
                }
                serverMSG = null;

            }

            Log.e("RESPONSE FROM SERVER", "S: Received Message: '" + serverMSG + "'");

        } catch (Exception e) {

            Log.e("TCP", "S: Error", e);

        } finally {
            //the socket must be closed. It is not possible to reconnect to this socket
            // after it is closed, which means a new socket instance has to be created.
            //socket.close();
        }


    }

    public static void setListner(OnMessageReceived listener) {
        mMessageListener = listener;
    }

    public static ClientClass getRefrence() {
        if (myRefrence != null)
            return myRefrence;
        return null;
    }



    //Declare the interface. The method messageReceived(String message) will must be implemented in the MyActivity
    //class at on asynckTask doInBackground
    public interface OnMessageReceived {
        public void messageReceived(String message);
    }




}