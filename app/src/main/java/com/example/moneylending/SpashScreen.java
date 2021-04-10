package com.example.moneylending;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.provider.SyncStateContract;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class SpashScreen extends AppCompatActivity {
    String id, auth_key;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spash_screen);

        if (isNetworkAvailable()) {
            // Getting user details from DB
            DBHelper DB = new DBHelper(SpashScreen.this);
            //DB.insertUserData("d05ae835-d112-4029-885b-a8571929faca","AGVJnLrGvVWRk0scp9bXG+pqfuC6XhcMhD9hGEzfx1ZANFQY+Ps1ImBeJU9t/ZyEfGz/S4Zweg23ffIpp4FiOQfnGA==");
            DB.deleteUserData("ee25e5b9-a443-4b9d-9afe-a0015cf73a6e");
            Cursor cursor = DB.getData();
            if(cursor.getCount()==0){
                // No existing account on device, redirect to account screen
                goToAccountScreenWithDelay();
            }else{
                cursor.moveToFirst();

                this.id = cursor.getString(cursor.getColumnIndex("id"));
                this.auth_key = cursor.getString(cursor.getColumnIndex("auth_key"));
                String url = "https://money-lending-app.herokuapp.com/api/login";

                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        response -> {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if(!(boolean)jsonObject.get("log_in_status")) goToAccountScreenNoDelay(); //Credentials didn't match, redirect user to account login
                                else{
                                    boolean b = DB.updateUserData(id,auth_key,String.valueOf(jsonObject.get("transact_id")), jsonObject.getInt("wallet"));
                                    if(!b)
                                        new AlertDialog.Builder(SpashScreen.this)
                                                .setTitle("Error:Transact id")
                                                .setMessage("An error occurred.")
                                                .setCancelable(false)
                                                .setPositiveButton("OK", (dialog, which) -> finish()).show();
                                    goToHomeScreen(); //Log the user in to main screen
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        },
                        error -> new AlertDialog.Builder(SpashScreen.this)
                                .setTitle("Error")
                                .setMessage(error.toString())
                                .setCancelable(false)
                                .setPositiveButton("OK", (dialog, which) -> finish()).show()){
                    @NotNull
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map <String, String> params = new HashMap<>();
                        params.put("id",id);
                        params.put("auth_key",auth_key);
                        return  params;
                    }
                };
                RequestQueue requestQueue = Volley.newRequestQueue(SpashScreen.this);
                requestQueue.add(stringRequest);
            }
        } else {
            if (!isFinishing()){
                new AlertDialog.Builder(SpashScreen.this)
                        .setTitle("No Internet Connection")
                        .setMessage("Please check your internet connectivity and try again")
                        .setCancelable(false)
                        .setPositiveButton("ok", (dialog, which) -> finish()).show();
            }
        }
        }

    private void goToAccountScreenWithDelay(){
        // Wait for 2 secs then go to next activity
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SpashScreen.this, AccountScreen.class);
            startActivity(intent);
            finish();
        }, 2000);
    }
    private void goToAccountScreenNoDelay(){
        // Wait for 2 secs then go to next activity
            Intent intent = new Intent(SpashScreen.this, AccountScreen.class);
            startActivity(intent);
            finish();
    }
    private void goToHomeScreen(){
        // Wait for 2 secs then go to next activity
        Intent intent = new Intent(SpashScreen.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
    }

}

