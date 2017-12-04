/*
Active Go

Sam Kennan 14320061,
Benjamin Kelly 14700869,
Eoin Kerr 13366801,
Darragh Mulhall 14318776
*/
package com.ucd.pepeclub.exerciseapp;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.support.annotation.Nullable;

// Followed roughly from this tutorial:
// https://www.youtube.com/watch?v=lvcGh2ZgHeA
public class GpsDataRetrieval extends Service {

    private LocationListener listener;
    private LocationManager locationManager;
    private int count = 1;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("MissingPermission")
    public void onCreate() {
        listener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                Intent intent = new Intent("location_update");
                long epoch = System.currentTimeMillis();
                intent.putExtra("coordinates",count++ + "," + location.getLongitude() + "," +
                                location.getLatitude() + "," + location.getAltitude() + "," + epoch);
                sendBroadcast(intent);
            }

            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {}

            @Override
            public void onProviderEnabled(String s) {}

            @Override
            public void onProviderDisabled(String s) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        };

        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 0, listener);
    }

    public void onDestroy() {
        super.onDestroy();
        if (locationManager != null) {
            locationManager.removeUpdates(listener);
        }
    }

}
