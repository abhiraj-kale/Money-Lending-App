package com.example.moneypayment;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.moneylending.R;
import com.example.moneypayment.ui.CustomClickListener;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class addMoney extends AppCompatActivity {
    private Button btn_add_money;
    private EditText et_add_money;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_money);

        et_add_money = (EditText) findViewById(R.id.et_add_money);
        btn_add_money = (Button) findViewById(R.id.btn_add_money);

        btn_add_money.setOnClickListener(new CustomClickListener() {
            @Override
            public void performClick(View v) {
                btn_add_money.setEnabled(false);
                String amount = et_add_money.getText().toString();

                DBHelper DB = new DBHelper(addMoney.this);
                Cursor cursor = DB.getData();
                cursor.moveToFirst();
                if(cursor.getCount()==0){
                    // No existing account on device, redirect to account screen
                    new AlertDialog.Builder(addMoney.this)
                            .setTitle("Error:Transact id")
                            .setMessage("An Local DB Error occurred.")
                            .setCancelable(false)
                            .setPositiveButton("OK", (dialog, which) -> finish()).show();
                }

                // Get money in wallet
                String url = "https://money-lending-app.herokuapp.com/api/addMoney";

                String transact_id = cursor.getString(cursor.getColumnIndex("transact_id"));
                String id = cursor.getString(cursor.getColumnIndex("id"));

                ProgressBar pg = findViewById(R.id.progressBar_Add_money);
                pg.setVisibility(View.VISIBLE);

                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        response -> {
                            try {
                                JSONObject json = new JSONObject(response);
                                if(!((boolean) json.get("status")))
                                    Toast.makeText(addMoney.this, String.valueOf(json.get("message")), Toast.LENGTH_LONG).show();
                                else{
                                    int wallet = json.getInt("wallet");
                                    if(DB.updateUserWallet(id, wallet)){
                                        Intent intent = new Intent(addMoney.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                    pg.setVisibility(View.INVISIBLE);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        },
                        error -> new AlertDialog.Builder(addMoney.this)
                                .setTitle("Error")
                                .setMessage(error.toString())
                                .setCancelable(false)
                                .setPositiveButton("OK", (dialog, which) -> {}).show()){
                    @NotNull
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map <String, String> params = new HashMap<>();
                        params.put("transact_id",transact_id);
                        params.put("amount",amount);
                        return  params;
                    }
                };
                RequestQueue requestQueue = Volley.newRequestQueue(addMoney.this);
                requestQueue.add(stringRequest);
            }
        });
/*
        btn_add_money.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

 */
    }
}