package com.ucd.pepeclub.exerciseapp;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class RunTracker extends AppCompatActivity {

    private Button button;
    private TextView textView;
    private BroadcastReceiver broadcastReceiver;
    private TextView timerTextView;
    private boolean running;

    long startTime = 0;

    Handler timerHandler = new Handler();
    Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;
            int hours = minutes / 60;
            minutes = minutes % 60;

            timerTextView.setText(String.format("%02d:%02d:%02d", hours, minutes, seconds));

            timerHandler.postDelayed(this, 500);
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        if(broadcastReceiver == null){
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                    // do some analysis here

                    String res = (String)intent.getExtras().get("coordinates");

                    if (res == null) {
                        // don't do this
                        throw new RuntimeException();
                    }

                    String[] data = res.split(",");

                    int count = Integer.parseInt(data[0]);
                    double lat = Double.parseDouble(data[1]);
                    double lon = Double.parseDouble(data[2]);
                    double alt = Double.parseDouble(data[3]);

                    // e.g.
                    // graphs last 7 runs
//                    double totalSpeed += getSpeed(lat, lon);
//                    double distance += getDistance(lat, lon, alt);
//                    double caloriesBurned;

                    // graph for just this run
//                    double[] altitudeForGraph += alt;

                    textView.append("Received location update.\n");



                }
            };
        }
        registerReceiver(broadcastReceiver,new IntentFilter("location_update"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(broadcastReceiver != null){
            unregisterReceiver(broadcastReceiver);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_tracker);

        button = (Button) findViewById(R.id.button);
        textView = (TextView) findViewById(R.id.textView);
        timerTextView = (TextView) findViewById(R.id.timerTextView);
        running = false;

        timerTextView.setText("00:00:00");

        if(!runtime_permissions())
            enable_buttons();

    }

    private void enable_buttons() {

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i =new Intent(getApplicationContext(),GPS_Service.class);

                if (!running) {
                    startService(i);
                    startTime = System.currentTimeMillis();
                    timerHandler.postDelayed(timerRunnable, 0);
                    button.setText("Stop");
                    running = true;
                }
                else {
                    stopService(i);
                    timerHandler.removeCallbacks(timerRunnable);
                    button.setText("Start");
                    running = false;
                }
            }
        });

    }

    private boolean runtime_permissions() {
        if(Build.VERSION.SDK_INT >= 23 && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},100);

            return true;
        }
        return false;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 100){
            if( grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                enable_buttons();
            }else {
                runtime_permissions();
            }
        }
    }
}