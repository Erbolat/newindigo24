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


public class AdapterAddFriend extends BaseAdapter {
    Context ctx;
    LayoutInflater lInflater;
    ArrayList<object> objects;
    ImageView img;
    TextView tvTitle, tvCount;
    String type;
    Map<String, String> map = new HashMap<String, String>();

    public AdapterAddFriend(Context context, ArrayList<object> obj, String type) {
        ctx = context;
        objects = obj;
        this.type = type;
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



    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.item_add_friend, parent, false);
        }
        TextView tvName = view.findViewById(R.id.tvName);
        TextView tvPhone = view.findViewById(R.id.tvPhone);
        ImageView img = view.findViewById(R.id.img);

        tvName.setText(objects.get(position).getName());
        tvPhone.setText(objects.get(position).getAccount());




        return view;
    }



}
