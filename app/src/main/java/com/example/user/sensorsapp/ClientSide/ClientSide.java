package com.example.user.sensorsapp.ClientSide;

import android.util.Log;

import com.example.user.sensorsapp.Parametres;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created by User on 19.01.2018.
 */

public class ClientSide {
    private String hostName = "10.0.2.2";
    private int hostPort = 2666;
    private Socket socket = null; // сокет, через который приложение общается с сервером
    private ObjectOutputStream objectOutputStream;

    public ClientSide() throws IOException {}



    public void openConnection() throws Exception {
        closeConnection();

        try {
            socket = new Socket(hostName, hostPort);
            objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
            if (socket != null) {
            System.out.println("socket was created");
            }
        } catch (IOException e) {
            throw new Exception("Impossible to create socket" + e.getMessage());
        }
    }

    public void closeConnection() {
        if (socket != null && !socket.isClosed()) {
            try {
                socket.close();
            } catch (IOException e) {
                Log.e("serverApp","Impossible to close socket");
            } finally {
                socket = null;
            }
        }
    }

    public void sendData (Parametres parametres) throws Exception {

        if (socket == null || socket.isClosed()) {
            throw new Exception("Impossible to send data. Socket closed or not initialized");
        }

        try {
            System.out.println("Socket is not null");
            objectOutputStream.writeObject(parametres);

         /*   socket.getOutputStream().write((Float.toString(parametres.getAccX()) + " ").getBytes());
            socket.getOutputStream().write((Float.toString(parametres.getAccY()) + " ").getBytes());
            socket.getOutputStream().write((Float.toString(parametres.getAccZ()) + " ").getBytes());
            socket.getOutputStream().write((parametres.getDataGPS() + " ").getBytes());
            socket.getOutputStream().write((parametres.getDataNetwork() + " ").getBytes());
         */
        } catch (IOException e) {
            throw new Exception("Impossible to send data" + e.getMessage());
        }
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        closeConnection();
    }
}
