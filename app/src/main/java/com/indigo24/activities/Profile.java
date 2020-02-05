package com.indigo24.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.indigo24.R;
import com.indigo24.fragments.pinCodeFR;
import com.indigo24.fragments.profileEditFR;
import com.indigo24.requests.Interface;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import tyrantgit.explosionfield.ExplosionField;

import static com.indigo24.requests.Interface.baseAVATAR;
import static com.indigo24.requests.Interface.baseIMG;

public class Profile extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.imgAvatar)
    ImageView imgAvatar;
    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.tvPhone)
    TextView tvPhone;

    @BindViews({R.id.btnExit, R.id.btnEdit,R.id.btnMenu, R.id.btnWallet})
    View[] viewBtns;

    @BindView(R.id.contentFragment)
    FrameLayout contentFragment;
    @BindView(R.id.rel1)
    RelativeLayout relProfile;
    String userID, unique, avatar, mail, name, phone, city;
    SharedPreferences sPref;
    ExplosionField explosionField;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        explosionField = ExplosionField.attach2Window(this);
        relProfile.setVisibility(View.VISIBLE);
        contentFragment.setVisibility(View.GONE);
        getProfileData();
    }

    private void getProfileData() {
        SharedPreferences sPref = getSharedPreferences("UserData",MODE_PRIVATE);
        userID  = sPref.getString("id","");
        unique  = sPref.getString("unique","");
        phone  = sPref.getString("phone","");
        Retrofit.Builder builder=new Retrofit.Builder().addCallAdapterFactory(RxJava2CallAdapterFactory.create()).baseUrl(Interface.baseURL).addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit= builder.build();
        Interface intc = retrofit.create(Interface.class);
        retrofit2.Call<ResponseBody> call = intc.getProfile(unique, userID);

        call.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    if(jsonObject.getBoolean("success")){
                        avatar = jsonObject.getString("avatar");
                        mail = jsonObject.getString("email");
                        name = jsonObject.getString("name");
                        if (jsonObject.has("city"))
                        city = jsonObject.getString("city");
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                Picasso.get().load(baseAVATAR+avatar).transform(new CropCircleTransformation()).into(imgAvatar);
                                tvName.setText(name);
                                tvPhone.setText(phone);
                            }
                        });
                    }
                    else {
                        if(jsonObject.has("message"))
                            Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException | NullPointerException | IOException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(retrofit2.Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Ошибка! или Проверьте доступ к Интернету", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }


    @OnClick({R.id.btnEdit, R.id.btnExit,R.id.btnWallet, R.id.btnMenu})
    void onSaveClick(View view) {
        switch (view.getId()) {
            case R.id.btnEdit:
                relProfile.setVisibility(View.GONE);
                contentFragment.setVisibility(View.VISIBLE);
                Bundle b = new Bundle();
                b.putString("name",name);
                b.putString("city",city);
                Fragment fragment = new profileEditFR();
                fragment.setArguments(b);
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                transaction.replace(R.id.contentFragment, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
                break;
            case R.id.btnExit:
                sPref = getSharedPreferences("UserData", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sPref.edit();
                editor.remove("id");
                editor.remove("phone");
                editor.remove("unique");
                editor.remove("pin");
                editor.apply();
                editor.clear();
                Intent intent = new Intent(Profile.this,SplashActivity.class);
                startActivity(intent);
                finish();
                view.setClickable(false);
                break;

                case R.id.btnWallet:
                    explosionField.explode(viewBtns[3]);
                    CountDownTimer myCountDown = new CountDownTimer(550, 400) {
                        public void onTick(long millisUntilFinished) {
                        }

                        public void onFinish() {
                            startActivity(new Intent(Profile.this, Wallet.class));
                            finish();
                        }
                    };
                    myCountDown.start();

                    break;
                case R.id.btnMenu:
                    explosionField.explode(viewBtns[2]);
                     myCountDown = new CountDownTimer(550, 400) {
                        public void onTick(long millisUntilFinished) {
                        }
                        public void onFinish() {
                            startActivity(new Intent(Profile.this, MainActivity.class));
                            finish();
                        }
                    };
                    myCountDown.start();
                break;
        }
    }

    @Override
    public void onClick(View v) {




    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(Profile.this, MainActivity.class));
        finish();
    }
}
