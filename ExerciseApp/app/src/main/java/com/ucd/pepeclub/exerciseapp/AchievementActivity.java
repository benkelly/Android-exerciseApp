package com.ucd.pepeclub.exerciseapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.share.ShareApi;
import com.facebook.share.model.*;
import com.facebook.share.widget.ShareDialog;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutionException;

public class AchievementActivity extends AppCompatActivity implements FriendsCallback {

    BackgroundDataBaseTasks backgroundTask = new BackgroundDataBaseTasks(this);
    private String friendSQL = "WHERE id=";
    private GridView gv;

    public int score = 0;

    private ArrayList<AchievementActivity.Entry> achievementLst = new ArrayList<>();
    class Entry {
        String achievement;
        int points;

        Entry(String name, int points) {
            this.achievement = name;
            this.points = points;
        }
    }


    private CallbackManager callbackManager;
    private ShareDialog shareDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        backgroundTask.delegate = this;
        SharedPreferences userInfo =  getSharedPreferences("user_info",
                Context.MODE_PRIVATE);
        String id = (userInfo.getString("id", ""));

        friendSQL += id + ";";

        achievementLst.add(new AchievementActivity.Entry("Couch Potato", 0));
        achievementLst.add(new AchievementActivity.Entry("Getting out!", 1));
        achievementLst.add(new AchievementActivity.Entry("Spring in your step!", 5));
        achievementLst.add(new AchievementActivity.Entry("Mastering this!", 50));
        achievementLst.add(new AchievementActivity.Entry("heh...", 69));
        achievementLst.add(new AchievementActivity.Entry("trail blazer", 420));
        achievementLst.add(new AchievementActivity.Entry("Waltz with da devil", 666));
        achievementLst.add(new AchievementActivity.Entry("YOU WIN!!!", 100000));


        String method = "friend";
        try {
            backgroundTask.execute(method, friendSQL).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_achievement);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        //The below is from the twitter dev tutorial
        //https://dev.twitter.com/twitterkit/android/installation
        Twitter.initialize(this);
        TwitterConfig config = new TwitterConfig.Builder(this)
                .logger(new DefaultLogger(Log.DEBUG))
                .twitterAuthConfig(new TwitterAuthConfig("CONSUMER_KEY", "CONSUMER_SECRET"))
                .debug(true)
                .build();
        Twitter.initialize(config);
        final TweetComposer.Builder builder = new TweetComposer.Builder(this);

        Button button;
        button = (Button) findViewById(R.id.button);
        shareDialog = new ShareDialog(this);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.text("Hey everyone! My score is now "+score+", on Active Go!");
                builder.show();
            }
        }
        );

    }

    @Override
    public void processFinish(String output) {
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

        ArrayList<String> gridList = new ArrayList<String>();
        for (AchievementActivity.Entry entry : achievementLst) {
            if(entry.points <= score) {
                gridList.add(entry.achievement);
                gridList.add("   " + entry.points);
            }
        }

        gv = (GridView) findViewById(R.id.grid_view);
        ArrayAdapter<String> gridViewArrayAdapter = new ArrayAdapter<String>
                (getApplicationContext(), R.layout.friends_grid, gridList);

        gv.setAdapter(gridViewArrayAdapter);

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,friends");


    }

    @Override
    public void userProcessFinish(String output) {
         processFinish(output);
        }
}

