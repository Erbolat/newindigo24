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
    LayoutInflater lInflater;
    ArrayList<object> objects;
    ImageView img;
    TextView tvName, tvDate, tvLastMsg;
    String type;
    Map<String, String> map = new HashMap<String, String>();

    public AdapterListChat(Context context, ArrayList<object> obj) {
        ctx = context;
        objects = obj;
        lInflater = (LayoutInflater) ctx.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
            view = lInflater.inflate(R.layout.item_list_chat, parent, false);
        }
        tvName = view.findViewById(R.id.tvName);
        tvDate = view.findViewById(R.id.tvDate);
        tvLastMsg = view.findViewById(R.id.tvLastMsg);
        img = view.findViewById(R.id.img);
        Log.e("AVA",objects.get(position).getAvatar()+ "!"+objects.get(position).getTitle());
        if(!objects.get(position).getAvatar().isEmpty() && objects.get(position).getAvatar().length()>3)
            Picasso.get().load(Interface.baseAVATAR+objects.get(position).getAvatar()).transform(new CropCircleTransformation()).into(img);
//            Glide.with(ctx).load(Interface.baseAVATAR+objects.get(position).getAvatar()).centerCrop().into(img);
        else {
            Drawable drawable = AppCompatResources.getDrawable(ctx, R.drawable.ic_person);
            img.setImageDrawable(drawable);
        }
        tvName.setText(objects.get(position).getName());

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy hh:mm ");
        Date updatedate = new Date(Integer.parseInt(objects.get(position).getData()) * 1000L);
        tvDate.setText((format.format(updatedate)).replace(" ","\n")+"");
        tvLastMsg.setText(objects.get(position).getLastMsg());


        return view;
    }



}
