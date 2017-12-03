package com.ucd.pepeclub.exerciseapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.widget.ProfilePictureView;
import com.facebook.share.model.ShareLinkContent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.ExecutionException;

public class Friends extends AppCompatActivity implements  FriendsCallback{

    BackgroundDataBaseTasks backgroundTask = new BackgroundDataBaseTasks(this);
    private String friendSQL = "WHERE id=";

    @Override
    public void processFinish(String output) {
        // after getting DB RESULT JASON
        System.out.println("processFinish"+output);
        JSONArray jArray = null;
        SharedPreferences userInfo =  getSharedPreferences("user_info",
                Context.MODE_PRIVATE);
        String id = (userInfo.getString("id", ""));

        boolean userFound = false;

        try {
            jArray = new JSONArray(output);
            // Extract data from json and store into ArrayList
            for (int i = 0; i < jArray.length(); i++) {
                JSONObject json_data = jArray.getJSONObject(i);
                points.add(new Entry(json_data.getString("name"), Integer.parseInt(json_data.getString("score")), json_data.getString("id")));
                if(points.get(i).id.equals(id)){
                    userFound = true;
                }
            }
            //user is not included in current users
            if(!userFound){
                points.add(new Entry(userInfo.getString("name", ""), score, userInfo.getString("id", "")));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }




        Collections.sort(points, new EntryComparator());


        ArrayList<User> users = new ArrayList<>();





        for(int i=0; i<points.size(); i++){
            users.add(new User("#"+(i+1), points.get(i).name, Integer.toString(points.get(i).points), points.get(i).id));
            if(users.get(i).getId().equals(id)){
                userRank = i+1;
            }

        }


        rankDisplay.setText("Rank #"+userRank+"/"+users.size());

        adapter.setUserList(users);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void userProcessFinish(String output) {
        // after getting DB RESULT JASON
        System.out.println("processFinish" + output);
        JSONArray jArray = null;
        try {
            jArray = new JSONArray(output);

            // Extract data from json and store into ArrayList
            JSONObject json_data = jArray.getJSONObject(0);
            score = Integer.parseInt(json_data.getString("score"));

            System.out.println("processFinish-> points: " + score);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SharedPreferences userInfo =  getSharedPreferences("user_info",
                Context.MODE_PRIVATE);


        userName = (TextView) findViewById(R.id.user_name);
        userName.setText(userInfo.getString("name", "DEFAULT"));
        pointsDisplay = (TextView) findViewById(R.id.points_display);
        pointsDisplay.setText(score + " points");
        rankDisplay = (TextView) findViewById(R.id.rank_display);
        leaderboardType = (TextView) findViewById(R.id.leaderboard_type);
        leaderboardType.setText("Friends Leaderboard");

        finder = (Button) findViewById(R.id.finder);
        finder.setText("Find me");

        //scrolls to user in the list
        finder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println(userRank);
                llm.scrollToPosition(userRank-1);
            }
        });


        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        llm = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(llm);
        adapter = new LeaderboardAdapter(getApplicationContext(), new ArrayList<User>());
        recyclerView.setAdapter(adapter);

    }

    public void onToggleClicked(View view) {
        boolean on = ((ToggleButton) view).isChecked();
        BackgroundDataBaseTasks tempTask = new BackgroundDataBaseTasks(this);
        points.clear();

        if (on) {
            leaderboardType.setText("Global Leaderboard");
            String method = "friend";
            try {
                tempTask.delegate = this;
                tempTask.execute(method, "").get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            leaderboardType.setText("Friends Leaderboard");
            String method = "friend";
            try {
                tempTask.delegate = this;
                tempTask.execute(method, friendSQL).get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    class Entry {
        String name;
        int points;
        String id;

        Entry(String name, int points, String id) {
            this.name = name;
            this.points = points;
            this.id = id;
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
    private RecyclerView recyclerView;
    private LinearLayoutManager llm;
    private LeaderboardAdapter adapter;
    private ArrayList<Entry> points = new ArrayList<>();



    private TextView userName;
    private TextView pointsDisplay;
    private TextView rankDisplay;
    private TextView leaderboardType;
    private Button finder;

    private int score =0;
    private int userRank;


    private void getUsersScore() {
        BackgroundDataBaseTasks tempTask =new BackgroundDataBaseTasks(this);
        tempTask.delegate = this;
        SharedPreferences userInfo =  getSharedPreferences("user_info",
                Context.MODE_PRIVATE);
        String id = (userInfo.getString("id", ""));
        String SQL = "WHERE id="+ id + ";";

        String method = "friend";
        try {
            tempTask.execute(method, SQL).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //getUsersScore();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);

        backgroundTask.delegate = this;


        ProfilePictureView profilePictureView;
        profilePictureView = (ProfilePictureView) findViewById(R.id.profilePicture);

        final SharedPreferences userInfo =  getSharedPreferences("user_info",
                Context.MODE_PRIVATE);

        String id = userInfo.getString("id", "DEFAULT");
        profilePictureView.setProfileId(id);

        getUsersScore();

        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject jsonObject,
                                            GraphResponse response) {
                        if (jsonObject != null) {
                            try {
                                JSONObject js = (JSONObject) jsonObject.get("friends");
                                if (js != null) {
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
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,friends");
        request.setParameters(parameters);
        request.executeAsync();
    }
}