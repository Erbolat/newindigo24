package com.indigo24.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.indigo24.R;
import com.indigo24.activities.Auth;
import com.indigo24.activities.MainActivity;
import com.indigo24.activities.Wallet;

import com.indigo24.adapters.AdapterCateg;
import com.indigo24.objects.object;
import com.indigo24.requests.Interface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class categoryPaysFR extends Fragment {
    ListView listobj;
    SwipeRefreshLayout swipe;
    SharedPreferences sPref;
    String unique,userID;
    ArrayList<object> arrobj = new ArrayList<>();
    AdapterCateg adapter;
    Toolbar toolbar;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_category, container, false);
        Wallet.shag=1;
        arrobj = new ArrayList<>();
        toolbar = (Toolbar) v.findViewById(R.id.toolbar);

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle("Категории");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               getActivity().startActivity(new Intent(getActivity(), MainActivity.class));
               getActivity().finish();
            }
        });

        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        listobj = v.findViewById(R.id.listobj);
        swipe = v.findViewById(R.id.swipe);
        sPref = getActivity().getSharedPreferences("UserData",getActivity().MODE_PRIVATE);
        userID  = sPref.getString("id","");
        unique  = sPref.getString("unique","");
        getCategory();

        adapter = new AdapterCateg(getContext(),arrobj,"obj");
        listobj.setAdapter(adapter);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipe.setRefreshing(false);
            }
        });
        listobj.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(!arrobj.get(position).getCount().equals("0")) {
                Bundle bundle=new Bundle();
                bundle.putString("objID", arrobj.get(position).getId()+"");
                Fragment fragment = new servicesPaysFR();
                fragment.setArguments(bundle);
                FragmentManager fm = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                transaction.replace(R.id.contentFragment, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
                }
                else Toast.makeText(getContext(), "Ближайшее время появится сервисы", Toast.LENGTH_SHORT).show();
            }
        });

        return v;
    }



    private void getCategory() {
        swipe.setRefreshing(true);
        Retrofit.Builder builder=new Retrofit.Builder().addCallAdapterFactory(RxJava2CallAdapterFactory.create()).baseUrl(Interface.baseURL).addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit= builder.build();
        Interface intc = retrofit.create(Interface.class);
        retrofit2.Call<ResponseBody> call = intc.getobj(unique,userID);
        call.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    if(jsonObject.getBoolean("success")){
                        JSONArray jsobj = jsonObject.getJSONArray("categories");
                        Log.e("BEKA1",jsobj.toString()+"");
                        if(jsobj.length()>0){
                            for(int i=0; i<jsobj.length(); i++)
                            {
                                Log.e("BEKA1",jsobj.getJSONObject(i).getInt("count")+"");
                                object obj = new object();
                                obj.setId(jsobj.getJSONObject(i).getString("ID"));
                                obj.setLogo(jsobj.getJSONObject(i).getString("logo"));
                                obj.setCount(jsobj.getJSONObject(i).getInt("count")+"");
                                obj.setTitle(jsobj.getJSONObject(i).getString("title"));
                                arrobj.add(obj);
                            }
                            adapter = new AdapterCateg(getContext(),arrobj, "obj");
                            listobj.setAdapter(adapter);
                            swipe.setRefreshing(false);
                        }
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