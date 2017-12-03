package com.ucd.pepeclub.exerciseapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class MainMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        Button toFriendsMenu;
        Button toRunTracker;
        Button toAchievements;
        Button logoutFacebook;

        toFriendsMenu = findViewById(R.id.friends_button);
        toRunTracker = findViewById(R.id.runTracker_button);
        toAchievements = findViewById(R.id.achievements_button);
        logoutFacebook = findViewById(R.id.logout_facebook_button);


        toFriendsMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), Friends.class);
                startActivity(i);
            }
        });

        toRunTracker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),ExerciseTabs.class);
                startActivity(i);
            }
        });

        toAchievements.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), AchievementActivity.class);
                startActivity(i);
            }
        });

        logoutFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), FacebookLogin.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                LoginManager.getInstance().logOut();
                Toast toast = Toast.makeText(getApplicationContext(), "Logout successful!", Toast.LENGTH_SHORT);
                toast.show();
                startActivity(intent);
            }
        });

    }

}
