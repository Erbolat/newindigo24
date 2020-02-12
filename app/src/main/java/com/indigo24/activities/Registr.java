package com.indigo24.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
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
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


public class Registr extends AppCompatActivity {
    @BindView(R.id.editPhone)
    EditText editPhoneStep2;
    @BindView(R.id.editUsername)
    EditText editUsername;
    @BindView(R.id.editMail)
    EditText editUserEmail;
    @BindView(R.id.editPass)
    EditText editNewPassword;
    @BindView(R.id.editRenewPass)
    EditText editRenewPass;

    @BindView(R.id.linReg)
    LinearLayout linReg;
    @BindView(R.id.llRegStep1)
    LinearLayout llRegStep1;
    @BindView(R.id.llRegStep2)
    LinearLayout llRegStep2;
    @BindView(R.id.btnAuth)
    ImageView btnAuth;
    @BindView(R.id.flagCountry)
    ImageButton btnFlagCountry;
    ArrayList<objCountry> arrCountries = new ArrayList<>();
    @BindView(R.id.editPhoneStep1)
    MaskedEditText editPhoneStep1;
    @BindView(R.id.editSms)
    EditText editSms;
    int replacer = 0 ;
    String device = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        ButterKnife.bind(this);
        getDeviceName();
        llRegStep1.setVisibility(View.VISIBLE);
        llRegStep2.setVisibility(View.GONE);
        menuCountry();

        editSms.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                if(editSms.getText().length() == 4){
                    checkSms(editSms.getText().toString());
                }
            }
        });

        editPhoneStep1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (editPhoneStep1.getMask().length() <= 3) {
                    if (editPhoneStep1.getRawText().length() != 0) {
                        String ph = editPhoneStep1.getText().toString().replace("+", "").replace("-", "").replace(" ", "");
                        checkMPhoneNew(ph);
                    }
                }
                else {
                    if (replacer == 1) {
                        if (editPhoneStep1.getRawText().length() == 0)
                            editPhoneStep1.setMask("###");
                        replacer = 0;
                    }
                    else
                        replacer = 1;
                }}
        });

    }


    @OnClick({R.id.btnAuth, R.id.linReg})
    void onSaveClick(View view) {
        switch (view.getId()){
            case R.id.linReg:
//                if (phoneInput.getText().toString().length() > 0) {
                    if (editUsername.getText().toString().length() > 1) {
//                        if (editUserEmail.getText().toString().length() > 2 && editUserEmail.getText().toString().indexOf("@") != -1) {
                        if (editNewPassword.getText().toString().length() > 5) {
                            if (editRenewPass.getText().toString().equals(editRenewPass.getText().toString()))
                                registr();
                            else
                                backgroundThreadShortToast(getApplicationContext(), "Указанные пароли не совпадают!");
                        } else
                            backgroundThreadShortToast(getApplicationContext(), "Вы указали слишком короткий пароль!");
//                        }
//                        else  backgroundThreadShortToast(getApplicationContext(), "Укажите корректный E-mail!");
                    } else
                        backgroundThreadShortToast(getApplicationContext(), "Укажите корректное имя!");
//                } else
//                    backgroundThreadShortToast(getApplicationContext(), "Отсутствует номер телефона");
                break;
            case R.id.btnAuth:
                if(editPhoneStep1.getRawText().length()>7){
                    sendSms();
                }
                break;
        }
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
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        if(! jsonObject.getBoolean("success")){
                            if(jsonObject.has("message"))
                                Toast.makeText(Registr.this, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                        }
                        ImageButton flag = (ImageButton) findViewById(R.id.flagCountry);
                        Picasso.get()
                                .load(jsonObject.getString("icon"))
                                .into(flag);

                        if(editPhoneStep1.getRawText().length() == 3){
                            editPhoneStep1.setMask(jsonObject.getString("mask").replace("*", "#"));
                            editPhoneStep1.setSelection(editPhoneStep1.getText().toString().length());
                            replacer = 0;

                        }   if(editPhoneStep1.getRawText().length() == 2){
                            editPhoneStep1.setMask(jsonObject.getString("mask").replace("*", "#"));
                            editPhoneStep1.setSelection(editPhoneStep1.getText().toString().length());
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
    private void sendSms() {
        btnAuth.setClickable(false);
        Retrofit.Builder builder=new Retrofit.Builder().addCallAdapterFactory(RxJava2CallAdapterFactory.create()).baseUrl(Interface.baseURL).addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit= builder.build();
        Interface intc = retrofit.create(Interface.class);
        retrofit2.Call<ResponseBody> call = intc.sendSMS(editPhoneStep1.getText().toString().replace("+","").replace("-","").replace(" ",""),Interface.tokenSMS);
        call.enqueue(new retrofit2.Callback<ResponseBody>() {
                @Override
                public void onResponse(retrofit2.Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        Log.e("ERA122", jsonObject.toString());
                        if(jsonObject.getBoolean("success")){
                            if(jsonObject.has("message"))
                                Toast.makeText(Registr.this, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                            editSms.setVisibility(View.VISIBLE);
                            editPhoneStep1.setKeyListener(null);
                        }
                        else {
                            if(jsonObject.has("message"))
                            Toast.makeText(Registr.this, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                            btnAuth.setClickable(true);
                        }
                    } catch (JSONException | NullPointerException | IOException e) {
                        e.printStackTrace();
                        btnAuth.setClickable(true);
                    }}

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(Registr.this, "Ошибка! или Проверьте доступ к Интернету", Toast.LENGTH_SHORT).show();
                btnAuth.setClickable(true);
                t.printStackTrace();
            }
        });
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
                                    editPhoneStep1.setMask(arrCountries.get(0).getMask().replace("*","#"));
                                    editPhoneStep1.setSelection(editPhoneStep1.getRawText().toString().length());
                                    Picasso.get().load("https://api.indigo24.com/flags/"+arrCountries.get(0).getIcon()).into(btnFlagCountry);
                                }
                            });
                        }

                    }
                    else {
                    }

                } catch (JSONException | NullPointerException | IOException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(retrofit2.Call<ResponseBody> call, Throwable t) {
                Toast.makeText(Registr.this, "Ошибка! или Проверьте доступ к Интернету", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });

    }
    private void checkSms(String code) {
        Retrofit.Builder builder=new Retrofit.Builder().addCallAdapterFactory(RxJava2CallAdapterFactory.create()).baseUrl(Interface.baseURL).addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit= builder.build();
        Interface intc = retrofit.create(Interface.class);
        retrofit2.Call<ResponseBody> call = intc.checkSMS(editPhoneStep1.getText().toString().replace("+","").replace("-","").replace(" ",""),code);
        call.enqueue(new retrofit2.Callback<ResponseBody>() {
                @Override
                public void onResponse(retrofit2.Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().string());
                        Log.e("ERA1223", jsonObject.toString());
                        if(jsonObject.getBoolean("success")){
                            llRegStep2.setVisibility(View.VISIBLE);
                            llRegStep1.setVisibility(View.GONE);
                            editPhoneStep2.setText(editPhoneStep1.getText().toString());
                            editPhoneStep2.setKeyListener(null);
                        }
                        else {
                            if(jsonObject.has("message"))
                            Toast.makeText(Registr.this, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException | NullPointerException | IOException e) {
                        e.printStackTrace();
                    }}

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(Registr.this, "Ошибка! или Проверьте доступ к Интернету", Toast.LENGTH_SHORT).show();
                btnAuth.setClickable(true);
                t.printStackTrace();
            }
        });
    }

    private void registr() {
        linReg.setClickable(false);
        Retrofit.Builder builder=new Retrofit.Builder().addCallAdapterFactory(RxJava2CallAdapterFactory.create()).baseUrl(Interface.baseURL).addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit= builder.build();
        Interface intc = retrofit.create(Interface.class);
        retrofit2.Call<ResponseBody> call = intc.reg(editPhoneStep2.getText().toString(),editNewPassword.getText().toString(),
                editUsername.getText().toString(),device,Interface.tokenREG);

        call.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    Log.e("ERA1", jsonObject.toString());
                    if(jsonObject.getBoolean("success")){
                        SharedPreferences settings = getSharedPreferences("UserData", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString( "phone", editPhoneStep2.getText().toString().replace("+","").replace("-","").replace(" ",""));
                        editor.putString( "id", jsonObject.getInt("ID")+"");
                        editor.putString( "unique", jsonObject.getString("unique"));
                        editor.commit();
                        editor.apply();
                        Toast.makeText(Registr.this, "Поздравляем! Вы успешно зарегистрированы", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Registr.this, MainActivity.class));
                        finish();
                    }
                    else {
                        if(jsonObject.has("message"))
                            Toast.makeText(Registr.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException | NullPointerException | IOException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(retrofit2.Call<ResponseBody> call, Throwable t) {
                Toast.makeText(Registr.this, "Ошибка! или Проверьте доступ к Интернету", Toast.LENGTH_SHORT).show();
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
                    editPhoneStep1.setMask(arrCountries.get(menuItem.getItemId()).getMask().replace("*", "#"));
                    editPhoneStep1.setSelection(editPhoneStep1.getText().toString().length());
                    replacer = 0;
                    return false;
                }
            });
            menu.show();
        }
    }

    public String getDeviceName() {
        int sdk = 0;
        String vers = null;
        try {
            sdk = Build.VERSION.SDK_INT;
            vers =  Build.VERSION.RELEASE;
            PackageInfo pInfo = getPackageManager().getPackageInfo(Registr.this.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        device = "Android "+vers + "("+sdk+")";
        return  device;
    }
    private String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }
    public static void backgroundThreadShortToast(final Context context, final String msg) {
        if (context != null && msg != null) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
