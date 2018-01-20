package com.example.user.sensorsapp;

/**
 * Created by User on 19.01.2018.
 */

public class Parametres {

    private float accX;
    private float accY;
    private float accZ;

    private String dataGPS;
    private String dataNetwork;

    public Parametres(float accX, float accY, float accZ, String dataGPS, String dataNetwork) {
        this.accX = accX;
        this.accY = accY;
        this.accZ = accZ;
        this.dataGPS = dataGPS;
        this.dataNetwork = dataNetwork;

    }

    public float getAccX() {
        return accX;
    }

    public void setAccX(float accX) {
        this.accX = accX;
    }

    public float getAccY() {
        return accY;
    }

    public void setAccY(float accY) {
        this.accY = accY;
    }

    public float getAccZ() {
        return accZ;
    }

    public void setAccZ(float accZ) {
        this.accZ = accZ;
    }

    public String getDataGPS() {
        return dataGPS;
    }

    public void setDataGPS(String dataGPS) {
        this.dataGPS = dataGPS;
    }

    public String getDataNetwork() {
        return dataNetwork;
    }

    public void setDataNetwork(String dataNetwork) {
        this.dataNetwork = dataNetwork;
    }
}
