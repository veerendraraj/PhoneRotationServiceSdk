package com.app.phonerotationservicesdk;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

public class PhoneRotationService extends Service {

    private Context context;
    private SensorManager mSensorManager;
    private Sensor mVector;
    private float[] mData;

    private SensorEventListener mVectorSensorListener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent event) {
            Log.d("MY_APP", event.toString());
            if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
                mData = event.values;

                // Uncomment this code to apply broadcast receiver
               /* Intent intent = new Intent("com.acs.ap_aidl.BROAD_CAST");
                intent.putExtra("SENSOR_DATA", sensorData);
                sendBroadcast(intent);*/
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy) {
            Log.d("MY_APP", sensor.toString() + " - " + accuracy);
        }
    };

    public PhoneRotationService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mVector = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
        if (mVector == null){
            Toast.makeText(getApplicationContext(), "TYPE_ROTATION_VECTOR sensor not support!", Toast.LENGTH_LONG).show();
            return;
        }
        mSensorManager.registerListener(mVectorSensorListener, mVector, 80000);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return binder;
    }

    IPhoneOrientationAidlInterface.Stub binder = new IPhoneOrientationAidlInterface.Stub() {
        @Override
        public float[] phoneOrientationListener() throws RemoteException {
            return mData;
        }
    };

    @Override
    public void onDestroy() {

        super.onDestroy();
        if (mVector != null) {
            mSensorManager.registerListener(mVectorSensorListener, mVector, 80000);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        if (mVector != null) {
            mSensorManager.registerListener(mVectorSensorListener, mVector, 80000);
        }
        return START_STICKY;
    }
}