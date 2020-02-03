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
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.indigo24.R;
import com.indigo24.requests.Interface;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


public class Registr extends AppCompatActivity {
    @BindView(R.id.editPhone)
    EditText phoneInput;
    @BindView(R.id.editName)
    EditText editUsername;
    @BindView(R.id.editMail)
    EditText editUserEmail;
    @BindView(R.id.editPass)
    EditText editNewPassword;
    @BindView(R.id.editRenewPass)
    EditText editRenewPass;

    @BindView(R.id.tvRegistr)
    TextView tvRegistr;

    @BindView(R.id.tvAuth)
    TextView tvAuth;
    String device = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        ButterKnife.bind(this);
        getDeviceName();

        tvRegistr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (phoneInput.getText().toString().length() > 0) {
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
                } else
                    backgroundThreadShortToast(getApplicationContext(), "Отсутствует номер телефона");
            }
        });

    }

    private void registr() {
        tvRegistr.setClickable(false);
        Retrofit.Builder builder=new Retrofit.Builder().addCallAdapterFactory(RxJava2CallAdapterFactory.create()).baseUrl(Interface.baseURL).addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit= builder.build();
        Interface intc = retrofit.create(Interface.class);
        retrofit2.Call<ResponseBody> call = intc.reg(phoneInput.getText().toString(),editNewPassword.getText().toString(),
                editUsername.getText().toString(),device,Interface.tokenREG);

        call.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    if(jsonObject.getBoolean("success")){
                        SharedPreferences settings = getSharedPreferences("UserData", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString( "phone", phoneInput.getText().toString());
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
        Log.e("DEV",device);
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
