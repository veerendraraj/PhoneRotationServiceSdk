package com.app.phonerotationservicesdk;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private IPhoneOrientationAidlInterface iPhoneOrientationData;
    private TextView rotatVector;
    private TextView rotatVectorroll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rotatVectorroll = findViewById(R.id.rotatVectorroll);
        rotatVector = findViewById(R.id.rotatVector);
        Intent intentService = new Intent(this, PhoneRotationService.class);
        bindService(intentService, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {

            iPhoneOrientationData = IPhoneOrientationAidlInterface.Stub.asInterface(service);
            Toast.makeText(MainActivity.this, "Service Connected", Toast.LENGTH_SHORT).show();
            getDataWith ();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Toast.makeText(MainActivity.this, "Service Disconnected", Toast.LENGTH_SHORT).show();
        }
    };

    private void getDataWith () {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    if (iPhoneOrientationData != null){
                        getDataFromVector(iPhoneOrientationData.phoneOrientationListener());
                    }
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }, 8000);
    }

    private void getDataFromVector(float[] matrix){

        if (matrix!=null){
            float[] rotMatrix = new float[9];
            float[] adjustedRotMatrix = new float[9];
            float[] orientation = new float[3];

            SensorManager.getRotationMatrixFromVector(rotMatrix, matrix);
            SensorManager.remapCoordinateSystem(rotMatrix, SensorManager.AXIS_X, SensorManager.AXIS_Z, adjustedRotMatrix);
            SensorManager.getOrientation(adjustedRotMatrix, orientation);
            float roll = orientation[2] * -57;

            rotatVector.setText(matrix[0]+"  "+matrix[1]+"  "+matrix[2]+"  "+matrix[3]);
            rotatVectorroll.setText("Roll :"+roll);

        }
    }
}