package com.indigo24.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.indigo24.R;
import com.indigo24.fragments.pinCodeFR;


public class Wallet extends AppCompatActivity  {
    public static int shag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        Fragment fragment = new pinCodeFR();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.contentFragment, fragment);
        transaction.addToBackStack(null);
        transaction.commit();

    }


    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        if(shag==1){
            startActivity(new Intent(Wallet.this, MainActivity.class));
            finish();
            }
        else if(shag>1)
            super.onBackPressed();
        }
}


