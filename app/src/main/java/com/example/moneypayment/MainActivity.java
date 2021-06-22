package com.example.moneypayment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.moneylending.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        openFragment(Home.newInstance("",""));

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                switch (item.getItemId()){
                    case R.id.nav_home:
                        openFragment(Home.newInstance("",""));
                        return true;
                    case R.id.nav_lend:
                        openFragment(LentMoney.newInstance("",""));
                        return true;
                    case R.id.nav_borrow:
                        openFragment(BorrowedMoney.newInstance("",""));
                        return true;
                }
                return false;
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.log_out:
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Do you want to log out?")
                        .setCancelable(true)

                        .setNegativeButton("No", (dialog, id) -> {dialog.cancel();})
                        .setPositiveButton("Yes", (dialog, which) -> {
                            DBHelper DB = new DBHelper(MainActivity.this);
                            if(DB.deleteAllUserData()){
                                goToAccountActivity();
                            }else Toast.makeText(this, "Couldn't log out", Toast.LENGTH_SHORT).show();
                        }).show();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    public void goToAccountActivity() {
        Intent intent = new Intent(MainActivity.this, AccountScreen.class);
        startActivity(intent);
        finish();
    }
}