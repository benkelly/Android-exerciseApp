/*
Active Go

Sam Kennan 14320061,
Benjamin Kelly 14700869,
Eoin Kerr 13366801,
Darragh Mulhall 14318776
*/
package com.ucd.pepeclub.exerciseapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.login.LoginManager;

public class MainMenu extends AppCompatActivity {
    //the main menu screen where other activities are executed
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
                finish();
            }
        });

        SharedPreferences sp = getSharedPreferences("your_prefs", Activity.MODE_PRIVATE);
        int pointsStored = sp.getInt("POINTS_TO_SEND", 0);

        if (pointsStored > 0) {
            BackgroundDataBaseTasks backgroundTask = new BackgroundDataBaseTasks(this);
            SharedPreferences userInfo =  getSharedPreferences("user_info", Context.MODE_PRIVATE);

            String id = (userInfo.getString("id", ""));
            String method = "post_score";
            backgroundTask.execute(method,"" + pointsStored,id);
        }
    }
}