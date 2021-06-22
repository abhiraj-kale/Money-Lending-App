package com.example.moneypayment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.moneylending.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Home#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Home extends Fragment {
    private int wallet;
    TextView wallet_money;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public Home() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Home.
     */
    // TODO: Rename and change types and number of parameters
    public static Home newInstance(String param1, String param2) {
        Home fragment = new Home();
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
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).show();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        DBHelper DB = new DBHelper(getActivity());
        Cursor cursor = DB.getData();
        cursor.moveToFirst();
        if(cursor.getCount()==0){
            // No existing account on device, redirect to account screen
            new AlertDialog.Builder(getActivity())
                    .setTitle("Error:Transact id")
                    .setMessage("An Local DB Error occurred.")
                    .setCancelable(false)
                    .setPositiveButton("OK", (dialog, which) -> {}).show();
        }

        wallet = cursor.getInt(cursor.getColumnIndex("wallet"));
        wallet_money = view.findViewById(R.id.wallet_money);
        wallet_money.setText("Rs."+ wallet);

        BottomNavigationView bottomNavigationView = view.findViewById(R.id.home_user_options);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.add_money:
                        Intent i = new Intent(getActivity(), addMoney.class);
                        startActivity(i);
                        requireActivity().finish();
                        return true;
                    case R.id.send_money:
                        Intent intent = new Intent(getActivity(), search.class);
                        startActivity(intent);
                        requireActivity().finish();
                        return true;
                }
                return false;
            }
        });

        return view;
    }
}