package com.example.moneypayment;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.moneylending.R;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class login extends AppCompatActivity {
    Button login;
    String phone, password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        login = findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phone = ((EditText)findViewById(R.id.login_phone)).getText().toString();
                password = ((EditText)findViewById(R.id.login_password)).getText().toString();
                //Toast.makeText(login.this, phone+"\n"+password, Toast.LENGTH_SHORT).show();
                // Make the call to the server
                findViewById(R.id.login_progressBar).setVisibility(View.VISIBLE);
                String url = "https://money-lending-app.herokuapp.com/api/getKeys";

                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        response -> {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if(!(boolean)jsonObject.get("account_status")){
                                    new AlertDialog.Builder(login.this)
                                            .setTitle("Couldn't sign in")
                                            .setMessage("Account doesn't exist")
                                            .setCancelable(false)
                                            .setPositiveButton("ok", (dialog, which) -> goToAccountActivity()).show();
                                }
                                else{
                                    String id = String.valueOf(jsonObject.get("id"));
                                    String auth_key = String.valueOf(jsonObject.get("auth_key"));
                                    //Toast.makeText(login.this, id+"\n"+auth_key, Toast.LENGTH_SHORT).show();

                                    String loginUrl = "https://money-lending-app.herokuapp.com/api/login";
                                    StringRequest strRequest = new StringRequest(Request.Method.POST, loginUrl,
                                            new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String resp) {
                                                    try {
                                                        JSONObject json = new JSONObject(resp);
                                                        if(!(boolean)json.get("log_in_status")){
                                                            //Credentials didn't match, redirect user to account login
                                                            new AlertDialog.Builder(login.this)
                                                                    .setTitle("Error:Couldn't log in")
                                                                    .setMessage(String.valueOf(json.get("message")))
                                                                    .setCancelable(false)
                                                                    .setPositiveButton("ok", (dialog, which) -> goToAccountActivity()).show();
                                                        }
                                                        else{
                                                            DBHelper DB = new DBHelper(login.this);
                                                            boolean b = DB.insertUserData(id,auth_key,String.valueOf(json.get("transact_id")), json.getInt("wallet"));
                                                            if(!b)
                                                                new AlertDialog.Builder(login.this)
                                                                        .setTitle("Error:Transact id")
                                                                        .setMessage("An error occurred.")
                                                                        .setCancelable(false)
                                                                        .setPositiveButton("ok", (dialog, which) -> finish()).show();

                                                            goToHomeScreen(String.valueOf(json.get("wallet"))); //Log the user in to main screen
                                                        }
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            },
                                            new Response.ErrorListener() {
                                                @Override
                                                public void onErrorResponse(VolleyError err) {
                                                    Toast.makeText(login.this, err.toString(), Toast.LENGTH_SHORT).show();
                                                }
                                            }){
                                        @NotNull
                                        @Override
                                        protected Map<String, String> getParams() throws AuthFailureError {
                                            Map <String, String> param = new HashMap<>();
                                            param.put("id",id);
                                            param.put("auth_key", auth_key);
                                            return  param;
                                        }
                                    };
                                    RequestQueue re = Volley.newRequestQueue(login.this);
                                    re.add(strRequest);

                                    //
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        },
                        error -> Toast.makeText(login.this, error.toString(), Toast.LENGTH_SHORT).show()){
                    @NotNull
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map <String, String> params = new HashMap<>();
                        params.put("phone",phone);
                        params.put("password", password);
                        return  params;
                    }
                };
                RequestQueue requestQueue = Volley.newRequestQueue(login.this);
                requestQueue.add(stringRequest);
            }
        });
    }
    public void goToSignupActivity(View view) {
        Intent intent = new Intent(login.this, signup.class);
        startActivity(intent);
        finish();
    }
    public void goToAccountActivity() {
        Intent intent = new Intent(login.this, AccountScreen.class);
        startActivity(intent);
        finish();
    }
    private void goToHomeScreen(String wallet){
        // Wait for 2 secs then go to next activity
        Intent intent = new Intent(login.this, MainActivity.class);
        intent.putExtra("wallet", wallet);
        startActivity(intent);
        finish();
    }
}