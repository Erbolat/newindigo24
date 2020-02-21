package com.indigo24.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.indigo24.R;
import com.indigo24.objects.User;
import com.indigo24.objects.object;
import com.indigo24.requests.Interface;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;


public class AdapterOfMembers extends BaseAdapter {
    Context ctx;

    ArrayList<User> objects;

    public AdapterOfMembers(Context context, ArrayList<User> obj) {
        ctx = context;
        objects = obj;


    }


    @Override
    public int getCount() {
        return objects.size();
    }

    @Override
    public Object getItem(int position) {
        return objects.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }



    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View view = convertView;
        if (view == null) {
            view =  LayoutInflater.from(ctx).inflate(R.layout.item_members, parent, false);
        }
        TextView tvName = view.findViewById(R.id.tvName);
        TextView tvAdmin = view.findViewById(R.id.tvAdmin);
        ImageView img = view.findViewById(R.id.img);

        tvName.setText(objects.get(position).getName());
        Picasso.get().load(Interface.baseAVATAR+objects.get(position).getAvatar()).transform(new CropCircleTransformation()).into(img);


        if(objects.get(position).getAdmin().equals("2"))
            tvAdmin.setText("Root-админ");
        else if(objects.get(position).getAdmin().equals("1") )
            tvAdmin.setText("админ");
        else   tvAdmin.setText("");




        return view;
    }



}
