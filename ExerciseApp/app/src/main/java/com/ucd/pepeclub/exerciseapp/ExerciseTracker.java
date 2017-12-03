package com.ucd.pepeclub.exerciseapp;

import android.Manifest;
import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class ExerciseTracker extends Fragment {

    BackgroundDataBaseTasks backgroundTask;

    private Button button;
    private BroadcastReceiver broadcastReceiver;
    private TextView timerTextView;
    private boolean running;

    private ArrayList<Double> speeds = new ArrayList<>();
    private ArrayList<Double> altitudes = new ArrayList<>();
    private double distance = 0;

    private double previousLatitude = -1;
    private double previousLongitude = -1;
    private long previousTime = -1;
    private double averageSpeed = -1;

    private int dataPoints = 0;
    private double highestAltitude = -1;
    private double lowestAltitude = 90000000;
    private double highestSpeed = -1;
    private double lowestSpeed = 90000000;
    protected String EXERCISE_NAME = "EXERCISE_NAME";
    protected double MAX_AVERAGE_SPEED = 1000000;

    private long startTime = 0;
    private long SHORTEST_EXERCISE_TIME = 30; // seconds

    private Handler timerHandler = new Handler();
    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            long millis = System.currentTimeMillis() - startTime;
            int seconds = (int) (millis / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;
            int hours = minutes / 60;
            minutes = minutes % 60;

            timerTextView.setText(String.format(Locale.ENGLISH, "%02d:%02d:%02d", hours, minutes, seconds));

            timerHandler.postDelayed(this, 500);
        }
    };

    MyReceiver r;
    public void refresh() {
        resetActivity();
    }

    public void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).unregisterReceiver(r);
    }

    private class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ExerciseTracker.this.refresh();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_exercise_timer_tab, container, false);

        button = rootView.findViewById(R.id.button);
        timerTextView = rootView.findViewById(R.id.timerTextView);

        resetActivity();

        if(!verifyRuntimePermissions())
            enableButtons();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        r = new MyReceiver();
        LocalBroadcastManager.getInstance(getActivity().getApplicationContext()).registerReceiver(r,
                new IntentFilter("TAG_REFRESH"));
        if(broadcastReceiver == null){
            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String res = (String)intent.getExtras().get("coordinates");

                    String[] data = res.split(",");

                    dataPoints = Integer.parseInt(data[0]);
                    double lat = Double.parseDouble(data[1]);
                    double lon = Double.parseDouble(data[2]);
                    double alt = Double.parseDouble(data[3]);
                    long epoch = Long.parseLong(data[4]);

                    if (lat == previousLatitude && lon == previousLongitude) {
                        getActivity().registerReceiver(broadcastReceiver,new IntentFilter("location_update"));
                        return;
                    }

                    if (previousTime != -1) {
                        double thisDist = calculateDistance(lat, lon);
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

                    previousLatitude = lat;
                    previousLongitude = lon;
                    previousTime = epoch;
                }
            };
        }
        getActivity().registerReceiver(broadcastReceiver, new IntentFilter("location_update"));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(broadcastReceiver != null){
            getActivity().unregisterReceiver(broadcastReceiver);
        }
    }

    private double calculateDistance(double lat, double lon) {
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

    private void calculateExerciseInfo(long exerciseTime) {
        averageSpeed = distance / exerciseTime; // metres per second
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
                Intent i = new Intent(getActivity(),GpsService.class);

                if (!running) {
                    getActivity().startService(i);
                    startTime = System.currentTimeMillis();
                    timerHandler.postDelayed(timerRunnable, 0);
                    button.setText(R.string.runtracker_stop_button);
                    running = true;
                }
                else {
                    getActivity().stopService(i);
                    timerHandler.removeCallbacks(timerRunnable);

                    if (dataPoints < 13) {
                        resetActivity();

                        String msg = "    Not enough GPS data collected.\nEnsure your GPS settings are correct.";
                        createToast(msg);
                        return;
                    }

                    String time = timerTextView.getText().toString();
                    long timeInSeconds = getSecondsFromTime(time);
                    // if time < X seconds don't bother analysing
                    if (timeInSeconds > SHORTEST_EXERCISE_TIME) {
                        calculateExerciseInfo(timeInSeconds);
                        if (averageSpeed > MAX_AVERAGE_SPEED) {
                            String msg = "Your average speed was too fast for a " + EXERCISE_NAME + "!";
                            createToast(msg);
                        }
                        else {
                            int points = calculatePoints();

                            if (isNetworkAvailable()) {
                                postPointToDataBase(points);
                            }
                            else {
                                SharedPreferences sp = getActivity().getSharedPreferences("your_prefs", Activity.MODE_PRIVATE);
                                int pointsStored = sp.getInt("POINTS_TO_SEND", 0);

                                SharedPreferences.Editor editor = sp.edit();
                                editor.putInt("POINTS_TO_SEND", pointsStored + points);
                                editor.apply();
                            }

                            createAnalysisActivity(points);
                        }
                    }
                    else {
                        String msg = "Not enough GPS points were captured to award points.";
                        createToast(msg);
                    }

                    resetActivity();
                }
            }
        });
    }

    private void createToast(String message) {
        Context context = getActivity().getApplicationContext();
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, message, duration);
        toast.show();
    }

    private void resetActivity() {
        running = false;
        timerHandler.removeCallbacks(timerRunnable);
        timerTextView.setText(R.string.runtracker_start_time);
        button.setText(R.string.runtracker_start_button);

        startTime = 0;
        distance = 0;
        previousLatitude = -1;
        previousLongitude = -1;
        previousTime = -1;
        averageSpeed = -1;
        highestAltitude = -1;
        lowestAltitude = 90000000;
        highestSpeed = -1;
        lowestSpeed = 90000000;
    }

    private int calculatePoints() {
        int temp = (int)(distance / averageSpeed);
        return temp / 20;
    }

    private void postPointToDataBase(int points) {
        System.out.println("postPointToDataBase: "+points);


        SharedPreferences userInfo =  getActivity().getSharedPreferences("user_info",
                Context.MODE_PRIVATE);

        String id = (userInfo.getString("id", ""));
        //String name = (userInfo.getString("name", ""));
        //System.out.println("id: "+id+", name: "+name);
        String score = Integer.toString(points);


        //send id and score to php for new user storage
        String method = "post_score";

        backgroundTask.execute(method,score,id);
    }

    private boolean verifyRuntimePermissions() {
        if (Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){

            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},100);

            return true;
        }
        return false;
    }

    protected void createAnalysisActivity(int points) {
        double[] newAlts = new double[altitudes.size()];
        for (int i = 0; i < newAlts.length; i++) {
            newAlts[i] = altitudes.get(i);
        }

        double[] newSpeeds = new double[speeds.size()];
        for (int i = 0; i < newSpeeds.length; i++) {
            newSpeeds[i] = speeds.get(i);
        }

        Intent myIntent = new Intent(getActivity(), ExerciseAnalysis.class);
        myIntent.putExtra("DISTANCE", distance);
        myIntent.putExtra("ALTITUDES", newAlts);
        myIntent.putExtra("SPEEDS", newSpeeds);
        myIntent.putExtra("AVERAGE_SPEED", averageSpeed);
        myIntent.putExtra("TOPALT", highestAltitude);
        myIntent.putExtra("BOTALT", lowestAltitude);
        myIntent.putExtra("TOPSPEED", highestSpeed);
        myIntent.putExtra("BOTSPEED", lowestSpeed);
        myIntent.putExtra("POINTS", points);
        ExerciseTracker.this.startActivity(myIntent);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
        return activeNetworkInfo != null;
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