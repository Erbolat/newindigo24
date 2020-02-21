package com.indigo24.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.indigo24.R;
import com.indigo24.objects.User;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

import static com.indigo24.requests.Interface.baseAVATAR;
import static com.indigo24.requests.Interface.baseURL;


public class AdapterAddFriendSelected extends BaseAdapter {
    Context ctx;
    LayoutInflater lInflater;
    ArrayList<User> objects;
    ImageView imgAva;

    public AdapterAddFriendSelected(Context context, ArrayList<User> obj) {
        ctx = context;
        objects = obj;
    }


    @Override
    public int getCount() {
        if(objects!=null)
        return objects.size();
        else
        return 0;
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
            view =  LayoutInflater.from(ctx).inflate(R.layout.item_add_friend_selected, parent, false);
        }
        TextView tvName = view.findViewById(R.id.tvName);
        TextView tvPhone = view.findViewById(R.id.tvPhone);
        ImageView imgAva = view.findViewById(R.id.imgAva);
        ImageView imgDel = view.findViewById(R.id.imgDel);

        tvName.setText(objects.get(position).getName());
        tvPhone.setText(objects.get(position).getPhone());

        Picasso.get().load(baseAVATAR+objects.get(position).getAvatar()).transform(new CropCircleTransformation()).into(imgAva);


        imgDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                objects.remove(position);
                notifyDataSetChanged();

            }
        });

        return view;
    }



}
