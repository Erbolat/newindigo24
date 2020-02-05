package com.indigo24.fragments;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.indigo24.R;
import com.indigo24.activities.MainActivity;
import com.indigo24.activities.Profile;
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

import static com.indigo24.requests.Interface.baseAVATAR;

public class profileEditFR extends Fragment {
     @BindView(R.id.editName)
     EditText editName;
     @BindView(R.id.editCity)
     EditText editCity;
     @BindView(R.id.btnSave)
     Button btnSave;
     String unique, userID ,name, city;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.edit_profile, container, false);
        ButterKnife.bind(this,v);

        Bundle args = getArguments();
        if (args != null) {
            editName.setText(args.getString("name"));
            editCity.setText(args.getString("city"));
        }

        return  v;
    }

    @OnClick({R.id.btnSave})
    void onSaveClick(View view) {
        switch (view.getId()) {
            case R.id.btnSave:
                name = editName.getText().toString();
                city = editCity.getText().toString();
                if(city.isEmpty()) city = "";
                if(!name.isEmpty()) save();
                else Toast.makeText(getContext(), "Имя не может быть пустым", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void save() {
        btnSave.setClickable(false);
        SharedPreferences sPref = getContext().getSharedPreferences("UserData",getContext().MODE_PRIVATE);
        userID  = sPref.getString("id","");
        unique  = sPref.getString("unique","");
        Retrofit.Builder builder=new Retrofit.Builder().addCallAdapterFactory(RxJava2CallAdapterFactory.create()).baseUrl(Interface.baseURL).addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit= builder.build();
        Interface intc = retrofit.create(Interface.class);
        retrofit2.Call<ResponseBody> call = intc.editProfile(unique, userID, name, city);

        call.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    if(jsonObject.getBoolean("success")){
                        if(jsonObject.has("message"))
                            Toast.makeText(getContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        getActivity().startActivity(new Intent(getActivity(), Profile.class));
                        getActivity().finish();
                    }
                    else {
                        if(jsonObject.has("message"))
                            Toast.makeText(getContext(), jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        btnSave.setClickable(true);
                    }
                } catch (JSONException | NullPointerException | IOException e) {
                    e.printStackTrace();
                    btnSave.setClickable(true);
                }
            }
            @Override
            public void onFailure(retrofit2.Call<ResponseBody> call, Throwable t) {
                btnSave.setClickable(true);
                Toast.makeText(getContext(), "Ошибка! или Проверьте доступ к Интернету", Toast.LENGTH_SHORT).show();
                t.printStackTrace();
            }
        });
    }


}
