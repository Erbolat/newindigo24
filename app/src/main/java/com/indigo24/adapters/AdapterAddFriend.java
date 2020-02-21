package com.indigo24.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.indigo24.R;
import com.indigo24.objects.User;
import com.indigo24.objects.object;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.Inflater;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;

import static com.indigo24.requests.Interface.baseAVATAR;
import static com.indigo24.requests.Interface.baseIMG;


public class AdapterAddFriend extends BaseAdapter {
    Context ctx;
    LayoutInflater lInflater;
    int type;
    ArrayList<User> objects;

    public AdapterAddFriend(Context context, ArrayList<User> obj, int type) {
        ctx = context;
        objects = obj;
        this.type = type;
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
            view =  LayoutInflater.from(ctx).inflate(R.layout.item_add_friend, parent, false);
        }
        TextView tvName = view.findViewById(R.id.tvName);
        TextView tvPhone = view.findViewById(R.id.tvPhone);
        ImageView imgStatus = view.findViewById(R.id.imgStatus);
        ImageView imgAva = view.findViewById(R.id.imgAva);
        tvName.setText(objects.get(position).getName());
        tvPhone.setText(objects.get(position).getPhone());




        if(type == 1) {
        if(objects.get(position).getStatus().equals("-2") || objects.get(position).getStatus().equals("-1") ) {
            imgAva.setImageDrawable(ContextCompat.getDrawable(ctx, R.drawable.ic_person));
            imgStatus.setVisibility(View.GONE);
        }
        else if(objects.get(position).getStatus().equals("0") ) {
            imgAva.setImageDrawable(ContextCompat.getDrawable(ctx, R.drawable.ic_person));
            imgStatus.setVisibility(View.VISIBLE);
            imgStatus.setImageDrawable(ContextCompat.getDrawable(ctx, R.drawable.delete));
        }
            else  {
            Picasso.get().load(baseAVATAR+objects.get(position).getAvatar()).transform(new CropCircleTransformation()).into(imgAva);
                 imgStatus.setVisibility(View.VISIBLE);
            imgStatus.setImageDrawable(ContextCompat.getDrawable(ctx, R.drawable.checkmark));
            }
        }
        else imgStatus.setVisibility(View.GONE);




        return view;
    }



}
