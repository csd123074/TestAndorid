package com.example.nj_ba.testanroid;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.widget.Toast;

public class MySensorListener implements SensorEventListener {
    public double accelX, accelY, accelZ;
    protected Context context;

    public MySensorListener(Context context) {
        this.context = context;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor sensor = event.sensor;
        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            accelX = event.values[0];
            accelY = event.values[1];
            accelZ = event.values[2];
            //Toast.makeText(context, "X: " + accelX + ", Y: " + accelY + ", Z: " + accelZ, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}