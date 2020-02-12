package com.indigo24.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;

import com.indigo24.R;

public class GifActivity extends AppCompatActivity {
    SharedPreferences sPref;
    String phone = "", unique="", id="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gif);
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
        new CountDownTimer(5000, 500) {
            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
                if(i==1)
                    startActivity(new Intent(GifActivity.this,MainActivity.class));
                else startActivity(new Intent(GifActivity.this,Auth.class));
                finish();
            }
        }.start();
    }
}