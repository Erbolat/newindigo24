package com.indigo24.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import com.indigo24.R;
import com.indigo24.activities.Chat;
import com.indigo24.activities.MainActivity;

import butterknife.BindView;
import butterknife.ButterKnife;


public class AddFriendFR extends Fragment {
    @BindView(R.id.list)
    ListView list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_friend, container, false);
        ButterKnife.bind(this,v);



        return v;
    }

}

