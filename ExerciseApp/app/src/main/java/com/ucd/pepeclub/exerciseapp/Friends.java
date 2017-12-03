package com.ucd.pepeclub.exerciseapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ToggleButton;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.ExecutionException;

public class Friends extends AppCompatActivity implements FriendsCallback {

    BackgroundDataBaseTasks backgroundTask = new BackgroundDataBaseTasks(this);
    private String friendSQL = "WHERE id=";


    @Override
    public void processFinish(String output) {
        // after getting DB RESULT JASON
        System.out.println("processFinish" + output);
        JSONArray jArray = null;
        try {
            jArray = new JSONArray(output);

            // Extract data from json and store into ArrayList
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject json_data = jArray.getJSONObject(i);
                points.add(new Entry(json_data.getString("name"), Integer.parseInt(json_data.getString("score"))));
            }
            System.out.println("processFinish-> points: " + points);

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
                (getApplicationContext(), R.layout.friends_grid, pointsList);

        gv.setAdapter(gridViewArrayAdapter);

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,friends");

    }

    public void onToggleClicked(View view) {
        boolean on = ((ToggleButton) view).isChecked();
        BackgroundDataBaseTasks tempTask = new BackgroundDataBaseTasks(this);
        points.clear();

        if (on) {
            String method = "friend";
            try {
                tempTask.delegate = this;
                tempTask.execute(method, "").get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        } else {
            String method = "friend";
            try {
                tempTask.delegate = this;
                tempTask.execute(method, friendSQL).get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

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
    private ArrayList<Entry> points = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        backgroundTask.delegate = this;


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

                            for (int i = 0; i < friends.length(); i++) {
                                String str = friends.get(i).toString();
                                str = str.replaceAll("[^0-9]", "");
                                friendSQL += str + " or id=";
                            }
                            friendSQL = friendSQL.substring(0, friendSQL.length() - 7);
                            friendSQL += ";";
                            System.out.println(friendSQL);

                            String method = "friend";
                            try {
                                backgroundTask.execute(method, friendSQL).get();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            }

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
                                (getApplicationContext(), R.layout.friends_grid, pointsList);

                        gv.setAdapter(gridViewArrayAdapter);
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,friends");
        request.setParameters(parameters);
        request.executeAsync();
    }
}