package com.ucd.pepeclub.exerciseapp;

import android.app.AlertDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;


public class BackgroundDataBaseTasks extends AsyncTask<String,Void,String> {
    public FriendsCallback delegate = null;

    AlertDialog alertDialog;
    Context ctx;


    BackgroundDataBaseTasks(Context ctx)
    {
        this.ctx =ctx;
    }
    @Override
    protected void onPreExecute() {
        alertDialog = new AlertDialog.Builder(ctx).create();
        alertDialog.setTitle("Login Information....");
    }
    @Override
    protected String doInBackground(String... params) {
        String reg_user_url = "http://benjamin.ie/exerciseapp/add_user.php";
        String friend_search_url = "http://benjamin.ie/exerciseapp/friend_search.php";
        //String reg_user_url = "http://benjamin.ie/exerciseapp/reg_user.php";
        //String login_url = "http://10.0.2.2/webapp/login.php";
        String method = params[0];
        if (method.equals("register")) {
            String name = params[1];
            String id = params[2];
            System.out.println(name+", "+id);
            try {
                String data = "?"+URLEncoder.encode("id", "UTF-8") +"="+ URLEncoder.encode(id, "UTF-8") + "&" +
                        URLEncoder.encode("name", "UTF-8") +"="+  URLEncoder.encode(name.replace(' ', '_'), "UTF-8");
                String link = reg_user_url+data;
                System.out.println(link);
                URL url = new URL(link);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream OS = httpURLConnection.getOutputStream();
                OS.close();
                InputStream IS = httpURLConnection.getInputStream();
                IS.close();
                httpURLConnection.connect();
                httpURLConnection.disconnect();
                return "Registration Success...";
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else if(method.equals("friend"))
        {
            final int CONNECTION_TIMEOUT = 10000;
            final int READ_TIMEOUT = 15000;
            HttpURLConnection conn;
            String searchQuery = params[1];

            try {

                String data = "?"+URLEncoder.encode("search", "UTF-8") +"="+ searchQuery.replace(' ', '_');
                System.out.println(friend_search_url+data);
                URL url = new URL(friend_search_url+data);
                conn = (HttpURLConnection)url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setDoInput(true);
                OutputStream outputStream = conn.getOutputStream();
                outputStream.close();


            } catch (IOException e1) {
                e1.printStackTrace();
                return e1.toString();
            }

            try {

                int response_code = conn.getResponseCode();

                // Check if successful connection made
                if (response_code == HttpURLConnection.HTTP_OK) {

                    // Read data sent from server
                    InputStream input = conn.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                    StringBuilder result = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        result.append(line);
                    }
                    System.out.println(result.toString());
                    // Pass data to onPostExecute method
                    return (result.toString());

                } else {
                    return("Connection error");
                }

            } catch (IOException e) {
                e.printStackTrace();
                return e.toString();
            } finally {
                conn.disconnect();
            }
        }
        return null;
    }
    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }
    @Override
    protected void onPostExecute(String result) {

        if (result.equals("Registration Success...")) {
            Toast.makeText(ctx, result, Toast.LENGTH_LONG).show();
        } else if (result.equals("no rows")) {
            Toast.makeText(ctx, "No Results found for entered query", Toast.LENGTH_LONG).show();
        } else {

            delegate.processFinish(result);
        }
    }
}
