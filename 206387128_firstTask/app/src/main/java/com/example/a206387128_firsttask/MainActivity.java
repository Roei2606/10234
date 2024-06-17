package com.example.a206387128_firsttask;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private EditText passwordField;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor magnetometer;
    private float[] gravity;
    private float[] geomagnetic;
    private float azimuth;
    private BluetoothAdapter bluetoothAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        passwordField = findViewById(R.id.password);
        Button loginButton = findViewById(R.id.loginButton);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        loginButton.setOnClickListener(v -> {
            String password = passwordField.getText().toString();
            if (checkConditions(password)) {
                Toast.makeText(MainActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(MainActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private boolean checkConditions(String password) {
        int batteryPercentage = getBatteryPercentage();
        boolean batteryCondition = password.equals(String.valueOf(batteryPercentage));
        boolean orientationCondition = isDevicePointingNorth();
        boolean wifiCondition = isConnectedToWiFi();
        boolean bluetoothCondition = isBluetoothEnabled();

        if (batteryCondition) {
            Toast.makeText(this, "Battery percentage condition met.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Battery percentage condition not met.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (orientationCondition) {
            Toast.makeText(this, "Orientation condition met.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Orientation condition not met.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (wifiCondition) {
            Toast.makeText(this, "WiFi connection condition met.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "WiFi connection condition not met.", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (bluetoothCondition) {
            Toast.makeText(this, "Bluetooth condition met.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Bluetooth condition not met.", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private int getBatteryPercentage() {
        Intent batteryIntent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = batteryIntent != null ? batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) : -1;
        int scale = batteryIntent != null ? batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1) : -1;

        if (level == -1 || scale == -1) {
            return 50; // Default value if something goes wrong
        }

        return (int) ((level / (float) scale) * 100);
    }

    private boolean isDevicePointingNorth() {
        return azimuth >= 350 || azimuth <= 10;
    }

    private boolean isConnectedToWiFi() {
        ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI && networkInfo.isConnected();
    }

    private boolean isBluetoothEnabled() {
        return bluetoothAdapter != null && bluetoothAdapter.isEnabled();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI);
        }
        if (magnetometer != null) {
            sensorManager.registerListener(this, magnetometer, SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            gravity = event.values;
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            geomagnetic = event.values;
        }
        if (gravity != null && geomagnetic != null) {
            float[] R = new float[9];
            float[] I = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, gravity, geomagnetic);
            if (success) {
                float[] orientation = new float[3];
                SensorManager.getOrientation(R, orientation);
                azimuth = (float) Math.toDegrees(orientation[0]);
                azimuth = (azimuth + 360) % 360;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // No action needed
    }
}
