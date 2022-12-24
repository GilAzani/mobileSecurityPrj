package utils;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import callbacks.MovementCallback;

public class MovementDetector {
    private MovementCallback movementCallback;
    private SensorManager sensorManager;
    private SensorEventListener sensorEventListener;
    private Sensor sensor;

    private long timestamp = 0;

    public MovementDetector(Context context, MovementCallback movementCallback) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        this.movementCallback = movementCallback;
        initEventListener();
    }

    private void initEventListener() {
        sensorEventListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                float x = sensorEvent.values[0];//get x movement
                
                xMovement(x);
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {
            }
        };
    }

    private void xMovement(float x) {
        if (System.currentTimeMillis() - timestamp > 300) {
            timestamp = System.currentTimeMillis();
            if (x > 3) {
                if (movementCallback != null) {
                    movementCallback.moveLeft();
                }
            }
            if (x < -3) {
                if (movementCallback != null) {
                    movementCallback.moveRight();
                }
            }
        }
    }

    public void start(){
        sensorManager.registerListener(sensorEventListener,
                sensor,
                SensorManager.SENSOR_DELAY_NORMAL
        );
    }

    public void stop(){
        sensorManager.unregisterListener(
                sensorEventListener,
                sensor
        );
    }
}
