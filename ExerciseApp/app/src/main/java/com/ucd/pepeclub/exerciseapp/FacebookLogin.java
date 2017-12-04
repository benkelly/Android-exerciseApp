/*
Active Go

Sam Kennan 14320061,
Benjamin Kelly 14700869,
Eoin Kerr 13366801,
Darragh Mulhall 14318776
*/
package com.ucd.pepeclub.exerciseapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.util.Arrays;

//allows login to the app using the facebook login feature of the facebook sdk
public class FacebookLogin extends AppCompatActivity {

    BackgroundDataBaseTasks backgroundTask = new BackgroundDataBaseTasks(this);

    CallbackManager callbackManager;
    LoginButton loginButton;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean loggedIn = AccessToken.getCurrentAccessToken() != null;
        //if the user is already logged in skip login prompt and go straight to main menu
        if (loggedIn) {
            Intent intent = new Intent(FacebookLogin.this, MainMenu.class);
            startActivity(intent);
            finish();
        }
        else {
            setContentView(R.layout.activity_facebook_login);

            loginButton = (LoginButton) findViewById(R.id.login_button);
            loginButton.setReadPermissions(Arrays.asList("public_profile", "user_friends"));
            callbackManager = CallbackManager.Factory.create();
            loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                //if the login succeeds store id and name of user and go to main menu
                @Override
                public void onSuccess(LoginResult loginResult) {
                    loginButton.setVisibility(View.GONE);

                    GraphRequest request = GraphRequest.newMeRequest(
                            loginResult.getAccessToken(),
                            new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(JSONObject jsonObject,
                                                        GraphResponse response) {
                                    try {
                                        String id = jsonObject.getString("id");
                                        String name = jsonObject.getString("name");


                                        SharedPreferences userInfo = getSharedPreferences("user_info",
                                                Context.MODE_PRIVATE);

                                        SharedPreferences.Editor editor = userInfo.edit();
                                        editor.putString("id", id);
                                        editor.putString("name", name);
                                        editor.commit();

                                        //send id and name to php for new user storage
                                        String method = "register";

                                        backgroundTask.execute(method, name, id);


                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });

                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "id,name,friends");
                    request.setParameters(parameters);
                    request.executeAsync();

                    Intent intent = new Intent(FacebookLogin.this, MainMenu.class);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onCancel() {

                }

                @Override
                public void onError(FacebookException error) {
                    if (!isNetworkAvailable()) {
                        Toast toast = Toast.makeText(getApplicationContext(), "No internet access! ", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                }
            });
        }
    }

    //checks is network available
    private boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    //required as part of facebook login to work
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // if you don't add following block,
        // your registered `FacebookCallback` won't be called

        if (callbackManager.onActivityResult(requestCode, resultCode, data)) {
            return;
        }
    }
}