package com.indigo24.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.devlomi.record_view.RecordButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.indigo24.ClientWebSocket;
import com.indigo24.MainApp;
import com.indigo24.R;
import com.indigo24.SingleTon;
import com.indigo24.SocketListener;
import com.indigo24.adapters.AdapterListChat;
import com.indigo24.adapters.AdapterListDialog;
import com.indigo24.fragments.AddFriendFR;
import com.indigo24.fragments.dialogUser;
import com.indigo24.fragments.profileEditFR;
import com.indigo24.objects.object;
import com.neovisionaries.ws.client.ThreadType;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFrame;
import com.neovisionaries.ws.client.WebSocketState;
import com.vanniktech.emoji.EmojiEditText;

import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketListener;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.exceptions.InvalidDataException;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.handshake.Handshakedata;
import org.java_websocket.handshake.ServerHandshake;
import org.java_websocket.handshake.ServerHandshakeBuilder;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;



public  class Chat extends AppCompatActivity  implements ClientWebSocket.MessageListener  {
    @BindView(R.id.fabAdd)
    FloatingActionButton fabAdd;
    @BindView(R.id.list)
    ListView list;
    @BindView(R.id.container)
    FrameLayout container;
     Toolbar toolbar;
    public WebSocketClient mWebSocketClient;
    WebSocketListener listener;
    AdapterListChat adapter;
    WebSocketListener webSocketListener = null;
    String userID,unique;
    boolean b = false;
    ArrayList<object> arrList = new ArrayList<>();
    ClientWebSocket.MessageListener  listeners;
    SharedPreferences sPref;
    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        sPref = getSharedPreferences("UserData",MODE_PRIVATE);
       // dialogUser.dialog= 0;
        SharedPreferences sPref = getSharedPreferences("UserData",MODE_PRIVATE);
        userID  = sPref.getString("id","");
        unique  = sPref.getString("unique","");
        Log.e("UN",unique);

        //connectWebSocket();

//        JSONObject sendInit = new JSONObject();
//        try {
//            sendInit.put("cmd", "chatList");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }


//        MainApp.mClientWebSocket = new ClientWebSocket(this::onSocketMessage,sendInit.toString());
//


//            MainApp.mClientWebSocket.getConnection().sendText(sendInit.toString());
//        MainApp.mClientWebSocket.getConnection().getSocket().add

        connectWebSocket();


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
                if(arrList.get(position).getType().equals("2"))
                    bundle.putString("groupName", arrList.get(position).getName());

                mWebSocketClient.close();
                Fragment fragment = new dialogUser();
                fragment.setArguments(bundle);
                FragmentManager fm = getSupportFragmentManager();
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
                    try {
                        openFragment();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }else {
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
        super.onBackPressed();
        if(dialogUser.dialog == 1) {
            startActivity(new Intent(Chat.this, Chat.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
            finish();
            dialogUser.dialog=0;
        }
        else {
        startActivity(new Intent(Chat.this, MainActivity.class));
        finish(); }
    }
    @SuppressLint("RestrictedApi")
    void openFragment(){
        list.setVisibility(View.GONE);
        fabAdd.setVisibility(View.GONE);
        container.setVisibility(View.VISIBLE);
        Fragment fragment = new AddFriendFR();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.container, fragment);
//        transaction.addToBackStack(null);
        transaction.commit();
    }

    private boolean checkWriteExternalPermission() {
        String permission = Manifest.permission.READ_CONTACTS;
        int res = checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }


    public void connectWebSocket() {
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
                    sendInit.put("userID", Integer.parseInt(userID));
                    sendInit.put("token", unique);
                    mWebSocketClient.send(sendInit.toString());
                } catch (JSONException | NullPointerException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onMessage(String s) {

                final String message = s;
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            JSONObject js = new JSONObject(message);
                            Log.e("ERA1",message);
                            JSONObject sendInit = new JSONObject();
                            if(js.has("cmd") && js.getString("cmd").equals("init")){
                                sendInit.put("cmd", "chatList");
                                mWebSocketClient.send(sendInit.toString());
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
//                try {
//                    dialogUser fragmentedChat = (dialogUser) getSupportFragmentManager().getFragments();
//                    fragmentedChat.getMessages(s);
//                }catch (  Exception e){
//                    e.printStackTrace();
//                }
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                Log.i("Websocket", "Closed " + s);
            }

            @Override
            public void onError(Exception e) {
                Log.i("Websocket", "Error " + e.getMessage());
            }
        };
        mWebSocketClient.connect();
    }

    public String  publicMessage(String message) {
        return message;
    }

    public void sendMessage(View view) {
        EditText editText = (EditText)findViewById(R.id.message);
        mWebSocketClient.send(editText.getText().toString());
        editText.setText("");
    }

    @Override
    public void onSocketMessage(String message) {
        Log.e("MESSAGE77", "ERA + "+message );
    }


    public final class EchoWebSocketListener implements WebSocketListener {

        private static final int NORMAL_CLOSURE_STATUS = 1000;

        @Override
        public ServerHandshakeBuilder onWebsocketHandshakeReceivedAsServer(WebSocket conn, Draft draft, ClientHandshake request) throws InvalidDataException {
            return null;
        }

        @Override
        public void onWebsocketHandshakeReceivedAsClient(WebSocket conn, ClientHandshake request, ServerHandshake response) throws InvalidDataException {


        }

        @Override
        public void onWebsocketHandshakeSentAsClient(WebSocket conn, ClientHandshake request) throws InvalidDataException {

        }

        @Override
        public void onWebsocketMessage(WebSocket conn, String message) {
            Log.e("ERA16",message+" !>>>>>>>>>>>>>>>");

        }

        @Override
        public void onWebsocketMessage(WebSocket conn, ByteBuffer blob) {

        }

        @Override
        public void onWebsocketOpen(WebSocket conn, Handshakedata d) {

        }

        @Override
        public void onWebsocketClose(WebSocket ws, int code, String reason, boolean remote) {

        }

        @Override
        public void onWebsocketClosing(WebSocket ws, int code, String reason, boolean remote) {

        }

        @Override
        public void onWebsocketCloseInitiated(WebSocket ws, int code, String reason) {

        }

        @Override
        public void onWebsocketError(WebSocket conn, Exception ex) {

        }

        @Override
        public void onWebsocketPing(WebSocket conn, Framedata f) {

        }

        @Override
        public void onWebsocketPong(WebSocket conn, Framedata f) {

        }

        @Override
        public void onWriteDemand(WebSocket conn) {

        }

        @Override
        public InetSocketAddress getLocalSocketAddress(WebSocket conn) {
            return null;
        }

        @Override
        public InetSocketAddress getRemoteSocketAddress(WebSocket conn) {
            return null;
        }
    }


}


