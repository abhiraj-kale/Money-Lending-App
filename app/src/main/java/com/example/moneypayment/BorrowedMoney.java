package com.example.moneypayment;

import android.app.AlertDialog;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.moneylending.R;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link BorrowedMoney#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BorrowedMoney extends Fragment {
    TableLayout tableLayout;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public BorrowedMoney() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment BorrowedMoney.
     */
    // TODO: Rename and change types and number of parameters
    public static BorrowedMoney newInstance(String param1, String param2) {
        BorrowedMoney fragment = new BorrowedMoney();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).hide();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_borrowed_money, container, false);
        tableLayout = view.findViewById(R.id.borrowed_tableLayout);
        tableLayout.setStretchAllColumns(true);

        TableRow tableRow = new TableRow(inflater.getContext());

        TextView name = new TextView(inflater.getContext());
        name.setText("Name");
        name.setTextSize(15);
        name.setTextColor(Color.BLACK);
        tableRow.addView(name);

        TextView phone = new TextView(inflater.getContext());
        phone.setText("Phone");
        phone.setTextSize(15);
        phone.setTextColor(Color.BLACK);
        tableRow.addView(phone);

        TextView amount = new TextView(inflater.getContext());
        amount.setText("Amount");
        amount.setTextSize(15);
        amount.setTextColor(Color.BLACK);
        tableRow.addView(amount);

        tableLayout.addView(tableRow);
        View v = new View(getActivity());
        v.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                5
        ));
        v.setBackgroundColor(Color.BLACK);
        tableLayout.addView(v);

        // Make a call to get the transactions
        DBHelper DB = new DBHelper(getActivity());
        Cursor cursor = DB.getData();
        cursor.moveToFirst();
        if(cursor.getCount()==0){
            // No existing account on device, redirect to account screen
            new AlertDialog.Builder(getActivity())
                    .setTitle("Error:Transact id")
                    .setMessage("An Local DB Error occurred.")
                    .setCancelable(false)
                    .setPositiveButton("OK", (dialog, which) -> requireActivity().finish()).show();
        }

        String transact_id = cursor.getString(cursor.getColumnIndex("transact_id"));

        ProgressBar pg = view.findViewById(R.id.progressBar_Borrowed);
        pg.setVisibility(View.VISIBLE);

        String url = "https://money-lending-app.herokuapp.com/api/borrowed";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        Map<String, ArrayList<String>> map = new HashMap<>();
                        int TOTAL_MONEY_BORROWED = 0;

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String sender_id = jsonObject.getString("sender_id");
                            String json_name = jsonObject.getString("name");
                            String json_phone = jsonObject.getString("phone");
                            int json_amount= jsonObject.getInt("amount");
                            TOTAL_MONEY_BORROWED += json_amount;

                            ArrayList<String> arrayList = new ArrayList<>(); // Create a new ArrayList
                            arrayList.add(json_name);
                            arrayList.add(json_phone);

                            if(map.containsKey(sender_id)){
                                ArrayList<String> arr = map.get(sender_id);
                                assert arr != null;
                                int old_amt = Integer.parseInt(arr.get(2));
                                int new_amt = old_amt + json_amount;
                                arrayList.add(String.valueOf(new_amt));
                            }else {
                                arrayList.add(String.valueOf(json_amount));
                            }

                            map.put(sender_id, arrayList);
                        }
                        // Iterate through the map and print every list
                        for (Map.Entry<String, ArrayList<String>> entry : map.entrySet()) {
                            System.out.println(entry.getKey() + "/" + entry.getValue());
                            String new_id = entry.getKey();
                            ArrayList<String> arr = entry.getValue();
                            String new_name = arr.get(0);
                            String new_phone = arr.get(1);
                            String new_amount = arr.get(2);

                            TableRow new_tableRow = new TableRow(inflater.getContext());
                            new_tableRow.setTag(new_id);
                            new_tableRow.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String tag = String.valueOf(v.getTag());
                                    Toast.makeText(getActivity(), tag, Toast.LENGTH_SHORT).show();
                                }
                            });

                            TextView tv_name = new TextView(inflater.getContext());
                            tv_name.setText(new_name);
                            tv_name.setTextSize(15);
                            tv_name.setTextColor(Color.BLACK);
                            new_tableRow.addView(tv_name);

                            TextView tv_phone = new TextView(inflater.getContext());
                            tv_phone.setText(new_phone);
                            tv_phone.setTextSize(15);
                            tv_phone.setTextColor(Color.BLACK);
                            new_tableRow.addView(tv_phone);

                            TextView tv_amount = new TextView(inflater.getContext());
                            tv_amount.setText(new_amount);
                            tv_amount.setTextSize(15);
                            tv_amount.setTextColor(Color.BLACK);
                            new_tableRow.addView(tv_amount);

                            tableLayout.addView(new_tableRow);
                        }
                        TextView tv_total_money = view.findViewById(R.id.total_money_borrowed);
                        String str_tot_money = "Total Money Received: Rs." + TOTAL_MONEY_BORROWED;
                        tv_total_money.setText(str_tot_money);
                        pg.setVisibility(View.INVISIBLE);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> new AlertDialog.Builder(getActivity())
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
        RequestQueue requestQueue = Volley.newRequestQueue(requireActivity());
        requestQueue.add(stringRequest);

        return view;
    }
}