package com.indigo24.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import com.indigo24.R;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import tyrantgit.explosionfield.ExplosionField;

import static android.view.View.VISIBLE;

public class MainActivity extends AppCompatActivity implements  View.OnClickListener{
    ExplosionField explosionField;
    @BindView(R.id.icon_wallet)
    ImageView imgWallet;
    @BindView(R.id.icon_profile)
    ImageView imgProfile;
    @BindView(R.id.icon1)
    ImageView imgCommunity;
    @BindView(R.id.icon2)
    ImageView imgNews;
    @BindView(R.id.icon3)
    ImageView imgTV;
    @BindView(R.id.icon4)
    ImageView imgShops;
    @BindView(R.id.icon5)
    ImageView imgFood;
    @BindView(R.id.icon6)
    ImageView imgServices;

    ImageView[] images;
    TextView[] tvs;
    @SuppressLint("CutPasteId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        explosionField = ExplosionField.attach2Window(this);

        ButterKnife.bind(this);
        imgWallet.setOnClickListener(this);
        imgProfile.setOnClickListener(this);
        imgCommunity.setOnClickListener(this);
        imgNews.setOnClickListener(this);
        imgTV.setOnClickListener(this);
        imgShops.setOnClickListener(this);
        imgFood.setOnClickListener(this);
        imgServices.setOnClickListener(this);
        images = new ImageView[]{findViewById(R.id.icon1), findViewById(R.id.icon2), findViewById(R.id.icon3), findViewById(R.id.icon4), findViewById(R.id.icon5), findViewById(R.id.icon6)};
        tvs = new TextView[]{findViewById(R.id.tv1), findViewById(R.id.tv2), findViewById(R.id.tv3), findViewById(R.id.tv4), findViewById(R.id.tv5), findViewById(R.id.tv6)};
    }

    @Override
    public void onClick(View v) {
        v.setClickable(false);
        switch (v.getId()) {
            case ( R.id.icon_wallet ):
                explosionField.explode(imgWallet);
                CountDownTimer myCountDown = new CountDownTimer(550, 400) {
                    public void onTick(long millisUntilFinished) {
                    }

                    public void onFinish() {
                        startActivity(new Intent(MainActivity.this, Wallet.class));
                        finish();
                    }
                };
                myCountDown.start();

                break;
            case ( R.id.icon_profile ):
                explosionField.explode(imgProfile);
                CountDownTimer myCountDown1 = new CountDownTimer(550, 400) {
                    public void onTick(long millisUntilFinished) {
                    }

                    public void onFinish() {
                        startActivity(new Intent(MainActivity.this, Profile.class));
                        finish();
                    }
                };
                myCountDown1.start();
                break;
            case ( R.id.icon1 ):
                explosionField.explode(imgCommunity);
                CountDownTimer myCountDown2 = new CountDownTimer(550, 400) {
                    public void onTick(long millisUntilFinished) {
                    }

                    public void onFinish() {
                        startActivity(new Intent(MainActivity.this, Community.class));
                        finish();
                    }
                };
                myCountDown2.start();
                break;
            case ( R.id.icon2 ):
                explosionField.explode(imgNews);
                CountDownTimer myCountDown4 = new CountDownTimer(550, 400) {
                    public void onTick(long millisUntilFinished) {
                    }

                    public void onFinish() {
                        startActivity(new Intent(MainActivity.this, News.class));
                        finish();
                    }
                };
                myCountDown4.start();
                break;
            case ( R.id.icon3 ):
                explosionField.explode(imgTV);
                CountDownTimer myCountDown5 = new CountDownTimer(550, 400) {
                    public void onTick(long millisUntilFinished) {
                    }

                    public void onFinish() {
                        startActivity(new Intent(MainActivity.this, TV.class));
                        finish();
                    }
                };
                myCountDown5.start();
                break;
            case ( R.id.icon4 ):
                explosionField.explode(imgShops);
                CountDownTimer myCountDown6 = new CountDownTimer(550, 400) {
                    public void onTick(long millisUntilFinished) {
                    }

                    public void onFinish() {
                        startActivity(new Intent(MainActivity.this, Shops.class));
                        finish();
                    }
                };
                myCountDown6.start();
                break;
            case ( R.id.icon5 ):
                explosionField.explode(imgFood);
                CountDownTimer myCountDown7 = new CountDownTimer(550, 400) {
                    public void onTick(long millisUntilFinished) {
                    }

                    public void onFinish() {
                        startActivity(new Intent(MainActivity.this, Food.class));
                        finish();
                    }
                };
                myCountDown7.start();
                break;
            case ( R.id.icon6 ):
                explosionField.explode(imgServices);
                CountDownTimer myCountDown8 = new CountDownTimer(550, 400) {
                    public void onTick(long millisUntilFinished) {
                    }

                    public void onFinish() {
                        startActivity(new Intent(MainActivity.this, Services.class));
                        finish();
                    }
                };
                myCountDown8.start();
                break;
        }
    }
}




