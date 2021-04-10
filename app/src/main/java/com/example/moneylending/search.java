package com.example.moneylending;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class search extends AppCompatActivity {
    TableLayout tableLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        SearchView searchView = findViewById(R.id.search_field);
        searchView.setIconifiedByDefault(true);
        searchView.setFocusable(true);
        searchView.setIconified(false);
        searchView.requestFocusFromTouch();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            @Override
            public boolean onQueryTextSubmit(String query) {
                callSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }

            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
            public void callSearch(String phone) {
                DBHelper DB = new DBHelper(search.this);
                Cursor cursor = DB.getData();
                cursor.moveToFirst();
                if(cursor.getCount()==0){
                    // No existing account on device, redirect to account screen
                    new AlertDialog.Builder(search.this)
                            .setTitle("Error:Transact id")
                            .setMessage("An Local DB Error occurred.")
                            .setCancelable(false)
                            .setPositiveButton("OK", (dialog, which) -> finish()).show();
                }

                tableLayout = findViewById(R.id.search_TableLayout);
                tableLayout.removeAllViews();
                tableLayout.setStretchAllColumns(true);
                String transact_id = cursor.getString(cursor.getColumnIndex("transact_id"));

                ProgressBar pg = findViewById(R.id.progressBar_search);
                pg.setVisibility(View.VISIBLE);

                String url = "https://money-lending-app.herokuapp.com/api/getUserId";

                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                        response -> {
                            try {
                                JSONObject json = new JSONObject(response);
                                if(!((boolean) json.get("status")))
                                    Toast.makeText(search.this, String.valueOf(json.get("message")), Toast.LENGTH_LONG).show();
                                else{

                                    TableRow tableRow = new TableRow(search.this);

                                    TextView tv_name = new TextView(search.this);
                                    tv_name.setText(String.valueOf(json.get("name")));
                                    tv_name.setTextSize(25);
                                    tv_name.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                    tv_name.setTextColor(Color.BLACK);
                                    tableRow.addView(tv_name);

                                    TextView tv_phone = new TextView(search.this);
                                    tv_phone.setText(phone);
                                    tv_phone.setTextSize(25);
                                    tv_phone.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                                    tv_phone.setTextColor(Color.BLACK);
                                    tableRow.addView(tv_phone);

                                    tableRow.setTag(String.valueOf(json.get("id")));

                                    tableRow.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent intent = new Intent(search.this, payment.class);
                                            intent.putExtra("receiver_id", String.valueOf(v.getTag()));
                                            startActivity(intent);
                                        }
                                    });

                                    tableLayout.addView(tableRow);

                                    pg.setVisibility(View.INVISIBLE);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        },
                        error -> new AlertDialog.Builder(search.this)
                                .setTitle("Error")
                                .setMessage(error.toString())
                                .setCancelable(false)
                                .setPositiveButton("OK", (dialog, which) -> {}).show()){
                    @NotNull
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map <String, String> params = new HashMap<>();
                        params.put("transact_id",transact_id);
                        params.put("phone",phone);
                        return  params;
                    }
                };
                RequestQueue requestQueue = Volley.newRequestQueue(search.this);
                requestQueue.add(stringRequest);
            }

        });

    }
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        startActivity(new Intent(search.this, MainActivity.class));
        finish();

    }
}