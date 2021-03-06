package com.example.user.sensorsapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.user.sensorsapp.ClientSide.ClientSide;

import java.io.IOException;
import java.util.Date;

import static java.lang.Thread.sleep;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor sensor;
    private LocationManager locationManager;

    private long lastUpdate = 0;
    private static final int SHAKE_THRESHOLD = 600;
    private float last_x;
    private float last_y;
    private float last_z;

    private TextView xView;
    private TextView yView;
    private TextView zView;
    private TextView tvEnabledGPS;
    private TextView tvStatusGPS;
    private TextView tvLocationGPS;
    private TextView tvEnabledNet;
    private TextView tvStatusNet;
    private TextView tvLocationNet;

    private Parametres parametres = new Parametres(1.22f, 2.22f, 3.22f, "default", "default");
    private Button buttonOpen = null;
    private Button buttonSend = null;
    private Button buttonClose = null;
    private ClientSide server = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        xView = (TextView) findViewById(R.id.xValue);
        yView = (TextView) findViewById(R.id.yValue);
        zView = (TextView) findViewById(R.id.zValue);
        tvEnabledGPS = (TextView) findViewById(R.id.tvEnabledGPS);
        tvStatusGPS = (TextView) findViewById(R.id.tvStatusGPS);
        tvLocationGPS = (TextView) findViewById(R.id.tvLocationGPS);
        tvEnabledNet = (TextView) findViewById(R.id.tvEnabledNet);
        tvStatusNet = (TextView) findViewById(R.id.tvStatusNet);
        tvLocationNet = (TextView) findViewById(R.id.tvLocationNet);

        //Инициализация акселерометра
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, sensor, sensorManager.SENSOR_DELAY_NORMAL);

        //Инициализация датчика  GPS
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        //Инициализация кнопок
        buttonOpen = (Button) findViewById(R.id.button_open_connection);
        buttonSend = (Button) findViewById(R.id.button_send_connection);
        buttonClose = (Button) findViewById(R.id.button_close_connection);
        buttonClose.setEnabled(false);
        buttonSend.setEnabled(false);

        buttonOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Объект для работы ссервером
                try {
                    server = new ClientSide();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //Открываем соединение
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            server.openConnection();

                            //Делаем кнопки активными в ui потоке
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    buttonSend.setEnabled(true);
                                    buttonClose.setEnabled(true);
                                }
                            });
                        } catch (Exception e) {
                            Log.e("serverApp", e.getMessage());
                            server = null;
                        }
                    }
                }).start();
            }
        });

        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (server == null) {
                    Log.e("serverApp","Сервер не создан");
                }
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            while (true) {
                                server.sendData(parametres);
                                sleep(3000);
                            }
                        } catch (Exception e) {
                            Log.e("serverApp",e.getMessage());
                        }
                    }
                }).start();
            }
        });

        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                server.closeConnection();
                buttonSend.setEnabled(false);
                buttonClose.setEnabled(false);
            }
        });
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor mySensor = event.sensor;

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {


            long curTime = System.currentTimeMillis();

            if ((curTime - lastUpdate) > 3000) {
                last_x = event.values[0];
                last_y = event.values[1];
                last_z = event.values[2];
                xView.setText(Float.toString(last_x));
                yView.setText(Float.toString(last_y));
                zView.setText(Float.toString(last_z));
                parametres.setAccX(last_x);
                parametres.setAccY(last_y);
                parametres.setAccZ(last_z);
                lastUpdate = curTime;
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onPause() {
        //Отключаем слушателей
        super.onPause();
        sensorManager.unregisterListener(this);
        locationManager.removeUpdates(locationListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, sensor, sensorManager.SENSOR_DELAY_NORMAL);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //Проверяем наличие разрешений на определение местоположений
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000 * 5, 10, locationListener);
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000 * 5, 10, locationListener);
        checkEnabled();
    }

    private LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            showLocation(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            if (provider.equals(LocationManager.GPS_PROVIDER)) {
                tvStatusGPS.setText("Status: " + String.valueOf(status));
            } else if (provider.equals(LocationManager.NETWORK_PROVIDER)) {
                tvStatusNet.setText("Status: " + String.valueOf(status));
            }
        }

        @Override
        public void onProviderEnabled(String provider) {
            checkEnabled();
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }
            showLocation(locationManager.getLastKnownLocation(provider));
        }

        @Override
        public void onProviderDisabled(String provider) {
            checkEnabled();
        }
    };

    private void showLocation(Location location) {
        if (location == null)
            return;
        if (location.getProvider().equals(LocationManager.GPS_PROVIDER)) {
            tvLocationGPS.setText(formatLocation(location));
            parametres.setDataGPS(formatLocation(location));
        }
        else if (location.getProvider().equals(LocationManager.NETWORK_PROVIDER)) {
            tvLocationNet.setText(formatLocation(location));
            parametres.setDataNetwork(formatLocation(location));
        }
    }

    private String formatLocation(Location location) {
        if (location == null)
            return "";
        return String.format(
                "Coordinates: lat = %1$.4f, lon = %2$.4f, time = %3$tF %3$tT",
                location.getLatitude(), location.getLongitude(), new Date(
                        location.getTime()));
    }

    private void checkEnabled() {
        tvEnabledGPS.setText("Enabled: "
                + locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER));
        tvEnabledNet.setText("Enabled: "
                + locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER));
    }
}
