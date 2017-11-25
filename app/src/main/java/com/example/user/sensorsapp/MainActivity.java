package com.example.user.sensorsapp;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor sensor;

    private long lastUpdate = 0;
    private static final int SHAKE_THRESHOLD = 600;
    private float last_x;
    private float last_y;
    private float last_z;

    private TextView xView;
    private TextView yView;
    private TextView zView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        xView = (TextView) findViewById(R.id.xValue);
        yView = (TextView) findViewById(R.id.yValue);
        zView = (TextView) findViewById(R.id.zValue);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, sensor, sensorManager.SENSOR_DELAY_NORMAL);

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor mySensor = event.sensor;

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {


            long curTime = System.currentTimeMillis();

            if ((curTime - lastUpdate) > 100) {
                last_x = event.values[0];
                last_y = event.values[1];
                last_z = event.values[2];
                xView.setText(Float.toString(last_x));
                yView.setText(Float.toString(last_y));
                zView.setText(Float.toString(last_z));
                lastUpdate = curTime;
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensor, sensorManager.SENSOR_DELAY_NORMAL);
    }
}
