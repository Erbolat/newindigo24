package com.indigo24.requests;


import android.content.Context;

import java.io.File;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.indigo24.requests.Interface.baseURL;

public class RetrofitClient {
        public  Retrofit retrofit;
        public  Retrofit getRetrofitClient(Context context) {
            if (retrofit == null) {
                OkHttpClient okHttpClient = new OkHttpClient.Builder()
                        .build();
                retrofit = new Retrofit.Builder()
                        .baseUrl(baseURL)
                        .client(okHttpClient)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
            }
            return retrofit;
        }
}
