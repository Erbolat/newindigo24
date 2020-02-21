package com.indigo24.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.indigo24.R;
import com.indigo24.objects.object;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.indigo24.requests.Interface.baseAVATAR;
import static com.indigo24.requests.Interface.baseIMG;


public class AdapterHistory extends BaseAdapter {
    Context ctx;
    LayoutInflater lInflater;
    ArrayList<object> objects;
    ImageView img;
    TextView tvTitle, tvCount;
    String type;
    Map<String, String> map = new HashMap<String, String>();

    public AdapterHistory(Context context, ArrayList<object> obj, String type) {
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
            view =  LayoutInflater.from(ctx).inflate(R.layout.item_history, parent, false);
        }
        TextView tvSumm = view.findViewById(R.id.tvSumm);
        TextView tvName1 = view.findViewById(R.id.tvName1);
        TextView tvName2 = view.findViewById(R.id.tvName2);
        TextView tvData = view.findViewById(R.id.tvData);
        ImageView img = view.findViewById(R.id.img);

        if(type.equals("send")){
            tvData.setText(objects.get(position).getData());
            tvSumm.setText(objects.get(position).getAmount());
            tvName1.setText(objects.get(position).getTitle());
            tvName2.setText(objects.get(position).getAccount());
            Picasso.get().load(baseIMG+objects.get(position).getLogo()).into(img);
        }
        else if(type.equals("trans")){
            tvData.setText(objects.get(position).getData());
            tvSumm.setText(objects.get(position).getAmount());
            tvName1.setText(objects.get(position).getDescription());
            tvName2.setText(objects.get(position).getType());
            if(objects.get(position).getType().equals("in"))
                tvName2.setTextColor(ContextCompat.getColor(ctx, R.color.green));
            else   tvName2.setTextColor(ContextCompat.getColor(ctx, R.color.red));
            Picasso.get().load(baseAVATAR+objects.get(position).getAvatar()).into(img);
        }


        return view;
    }



}
