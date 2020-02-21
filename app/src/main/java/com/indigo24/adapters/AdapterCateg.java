package com.indigo24.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.indigo24.R;
import com.indigo24.objects.object;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.indigo24.requests.Interface.baseIMG;


public class AdapterCateg extends BaseAdapter {
    Context ctx;
    LayoutInflater lInflater;
    ArrayList<object> objects;
    ImageView img;
    TextView tvTitle, tvCount;
    String type;
    Map<String, String> map = new HashMap<String, String>();

    public AdapterCateg(Context context, ArrayList<object> obj, String type) {
        ctx = context;
        objects = obj;
        this.type = type;

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
            view = LayoutInflater.from(ctx).inflate(R.layout.item_categ, parent, false);
        }
        tvTitle = view.findViewById(R.id.tvTitle);
        tvCount = view.findViewById(R.id.tvCount);
        img = view.findViewById(R.id.img);

        tvTitle.setText(objects.get(position).getTitle());
        if(type.equals("obj")) {
            tvCount.setVisibility(View.VISIBLE);
            tvCount.setText(objects.get(position).getCount());
        }
        else tvCount.setVisibility(View.GONE);

        Glide.with(ctx)
                .load(baseIMG+objects.get(position).getLogo())
                .centerCrop()
                .into(img);
// .crossFade()
        //                .placeholder(R.drawable.loading_spinner)

//        Picasso.get().load(baseIMG+objects.get(position).getLogo()).into(img);

        return view;
    }



}
