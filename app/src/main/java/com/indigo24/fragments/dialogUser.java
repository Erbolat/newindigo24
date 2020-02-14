package com.indigo24.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.devlomi.record_view.RecordButton;
import com.google.gson.JsonArray;
import com.indigo24.R;
import com.indigo24.activities.Chat;
import com.indigo24.activities.Profile;
import com.indigo24.activities.Wallet;
import com.indigo24.adapters.AdapterListChat;
import com.indigo24.adapters.AdapterListDialog;
import com.indigo24.objects.User;
import com.indigo24.objects.VolleyMultipartRequest;
import com.indigo24.objects.object;
import com.indigo24.requests.Interface;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Picasso;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiPopup;

import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;
import okhttp3.ResponseBody;
import pl.droidsonroids.gif.GifImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.PATCH;



public class dialogUser extends Fragment {
    JSONObject sendInit ;
    int page = 0;
    static int newPage=0;
    SharedPreferences sPref;
    int userID=0;
    String unique, id = "";
    ArrayList<object> arrDialog = new ArrayList<>();
    ArrayList<User> arrUsers = new ArrayList<>();
    String userAVA, userNAME, userPHONE;
    AdapterListDialog adapter;
    @BindView(R.id.messageInput)
    public EmojiEditText editMessage;
    @BindView(R.id.swipe)
    SwipeRefreshLayout swipe;
    @BindView(R.id.gifDwn)
    GifImageView gifDwn;
    @BindView(R.id.tvName)
    TextView tvName;
    @BindView(R.id.imgAvatar)
    ImageView imgAvatar;
    @BindView(R.id.record_button)
    RecordButton btnRecord;
    @BindView(R.id.btnSend)
    ImageButton btnSend;
    @BindView(R.id.btnSmile)
    ImageButton btnSmile;
    @BindView(R.id.gifTyping)
    GifImageView gifTyping;
    @BindView(R.id.list)
    ListView list;
    WebSocketClient mWebSocketClient;
    boolean get= false;
    boolean teg= false;
    boolean onTick= false;
    String type = "";
    String cabinetID = "";
    String messageEdit = "";
    ViewGroup rootView;
    EmojiPopup emojiPopup;
    static boolean isWrite = false;
    public static int dialog = 1;
    @BindView(R.id.imgBack)
    ImageView imgBack;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dialog_user, container, false);
        rootView = v.findViewById(R.id.activityRoot);
        ButterKnife.bind(this, v);
        newPage=0;
        emojiPopup = EmojiPopup.Builder.fromRootView(rootView).build(editMessage);
        SharedPreferences sPref = getActivity().getSharedPreferences("UserData",getActivity().MODE_PRIVATE);
        dialog = 1;
        userID  = Integer.parseInt(sPref.getString("id",""));
        unique  = sPref.getString("unique","");
        Bundle args = getArguments();
        sendInit = new JSONObject();
        if (args != null) {
            type = args.getString("type");
            cabinetID = args.getString("cabinetID");
            if(type.equals("2"))  {
                userNAME = args.getString("groupName");
            tvName.setText(userNAME);
                Picasso.get().load(Interface.baseAVATAR+  "5.jpeg").transform(new CropCircleTransformation()).into(imgAvatar);
            }
            Log.e("ERA1+2",  "type= "+type+ " userID="+userID + " grName "+userNAME);
//            editName.setText(args.getString("name"));
//            editCity.setText(args.getString("city"));
//            Picasso.get().load(args.getString("url")).transform(new CropCircleTransformation()).into(img);
        }
        Log.e("TYPEEEEEE",  "type= "+type + " NA"+userNAME);
        page = 0;
        if(mWebSocketClient==null || (mWebSocketClient!=null && !mWebSocketClient.isOpen())) {
           get = false;
           Log.e("ERA789","111");
           arrDialog = new ArrayList<>();
           connectWebSocket();
        }
        else
            Log.e("ERA","7777777777777777");
        adapter = new AdapterListDialog(getContext(), arrDialog, String.valueOf(userID));
        list.setAdapter(adapter);


        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipe.setRefreshing(false);
            }
        });

        list.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (list.getChildAt(0) != null) {
                    swipe.setEnabled(list.getFirstVisiblePosition() == 0 && list.getChildAt(0).getTop() == 0);

                }
                if(firstVisibleItem==0 && get && !teg) {
                        try {
                            if(type.equals("1"))
                                sendInit.put("cmd", "private");
                            else    sendInit.put("cmd", "group");
                            sendInit.put("cabinetID", Integer.parseInt(cabinetID));
                            Log.e("newPAge", newPage+"");
                            sendInit.put("page", newPage);
                            mWebSocketClient.send(sendInit.toString());
                            teg=true;
                            swipe.setRefreshing(false);

                        } catch (JSONException | NullPointerException e) {
                            e.printStackTrace();
                        }

                }
            }});


        editMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(!editMessage.getText().toString().isEmpty()) isWrite = true;
                else isWrite=false;
                if(mWebSocketClient.isOpen() && !editMessage.getText().toString().isEmpty() && isWrite && !onTick){

                    Log.e("ERATick", String.valueOf(onTick));

                    new CountDownTimer(6000, 1000) {
                        public void onTick(long millisUntilFinished) {
                            if(millisUntilFinished / 1000 == 5 ) {
                                onTick = true;
                                sendInit = new JSONObject();
                                try {
                                    sendInit.put("cmd", "sendStatus");
                                    sendInit.put("cabinetID", Integer.parseInt(cabinetID));
                                    sendInit.put("status", 1);
                                    mWebSocketClient.send(sendInit.toString());;
                                } catch (JSONException | NullPointerException e) {
                                    e.printStackTrace();
                                }}
                        }
                        public void onFinish() {
                            onTick = false;
                        }
                    }.start(); }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }

        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @OnClick({R.id.record_button, R.id.btnSend, R.id.btnSmile, R.id.imgBack})
    void onSaveClick(View view) {
        switch (view.getId()) {
            case R.id.btnSend:
                if(!editMessage.getText().toString().isEmpty()) {
                     messageEdit = editMessage.getText().toString();
                    Log.e("ERA1234",messageEdit);
                    btnSend.setVisibility(View.GONE);
                    gifDwn.setVisibility(View.VISIBLE);

                    CountDownTimer myCountDown = new CountDownTimer(450, 400) {
                        public void onTick(long millisUntilFinished) {
                        }

                        public void onFinish() {
                            if(mWebSocketClient.isOpen()){
                                sendInit = new JSONObject();
                            try {
                                sendInit.put("cmd", "sendMessage");
                                sendInit.put("message", messageEdit);
                                sendInit.put("cabinetID", Integer.parseInt(cabinetID));
                                sendInit.put("type", Integer.parseInt(type));
                                mWebSocketClient.send(sendInit.toString());
                                gifDwn.setVisibility(View.GONE);
                                btnSend.setVisibility(View.VISIBLE);
                            } catch (JSONException | NullPointerException e) {
                                e.printStackTrace();
                            }}
                        }
                    };
                    myCountDown.start();

                list.clearFocus();
                list.post(new Runnable() {
                    @Override
                    public void run() {
                        list.setSelection(list.getCount());
                    }
                }); }
                break;
            case R.id.messageInput:
                list.clearFocus();
                list.post(new Runnable() {
                    @Override
                    public void run() {
                        list.setSelection(list.getCount());
                    }
                });
                break;
            case R.id.btnSmile:
                    emojiPopup.toggle();
                break;
                case R.id.imgBack:
                    startActivity(new Intent(getActivity(), Chat.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                    getActivity().finish();

                break;

        }
    }

    private void connectWebSocket() {
        URI uri;
        try {
            uri = new URI("ws://indigo24.xyz:33080/");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        mWebSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.i("Websocket", "Opened");
                JSONObject sendInit = new JSONObject();

                try {
                    sendInit.put("cmd", "init");
                    sendInit.put("userID", userID);
                    sendInit.put("token", unique);
                    mWebSocketClient.send(sendInit.toString());
                } catch (JSONException | NullPointerException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onMessage( String s) {

                final String message = s;
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if(!message.isEmpty()){
                            swipe.setRefreshing(true);
                            Log.e("ERA1+1", message);
                            Log.e("ERA1+2", cabinetID+ " "+type);
                            try {
                                JSONObject js = new JSONObject(message);

                                if(js.has("cmd") && js.getString("cmd").equals("newStatus")){

                                        Log.e("Bekzat","1");
                                        String typing = "\n печатает..";
                                        final String name = tvName.getText().toString().replace(typing,"");

                                        tvName.setText(name+typing);
                                        gifTyping.setVisibility(View.VISIBLE);
                                        CountDownTimer myCountDown = new CountDownTimer(6000, 1000) {
                                            public void onTick(long millisUntilFinished) {
                                            }

                                            public void onFinish() {
                                                tvName.setText(name);
                                                gifTyping.setVisibility(View.GONE);
                                            }
                                        };
                                        myCountDown.start();

                                        swipe.setRefreshing(false);

                                }

                                if(!get && js.has("cmd") && (js.getString("cmd").equals("init"))) {
                                    try {
                                        get = true;
                                        if(type.equals("1"))
                                        sendInit.put("cmd", "private");
                                        else    sendInit.put("cmd", "group");
                                        sendInit.put("cabinetID", Integer.parseInt(cabinetID));
                                        sendInit.put("page", newPage);
                                        mWebSocketClient.send(sendInit.toString());
                                        swipe.setRefreshing(false);

                                    } catch (JSONException | NullPointerException e) {
                                        e.printStackTrace();
                                    }
                                }

                                if(js.has("cmd")  && js.has("insertedID") ){
                                    object obj = new object();
                                    obj.setMsg(messageEdit);
                                    obj.setData(js.getString("date"));
                                    obj.setId(js.getString("insertedID"));
                                    obj.setUserID(String.valueOf(userID));
                                    if(arrUsers.size()>0 && type.equals("1")){
                                        obj.setAvatar(arrUsers.get(0).getAvatar());
                                    }
                                    arrDialog.add(obj);
                                    adapter.notifyDataSetChanged();
                                    list.setSelection(list.getCount());
                                    editMessage.setText("");
                                    swipe.setRefreshing(false);
                                }



                                if(js.has("cmd")  && js.getString("cmd").equals("newMessage") ){
                                    object obj = new object();
                                    obj.setMsg(js.getString("message"));
                                    obj.setData(js.getString("date"));
                                    obj.setUserID(js.getString("fromID"));
                                    obj.setId(js.getString("messageID"));
                                    if(arrUsers.size()>0 && type.equals("1")){
                                        obj.setAvatar(arrUsers.get(0).getAvatar());
                                    }
                                    arrDialog.add(obj);
                                    adapter.notifyDataSetChanged();
                                    list.setSelection(list.getCount());
                                    swipe.setRefreshing(false);
                                }



                                if(js.has("cmd")  && (js.has("members") || js.has("messages"))){
                                    Log.e("EEE123", "11117777");
                                    JSONArray jsArrayMembers = null;

                                    if(js.has("members")){
                                        teg=false;
                                    try {
                                        jsArrayMembers = js.getJSONArray("members");
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    if(jsArrayMembers.length()>0){
                                        for (int i=0;i<jsArrayMembers.length();i++) {
                                            Log.e("ERA1237 Name",jsArrayMembers.getJSONObject(i).getString("name")+"");

                                            if(js.getString("cmd").equals("private")){
                                                Picasso.get().load(Interface.baseAVATAR+  jsArrayMembers.getJSONObject(i).getString("avatar")).transform(new CropCircleTransformation()).into(imgAvatar);
                                                  tvName.setText(jsArrayMembers.getJSONObject(i).getString("name"));
                                            }
                                            User user = new User(jsArrayMembers.getJSONObject(i).getString("userID"), jsArrayMembers.getJSONObject(i).getString("name"), jsArrayMembers.getJSONObject(i).getString("avatar"));
                                            arrUsers.add(user);
                                        }
                                    }} else teg=true;
                                    ArrayList<object>arrayList = new ArrayList<>();
                                    if(teg)  {

                                    arrayList = arrDialog;
                                    arrDialog = new ArrayList<>();
                                    }
                                    JSONArray jsArrayMessages = js.getJSONArray("messages");
                                    if(jsArrayMessages.length()>0){
                                        for(int i =jsArrayMessages.length()-1 ; i>=0; i--) {
                                            object obj = new object();
                                            obj.setMsg(jsArrayMessages.getJSONObject(i).getString("message"));
                                            obj.setStatus(jsArrayMessages.getJSONObject(i).getString("status"));
                                            obj.setData(jsArrayMessages.getJSONObject(i).getString("date"));
                                            obj.setId(jsArrayMessages.getJSONObject(i).getString("ID"));
                                            obj.setUserID(jsArrayMessages.getJSONObject(i).getString("userID"));
                                            if(arrUsers.size()>0 && type.equals("1")){
                                                Log.e("ERR", arrUsers.size()+" !! "+arrUsers.get(0).getAvatar());
                                                obj.setAvatar(arrUsers.get(0).getAvatar());
                                            }
                                            arrDialog.add(obj);
                                        }
                                        if(teg) {
                                        arrDialog.addAll(arrayList);
                                            adapter = new AdapterListDialog(getContext(), arrDialog, String.valueOf(userID));
                                            list.setAdapter(adapter);
                                            list.setSelection(20);
                                            newPage=newPage+1;
                                        }
                                        else  {
                                            newPage=1;
                                            adapter = new AdapterListDialog(getContext(), arrDialog, String.valueOf(userID));
                                            list.setAdapter(adapter);
                                            list.setSelection(list.getCount()-1);
                                        }
                                        teg=false;
                                        swipe.setRefreshing(false);
                                    }
                                }

                } catch (JSONException e) { e.printStackTrace();
            }}}});}

            @Override
            public void onClose(int i, String s, boolean b) {
//                Log.i("Websocket", "Closed " + s);
            }

            @Override
            public void onError(Exception e) {
//                Log.i("Websocket", "Error " + e.getMessage());
            }
        };
        mWebSocketClient.connect();
    }

}
