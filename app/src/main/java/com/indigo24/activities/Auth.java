package com.indigo24.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;



import com.indigo24.R;
import com.indigo24.requests.Interface;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import br.com.sapereaude.maskedEditText.MaskedEditText;
import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class Auth extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.editPhone)
    EditText editPhone;
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
    String unique="";
    SharedPreferences sPref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);
        ButterKnife.bind(this);




        getPhoneNumber();
        btnAuth.setOnClickListener(this);
        btnForgetPass.setOnClickListener(this);
        swipeAuth.setOnClickListener(this);
        linRegistr.setOnClickListener(this);

    }

    private void getPhoneNumber() {
        sPref = getSharedPreferences("UserData",Context.MODE_PRIVATE);
        phone = sPref.getString("phone", "");
        if(!phone.isEmpty())
        editPhone.setText(phone);
    }

    @Override
    public void onClick(View v) {
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
            case R.id.linRegistr:
                startActivity(new Intent(Auth.this,Registr.class));
                finish();
                break;
        }
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
                                editor.putString( "phone", editPhone.getText().toString());
                                editor.putString( "unique", jsonObject.getString("unique"));
                                editor.putString( "id", jsonObject.getInt("ID")+"");
                                editor.putBoolean( "pin", jsonObject.getBoolean("pin"));
                                editor.commit();
                                editor.apply();
                                refresh();
                                startActivity(new Intent(Auth.this, MainActivity.class));
                                finish();
                            }catch (NullPointerException e){
                                refresh();
                                e.printStackTrace();
                            }
                        }
                        else {
                            if(jsonObject.has("message"))
                                Toast.makeText(Auth.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                            else
                            Toast.makeText(Auth.this, "Неправильный телефон или пароль", Toast.LENGTH_SHORT).show();
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
                Toast.makeText(Auth.this, "Ошибка! или Проверьте доступ к Интернету", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }

    private void refresh() {
        swipeAuth.setRefreshing(false);
        btnAuth.setClickable(true);
    }

    private void requestForgetPass() {
    }
}
