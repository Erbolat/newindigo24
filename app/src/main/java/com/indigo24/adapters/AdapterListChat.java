package com.indigo24.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;

import com.bumptech.glide.Glide;
import com.indigo24.R;
import com.indigo24.objects.object;
import com.indigo24.requests.Interface;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

import static com.indigo24.requests.Interface.baseIMG;


public class AdapterListChat extends BaseAdapter {
    Context ctx;
    ArrayList<object> objects;
    ImageView img;
    TextView tvName, tvDate, tvLastMsg, tvCount;
    public AdapterListChat(Context context, ArrayList<object> obj) {
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



    @SuppressLint({"SetTextI18n", "ResourceType"})
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View view = convertView;
        if (view == null) {
            view =  LayoutInflater.from(ctx).inflate(R.layout.item_list_chat, parent, false);
        }
        tvName = view.findViewById(R.id.tvName);
        tvCount = view.findViewById(R.id.tvCount);
        tvLastMsg = view.findViewById(R.id.tvLastMsg);
        img = view.findViewById(R.id.img);


        Picasso.get().load(Interface.baseAVATAR + objects.get(position).getAvatar()).transform(new CropCircleTransformation()).into(img);
        tvName.setText(objects.get(position).getName());
        tvLastMsg.setText(objects.get(position).getLastMsg());

        if(objects.get(position).getCount().isEmpty() || objects.get(position).getCount().equals("0") )
            tvCount.setVisibility(View.GONE);
        else  tvCount.setText(objects.get(position).getCount());
//        @SuppressLint("SimpleDateFormat")
//        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy hh:mm ");
//        Date updatedate = new Date(Integer.parseInt(objects.get(position).getData()) * 1000L);
//        tvDate.setText((format.format(updatedate)).replace(" ","\n")+"");



        return view;
    }



}
