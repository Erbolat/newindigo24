package com.indigo24.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.indigo24.R;
import com.indigo24.activities.Community;
import com.indigo24.activities.MainActivity;
import com.indigo24.activities.Wallet;
import com.indigo24.objects.object;
import com.indigo24.requests.Interface;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import tyrantgit.explosionfield.ExplosionField;

import static com.indigo24.requests.Interface.baseIMG;
import static com.indigo24.requests.Interface.tokenEXCHANGE;

public class exchangeFR extends Fragment {


    @BindView(R.id.tvEuro)
    TextView tvEuro;
    @BindView(R.id.tvDollar)
    TextView tvDollar;
    @BindView(R.id.tvRub)
    TextView tvRub;
    @BindView(R.id.swipe)
    SwipeRefreshLayout swipe;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_exchange, container, false);
        ButterKnife.bind(this,v);
        Toolbar toolbar = v.findViewById(R.id.toolbar_top);
        getExchange();
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });

//        btnExchange.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//              getExchange();
//            }
//        });



        return v;
    }
    @OnClick({R.id.tvDollar ,R.id.tvRub, R.id.tvEuro})
    void onSaveClick(View view) {
        switch (view.getId()) {
            case R.id.tvRub:
                tvRub.setTextColor(ContextCompat.getColor(getContext(), R.color.green));
                tvEuro.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
                tvDollar.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
                break;
            case R.id.tvEuro:
                tvRub.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
                tvEuro.setTextColor(ContextCompat.getColor(getContext(), R.color.green));
                tvDollar.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
                break;
            case R.id.tvDollar:
                tvRub.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
                tvEuro.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
                tvDollar.setTextColor(ContextCompat.getColor(getContext(), R.color.green));
                break;

        }
    }




    private void getExchange() {
        SharedPreferences sPref = getContext().getSharedPreferences("UserData",getContext().MODE_PRIVATE);
        String userID  = sPref.getString("id","");
        String unique  = sPref.getString("unique","");
        swipe.setRefreshing(true);
        Retrofit.Builder builder=new Retrofit.Builder().addCallAdapterFactory(RxJava2CallAdapterFactory.create()).baseUrl(Interface.baseURL).addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit= builder.build();
        Interface intc = retrofit.create(Interface.class);
        retrofit2.Call<ResponseBody> call = intc.getExchange(unique,userID,tokenEXCHANGE);
        call.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                try {
                    final JSONObject jsonObject = new JSONObject(response.body().string());
                    if(jsonObject.getBoolean("success")){
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    JSONObject js = jsonObject.getJSONObject("exchangeRates");
                                    tvDollar.setText("USD \n "+js.getString("USD"));
                                    tvEuro.setText("EURO \n "+js.getString("EUR"));
                                    tvRub.setText("RUB \n "+js.getString("RUB"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                        swipe.setRefreshing(false);
                    }
                    else {

                        swipe.setRefreshing(false);
                    }

                } catch (JSONException | NullPointerException | IOException e) {
                    swipe.setRefreshing(false);
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(retrofit2.Call<ResponseBody> call, Throwable t) {
                swipe.setRefreshing(false);
                Toast.makeText(getContext(), "Ошибка! или Проверьте доступ к Интернету", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }
}