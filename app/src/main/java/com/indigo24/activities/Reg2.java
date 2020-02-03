package com.indigo24.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
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

public class Reg2 extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.editPhone)
    MaskedEditText editPhone;
//    @BindView(R.id.editPass)
//    EditText editPass;
    @BindView(R.id.btnAuth)
    ImageView btnAuth;
//    @BindView(R.id.btnForgetPass)
//    Button btnForgetPass;
//    @BindView(R.id.swipeAuth)
//    SwipeRefreshLayout swipeAuth;
//    @BindView(R.id.linRegistr)
//    LinearLayout linRegistr;

    String phone, password;
    String unique="";
    SharedPreferences sPref;
    int start=0;
    String s1 = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg2);
        ButterKnife.bind(this);
        btnAuth.setOnClickListener(this);
        //editPhone.setMask("###########");




        TextWatcher fieldValidatorTextWatcher = new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {
//                    if(editPhone.getRawText().length() == 2) {
//                String oldText = (String) editPhone.getRawText().substring(2, editPhone.getRawText().length());
//                editPhone.setMask("##########");
//                editPhone.setSelection(editPhone.getText().toString().length());
//                editPhone.setText(oldText);
//                Log.e("ERA17", oldText);
//                    }

            }
        };
        editPhone.addTextChangedListener(fieldValidatorTextWatcher);


    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAuth:
                phone = editPhone.getText().toString();
//                password = editPass.getText().toString();
//                if(!phone.isEmpty())
////                requestAuth();
//                else Toast.makeText(this, "Заполните логин и пароль", Toast.LENGTH_SHORT).show();
//                break;

        }
    }



}
