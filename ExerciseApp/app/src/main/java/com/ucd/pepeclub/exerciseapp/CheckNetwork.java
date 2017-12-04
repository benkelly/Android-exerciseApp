/*
Active Go

Sam Kennan 14320061,
Benjamin Kelly 14700869,
Eoin Kerr 13366801,
Darragh Mulhall 14318776
*/
package com.ucd.pepeclub.exerciseapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


public class CheckNetwork {
    private Context context;

    public CheckNetwork(Context context) {
        this.context = context;
    }
    //looks for an active network connection
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}
