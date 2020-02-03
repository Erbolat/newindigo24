package com.indigo24.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.indigo24.R;
import com.indigo24.adapters.AdapterMyPays;
import com.indigo24.adapters.ViewPageAdapterPays;
import com.indigo24.objects.myPay;

import java.util.ArrayList;


public class allPaysFR extends Fragment {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    public ListView SerachServides;
    Toolbar toolbar;
    FrameLayout backed;
    AdapterMyPays boxAdaptered;
    ArrayList<myPay> myPaysed = new ArrayList<myPay>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_all_pays, container, false);
        toolbar = (Toolbar) v.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        toolbar.setTitle("Все платежи");
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });

        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        tabLayout = (TabLayout) v.findViewById(R.id.tabsPays);
        viewPager = (ViewPager) v.findViewById(R.id.viepage);
        SerachServides = (ListView) v.findViewById(R.id.serachServidesed);

        boxAdaptered = new AdapterMyPays(getActivity(), myPaysed);

        ViewPageAdapterPays adapterPays = new ViewPageAdapterPays(getChildFragmentManager());
//        adapterPays.AddFragment(new myPays(), "Мои платежи");
        adapterPays.AddFragment(new categoryPaysFR(), "Все платежи");
//        adapterPays.AddFragment(new trazaction(), "История платежей");

        viewPager.setAdapter(adapterPays);
        tabLayout.setupWithViewPager(viewPager);
        backed = v.findViewById(R.id.backed);

        return v;
    }

}
