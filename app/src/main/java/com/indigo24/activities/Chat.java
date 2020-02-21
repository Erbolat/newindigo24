package com.indigo24.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.indigo24.ClientWebSocket;
import com.indigo24.MainApp;
import com.indigo24.R;
import com.indigo24.adapters.AdapterListChat;
import com.indigo24.fragments.AddFriendFR;
import com.indigo24.fragments.dialogUser;
import com.indigo24.objects.object;
import com.indigo24.requests.Interface;
import com.neovisionaries.ws.client.ThreadType;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFrame;
import com.neovisionaries.ws.client.WebSocketState;

import org.java_websocket.client.WebSocketClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.content.Context.MODE_PRIVATE;
import static com.indigo24.requests.Interface.TAG;


public  class Chat extends AppCompatActivity implements  ClientWebSocket.MessageListener {
    @BindView(R.id.fabAdd) FloatingActionButton fabAdd;
    @BindView(R.id.list) ListView list;
    @BindView(R.id.container) FrameLayout container;
    Toolbar toolbar;

    AdapterListChat adapter;
    String userID,unique;
    ArrayList<object> arrList = new ArrayList<>();
    SharedPreferences sPref;
    ClientWebSocket  clientWebSocket;
    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        sPref = getSharedPreferences("UserData",MODE_PRIVATE);
        SharedPreferences sPref = getSharedPreferences("UserData",MODE_PRIVATE);
        userID  = sPref.getString("id","");
        unique  = sPref.getString("unique","");

        JSONObject sendInit = new JSONObject();
        try {
            sendInit.put("cmd", "chatList");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        clientWebSocket = new ClientWebSocket(this);
        if(clientWebSocket.getConnection().getSocket()!=null) {
        clientWebSocket.getConnection().addListener(this);
        clientWebSocket.getConnection().sendText(sendInit.toString());
            if(MainApp.arrOnlines.size()>0) {

                for(int i=0; i< MainApp.arrOnlines.size(); i++) {
                    Log.e("ONLINE - ", MainApp.arrOnlines.get(i));
                }
            }
        }

        adapter = new AdapterListChat(getApplicationContext(), arrList);
        list.setAdapter(adapter);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Чат");
        setSupportActionBar(toolbar);
        getSupportActionBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Chat.this, MainActivity.class));
                finish();;
            }
        });
        list.setVisibility(View.VISIBLE);
        fabAdd.setVisibility(View.VISIBLE);
        container.setVisibility(View.GONE);
        toolbar.setVisibility(View.VISIBLE);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                list.setVisibility(View.GONE);
                fabAdd.setVisibility(View.GONE);
                container.setVisibility(View.VISIBLE);
                toolbar.setVisibility(View.GONE);
                Bundle bundle=new Bundle();
                bundle.putString("cabinetID", arrList.get(position).getCabinetID()+"");
                bundle.putString("type", arrList.get(position).getType());
                bundle.putString("name", arrList.get(position).getName());
                bundle.putString("avatar",arrList.get(position).getAvatar());
                Fragment fragment = new dialogUser();
                fragment.setArguments(bundle);
                FragmentManager fm  =  getSupportFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();
                transaction.replace(R.id.container, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
    }






    @OnClick({R.id.fabAdd})
    void onSaveClick(View view) {
        switch (view.getId()) {
            case R.id.fabAdd:
                if(checkWriteExternalPermission()){
                    Log.e(Interface.TAG+"fab1","");
                    try {
                        openFragment();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else {
                    Log.e(Interface.TAG+"fab2","");
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, 123);
                    }
                    if(checkWriteExternalPermission()){
                        openFragment();
                    }
                }
                break;
        }
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onBackPressed() {

        if(dialogUser.step == 1) {
            startActivity(new Intent(Chat.this, Chat.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
            finish();
            dialogUser.step=0;
        }
        else if(dialogUser.step==2) {
            super.onBackPressed();
            dialogUser.step=1;
        }
        else   {
        startActivity(new Intent(Chat.this, MainActivity.class));
        finish(); }

    }
    @SuppressLint("RestrictedApi")
    void openFragment(){
        list.setVisibility(View.GONE);
        fabAdd.setVisibility(View.GONE);
        toolbar.setVisibility(View.GONE);
        container.setVisibility(View.VISIBLE);
        Fragment fragment = new AddFriendFR();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }

    private boolean checkWriteExternalPermission() {
        String permission = Manifest.permission.READ_CONTACTS;
        int res = checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }


    @Override
    public void onStateChanged(WebSocket websocket, WebSocketState newState) throws Exception {

    }

    @Override
    public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {

    }

    @Override
    public void onConnectError(WebSocket websocket, WebSocketException cause) throws Exception {

    }

    @Override
    public void onDisconnected(WebSocket websocket, WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame, boolean closedByServer) throws Exception {

    }

    @Override
    public void onFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {

    }

    @Override
    public void onContinuationFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {

    }

    @Override
    public void onTextFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {

    }

    @Override
    public void onBinaryFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {

    }

    @Override
    public void onCloseFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {

    }

    @Override
    public void onPingFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {

    }

    @Override
    public void onPongFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {

    }

    @Override
    public void onTextMessage(WebSocket websocket, String text) throws Exception {

        final String message = text;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject js = new JSONObject(message);
                    Log.e("ERA1",message);
                    JSONObject sendInit = new JSONObject();
                    if(js.has("cmd") && js.getString("cmd").equals("init")){
                        sendInit.put("cmd", "chatList");
                        clientWebSocket.getConnection().sendText(sendInit.toString());
                    }

                    if(js.has("cmd") && js.getString("cmd").equals("newMessage")){

                        for (int i=0; i<arrList.size(); i++)
                        if(String.valueOf(js.getInt("cabinetID")).equals(arrList.get(i).getCabinetID()+"")) {
                            arrList.get(i).setCount((Integer.valueOf(arrList.get(i).getCount())+1)+"");
                        }
                        adapter = new AdapterListChat(getApplicationContext(), arrList);
                        list.setAdapter(adapter);
                    }


                    if(js.has("cmd") && js.getString("cmd").equals("chatList")) {
                        JSONArray jsonArray = new JSONArray();
                        if (js.has("list")) {
                            jsonArray = js.getJSONArray("list");
                            if(jsonArray.length()>0) {
                                for(int i=0; i<jsonArray.length();i++) {
                                    object obj = new object();
                                    obj.setLastMsg(jsonArray.getJSONObject(i).getString("message"));
                                    obj.setStatus(jsonArray.getJSONObject(i).getString("status"));
                                    obj.setAvatar(jsonArray.getJSONObject(i).getString("avatar"));
                                    obj.setName(jsonArray.getJSONObject(i).getString("name"));
                                    obj.setData(jsonArray.getJSONObject(i).getString("date"));
                                    obj.setCabinetID(jsonArray.getJSONObject(i).getInt("cabinetID"));
                                    obj.setType(jsonArray.getJSONObject(i).getString("type"));
                                    obj.setCount("0");
                                    arrList.add(obj);
                                } } }
                        adapter = new AdapterListChat(getApplicationContext(), arrList);
                        list.setAdapter(adapter);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });

    }

    @Override
    public void onTextMessage(WebSocket websocket, byte[] data) throws Exception {

    }

    @Override
    public void onBinaryMessage(WebSocket websocket, byte[] binary) throws Exception {

    }

    @Override
    public void onSendingFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {

    }

    @Override
    public void onFrameSent(WebSocket websocket, WebSocketFrame frame) throws Exception {

    }

    @Override
    public void onFrameUnsent(WebSocket websocket, WebSocketFrame frame) throws Exception {

    }

    @Override
    public void onThreadCreated(WebSocket websocket, ThreadType threadType, Thread thread) throws Exception {

    }

    @Override
    public void onThreadStarted(WebSocket websocket, ThreadType threadType, Thread thread) throws Exception {

    }

    @Override
    public void onThreadStopping(WebSocket websocket, ThreadType threadType, Thread thread) throws Exception {

    }

    @Override
    public void onError(WebSocket websocket, WebSocketException cause) throws Exception {

    }

    @Override
    public void onFrameError(WebSocket websocket, WebSocketException cause, WebSocketFrame frame) throws Exception {

    }

    @Override
    public void onMessageError(WebSocket websocket, WebSocketException cause, List<WebSocketFrame> frames) throws Exception {

    }

    @Override
    public void onMessageDecompressionError(WebSocket websocket, WebSocketException cause, byte[] compressed) throws Exception {

    }

    @Override
    public void onTextMessageError(WebSocket websocket, WebSocketException cause, byte[] data) throws Exception {

    }

    @Override
    public void onSendError(WebSocket websocket, WebSocketException cause, WebSocketFrame frame) throws Exception {

    }

    @Override
    public void onUnexpectedError(WebSocket websocket, WebSocketException cause) throws Exception {

    }

    @Override
    public void handleCallbackError(WebSocket websocket, Throwable cause) throws Exception {

    }

    @Override
    public void onSendingHandshake(WebSocket websocket, String requestLine, List<String[]> headers) throws Exception {

    }

    @Override
    public void onSocketMessage(String message) {

    }
}


