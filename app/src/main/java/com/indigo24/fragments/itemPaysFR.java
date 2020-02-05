package com.indigo24.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.indigo24.R;
import com.indigo24.activities.MainActivity;
import com.indigo24.activities.Profile;
import com.indigo24.activities.Wallet;
import com.indigo24.adapters.AdapterCateg;
import com.indigo24.objects.categories;
import com.indigo24.requests.Interface;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import tyrantgit.explosionfield.ExplosionField;

import static com.indigo24.requests.Interface.baseIMG;

public class itemPaysFR extends Fragment {
    SwipeRefreshLayout swipe;
    SharedPreferences sPref;
    String unique,userID,serviceID;
    ArrayList<categories> arrServices = new ArrayList<>();
    AdapterCateg adapter;
    Toolbar toolbar;




    @BindView(R.id.btnMenu)
    ImageView btnMenu;
    @BindView(R.id.editLogin)
    EditText editLogin;
    @BindView(R.id.editSumm)
    EditText editSumm;

    TextView tvPlaceholder;
    Button btnPay;

    String urlPhoto;
    ImageView img;
    String phone, summ;
    ExplosionField explosionField;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.item_pays, container, false);
        ButterKnife.bind(this,v);
        explosionField = ExplosionField.attach2Window(getActivity());
        arrServices = new ArrayList<>();
        Wallet.shag=3;
        btnPay = v.findViewById(R.id.btnPay);
        tvPlaceholder = v.findViewById(R.id.tvPlaceholder);
        img = v.findViewById(R.id.img);
        swipe = v.findViewById(R.id.swipe);
        sPref = getActivity().getSharedPreferences("UserData",getActivity().MODE_PRIVATE);
        userID  = sPref.getString("id","");
        unique  = sPref.getString("unique","");
        Bundle bundle = this.getArguments();
        if(bundle != null){
            serviceID = bundle.getString("serviceID");
            urlPhoto = bundle.getString("urlPhoto");
            getPayment();
            Picasso.get().load(baseIMG+urlPhoto).into(img);
        }

        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phone = editLogin.getText().toString();
                summ = editSumm.getText().toString();

                if(!summ.isEmpty()) {
                    if(Integer.parseInt(summ)>0)
                        setPayment();
                    else Toast.makeText(getContext(), "Введите сумму больше 0", Toast.LENGTH_SHORT).show();
                }
                else Toast.makeText(getContext(), "Поле сумма не может быть пустым", Toast.LENGTH_SHORT).show();
            }
        });
        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                explosionField.explode(btnMenu);
                CountDownTimer myCountDown = new CountDownTimer(550, 400) {
                    public void onTick(long millisUntilFinished) {
                    }

                    public void onFinish() {
                        startActivity(new Intent(getActivity(), Wallet.class));
                        getActivity().finish();
                    }
                };
                myCountDown.start();
            }
        });

        return v;
    }

    private void setPayment() {

        swipe.setRefreshing(true);
        Retrofit.Builder builder=new Retrofit.Builder().addCallAdapterFactory(RxJava2CallAdapterFactory.create()).baseUrl(Interface.baseURL).addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit= builder.build();
        Interface intc = retrofit.create(Interface.class);
        retrofit2.Call<ResponseBody> call = intc.setPay(unique,userID,summ, serviceID,phone);

        Log.e("Kuan", unique+" "+userID+" "+summ+" "+serviceID+" "+phone);
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
                                    Toast.makeText(getContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                                    getActivity().getSupportFragmentManager().popBackStack();

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        swipe.setRefreshing(false);
                    }
                    else {
                        Toast.makeText(getContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
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
                Toast.makeText(getContext(), "Ошибка! или Проверьте доступ к Интернету", Toast.LENGTH_LONG).show();
                t.printStackTrace();
            }
        });
    }

    private void getPayment() {
        swipe.setRefreshing(true);
        Retrofit.Builder builder=new Retrofit.Builder().addCallAdapterFactory(RxJava2CallAdapterFactory.create()).baseUrl(Interface.baseURL).addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit= builder.build();
        Interface intc = retrofit.create(Interface.class);
        retrofit2.Call<ResponseBody> call = intc.getPayments(unique,userID,serviceID);
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
                                    tvPlaceholder.setText(jsonObject.getString("placeholder"));

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