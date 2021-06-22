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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.moneylending.R;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class signup extends AppCompatActivity {
    Button btn_sign_up;
    String name, phone, password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        btn_sign_up = findViewById(R.id.sign_up);
        btn_sign_up.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phone = ((EditText)findViewById(R.id.signup_phone)).getText().toString();
                name = ((EditText)findViewById(R.id.signup_name)).getText().toString();
                password = ((EditText)findViewById(R.id.sign_up_password)).getText().toString();
                //Toast.makeText(signup.this, phone+"\n"+name+"\n"+password, Toast.LENGTH_SHORT).show();
                findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
                DBHelper DB = new DBHelper(signup.this);
                String url = "https://money-lending-app.herokuapp.com/api/signup";

                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        response -> {
                            try {
                                JSONObject jsonObject = new JSONObject(response);
                                if(!(boolean)jsonObject.get("log_in_status")){
                                    new AlertDialog.Builder(signup.this)
                                            .setTitle("Couldn't sign up")
                                            .setMessage(String.valueOf(jsonObject.get("status")))
                                            .setCancelable(false)
                                            .setPositiveButton("ok", (dialog, which) -> goToAccountActivity()).show();
                                }
                                else{
                                    String id = String.valueOf(jsonObject.get("id"));
                                    String auth_key = String.valueOf(jsonObject.get("auth_key"));
                                    String transact_id = String.valueOf(jsonObject.get("transact_id"));
                                    if(!DB.insertUserData(id, auth_key, transact_id, 0))
                                        new AlertDialog.Builder(signup.this)
                                                .setTitle("Error occurred.")
                                                .setMessage("Error:user details")
                                                .setCancelable(false)
                                                .setPositiveButton("ok", (dialog, which) -> goToAccountActivity()).show();
                                    else {
                                        Toast.makeText(signup.this, "Success", Toast.LENGTH_SHORT).show();
                                        goToHomeScreen();
                                    }
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        },
                        error -> Toast.makeText(signup.this, error.toString(), Toast.LENGTH_SHORT).show()){
                    @NotNull
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map <String, String> params = new HashMap<>();
                        params.put("phone",phone);
                        params.put("name",name);
                        params.put("password", password);
                        return  params;
                    }
                };
                RequestQueue requestQueue = Volley.newRequestQueue(signup.this);
                requestQueue.add(stringRequest);
            }
        });
    }
    public void goToLoginActivity(View view) {
        Intent intent = new Intent(signup.this, login.class);
        startActivity(intent);
        finish();
    }
    public void goToAccountActivity() {
        Intent intent = new Intent(signup.this, AccountScreen.class);
        startActivity(intent);
        finish();
    }
    private void goToHomeScreen(){
        // Wait for 2 secs then go to next activity
        Intent intent = new Intent(signup.this, MainActivity.class);
        intent.putExtra("wallet", "0");
        startActivity(intent);
        finish();
    }
}