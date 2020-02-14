package com.indigo24.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.indigo24.MainApp;
import com.indigo24.R;

public class SplashActivity extends AppCompatActivity {
    SharedPreferences sPref;
 public  static    String phone = "", unique="", id="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        getPhoneNumber();

    }
    private void getPhoneNumber() {
        sPref = getSharedPreferences("UserData", Context.MODE_PRIVATE);
        phone = sPref.getString("phone", "");
        unique = sPref.getString("unique", "");
        id = sPref.getString("id", "");
        if(!phone.isEmpty() && !unique.isEmpty() && !id.isEmpty())
            intent(1);
        else intent(0);
    }

    private void intent(final int i) {
        new CountDownTimer(2500, 500) {
            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
                if(i==1)
                    startActivity(new Intent(SplashActivity.this, MainActivity.class));
                else startActivity(new Intent(SplashActivity.this,Auth2.class));
                finish();
            }
        }.start();
    }
}