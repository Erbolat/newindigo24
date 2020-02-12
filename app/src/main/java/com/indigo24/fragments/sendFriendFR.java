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

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.indigo24.R;
import com.indigo24.activities.Wallet;
import com.indigo24.requests.Interface;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import tyrantgit.explosionfield.ExplosionField;

import static com.indigo24.requests.Interface.TAG;
import static com.indigo24.requests.Interface.baseAVATAR;
import static com.indigo24.requests.Interface.baseIMG;


public class sendFriendFR extends Fragment {
    @BindView(R.id.btnMenu)
    ImageView btnMenu;
    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.editPhone)
    EditText editPhone;
    @BindView(R.id.editSumm)
    EditText editSumm;
    @BindView(R.id.btnPayobj)
    Button btnPay;
    @BindView(R.id.img)
    ImageView img;
    ExplosionField explosionField;
    String unique, phone, userID;
    @BindView(R.id.swipe)
    SwipeRefreshLayout swipe;
    SharedPreferences sPref;
    String avatar,name, toID, summ;
    boolean checked = false;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_send_friend, container, false);
        ButterKnife.bind(this,v);
        explosionField = ExplosionField.attach2Window(getActivity());
        checked = false;

        editPhone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    phone = editPhone.getText().toString();
                    if(!phone.isEmpty() && phone.length()>8)
                        checkPhone();
                    else Toast.makeText(getContext(), "Введите номер телефона правильно", Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("focus", "focused");
                }
            }
        });

        editSumm.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                    phone = editPhone.getText().toString();
                    if(phone.isEmpty()) {
                        Toast.makeText(getContext(), "Нельзя ввести сумму до тех пор пока не вводите номер телефона", Toast.LENGTH_SHORT).show();
                        editSumm.setText("");
                    }
            }
        });

        return v;
    }

    @OnClick({R.id.btnMenu ,R.id.btnPayobj})
    void onSaveClick(View view) {
        switch (view.getId()) {
            case R.id.btnMenu: {
                explosionField.explode(btnMenu);
                effectOpen(Wallet.class);
                break;
            }
            case R.id.btnPayobj: {
                phone = editPhone.getText().toString();
                summ = editSumm.getText().toString();
                if(phone.isEmpty()) Toast.makeText(getContext(), "Введите номер телефона правильно", Toast.LENGTH_SHORT).show();
                if(summ.isEmpty()) Toast.makeText(getContext(), "Введите сумму", Toast.LENGTH_SHORT).show();
                if(summ.equals("0")) Toast.makeText(getContext(), "Cумму должен быть больше 0", Toast.LENGTH_SHORT).show();
                else if(checked && !summ.isEmpty() && !phone.isEmpty())
                sendMoney();
                break;
            }
        }
    }

    private void sendMoney() {
        swipe.setRefreshing(true);
        Retrofit.Builder builder=new Retrofit.Builder().addCallAdapterFactory(RxJava2CallAdapterFactory.create()).baseUrl(Interface.baseURL).addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit= builder.build();
        Interface intc = retrofit.create(Interface.class);
        retrofit2.Call<ResponseBody> call = intc.sendMoney( unique,userID,summ,toID);

        Log.e(TAG, unique+" "+userID+" "+phone+" ");
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
                                    if(jsonObject.has("message"))
                                        Toast.makeText(getContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                                    else  Toast.makeText(getContext(), "Успешно!", Toast.LENGTH_LONG).show();
                                    editPhone.setText("");
                                    editSumm.setText("");

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        swipe.setRefreshing(false);
                    }
                    else {
                        if(jsonObject.has("message"))
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

    private void checkPhone() {
        sPref = getActivity().getSharedPreferences("UserData",getActivity().MODE_PRIVATE);
        userID  = sPref.getString("id","");
        unique  = sPref.getString("unique","");
        swipe.setRefreshing(true);
        Retrofit.Builder builder=new Retrofit.Builder().addCallAdapterFactory(RxJava2CallAdapterFactory.create()).baseUrl(Interface.baseURL).addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit= builder.build();
        Interface intc = retrofit.create(Interface.class);
        retrofit2.Call<ResponseBody> call = intc.checkPhoneSendMoney( unique,userID,phone);

        Log.e(TAG, unique+" "+userID+" "+phone+" ");
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
                                    checked = true;
                                    Log.e(TAG+"1", jsonObject.toString());
                                    if(jsonObject.has("message"))
                                    Toast.makeText(getContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                                    name = jsonObject.getString("name");
                                    avatar = jsonObject.getString("avatar");
                                    toID = jsonObject.getInt("toID")+"";
                                    tvName.setText(name);
                                    Picasso.get().load(baseAVATAR+avatar).transform(new CropCircleTransformation()).into(img);


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        swipe.setRefreshing(false);
                    }
                    else {
                        if(jsonObject.has("message"))
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

    void effectOpen(final Class myClass){
        CountDownTimer myCountDown = new CountDownTimer(550, 400) {
            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
                startActivity(new Intent(getActivity(), myClass));
                getActivity().finish();
            }
        };
        myCountDown.start();
    }



}
