package com.ucd.pepeclub.exerciseapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

public class MainMenu extends AppCompatActivity {


    private Button toFriendsMenu;
    private Button toRunTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        toFriendsMenu = findViewById(R.id.friends_button);
        toRunTracker = findViewById(R.id.runTracker_button);

        toFriendsMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),Friends.class);
                startActivity(i);
                //finish();
            }
        });

        toRunTracker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),RunTracker.class);
                startActivity(i);
                //finish();
            }
        });




    }

}
