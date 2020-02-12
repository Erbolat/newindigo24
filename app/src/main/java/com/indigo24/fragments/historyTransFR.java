package com.indigo24.fragments;

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
import com.indigo24.activities.Wallet;

import com.indigo24.adapters.AdapterHistory;
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

public class historyTransFR extends Fragment {
    ListView list;
    SwipeRefreshLayout swipe;
    SharedPreferences sPref;
    String unique,userID,objID;
    ArrayList<object> arrList = new ArrayList<>();
    AdapterHistory adapter;
    Toolbar toolbar;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_history_trans, container, false);
        arrList = new ArrayList<>();
        list = v.findViewById(R.id.list);
        swipe = v.findViewById(R.id.swipe);
        sPref = getActivity().getSharedPreferences("UserData",getActivity().MODE_PRIVATE);
        userID  = sPref.getString("id","");
        unique  = sPref.getString("unique","");

        getTrans();

        toolbar = (Toolbar) v.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle("История транзакции");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        adapter = new AdapterHistory(getContext(),arrList, "trans");
        list.setAdapter(adapter);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipe.setRefreshing(false);
            }
        });
        return v;
    }

    private void getTrans() {
        swipe.setRefreshing(true);
        Retrofit.Builder builder=new Retrofit.Builder().addCallAdapterFactory(RxJava2CallAdapterFactory.create()).baseUrl(Interface.baseURL).addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit= builder.build();
        Interface intc = retrofit.create(Interface.class);
        retrofit2.Call<ResponseBody> call = intc.getTrans(unique,userID,objID);
        call.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    if(jsonObject.getBoolean("success")){
                        Log.e("TAG1", jsonObject.toString());
                        JSONArray jsobj = jsonObject.getJSONArray("transactions");
                        if(jsobj.length()>0){

                            for(int i=0; i<jsobj.length(); i++)
                            {
                                object obj = new object();
                                obj.setAmount(jsobj.getJSONObject(i).getInt("amount")+"");
                                obj.setAvatar(jsobj.getJSONObject(i).getString("avatar"));
                                obj.setData(jsobj.getJSONObject(i).getString("data"));
                                obj.setFrom(jsobj.getJSONObject(i).getString("from"));
                                obj.setTo(jsobj.getJSONObject(i).getString("to"));
                                obj.setType(jsobj.getJSONObject(i).getString("type"));
                                obj.setDescription(jsobj.getJSONObject(i).getString("description"));
                                arrList.add(obj);
                            }
                            adapter = new AdapterHistory(getContext(),arrList, "trans");
                            list.setAdapter(adapter);
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