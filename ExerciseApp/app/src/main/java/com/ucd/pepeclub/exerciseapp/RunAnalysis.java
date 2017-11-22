package com.ucd.pepeclub.exerciseapp;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;

public class RunAnalysis extends AppCompatActivity {

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
        }

        distanceBox.setText(distance/1000 + " km");
        avgSpeedBox.setText(averageSpeed + " m/s");

        int count = 0;
        int stepCount = 20;
        String[] xAxis = new String[stepCount + 1];
        ArrayList<Entry> yAxis = new ArrayList<>();
        double altStep = (highestAltitude - lowestAltitude) / stepCount;
        for (float i = (float)lowestAltitude; i < highestAltitude; i += altStep) {
            // i know this looks bad
            xAxis[count] = "";
            yAxis.add(new Entry(i, count++));
        }

        LineDataSet temp = new LineDataSet(yAxis, "");
        temp.setDrawCircles(false);
        temp.setColor(Color.RED);

        altGraph.setData(new LineData(temp));

    }



}
