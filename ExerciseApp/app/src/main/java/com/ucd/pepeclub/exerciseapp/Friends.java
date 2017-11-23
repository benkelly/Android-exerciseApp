package com.ucd.pepeclub.exerciseapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Friends extends AppCompatActivity {

    class Entry {
        String name;
        int points;

        Entry(String name, int points) {
            this.name = name;
            this.points = points;
        }
    }

    public class EntryComparator implements Comparator<Entry> {
        @Override
        public int compare(Entry o1, Entry o2) {
            int a = o1.points;
            int b = o2.points;

            return a > b ? -1 : a < b ? 1 : 0;
        }
    }

    private JSONArray friends;
    private GridView gv;
    private ArrayList<Entry> points;

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
                            points = new ArrayList<>();

                            Random r = new Random();
                            for (int i = 0; i < friends.length(); i++) {
                                String name = friends.getJSONObject(i).getString("name");
                                // for the time being use random number as points
                                // TODO: get points from server
                                int randomInt = r.nextInt(100);
                                points.add(new Entry(name, randomInt));
                            }

                            //<------------------------>
                            //send friends string to php to get names and points
                            //fill points hashmap with user and correspondng points
                            //<------------------------>

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Collections.sort(points, new EntryComparator());

                        ArrayList<String> pointsList = new ArrayList<String>();
                        for (Entry entry : points) {
                            pointsList.add(entry.name);
                            pointsList.add("   " + entry.points);
                        }

                        gv = (GridView) findViewById(R.id.grid_view);
                        ArrayAdapter<String> gridViewArrayAdapter = new ArrayAdapter<String>
                                (getApplicationContext() ,R.layout.friends_grid, pointsList);

                        gv.setAdapter(gridViewArrayAdapter);

                    }
                });
            Bundle parameters = new Bundle();
            parameters.putString("fields", "id,name,friends");
            request.setParameters(parameters);
            request.executeAsync();
    }
}
