package com.indigo24.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.indigo24.R;
import com.indigo24.objects.categories;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.indigo24.requests.Interface.baseIMG;


public class AdapterCateg extends BaseAdapter {
    Context ctx;
    LayoutInflater lInflater;
    ArrayList<categories> objects;
    ImageView img;
    TextView tvTitle, tvCount;
    String type;
    Map<String, String> map = new HashMap<String, String>();

    public AdapterCateg(Context context, ArrayList<categories> categ, String type) {
        ctx = context;
        objects = categ;
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
            view = lInflater.inflate(R.layout.item_categ, parent, false);
        }
        tvTitle = view.findViewById(R.id.tvTitle);
        tvCount = view.findViewById(R.id.tvCount);
        img = view.findViewById(R.id.img);

        tvTitle.setText(objects.get(position).getTitle());
        if(type.equals("categ")) {
            tvCount.setVisibility(View.VISIBLE);
            tvCount.setText(objects.get(position).getCount());
        }
        else tvCount.setVisibility(View.GONE);
        Picasso.get().load(baseIMG+objects.get(position).getLogo()).into(img);
        return view;
    }



}
