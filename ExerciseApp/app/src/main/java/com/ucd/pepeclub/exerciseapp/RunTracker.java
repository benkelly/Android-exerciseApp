package com.ucd.pepeclub.exerciseapp;

import android.Manifest;
import android.app.FragmentManager;
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
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class RunTracker extends AppCompatActivity {

    private Button button;
    private BroadcastReceiver broadcastReceiver;
    private TextView timerTextView;
    private boolean running;
    private FragmentManager fragmentManager;


    private ArrayList<Double> speeds;
    private ArrayList<Double> altitudes;
    private double distance;

    private double previousLatitude;
    private double previousLongitude;
    private double previousAltitude;
    private long previousTime;
    private long firstTime;
    private double averageSpeed;

    private double highestAltitude;
    private double lowestAltitude;
    private double highestSpeed;
    private double lowestSpeed;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_tracker);
        setTitle(R.string.title_activity_run_tracker);

        button = (Button) findViewById(R.id.button);
        timerTextView = (TextView) findViewById(R.id.timerTextView);
        running = false;

        timerTextView.setText("00:00:00");

        if(!verifyRuntimePermissions())
            enableButtons();

        distance = 0;

        previousAltitude = -1;
        previousLatitude = -1;
        previousLongitude = -1;
        previousTime = -1;

        highestAltitude = -1;
        lowestAltitude = 900000;
        highestSpeed = -1;
        lowestSpeed = 900000;

        speeds = new ArrayList<>();
        altitudes = new ArrayList<>();

        fragmentManager = getFragmentManager();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(broadcastReceiver == null){
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

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
                    long epoch = Long.parseLong(data[4]);

                    if (lat == previousLatitude && lon == previousLongitude) {
                        registerReceiver(broadcastReceiver,new IntentFilter("location_update"));
                        return;
                    }

                    if (previousTime != -1) {
                        double thisDist = calculateDistance(lat, lon, alt);
                        distance += thisDist;
                        double speed = calculateSpeed(thisDist, epoch);
                        speeds.add(speed);
                        altitudes.add(alt);

                        if (alt > highestAltitude) {
                            highestAltitude = alt;
                        }

                        if (alt < lowestAltitude) {
                            lowestAltitude = alt;
                        }

                        if (speed > highestSpeed) {
                            highestSpeed = speed;
                        }

                        if (speed < lowestSpeed) {
                            lowestSpeed = speed;
                        }
                    }
                    else {
                        firstTime = epoch;
                    }

                    previousLatitude = lat;
                    previousLongitude = lon;
                    previousAltitude = alt;
                    previousTime = epoch;
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

    private double calculateDistance(double lat, double lon, double alt) {
        double earthRadius = 6371000.;

        double diffLat  = Math.toRadians(Math.abs(lat - previousLatitude));
        double diffLon = Math.toRadians(Math.abs(lon - previousLongitude));

        double pLat = Math.toRadians(previousLatitude);
        double cLat   = Math.toRadians(lat);

        double a = haversine(diffLat) + Math.cos(pLat) * Math.cos(cLat) * haversine(diffLon);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return earthRadius * c;
    }

    private double calculateSpeed(double dist, long epoch) {
        return dist / ((double)((epoch - previousTime)) / 1000);
    }

    public static double haversine(double angle) {
        return Math.pow(Math.sin(angle / 2), 2);
    }

    private void calculateExerciseInfo(long runTime) {
        System.out.println("----- EXERCISE INFO -----");
        System.out.println("Distance: " + distance + " metres");
        System.out.print("Altitudes: ");
        for (double a : altitudes) {
            System.out.print(a + "m, ");
        }
        System.out.print("\nSpeeds: ");
        for (double a : speeds) {
            System.out.print(a + "m/s, ");
        }
        averageSpeed = distance / runTime;
        System.out.println("\nAverage speed: " + averageSpeed + " m/sec");
    }

    private long getSecondsFromTime(String time) {
        // time hh:mm:ss
        String[] parts = time.split(":");
        long hours = (Integer.parseInt(parts[0])) * 60 * 60;
        long minutes = (Integer.parseInt(parts[1])) * 60;
        long seconds = Integer.parseInt(parts[2]);

        return hours + minutes + seconds;
    }

    private void enableButtons() {

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),GpsService.class);

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

                    String time = timerTextView.getText().toString();
                    long timeInSeconds = getSecondsFromTime(time);
                    System.out.println("Time Seconds: " + timeInSeconds);
                    // if time < X seconds don't bother analysing
                    if (timeInSeconds > 1) {
                        calculateExerciseInfo(timeInSeconds);
                        PointsReward pr = new PointsReward();
                        Bundle args = new Bundle();
                        args.putString("points", "Great run! You earned " + calculatePoints() + " points!");
                        pr.setArguments(args);
                        pr.show(fragmentManager, "tag");
                    }

                    timerTextView.setText("00:00:00");
                    button.setText("Start");
                    running = false;
                }
            }
        });

    }

    private int calculatePoints() {
        int temp = (int)(distance / averageSpeed);
        return temp / 20;
    }

    private boolean verifyRuntimePermissions() {
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},100);

            return true;
        }
        return false;
    }

    protected void createAnalysisActivity() {
        double[] newAlts = new double[altitudes.size()];
        for (int i = 0; i < newAlts.length; i++) {
            newAlts[i] = altitudes.get(i);
        }

        double[] newSpeeds = new double[speeds.size()];
        for (int i = 0; i < newSpeeds.length; i++) {
            newSpeeds[i] = speeds.get(i);
        }

        Intent myIntent = new Intent(RunTracker.this, RunAnalysis.class);
        myIntent.putExtra("DISTANCE", distance);
        myIntent.putExtra("ALTITUDES", newAlts);
        myIntent.putExtra("SPEEDS", newSpeeds);
        myIntent.putExtra("AVERAGE_SPEED", averageSpeed);
        myIntent.putExtra("TOPALT", highestAltitude);
        myIntent.putExtra("BOTALT", lowestAltitude);
        myIntent.putExtra("TOPSPEED", highestSpeed);
        myIntent.putExtra("BOTSPEED", lowestSpeed);
        RunTracker.this.startActivity(myIntent);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 100){
            if( grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                enableButtons();
            }
            else {
                verifyRuntimePermissions();
            }
        }
    }
}