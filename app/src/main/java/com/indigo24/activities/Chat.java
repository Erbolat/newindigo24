package com.indigo24.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
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
import com.indigo24.R;
import com.indigo24.adapters.AdapterListChat;
import com.indigo24.adapters.AdapterListDialog;
import com.indigo24.fragments.AddFriendFR;
import com.indigo24.fragments.dialogUser;
import com.indigo24.fragments.profileEditFR;
import com.indigo24.objects.object;
import com.vanniktech.emoji.EmojiEditText;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class Chat extends AppCompatActivity {
    @BindView(R.id.fabAdd)
    FloatingActionButton fabAdd;
    @BindView(R.id.list)
    ListView list;
    @BindView(R.id.container)
    FrameLayout container;
     Toolbar toolbar;
    public WebSocketClient mWebSocketClient;
    AdapterListChat adapter;
    String userID;
    boolean b = false;
    ArrayList<object> arrList = new ArrayList<>();
    SharedPreferences sPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        sPref = getSharedPreferences("UserData",MODE_PRIVATE);
        userID  = sPref.getString("id","");
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

    @Override
    public void onBackPressed() {
        startActivity(new Intent(Chat.this, MainActivity.class));
        finish();
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
        transaction.addToBackStack(null);
        transaction.commit();
    }

    private boolean checkWriteExternalPermission() {
        String permission = Manifest.permission.READ_CONTACTS;
        int res = checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }


    private void connectWebSocket() {
        URI uri;
        try {
            uri = new URI("ws://indigo24.site:33080/");
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
                    sendInit.put("userID", 30220);
                    sendInit.put("token", "4a84890cad83d81facb7eb075bbe74d50307bdce");
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
                                }}}
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


}


