package com.indigo24.fragments;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.indigo24.ClientWebSocket;
import com.indigo24.R;
import com.indigo24.activities.Auth;
import com.indigo24.activities.Chat;
import com.indigo24.activities.GifActivity;
import com.indigo24.activities.MainActivity;
import com.indigo24.adapters.AdapterMyPays;
import com.indigo24.adapters.AdapterOfMembers;
import com.indigo24.adapters.ViewPageAdapterPays;
import com.indigo24.objects.User;
import com.indigo24.objects.myPay;
import com.indigo24.requests.Interface;
import com.neovisionaries.ws.client.ThreadType;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFrame;
import com.neovisionaries.ws.client.WebSocketState;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

import static com.indigo24.requests.Interface.TAG;


public class cabinetFR extends Fragment implements ClientWebSocket.MessageListener {
    ArrayList<User>arrListUsers;
    JSONObject sendInit;
    JSONArray jsArrayMembers = null;
    AdapterOfMembers adapter;
    SharedPreferences sPref;
    ClientWebSocket clientWebSocket;

    @BindView(R.id.tvName) TextView tvName;
    @BindView(R.id.fabAdd)
    FloatingActionButton fabAdd;
    @BindView(R.id.imgAvatar) ImageView imgAvatar;
    @BindView(R.id.imgExit) ImageView imgExit;
    @BindView(R.id.list) ListView list;
    static int posit = 0 , toID = 0 , isAdmin = 0, countAdmins=0,isAdminNotIam = 0, isRootAdmin = 0;
    int userID;
    String unique ,type, cabinetID, name,avatar, myID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_cabinet, container, false);
        ButterKnife.bind(this, v);
        dialogUser.step = 2;
        sPref = getActivity().getSharedPreferences("UserData",getActivity().MODE_PRIVATE);
        userID  = Integer.parseInt(sPref.getString("id",""));
        unique  = sPref.getString("unique","");

        Bundle args = getArguments();
        if(args!=null){
            type = args.getString("type");
            cabinetID = args.getString("cabinetID");
            name = args.getString("name");
            avatar = args.getString("avatar");
            tvName.setText(name);
            Picasso.get().load(Interface.baseAVATAR+  avatar).transform(new CropCircleTransformation()).into(imgAvatar);

            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    posit = position;
                    if(arrListUsers.size()>0 && !arrListUsers.get(position).getId().equals(String.valueOf(userID)))
                    getDialog(position);


//                    Bundle bundle=new Bundle();
//                    bundle.putString("cabinetID", cabinetID+"");
////                    bundle.putString("type",type);
//                    bundle.putString("name", name);
//                    bundle.putString("avatar", avatar);
//                    Fragment fragment = new dialogUser();
//                    fragment.setArguments(bundle);
//                    FragmentManager fm = getActivity().getSupportFragmentManager();
//                    FragmentTransaction transaction = fm.beginTransaction();
//                    transaction.replace(R.id.container, fragment);
//                    transaction.addToBackStack(null);
//                    transaction.commit();
                }
            });
        }


        clientWebSocket = new ClientWebSocket(this);
        if(clientWebSocket.getConnection().getSocket()!=null) {
            sendInit = new JSONObject();
            try {
                sendInit.put("cmd", "groupMembers");
                sendInit.put("cabinetID", Integer.parseInt(cabinetID));
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            clientWebSocket.getConnection().addListener(this);
            clientWebSocket.getConnection().sendText(sendInit.toString());
        }


        return v;
    }

    private void getDialog(int position) {
        final Dialog dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.dialog_cabinet);
        dialog.setTitle("Выберите...");
        TextView tvChat = (TextView) dialog.findViewById(R.id.tvChat);
        TextView tvAdmin = (TextView) dialog.findViewById(R.id.tvAdmin);
        TextView tvDelete = (TextView) dialog.findViewById(R.id.tvDelete);

        for(int i=0; i<arrListUsers.size(); i++) {
            if (arrListUsers.get(i).getId().equals(String.valueOf(userID))) {
                if (arrListUsers.get(i).getAdmin().equals("1") || arrListUsers.get(i).getAdmin().equals("2")) {
                    tvAdmin.setVisibility(View.VISIBLE);
                    tvDelete.setVisibility(View.VISIBLE);
                }
                else  {
                    tvAdmin.setVisibility(View.GONE);
                    tvDelete.setVisibility(View.GONE);
                }
            }

            if(arrListUsers.get(i).getId().equals(String.valueOf(userID)) && arrListUsers.get(i).getAdmin().equals("2"))
                isAdmin = 1;

        }

        tvChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });
        tvAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(arrListUsers.size()>0) {
                    if ( (!arrListUsers.get(position).getAdmin().equals("1") && !arrListUsers.get(position).getAdmin().equals("2"))) {
                        if (clientWebSocket.getConnection().getSocket() != null) {

                            sendInit = new JSONObject();
                            try {
                                sendInit.put("cmd", "controlCabinet");
                                sendInit.put("cabinetID", Integer.parseInt(cabinetID));
                                sendInit.put("userID", userID);
                                sendInit.put("toID", Integer.parseInt(arrListUsers.get(position).getId()));
                                sendInit.put("changeAdmin", 1);
                                toID = Integer.parseInt(arrListUsers.get(position).getId());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            clientWebSocket.getConnection().sendText(sendInit.toString());
                        }

                    }
                    adapter.notifyDataSetChanged();

                }
                dialog.dismiss();
            }
        });
        tvDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(arrListUsers.size()>0) {
                    if ( (!arrListUsers.get(position).getAdmin().equals("1") && !arrListUsers.get(position).getAdmin().equals("2"))) {
                        if (clientWebSocket.getConnection().getSocket() != null) {
                            sendInit = new JSONObject();
                            try {
                                sendInit.put("cmd", "deleteUserFromGroup");
                                sendInit.put("cabinetID", Integer.parseInt(cabinetID));
                                sendInit.put("fromID", userID);
                                sendInit.put("toID", Integer.parseInt(arrListUsers.get(position).getId()));
                                toID = Integer.parseInt(arrListUsers.get(position).getId());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            clientWebSocket.getConnection().sendText(sendInit.toString());
                            adapter.notifyDataSetChanged();
                        }

                    }

                    if(arrListUsers.get(position).getAdmin().equals("1")) {
                        if(isAdmin == 1) {
                            if (clientWebSocket.getConnection().getSocket() != null) {
                                sendInit = new JSONObject();
                                try {
                                    sendInit.put("cmd", "deleteUserFromGroup");
                                    sendInit.put("cabinetID", Integer.parseInt(cabinetID));
                                    sendInit.put("fromID", userID);
                                    sendInit.put("toID", Integer.parseInt(arrListUsers.get(position).getId()));
                                    toID = Integer.parseInt(arrListUsers.get(position).getId());
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                clientWebSocket.getConnection().sendText(sendInit.toString());
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }


                }
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    @OnClick({R.id.imgExit, R.id.fabAdd})
    void onSaveClick(View view) {
        switch (view.getId()) {
            case R.id.imgExit:
                countAdmins = 0;
                isAdmin = 0; isAdminNotIam = 0; isRootAdmin = 0;
                for(int i=0; i<arrListUsers.size(); i++) {
                    if(arrListUsers.get(i).getAdmin().equals("2"))
                        isAdminNotIam = 1;
                    if(arrListUsers.get(i).getId().equals(String.valueOf(userID)) && arrListUsers.get(i).getAdmin().equals("2"))
                        isRootAdmin = 1;
                    if(arrListUsers.get(i).getAdmin().equals("1"))
                        countAdmins++;
                    if(arrListUsers.get(i).getId().equals(String.valueOf(userID)) && arrListUsers.get(i).getAdmin().equals("1"))
                        isAdmin = 1;
                }

                new CountDownTimer(150, 100) {
                    public void onTick(long millisUntilFinished) {
                    }
                    public void onFinish() {
                       if(isRootAdmin == 1 && countAdmins == 0)
                           Toast.makeText(getContext(), "Прежде чем Выйти из группы назначьте кого-то Админом!", Toast.LENGTH_SHORT).show();
                       else if(isAdminNotIam == 0 && countAdmins == 1 && isAdmin == 1)
                           Toast.makeText(getContext(), "Прежде чем Выйти из группы назначьте кого-то Админом!", Toast.LENGTH_SHORT).show();
                       else exitFromGroup();
                    }
                }.start();
                break;
            case R.id.fabAdd:
                FragmentManager fm = ((Chat) getContext()).getSupportFragmentManager();
                Bundle b = new Bundle();
                b.putBoolean("newGroup", false);
                b.putString("cabinetID", cabinetID+"");
                Fragment fragment = new AddFriendGroupFR();
                fragment.setArguments(b);
                FragmentTransaction transaction = fm.beginTransaction();
                transaction.replace(R.id.container, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
                break;
        }
    }


    private void exitFromGroup() {
        if(clientWebSocket.getConnection().getSocket()!=null) {
            sendInit = new JSONObject();
            try {
                sendInit.put("cmd", "leaveUserFromGroup");
                sendInit.put("cabinetID", Integer.parseInt(cabinetID));
                sendInit.put("userID", userID);
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
            clientWebSocket.getConnection().sendText(sendInit.toString());

            Log.e("SENDING", sendInit.toString());
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
        JSONObject js = new JSONObject(text);
        Log.e("GROUP MEM", text);
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                try {
                    if(js.has("cmd")  && js.getString("cmd").equals("groupMembers")) {

                        try {
                            jsArrayMembers = js.getJSONArray("list");
                            if (jsArrayMembers.length() > 0) {
                                arrListUsers = new ArrayList<>();
                                for (int i = 0; i < jsArrayMembers.length(); i++) {
                                    User user = new User();
                                    user.setAvatar(jsArrayMembers.getJSONObject(i).getString("avatar"));
                                    user.setId(jsArrayMembers.getJSONObject(i).getInt("userID") + "");
            //                    user.setPhone(jsArrayMembers.getJSONObject(i).getString("phone") + "");
            //                    user.setCabinetID(jsArrayMembers.getJSONObject(i).getString("cabinetID") + "");
                                    user.setName(jsArrayMembers.getJSONObject(i).getString("name"));
                                    user.setAdmin(jsArrayMembers.getJSONObject(i).getString("admin"));
                                    arrListUsers.add(user);
                                }
                                adapter = new AdapterOfMembers(getContext(), arrListUsers);
                                list.setAdapter(adapter);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    if(js.has("cmd")  && js.getString("cmd").equals("controlCabinet") && toID == Integer.parseInt(arrListUsers.get(posit).getId())) {
                        arrListUsers.get(posit).setAdmin("1");
                        posit = 0;
                        toID = 0;
                        adapter = new AdapterOfMembers(getContext(), arrListUsers);
                        list.setAdapter(adapter);
                    }

                    if(js.has("cmd")  && js.getString("cmd").equals("leaveUserFromGroup")) {
                        getActivity().startActivity(new Intent(getActivity(), Chat.class));
                        getActivity().finish();
                    }

                    if(js.has("cmd")  && js.getString("cmd").equals("deleteUserFromGroup") && toID == Integer.parseInt(arrListUsers.get(posit).getId())) {
                        arrListUsers.remove(posit);
                        posit = 0;
                        toID = 0;
                        adapter = new AdapterOfMembers(getContext(), arrListUsers);
                        list.setAdapter(adapter);
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
