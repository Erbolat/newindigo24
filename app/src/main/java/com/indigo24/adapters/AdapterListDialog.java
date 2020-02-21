package com.indigo24.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.indigo24.R;
import com.indigo24.objects.User;
import com.indigo24.objects.object;
import com.indigo24.requests.Interface;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import jp.wasabeef.picasso.transformations.CropCircleTransformation;
import okhttp3.MediaType;


public class AdapterListDialog extends BaseAdapter {
    Context ctx;
    ArrayList<object> objects;
    LinearLayout llIn, llOut,llSystem;
    ImageView img;
    TextView tvMsgOut, tvMsgIn, tvDateIn, tvDateOut ,tvTitleIn, tvTitleOut, tvSystem;
    ImageView imgAvaIn, imgAvaOut;
    String type;
    String myUserID;
    MediaPlayer mp = null;
    Handler mSeekbarUpdateHandler;
    Runnable mUpdateSeekbar;

    public AdapterListDialog(Context context, ArrayList<object> obj, String myID) {
        myUserID = myID;
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
            if(objects.get(position).getType().equals("15"))
            view =  LayoutInflater.from(ctx).inflate(R.layout.item_list_dialog, parent, false);
            else if(objects.get(position).getType().equals("1"))
                view =  LayoutInflater.from(ctx).inflate(R.layout.item_list_audio, parent, false);

        }



        if(objects.get(position).getType().equals("15")) {
            view =  LayoutInflater.from(ctx).inflate(R.layout.item_list_dialog, parent, false);
            llIn = view.findViewById(R.id.llIn);
            llOut = view.findViewById(R.id.llOut);
            llSystem = view.findViewById(R.id.llSystem);
            tvMsgOut = view.findViewById(R.id.tvMsgOut);
            tvMsgIn = view.findViewById(R.id.tvMsgIn);
            tvSystem = view.findViewById(R.id.tvSystem);
            tvDateIn = view.findViewById(R.id.tvDateIn);
            tvDateOut = view.findViewById(R.id.tvDateOut);
            tvTitleIn = view.findViewById(R.id.tvTitle);
            tvTitleOut = view.findViewById(R.id.tvTitleOut);
            imgAvaIn = view.findViewById(R.id.imgAvaIn);
            imgAvaOut = view.findViewById(R.id.imgAvaOut);

        if(!objects.get(position).getUserID().equals(myUserID) && !objects.get(position).getUserID().equals("0")) {
            tvTitleIn.setText(objects.get(position).getTitle());
            llIn.setVisibility(View.VISIBLE);
            llOut.setVisibility(View.GONE);
            llSystem.setVisibility(View.GONE);
            tvMsgIn.setText(objects.get(position).getMsg());
//            if(objects.get(position).getStatus().equals("online")) {
//
//            tvTitleIn.setBackgroundColor(R.color.green);
//            tvTitleIn.setTextColor(R.color.white);}
//            else  {
//            tvTitleIn.setBackgroundColor(R.color.white);
//            tvTitleIn.setTextColor(R.color.black);
//            }
            Picasso.get().load(Interface.baseAVATAR+objects.get(position).getAvatar()).transform(new CropCircleTransformation()).into(imgAvaIn);
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy hh:mm ");
            Date updatedate = new Date(Integer.parseInt(objects.get(position).getData()) * 1000L);
            tvDateIn.setText((format.format(updatedate))+"");

        }
       else if(objects.get(position).getUserID().equals(myUserID) && !objects.get(position).getUserID().equals("0")) {
            tvTitleOut.setText(objects.get(position).getTitle());
            llIn.setVisibility(View.GONE);
            llSystem.setVisibility(View.GONE);
            llOut.setVisibility(View.VISIBLE);
            tvMsgOut.setText(objects.get(position).getMsg());
            Picasso.get().load(Interface.baseAVATAR+objects.get(position).getAvatar()).transform(new CropCircleTransformation()).into(imgAvaOut);
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy hh:mm ");
            Date updatedate = new Date(Integer.parseInt(objects.get(position).getData()) * 1000L);
            tvDateOut.setText((format.format(updatedate))+"");
        }
//        else if (objects.get(position).getUserID().equals("0")){
//            llIn.setVisibility(View.GONE);
//            llOut.setVisibility(View.GONE);
//            llSystem.setVisibility(View.VISIBLE);
//            tvSystem.setText(objects.get(position).getMsg());
//        }
      }
         if(objects.get(position).getType().equals("1")){
            view =  LayoutInflater.from(ctx).inflate(R.layout.item_list_audio, parent, false);
            ImageButton btnPlay = view.findViewById(R.id.btnPlay);
             SeekBar seekbar_audio = view.findViewById(R.id.seekbar_audio);

             seekbar_audio.setEnabled(false);
                btnPlay.setTag(objects.get(position).getMsg());
                btnPlay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String nameFile = objects.get(position).getMsg().substring(objects.get(position).getMsg().lastIndexOf('/') + 1);
                        String searchUrlFile =  Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() +"/"+ nameFile;

                        File loadFile = new File(searchUrlFile);
                        if(loadFile.exists()) {
                            seekbar_audio.setProgress(0);
                            Log.e("SFile4","DSADSAD");
                            mp = MediaPlayer.create(ctx, Uri.parse(loadFile.getAbsolutePath()));
                            if(mp!=null) {
                            mp.start();

                            if(mSeekbarUpdateHandler != null){
                                mSeekbarUpdateHandler.removeCallbacks(mUpdateSeekbar);
                            }


                            mSeekbarUpdateHandler = new Handler();
                            mUpdateSeekbar = new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        seekbar_audio.setProgress(mp.getCurrentPosition()/ 500);
                                        mSeekbarUpdateHandler.postDelayed(this, 500);
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }
                            };

                            seekbar_audio.setMax(mp.getDuration() / 500);
                            mSeekbarUpdateHandler.postDelayed(mUpdateSeekbar, 500);


//                            seekbar_audio.setProgress(0);

                            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mediaPlayer) {
                                    seekbar_audio.setProgress(100);
                                    btnPlay.setImageResource(R.drawable.exo_controls_play);

                                    mSeekbarUpdateHandler.removeCallbacks(mUpdateSeekbar);
                                    seekbar_audio.setProgress(0);
                                }
                            });

                            btnPlay.setImageResource(R.drawable.exo_pause);}
                        }
                        else  {
                            Toast.makeText(ctx, "Идет скачивание.. Ожидайте 3 сек и снова Play", Toast.LENGTH_SHORT).show();
                            new DownloadFileFromURL().execute("https://indigo24.xyz/uploads/media/"+objects.get(position).getMsg());

                        }
                    }
                });





        }
        return view;
    }




    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        /**
         * Downloading file in background thread
         * */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
                URL url = new URL(f_url[0]);
                String nameFile = f_url[0].substring(f_url[0].lastIndexOf('/') + 1);

                Log.e("rrrrr",f_url[0]);
                Log.e("rrrrr2",nameFile);
                URLConnection conection = url.openConnection();
                conection.connect();
                int lenghtOfFile = conection.getContentLength();
                InputStream input = new BufferedInputStream(url.openStream(), 8192);
                OutputStream output = new FileOutputStream(root+"/"+ nameFile);
                byte data[] = new byte[1024];
                long total = 0;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    output.write(data, 0, count);
                }
                output.flush();
                output.close();
                input.close();

            } catch (Exception e) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ctx, "Ошибка скачивания", Toast.LENGTH_LONG).show();
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(String file_url) {
            notifyDataSetChanged();
        }

    }

}
