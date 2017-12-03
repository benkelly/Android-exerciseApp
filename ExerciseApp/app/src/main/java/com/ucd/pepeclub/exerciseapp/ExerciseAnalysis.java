package com.ucd.pepeclub.exerciseapp;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;
import java.util.Locale;

public class ExerciseAnalysis extends AppCompatActivity {

    private TextView distanceBox;
    private TextView avgSpeedBox;

    private LineChart speedGraph;
    private LineChart altGraph;

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_run_analysis);
        distanceBox = findViewById(R.id.distanceBox);
        avgSpeedBox = findViewById(R.id.avgSpeedBox);

        speedGraph = findViewById(R.id.speedGraph);
        altGraph = findViewById(R.id.altGraph);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        double distance = -1;
        double averageSpeed = -1;
        double[] altitudes = null;
        double[] speeds = null;
        double highestAltitude = -1;
        double lowestAltitude = -1;
        double highestSpeed = -1;
        double lowestSpeed = -1;
        int points = 0;

        Bundle extras = getIntent().getExtras();
        if (extras == null) {
            finish();
        }
        else {
            distance = extras.getDouble("DISTANCE");
            averageSpeed = extras.getDouble("AVERAGE_SPEED");
            altitudes = extras.getDoubleArray("ALTITUDES");
            speeds = extras.getDoubleArray("SPEEDS");
            highestAltitude = extras.getDouble("TOPALT");
            lowestAltitude = extras.getDouble("BOTALT");
            highestSpeed = extras.getDouble("TOPSPEED");
            lowestSpeed = extras.getDouble("BOTSPEED");
            points = extras.getInt("POINTS");
        }

        setTitle("You earned " + points + " points!");

        distanceBox.setText(String.format(Locale.ENGLISH,"%.2f", distance/1000) + " km");
        avgSpeedBox.setText(String.format(Locale.ENGLISH,"%.2f", averageSpeed) + " m/s");

        createSpeedGraph(lowestSpeed, highestSpeed, speeds);
        createAltitudeGraph(lowestAltitude, highestAltitude, altitudes);
    }

    private void createSpeedGraph(double lowestSpeed, double highestSpeed, double[] speeds) {
        // average the speeds
        ArrayList<Double> newSpeeds = new ArrayList<>();
        for (int i = 0; i < speeds.length; i+=5) {
            if (i+4 > speeds.length-1) break;
            double sum = speeds[i] + speeds[i + 1] + speeds[i + 2] + speeds[i + 3] + speeds[i + 4];
            newSpeeds.add(sum / 5);
        }

        int count = 0;
        int stepCount = 20;

        if (newSpeeds.size() < stepCount) {
            stepCount = newSpeeds.size();
        }

        String[] xAxis = new String[stepCount + 1];
        ArrayList<Entry> yAxis = new ArrayList<>();
        double speedStep = (highestSpeed - lowestSpeed) / stepCount;

        for (float i = (float)lowestSpeed; i < highestSpeed; i += speedStep) {
            if (count >= xAxis.length || count >= newSpeeds.size()) break;

            xAxis[count] = "";
            yAxis.add(new Entry(count, (float)(double)newSpeeds.get(count++)));
        }

        LineDataSet temp = new LineDataSet(yAxis, "Speed (metres/second)");
        temp.setDrawCircles(false);
        temp.setColor(Color.BLUE);
        temp.setDrawValues(false);
        XAxis xaxis = speedGraph.getXAxis();
        xaxis.setEnabled(false);
        YAxis yaxis = speedGraph.getAxisRight();
        yaxis.setEnabled(false);
        Description d = new Description();
        d.setText("");
        speedGraph.setDescription(d);
        temp.setLineWidth(2.5f);
        speedGraph.setContentDescription("");
        speedGraph.setData(new LineData(temp));
    }

    private void createAltitudeGraph(double lowestAltitude, double highestAltitude, double[] altitudes) {
        int count = 0;
        int stepCount = 20;
        String[] xAxis = new String[stepCount + 1];
        ArrayList<Entry> yAxis = new ArrayList<>();
        double altStep = (highestAltitude - lowestAltitude) / stepCount;

        for (float i = (float)lowestAltitude; i < highestAltitude; i += altStep) {
            if (count >= xAxis.length || count >= altitudes.length) break;

            xAxis[count] = "";
            yAxis.add(new Entry(count, (float)altitudes[count++]));
        }

        LineDataSet temp = new LineDataSet(yAxis, "Altitude Change (metres)");
        temp.setDrawCircles(false);
        temp.setColor(Color.RED);
        temp.setDrawValues(false);
        XAxis xaxis = altGraph.getXAxis();
        xaxis.setEnabled(false);
        YAxis yaxis = altGraph.getAxisRight();
        yaxis.setEnabled(false);
        Description d = new Description();
        d.setText("");
        altGraph.setDescription(d);
        temp.setLineWidth(2.5f);
        altGraph.setContentDescription("");
        altGraph.setData(new LineData(temp));
    }


}
