package com.ucd.pepeclub.exerciseapp;

import android.content.Intent;
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
import android.widget.Button;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.share.ShareApi;
import com.facebook.share.internal.ShareFeedContent;
import com.facebook.share.model.*;
import com.facebook.share.widget.ShareDialog;
import com.twitter.sdk.android.core.DefaultLogger;
import com.twitter.sdk.android.core.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterConfig;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;

public class AchievementActivity extends AppCompatActivity {
    private CallbackManager callbackManager;
    private ShareDialog shareDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
                builder.text("just setting up my Twitter Kit.");
                builder.show();
                /*Intent share = new Intent(Intent.ACTION_SEND);
                share.putExtra(Intent.EXTRA_TEXT, "text");
                startActivity(Intent.createChooser(share, "Share this via"));*/

                /*ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse("https://twitter.com/Renvark/status/913042367904718848")).setContentDescription("test").build();
                shareDialog.show(content);*/
            }
        });

    }

}

