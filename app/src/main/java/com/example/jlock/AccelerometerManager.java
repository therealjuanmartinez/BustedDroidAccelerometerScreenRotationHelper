package com.example.jlock;

        import android.content.Context;
        import android.hardware.Sensor;
        import android.hardware.SensorEvent;
        import android.hardware.SensorEventListener;
        import android.hardware.SensorManager;
        import android.widget.Toast;

        import android.app.Activity;
        import android.content.pm.ActivityInfo;
        import android.os.Bundle;
        import android.view.View;
        import android.widget.Button;

        import java.util.List;
public class AccelerometerManager {

    private static Context context = null;
    /**
     * Accuracy configuration
     */
    private static float threshold = 15.0f;
    private static int interval = 200;

    private static Sensor sensor;
    private static SensorManager sensorManager;
    // you could use an OrientationListener array instead
// if you plans to use more than one listener
    private static AccelerometerListener listener;

    private static float[] xArr = new float[60];
    private static float[] zArr = new float[60];

    /**
     * indicates whether or not Accelerometer Sensor is supported
     */
    private static Boolean supported;
    /**
     * indicates whether or not Accelerometer Sensor is running
     */
    private static boolean running = false;

    /**
     * Returns true if the manager is listening to orientation changes
     */
    public static boolean isListening() {
        return running;
    }

    /**
     * Unregisters listeners
     */
    public static void stopListening() {
        running = false;
        try {
            if (sensorManager != null && sensorEventListener != null) {
                sensorManager.unregisterListener(sensorEventListener);
            }
        } catch (Exception e) {
        }
    }

    /**
     * Returns true if at least one Accelerometer sensor is available
     */
    public static boolean isSupported(Context cntxt) {
        context = cntxt;
        if (supported == null) {
            if (context != null) {

                sensorManager = (SensorManager) context.
                        getSystemService(Context.SENSOR_SERVICE);

// Get all sensors in device
                List<Sensor> sensors = sensorManager.getSensorList(
                        Sensor.TYPE_ACCELEROMETER);

                supported = new Boolean(sensors.size() > 0);
            } else {
                supported = Boolean.FALSE;
            }
        }
        return supported;
    }

    /**
     * Configure the listener for shaking
     *
     * @param threshold minimum acceleration variation for considering shaking
     * @param interval minimum interval between to shake events
     */
    public static void configure(int threshold, int interval) {
        AccelerometerManager.threshold = threshold;
        AccelerometerManager.interval = interval;
    }

    /**
     * Registers a listener and start listening
     *
     * @param accelerometerListener callback for accelerometer events
     */
    public static void startListening(AccelerometerListener accelerometerListener) {

        sensorManager = (SensorManager) context.
                getSystemService(Context.SENSOR_SERVICE);


        int i = 0;
        while (i < 10)
        {
            xArr[i] = 0;
            zArr[i] = 0;
            i++;
        }

// Take all sensors in device
        List<Sensor> sensors = sensorManager.getSensorList(
                Sensor.TYPE_ACCELEROMETER);

        if (sensors.size() > 0) {

            sensor = sensors.get(0);

// Register Accelerometer Listener
            running = sensorManager.registerListener(
                    sensorEventListener, sensor,
                    SensorManager.SENSOR_DELAY_GAME);

            listener = accelerometerListener;
        }
    }

    /**
     * Configures threshold and interval
     * And registers a listener and start listening
     *
     * @param accelerometerListener callback for accelerometer events
     * @param threshold minimum acceleration variation for considering shaking
     * @param interval minimum interval between to shake events
     */
    public static void startListening(AccelerometerListener accelerometerListener, int threshold, int interval) {
        configure(threshold, interval);
        startListening(accelerometerListener);
    }

    private static SensorEventListener sensorEventListener = new SensorEventListener() {

        private long now = 0;
        private long timeDiff = 0;
        private long lastUpdate = 0;
        private long lastShake = 0;

        private float x = 0;
        private float y = 0;
        private float z = 0;
        private float lastX = 0;
        private float lastY = 0;
        private float lastZ = 0;
        private float force = 0;

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }






        public void onSensorChanged(SensorEvent event) {
// use the event timestamp as reference
// so the manager precision won't depends
// on the AccelerometerListener implementation
// processing time
            now = event.timestamp;

            x = event.values[0];
            y = event.values[1];
            z = event.values[2];


            float xAvg = 0;
            float zAvg = 0;

            int i = 0;
            while (i < 59) {
                zArr[i] = zArr[i+1];
                xArr[i] = xArr[i+1];
                i++;

                xAvg += xArr[i];
                zAvg += zArr[i];
            }
            xArr[59] = x;
            zArr[59] = z;

            xAvg += xArr[59];
            zAvg += zArr[59];

            xAvg = xAvg / 60;
            zAvg = zAvg / 60;

            double xZeroTol = 0.6;
            double x90Tol = 8;

            if (xAvg < xZeroTol && xAvg > xZeroTol * -1)
            {
                //upright
                listener.changeRotation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
            else if (xAvg > x90Tol)
            {
                //-90
                listener.changeRotation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            }
            else if (xAvg < x90Tol * -1)
            {
                listener.changeRotation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                //+90
            }

            System.out.println(x + "  " + y + "  " + z +  " ***** " + xAvg + " " + zAvg + "     fdsafds");





// if not interesting in shake events
// just remove the whole if then else block
            if (lastUpdate == 0) {
                lastUpdate = now;
                lastShake = now;
                lastX = x;
                lastY = y;
                lastZ = z;
                Toast.makeText(context, "No Motion detected", Toast.LENGTH_SHORT).show();

            } else {
                timeDiff = now - lastUpdate;

                if (timeDiff > 0) {

                    force = Math.abs(x + y + z - lastX - lastY - lastZ);

                    if (Float.compare(force, threshold) > 0) {

                        if (now - lastShake >= interval) {
// trigger shake event
                            listener.onShake(force);
                        } else {
                            Toast.makeText(context, "No Motion detected",
                                    Toast.LENGTH_SHORT).show();

                        }
                        lastShake = now;
                    }
                    lastX = x;
                    lastY = y;
                    lastZ = z;
                    lastUpdate = now;
                } else {
                    Toast.makeText(context, "No Motion detected", Toast.LENGTH_SHORT).show();
                }
            }
// trigger change event
            listener.onAccelerationChanged(x, y, z);
        }
    };
}
