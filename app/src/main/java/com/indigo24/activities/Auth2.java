package com.indigo24.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.textfield.TextInputLayout;
import com.indigo24.ClientWebSocket;
import com.indigo24.MainApp;
import com.indigo24.R;
import com.indigo24.objects.objCountry;
import com.indigo24.requests.Interface;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import br.com.sapereaude.maskedEditText.MaskedEditText;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class Auth2 extends AppCompatActivity  {

    @BindView(R.id.editPhone)
    MaskedEditText editPhone;
    @BindView(R.id.flagCountry)
    ImageButton btnFlagCountry;
    ArrayList<objCountry> arrCountries = new ArrayList<>();
    @BindView(R.id.editPass)
    EditText editPass;
    @BindView(R.id.btnAuth)
    ImageView btnAuth;
    @BindView(R.id.btnForgetPass)
    Button btnForgetPass;
    @BindView(R.id.swipeAuth)
    SwipeRefreshLayout swipeAuth;
    @BindView(R.id.linRegistr)
    LinearLayout linRegistr;
    String phone, password;
    String unique="",userID="";
    SharedPreferences sPref;
    int replacer = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth2);
        ButterKnife.bind(this);
        sPref = getSharedPreferences("UserData",Context.MODE_PRIVATE);
        userID  = sPref.getString("id","");
        unique  = sPref.getString("unique","");
//        Log.e("ER111", userID);
//        Log.e("ER222", unique);
        menuCountry();
        getPhoneNumber();




        editPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (editPhone.getMask().length() <= 3) {
                    if (editPhone.getRawText().length() != 0) {
                        String ph = editPhone.getText().toString().replace("+", "").replace("-", "").replace(" ", "");
                        checkMPhoneNew(ph);
                    }

                }
                else {
                    if (replacer == 1) {
                        if (editPhone.getRawText().length() == 0)
                            editPhone.setMask("###");
                            replacer = 0;
                        }
                    else
                        replacer = 1;
            }}
        });
    }


    private void checkMPhoneNew(String phone){
        Retrofit.Builder builder=new Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .baseUrl("https://api.indigo24.com/api/v2/")
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit= builder.build();
        Interface client = retrofit.create(Interface.class);
        retrofit2.Call<ResponseBody> call = client.checkPhone("unique",phone);

        call.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                if (response.isSuccessful()){
                    try {
                        JSONObject yu = new JSONObject(response.body().string());
                        if(! yu.getBoolean("success")){
                            if(yu.has("message"))
                                Toast.makeText(Auth2.this, yu.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                        ImageButton flag = (ImageButton) findViewById(R.id.flagCountry);

                        Picasso.get()
                                .load(yu.getString("icon"))
                                .into(flag);

                        if(editPhone.getRawText().length() == 3){
                            editPhone.setMask(yu.getString("mask").replace("*", "#"));
                            editPhone.setSelection(editPhone.getText().toString().length());
                            replacer = 0;

                        }   if(editPhone.getRawText().length() == 2){
                            editPhone.setMask(yu.getString("mask").replace("*", "#"));
                            editPhone.setSelection(editPhone.getText().toString().length());
                            replacer = 0;
                        }

                    } catch (JSONException | NullPointerException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

            }
            @Override
            public void onFailure(retrofit2.Call<ResponseBody> call, Throwable t) {
                t.printStackTrace();
            }

        });
    }
    private void getPhoneNumber() {
        phone = sPref.getString("phone", "");
        if(!phone.isEmpty())
        editPhone.setText(phone);
    }
    private void requestAuth() {
        swipeAuth.setRefreshing(true);
        btnAuth.setClickable(false);
        Retrofit.Builder builder=new Retrofit.Builder().addCallAdapterFactory(RxJava2CallAdapterFactory.create()).baseUrl(Interface.baseURL).addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit= builder.build();
        Interface intc = retrofit.create(Interface.class);
        retrofit2.Call<ResponseBody> call = intc.auth(phone,password);
        call.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {

                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    Log.e("Auth",jsonObject.toString());
                    if(jsonObject.getBoolean("success")){
                        try{
                            SharedPreferences settings = getSharedPreferences("UserData", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putString( "phone", editPhone.getText().toString().replace("+","").replace("-","").replace(" ",""));
                            editor.putString( "unique", jsonObject.getString("unique"));
                            editor.putString( "id", jsonObject.getInt("ID")+"");
                            editor.putBoolean( "pin", jsonObject.getBoolean("pin"));
                            editor.commit();
                            editor.apply();
                            refresh();
                            startActivity(new Intent(Auth2.this, MainActivity.class));
                            finish();
                        }catch (NullPointerException e){
                            refresh();
                            e.printStackTrace();
                        }
                    }
                    else {
                        if(jsonObject.has("message"))
                            Toast.makeText(Auth2.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        else
                            Toast.makeText(Auth2.this, "Неправильный телефон или пароль", Toast.LENGTH_SHORT).show();
                        refresh();
                    }

                } catch (JSONException | NullPointerException | IOException e) {
                    refresh();
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(retrofit2.Call<ResponseBody> call, Throwable t) {
                refresh();
                Toast.makeText(Auth2.this, "Ошибка! или Проверьте доступ к Интернету", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }
    @OnClick({R.id.btnAuth, R.id.btnForgetPass,R.id.swipeAuth, R.id.linRegistr,
    R.id.flagCountry})
    void onSaveClick(View v) {

        switch (v.getId()) {
            case R.id.btnAuth:
                phone = editPhone.getText().toString();
                password = editPass.getText().toString();
                if(!phone.isEmpty() && !password.isEmpty())
                requestAuth();
                else Toast.makeText(this, "Заполните логин и пароль", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnForgetPass:
                requestForgetPass();
                break;
            case R.id.swipeAuth:
                swipeAuth.setRefreshing(false);
                break;
//            case R.id.editPhone:
//               
//                break;
            case R.id.linRegistr:
                startActivity(new Intent(Auth2.this,Registr.class));
                finish();
                break;
            case R.id.flagCountry:
                showPopup(btnFlagCountry);
                break;
        }
    }

    private void menuCountry() {
        Retrofit.Builder builder=new Retrofit.Builder().addCallAdapterFactory(RxJava2CallAdapterFactory.create()).baseUrl("https://api.indigo24.com/api/v2/").addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit= builder.build();
        Interface intc = retrofit.create(Interface.class);
        retrofit2.Call<ResponseBody> call = intc.getCountries("1");
        call.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {

                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        Log.e("ERA!", jsonObject.toString());
                        if(jsonObject.getBoolean("success")){
                            JSONArray jsonArray = jsonObject.getJSONArray("result");
                            if(jsonArray.length()>0){
                                for (int i=0; i<jsonArray.length(); i++) {
                                objCountry country = new objCountry();
                                country.setCode(jsonArray.getJSONObject(i).getString("code"));
                                country.setIcon(jsonArray.getJSONObject(i).getString("icon"));
                                country.setLength(jsonArray.getJSONObject(i).getInt("length"));
                                country.setMask(jsonArray.getJSONObject(i).getString("mask"));
                                country.setPrefix(jsonArray.getJSONObject(i).getString("prefix"));
                                country.setTitle(jsonArray.getJSONObject(i).getString("title"));
                                arrCountries.add(country);
                                }

                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        editPhone.setMask(arrCountries.get(0).getMask().replace("*","#"));
                                        editPhone.setSelection(editPhone.getRawText().length());
                                        Picasso.get().load("https://api.indigo24.com/flags/"+arrCountries.get(0).getIcon()).into(btnFlagCountry);
                                }
                                });
                            }


                        }
                        else {
                        }

                    } catch (JSONException | NullPointerException | IOException e) {
                        refresh();
                        e.printStackTrace();
                    }
            }
            @Override
            public void onFailure(retrofit2.Call<ResponseBody> call, Throwable t) {
                Toast.makeText(Auth2.this, "Ошибка! или Проверьте доступ к Интернету", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });

    }
    public void showPopup(View v) {
        PopupMenu menu = new PopupMenu(this, v);
        if(arrCountries.size()>0) {
            for (int i = 0; i < arrCountries.size(); i++)
                menu.getMenu().add(0, i, i, arrCountries.get(i).getTitle());

            menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    Picasso.get().load("https://api.indigo24.com/flags/" + arrCountries.get(menuItem.getItemId()).getIcon()).into(btnFlagCountry);
                    editPhone.setMask(arrCountries.get(menuItem.getItemId()).getMask().replace("*", "#"));
                    editPhone.setSelection(editPhone.getText().toString().length());
                    replacer = 0;
                    return false;
                }
            });
            menu.show();
        }
    }
    private void refresh() {
        swipeAuth.setRefreshing(false);
        btnAuth.setClickable(true);
    }

    private void requestForgetPass() {
    }
}
