package com.indigo24.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import com.indigo24.R;
import com.indigo24.activities.Auth;
import com.indigo24.activities.MainActivity;
import com.indigo24.activities.Wallet;
import com.indigo24.requests.Interface;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class pinCodeFR extends Fragment {
    FrameLayout sims1, sims2, sims3, sims4;
    FrameLayout sims21, sims22, sims23, sims24;
    Button button0, button1, button2, button3, button4, button5, button6, button7, button8, button9;
    ImageButton buttonDeleteBack;
    EditText pinCodes, repeat;
    SharedPreferences sPref;
    boolean hasPin;
    String unique="",id="", sha1="";
    TextView tvName;
    SwipeRefreshLayout swipe;
    int clicked = 0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fr_pincode, container, false);
        Wallet.shag=1;
        sPref = getActivity().getSharedPreferences("UserData",getActivity().MODE_PRIVATE);
        hasPin  = sPref.getBoolean("pin",false);
        id  = sPref.getString("id","");
        unique  = sPref.getString("unique","");
        clicked = 0;



        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        long mSec = calendar.get(Calendar.MILLISECOND);

//        LocalDateTime localDateTime = LocalDateTime.parse(myDate,
//                DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss") );
//        long millis = localDateTime
//                .atZone(ZoneId.systemDefault())
//                .toInstant().toEpochMilli();
//
//        Log.e("data",mSec+"");


//        sPref = getContext().getSharedPreferences("UserData", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sPref.edit();
//        editor.putString( "pin_time", "1");
//        editor.commit();
//        editor.apply();



        sims1 = (FrameLayout)v.findViewById(R.id.sum1);
        sims2 = (FrameLayout)v.findViewById(R.id.sum2);
        sims3 = (FrameLayout)v.findViewById(R.id.sum3);
        sims4 = (FrameLayout)v.findViewById(R.id.sum4);

        sims21 = (FrameLayout)v.findViewById(R.id.sum1i);
        sims22 = (FrameLayout)v.findViewById(R.id.sum2i);
        sims23 = (FrameLayout)v.findViewById(R.id.sum3i);
        sims24 = (FrameLayout)v.findViewById(R.id.sum4i);

        tvName = (TextView) v.findViewById(R.id.tvName);
        swipe = (SwipeRefreshLayout) v.findViewById(R.id.swipe);

        button0 = (Button)v.findViewById(R.id.button0);
        button1 = (Button)v.findViewById(R.id.button1);
        button2 = (Button)v.findViewById(R.id.button2);
        button3 = (Button)v.findViewById(R.id.button3);
        button4 = (Button)v.findViewById(R.id.button4);
        button5 = (Button)v.findViewById(R.id.button5);
        button6 = (Button)v.findViewById(R.id.button6);
        button7 = (Button)v.findViewById(R.id.button7);
        button8 = (Button)v.findViewById(R.id.button8);
        button9 = (Button)v.findViewById(R.id.button9);

        buttonDeleteBack = (ImageButton) v.findViewById(R.id.buttonDeleteBack);

        buttonDeleteBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(pinCodes.getText().length() > 0){
                    pinCodes.setText(pinCodes.getText().toString().substring(0, pinCodes.getText().toString().length() - 1));
                    changedInpService();
                }
            }
        });

        if(hasPin)  tvName.setText("Проверка Pin кода");
        else tvName.setText("Установка Pin кода");

        View.OnClickListener clicker = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch(v.getId()){
                    case R.id.button0 :
                        addNumberInPin(0);
                        break;
                    case R.id.button1 :
                        addNumberInPin(1);
                        break;
                    case R.id.button2 :
                        addNumberInPin(2);
                        break;
                    case R.id.button3 :
                        addNumberInPin(3);
                        break;
                    case R.id.button4 :
                        addNumberInPin(4);
                        break;
                    case R.id.button5 :
                        addNumberInPin(5);
                        break;
                    case R.id.button6 :
                        addNumberInPin(6);
                        break;
                    case R.id.button7 :
                        addNumberInPin(7);
                        break;
                    case R.id.button8 :
                        addNumberInPin(8);
                        break;
                    case R.id.button9 :
                        addNumberInPin(9);
                        break;
                }
            }
        };

        button0.setOnClickListener(clicker);
        button1.setOnClickListener(clicker);
        button2.setOnClickListener(clicker);
        button3.setOnClickListener(clicker);
        button4.setOnClickListener(clicker);
        button5.setOnClickListener(clicker);
        button6.setOnClickListener(clicker);
        button7.setOnClickListener(clicker);
        button8.setOnClickListener(clicker);
        button9.setOnClickListener(clicker);
        swipe.setOnClickListener(clicker);


        pinCodes = (EditText)v.findViewById(R.id.pinCodes);
        repeat = (EditText)v.findViewById(R.id.repeat);

        changedInpService();
        return v;
    }


    @SuppressLint("SetTextI18n")
    public void addNumberInPin(Integer pin){
        if(pinCodes.getText().length() <4){
            pinCodes.setText(pinCodes.getText() + String.valueOf(pin));
            changedInpService();
            if(pinCodes.getText().length() == 4){

                if(hasPin){
                    tvName.setText("Проверка Pin кода");
                    try {
                        request(getSha1((id+pinCodes.getText().toString())));
                    } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                }
                else {
                tvName.setText("Установка Pin кода");
                if(repeat.getText().length() > 0){
                    if(Integer.parseInt(String.valueOf(repeat.getText())) == Integer.parseInt(String.valueOf(pinCodes.getText()))) {
                        try {
                            request(getSha1((id+pinCodes.getText().toString())));
                        } catch (NoSuchAlgorithmException e) {
                            e.printStackTrace();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }

                    }
                    else backgroundThreadShortToast(getContext(),"Указанные Pin коды не совпадают!");
                }
                else{
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setCancelable(false).setPositiveButton("Ввести PIN код", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                }
                            })
                            .setTitle("Повторите еще раз для создания PIN кода");
                    AlertDialog alert = builder.create();
                    alert.show();
                    repeat.setText(pinCodes.getText());
                    pinCodes.getText().clear();
                    changedInpService();
                }
                }
            }
        }
    }

    public static void backgroundThreadShortToast(final Context context, final String msg) {
        if (context != null && msg != null) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
                }
            });
        }
    }


    private  String getSha1(String pin) throws NoSuchAlgorithmException, UnsupportedEncodingException {

        MessageDigest crypt = MessageDigest.getInstance("SHA-1");
        crypt.reset();
        crypt.update(pin.getBytes("UTF-8"));
        sha1 = new BigInteger(1, crypt.digest()).toString(16);
        return sha1;
    }

    public void request(final String pinched){
        Log.e("Auth_SHA1",pinched);
        if(clicked==0) {
            swipe.setRefreshing(true);
            clicked = 1;
            Retrofit.Builder builder=new Retrofit.Builder().addCallAdapterFactory(RxJava2CallAdapterFactory.create()).baseUrl(Interface.baseURL).addConverterFactory(GsonConverterFactory.create());
            Retrofit retrofit= builder.build();
            Interface intc = retrofit.create(Interface.class);
            retrofit2.Call<ResponseBody> call;
            if(hasPin) call = intc.checkPin(unique,id,pinched);
            else  call = intc.createPin(id,unique,pinched);
            call.enqueue(new retrofit2.Callback<ResponseBody>() {
                @Override
                public void onResponse(retrofit2.Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {

                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        Log.e("Auth_PIN",jsonObject.toString());
                        if(jsonObject.getBoolean("success")){
                                if(hasPin)
                                    Toast.makeText(getContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                else {
                                    sPref = getContext().getSharedPreferences("UserData", Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor = sPref.edit();
                                    editor.putBoolean( "pin", true);
                                    editor.commit();
                                    editor.apply();
                                    Toast.makeText(getContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                                }
                                swipe.setRefreshing(false);
                                Fragment fragment = new walletFR();
                                FragmentManager fm = getActivity().getSupportFragmentManager();
                                FragmentTransaction transaction = fm.beginTransaction();
                                transaction.replace(R.id.contentFragment, fragment);
                                //transaction.addToBackStack(null);
                                transaction.commit();
                                clicked=0;
                        }
                        else {
                            if(jsonObject.has("message"))
                                Toast.makeText(getContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(getContext(), "Ошибка! или Проверьте доступ к Интернету", Toast.LENGTH_SHORT).show();
                            swipe.setRefreshing(false);
                            clicked=0;
                        }
                    }
                        catch (JSONException | NullPointerException | IOException e) {
                        swipe.setRefreshing(false);
                        clicked=0;
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Ошибка! или Проверьте доступ к Интернету", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(retrofit2.Call<ResponseBody> call, Throwable t) {
                    swipe.setRefreshing(false);
                    clicked=0;
                    Toast.makeText(getContext(), "Ошибка! или Проверьте доступ к Интернету", Toast.LENGTH_SHORT).show();
                    t.printStackTrace();
                }
            });
       }
    }
    public void changedInpService(){
        for(int i = 0; i < 4; i++){
            try{
                char codeChar = pinCodes.getText().charAt(i);
                if(i == 0)
                    sims21.setVisibility(View.VISIBLE);
                else if(i == 1)
                    sims22.setVisibility(View.VISIBLE);
                else if(i == 2)
                    sims23.setVisibility(View.VISIBLE);
                else if(i == 3)
                    sims24.setVisibility(View.VISIBLE);
            }
            catch (Exception e){
                if(i == 0)
                    sims21.setVisibility(View.GONE);
                else if(i == 1)
                    sims22.setVisibility(View.GONE);
                else if(i == 2)
                    sims23.setVisibility(View.GONE);
                else if(i == 3)
                    sims24.setVisibility(View.GONE);
            }
        }
    }
}
