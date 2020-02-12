package com.indigo24.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
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

public class servicesPaysFR extends Fragment {
    ListView listServices;
    SwipeRefreshLayout swipe;
    SharedPreferences sPref;
    String unique,userID,objID;
    ArrayList<object> arrServices = new ArrayList<>();
    AdapterCateg adapter;
    Toolbar toolbar;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_services, container, false);
        arrServices = new ArrayList<>();
        Wallet.shag=2;
        listServices = v.findViewById(R.id.listServices);
        swipe = v.findViewById(R.id.swipe);
        sPref = getActivity().getSharedPreferences("UserData",getActivity().MODE_PRIVATE);
        userID  = sPref.getString("id","");
        unique  = sPref.getString("unique","");
        Bundle bundle = this.getArguments();
        if(bundle != null){
            objID = bundle.getString("objID");
            Log.e("NEN1", objID+"");
            getServices();
        }

        toolbar = (Toolbar) v.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle("Сервисы");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        adapter = new AdapterCateg(getContext(),arrServices, "services");
        listServices.setAdapter(adapter);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipe.setRefreshing(false);
            }
        });


        listServices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle=new Bundle();
                bundle.putString("serviceID", arrServices.get(position).getId()+"");
                bundle.putString("urlPhoto", arrServices.get(position).getLogo()+"");
                Fragment fragment = new itemPaysFR();
                fragment.setArguments(bundle);
                FragmentManager fm = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                transaction.replace(R.id.contentFragment, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return v;
    }

    private void getServices() {
        swipe.setRefreshing(true);
        Retrofit.Builder builder=new Retrofit.Builder().addCallAdapterFactory(RxJava2CallAdapterFactory.create()).baseUrl(Interface.baseURL).addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit= builder.build();
        Interface intc = retrofit.create(Interface.class);
        retrofit2.Call<ResponseBody> call = intc.getServices(unique,userID,objID);
        Log.e("NEN1", unique+ "  "+userID+" "+objID);
        call.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    Log.e("NEN", jsonObject.toString());
                    if(jsonObject.getBoolean("success")){
                        JSONArray jsobj = jsonObject.getJSONArray("services");
                        if(jsobj.length()>0){

                            for(int i=0; i<jsobj.length(); i++)
                            {
                                object obj = new object();
                                obj.setId(jsobj.getJSONObject(i).getString("id"));
                                obj.setLogo(jsobj.getJSONObject(i).getString("logo"));
                                obj.setTitle(jsobj.getJSONObject(i).getString("title"));
                                obj.setComission(jsobj.getJSONObject(i).getString("commission"));
                                arrServices.add(obj);
                            }
                            adapter = new AdapterCateg(getContext(),arrServices, "services");
                            listServices.setAdapter(adapter);
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