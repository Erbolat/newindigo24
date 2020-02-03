package com.indigo24.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.indigo24.R;
import com.indigo24.requests.Interface;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.indigo24.requests.Interface.baseAVATAR;
import static com.indigo24.requests.Interface.baseIMG;

public class Profile extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.btnExit)
    Button btnExit;
    @BindView(R.id.imgAvatar)
    ImageView imgAvatar;
    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.tvPhone)
    TextView tvPhone;

    String userID, unique, avatar, mail, name, phone;
    SharedPreferences sPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);
        btnExit.setOnClickListener(this);

        Toolbar toolbar = findViewById(R.id.toolbar_top);
        toolbar.setTitle("Профиль");
        setSupportActionBar(toolbar);
        getSupportActionBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Profile.this, MainActivity.class));
                finish();
            }
        });
        imgAvatar.setImageDrawable(getResources().getDrawable(R.drawable.icon1));

        SharedPreferences sPref = getSharedPreferences("UserData",MODE_PRIVATE);
        userID  = sPref.getString("id","");
        unique  = sPref.getString("unique","");
        phone  = sPref.getString("phone","");
        getProfileData();
    }

    private void getProfileData() {
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

                        new Handler(Looper.getMainLooper()).post(new Runnable() {

                            @Override
                            public void run() {
                                Picasso.get().load(baseAVATAR+avatar).into(imgAvatar);
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
        }


        );
    }

    @Override
    public void onClick(View v) {
        btnExit.setClickable(false);
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
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(Profile.this, MainActivity.class));
        finish();
    }
}
