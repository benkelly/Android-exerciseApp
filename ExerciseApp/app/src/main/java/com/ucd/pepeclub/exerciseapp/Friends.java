package com.ucd.pepeclub.exerciseapp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.FractionRes;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Friends extends AppCompatActivity {

    private JSONArray friends;
    private HashMap<String, Integer> points;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);


        SharedPreferences settings = getSharedPreferences("userInfo",
                Context.MODE_PRIVATE);



        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject jsonObject,
                                            GraphResponse response) {
                        try {
                            JSONObject js = (JSONObject) jsonObject.get("friends");
                            friends = (JSONArray) js.get("data");
                            String friendsToString = friends.toString();
                            points = new HashMap<>();

                            //hard coding values for test

                            points.put("dar", 10);
                            points.put("slim", -1000);
                            points.put("al", 1000);

                            //<------------------------>
                            //send friends string to php to get names and points
                            //fill points hashmap with user and correspondng points
                            //<------------------------>


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        String[] items = new String[points.size()];
                        int count = 0;
                        for (Map.Entry<String, Integer> entry : points.entrySet()) {
                            String info = "";
                            info += entry.getKey() + "\t\t" + entry.getValue() + " points";
                            items[count++] = info;
                        }

                        ArrayAdapter adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.friends_listview_layout, items);
                        listView = findViewById(R.id.friends_listview);
                        listView.setAdapter(adapter);
                    }
                });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,friends");
            request.setParameters(parameters);
            request.executeAsync();
    }
}
