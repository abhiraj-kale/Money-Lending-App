package com.example.moneylending;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.moneylending.ui.CustomClickListener;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class payment extends AppCompatActivity {
    TextView money_in_wallet;
    Button pay_button;
    EditText pay_amount;
    int wallet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        DBHelper DB = new DBHelper(payment.this);
        Cursor cursor = DB.getData();
        cursor.moveToFirst();
        if(cursor.getCount()==0){
            // No existing account on device, redirect to account screen
            new AlertDialog.Builder(payment.this)
                    .setTitle("Error:Transact id")
                    .setMessage("An Local DB Error occurred.")
                    .setCancelable(false)
                    .setPositiveButton("OK", (dialog, which) -> finish()).show();
        }

        // Get money in wallet
        String url = "https://money-lending-app.herokuapp.com/api/getWallet";

        String transact_id = cursor.getString(cursor.getColumnIndex("transact_id"));
        String id = cursor.getString(cursor.getColumnIndex("id"));

        ProgressBar pg = findViewById(R.id.progressBar_payment);
        pg.setVisibility(View.VISIBLE);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        if(!((boolean) json.get("status")))
                            Toast.makeText(payment.this, String.valueOf(json.get("message")), Toast.LENGTH_LONG).show();
                        else{
                            String wallet_money = "Money in your wallet: Rs."+ String.valueOf(json.get("wallet"));

                            money_in_wallet = findViewById(R.id.money_in_wallet);
                            money_in_wallet.setText(wallet_money);
                            wallet = json.getInt("wallet");
                            pg.setVisibility(View.INVISIBLE);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                },
                error -> new AlertDialog.Builder(payment.this)
                        .setTitle("Error")
                        .setMessage(error.toString())
                        .setCancelable(false)
                        .setPositiveButton("OK", (dialog, which) -> {}).show()){
            @NotNull
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map <String, String> params = new HashMap<>();
                params.put("transact_id",transact_id);
                return  params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(payment.this);
        requestQueue.add(stringRequest);

        // Pay money
        pay_button = findViewById(R.id.pay_button);
        pay_button.setOnClickListener(new CustomClickListener() {
            @Override
            public void performClick(View v) {
                pay_button.setEnabled(false);
                pay_amount = (EditText) findViewById(R.id.pay_amount);
                String amount = pay_amount.getText().toString();

                String url = "https://money-lending-app.herokuapp.com/api/pay";

                String transact_id = cursor.getString(cursor.getColumnIndex("transact_id"));

                ProgressBar pg = findViewById(R.id.progressBar_payment);
                pg.setVisibility(View.VISIBLE);

                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        response -> {
                            try {
                                JSONObject json = new JSONObject(response);
                                if(!((boolean) json.get("status")))
                                    Toast.makeText(payment.this, String.valueOf(json.get("message")), Toast.LENGTH_LONG).show();
                                else{
                                    DB.updateUserWallet(id, json.getInt("wallet"));
                                    pg.setVisibility(View.INVISIBLE);
                                    Intent intent = new Intent(payment.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        },
                        error -> new AlertDialog.Builder(payment.this)
                                .setTitle("Error")
                                .setMessage(error.toString())
                                .setCancelable(false)
                                .setPositiveButton("OK", (dialog, which) -> {}).show()){
                    @NotNull
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map <String, String> params = new HashMap<>();
                        params.put("sender_trans",transact_id);
                        params.put("receiver_id", getIntent().getStringExtra("receiver_id"));
                        params.put("amount", amount);
                        return  params;
                    }
                };
                RequestQueue requestQueue = Volley.newRequestQueue(payment.this);
                requestQueue.add(stringRequest);
            }
        });
        /*
        pay_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        */
    }
}