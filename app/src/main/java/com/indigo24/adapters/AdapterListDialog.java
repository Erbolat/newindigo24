package com.indigo24.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.indigo24.R;
import com.indigo24.objects.object;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class AdapterListDialog extends BaseAdapter {
    Context ctx;
    LayoutInflater lInflater;
    ArrayList<object> objects;
    LinearLayout llIn, llOut;
    ImageView img;
    TextView tvMsgOut, tvMsgIn, tvDateIn, tvDateOut;
    String type;
    String myUserID="30220";
    Map<String, String> map = new HashMap<String, String>();

    public AdapterListDialog(Context context, ArrayList<object> obj) {
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
            view = lInflater.inflate(R.layout.item_list_dialog, parent, false);
        }
        llIn = view.findViewById(R.id.llIn);
        llOut = view.findViewById(R.id.llOut);
        tvMsgOut = view.findViewById(R.id.tvMsgOut);
        tvMsgIn = view.findViewById(R.id.tvMsgIn);
        tvDateIn = view.findViewById(R.id.tvDateIn);
        tvDateOut = view.findViewById(R.id.tvDateOut);

        if(!objects.get(position).getUserID().equals(myUserID)) {
            llIn.setVisibility(View.VISIBLE);
            llOut.setVisibility(View.INVISIBLE);
            tvMsgIn.setText(objects.get(position).getMsg());
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy hh:mm ");
            Date updatedate = new Date(Integer.parseInt(objects.get(position).getData()) * 1000L);
            tvDateIn.setText((format.format(updatedate))+"");

        }
        else {
            llIn.setVisibility(View.INVISIBLE);
            llOut.setVisibility(View.VISIBLE);
            tvMsgOut.setText(objects.get(position).getMsg());
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy hh:mm ");
            Date updatedate = new Date(Integer.parseInt(objects.get(position).getData()) * 1000L);
            tvDateOut.setText((format.format(updatedate))+"");
        }

        return view;
    }



}
