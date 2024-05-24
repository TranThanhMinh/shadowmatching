package org.cocos2dx.lib;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.view.Display;
import android.view.WindowManager;

/* loaded from: classes2.dex */
public class Cocos2dxAccelerometer implements SensorEventListener {
    static final float ALPHA = 0.25f;
    private static final String TAG = Cocos2dxAccelerometer.class.getSimpleName();
    final float[] accelerometerValues = new float[3];
    final float[] compassFieldValues = new float[3];
    private final Sensor mAccelerometer;
    private final Sensor mCompass;
    private final Context mContext;
    private final int mNaturalOrientation;
    private final SensorManager mSensorManager;

    public static native void onSensorChanged(float f, float f2, float f3, long j);

    public Cocos2dxAccelerometer(Context context) {
        this.mContext = context;
        SensorManager sensorManager = (SensorManager) context.getSystemService("sensor");
        this.mSensorManager = sensorManager;
        this.mAccelerometer = sensorManager.getDefaultSensor(1);
        this.mCompass = sensorManager.getDefaultSensor(2);
        Display display = ((WindowManager) context.getSystemService("window")).getDefaultDisplay();
        this.mNaturalOrientation = display.getOrientation();
    }

    public void enableCompass() {
        this.mSensorManager.registerListener(this, this.mCompass, 1);
    }

    public void enableAccel() {
        this.mSensorManager.registerListener(this, this.mAccelerometer, 1);
    }

    public void setInterval(float interval) {
        if (Build.VERSION.SDK_INT < 11) {
            this.mSensorManager.registerListener(this, this.mAccelerometer, 1);
        } else {
            this.mSensorManager.registerListener(this, this.mAccelerometer, (int) (1000000.0f * interval));
        }
    }

    public void disable() {
        this.mSensorManager.unregisterListener(this);
    }

    @Override // android.hardware.SensorEventListener
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == 1) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];
            float[] fArr = this.accelerometerValues;
            fArr[0] = x;
            fArr[1] = y;
            fArr[2] = z;
            int orientation = this.mContext.getResources().getConfiguration().orientation;
            if (orientation == 2 && this.mNaturalOrientation != 0) {
                x = -y;
                y = x;
            } else if (orientation == 1 && this.mNaturalOrientation != 0) {
                x = y;
                y = -x;
            }
            int rotation = Cocos2dxHelper.getActivity().getWindowManager().getDefaultDisplay().getRotation();
            if (rotation == 2 || rotation == 3) {
                x = -x;
                y = -y;
            }
            Cocos2dxGLSurfaceView.queueAccelerometer(x, y, z, sensorEvent.timestamp);
            return;
        }
        if (sensorEvent.sensor.getType() == 2) {
            this.compassFieldValues[0] = sensorEvent.values[0];
            this.compassFieldValues[1] = sensorEvent.values[1];
            this.compassFieldValues[2] = sensorEvent.values[2];
        }
    }

    @Override // android.hardware.SensorEventListener
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
