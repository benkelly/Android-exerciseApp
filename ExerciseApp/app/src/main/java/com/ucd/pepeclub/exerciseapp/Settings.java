package com.ucd.pepeclub.exerciseapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.login.LoginManager;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle("Settings");

        Button logoutFacebook = findViewById(R.id.logoutFacebook);
        Button refreshFriends = findViewById(R.id.refreshFriends);
        Button about = findViewById(R.id.about);

        logoutFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), FacebookLogin.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                LoginManager.getInstance().logOut();
                Toast toast = Toast.makeText(getApplicationContext(), "Logout successful", Toast.LENGTH_SHORT);
                toast.show();
                startActivity(intent);
            }
        });

        refreshFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // send request to webservice
                // toast
            }
        });

        about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent i = new Intent(getApplicationContext(),About.class);
//                startActivity(i);
                // some sort of description/author stuff could go in here??
            }
        });
    }
}
