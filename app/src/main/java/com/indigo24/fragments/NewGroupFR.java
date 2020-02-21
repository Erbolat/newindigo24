package com.indigo24.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.indigo24.ClientWebSocket;
import com.indigo24.R;
import com.indigo24.activities.Chat;
import com.indigo24.adapters.AdapterAddFriend;
import com.indigo24.adapters.AdapterAddFriendSelected;
import com.indigo24.objects.User;
import com.indigo24.requests.Interface;
import com.neovisionaries.ws.client.ThreadType;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFrame;
import com.neovisionaries.ws.client.WebSocketState;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class NewGroupFR extends Fragment   implements ClientWebSocket.MessageListener{

    @BindView(R.id.editGroupName)
    EditText editGroupName;
    @BindView(R.id.imgBack)
    ImageView imgBack;
    @BindView(R.id.list)
    ListView list;
    ClientWebSocket clientWebSocket;
    SharedPreferences sPref;
    ArrayList<User> arrContacts;
    String name="";
    AdapterAddFriendSelected adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_new_group, container, false);

        ButterKnife.bind(this,v);
        sPref = getContext().getSharedPreferences("UserData", getContext().MODE_PRIVATE);


        arrContacts = AddFriendGroupFR.arrContactsSelected;
        AddFriendGroupFR.arrContactsSelected = null;
        clientWebSocket = new ClientWebSocket(this);
        if(clientWebSocket.getConnection().getSocket()!=null)
            clientWebSocket.getConnection().addListener(this);


        adapter = new AdapterAddFriendSelected(getContext(), arrContacts);
        list.setAdapter(adapter);


        return v;
    }

    @OnClick({R.id.tvCreate, R.id.imgAva, R.id.imgBack})
    void onSaveClick(View view) {
        switch (view.getId()) {
            case R.id.tvCreate:
                name = editGroupName.getText().toString();
                if (!name.isEmpty() && arrContacts.size()>0) save();
                else
                    Toast.makeText(getContext(), "Название группы не может быть пустым или для созданиии группы нужен хотя бы 1 человек!", Toast.LENGTH_LONG).show();
                break;
            case  R.id.imgBack:
                getActivity().startActivity(new Intent(getActivity(), Chat.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                Objects.requireNonNull(getActivity()).finish();
                break;

        }
    }

    private void save() {
        JSONObject sendInit = new JSONObject();
        String arrayIDS = null;
        JSONArray mJSONArray = new JSONArray();
        Integer[] ids = new Integer[arrContacts.size()];
        try {
            sendInit.put("cmd", "createCabinet");
            sendInit.put("type", 2);  //группа
            sendInit.put("name", editGroupName.getText().toString());
        for(int i=0; i<arrContacts.size();i++) {
            ids[i] = Integer.valueOf(arrContacts.get(i).getId());
            mJSONArray.put(ids[i]);
        }



        sendInit.put("members", mJSONArray);
            if(clientWebSocket.getConnection().getSocket()!=null) {
                clientWebSocket.getConnection().sendText(sendInit.toString());
                Log.e("SEND",sendInit.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onSocketMessage(String message) {

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
        Log.e("ERA99", text);
        JSONObject js = new JSONObject(text);
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                try {
                    if(js.has("cabinetID") || (js.has("cmd") && js.getString("cmd").equals("createCabinet"))){
                        Objects.requireNonNull(getActivity()).startActivity(new Intent(getContext(), Chat.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                        getActivity().finish();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }});

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
}

