package com.indigo24.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.indigo24.ClientWebSocket;
import com.indigo24.R;
import com.indigo24.activities.Chat;
import com.indigo24.adapters.AdapterAddFriend;
import com.indigo24.objects.User;
import com.neovisionaries.ws.client.ThreadType;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFrame;
import com.neovisionaries.ws.client.WebSocketState;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class AddFriendFR extends Fragment implements ClientWebSocket.MessageListener {
    ClientWebSocket clientWebSocket;
    @BindView(R.id.list) ListView list;
    @BindView(R.id.swipe) SwipeRefreshLayout swipe;
    @BindView(R.id.tvGroup) TextView tvGroup;
    @BindView(R.id.tvNext)
    TextView tvNext;
    @BindView(R.id.editSearch)
    EditText editSearch;
    @BindView(R.id.imgBack)
    ImageView imgBack;
//    @BindView(R.id.swipe) SwipeRefreshLayout swipe;
    ArrayList<User> arrContacts;
    ArrayList<User> arrContactsSelected;
    ArrayList<User> arrContactsSearched;
    static String name, avatar;
    AdapterAddFriend adapter;
    boolean isSearch = false;
    String phone;
    JSONArray contscts;
    static int posit = 0;
    static FragmentManager fm;
    static int type = 1;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_friend, container, false);
        ButterKnife.bind(this,v);
        swipe.setRefreshing(true);
        fm = ((Chat) getContext()).getSupportFragmentManager();
        adapter = new AdapterAddFriend(getContext(), arrContacts,0);
        list.setAdapter(adapter);

        fillData();
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipe.setRefreshing(false);
            }
        });

        clientWebSocket = new ClientWebSocket(this);
        if(clientWebSocket.getConnection().getSocket()!=null)
            clientWebSocket.getConnection().addListener(this);
//
//        list.addOnItemTouchListener( new RecyclerItemClickListener(getContext(), rvList ,new RecyclerItemClickListener.OnItemClickListener() {
//            @Override public void onItemClick(View view, int position) {
//                JSONObject sendInit = new JSONObject();
//                String strPhone = arrContacts.get(position).getPhone();
//                StringBuffer sb = new StringBuffer(strPhone);
//                if(strPhone.charAt(0) == '8')    sb.setCharAt(0, '7');
//                phone = sb.toString();
//                phone = phone.replaceAll("[()\\s-]+", "");
//                phone = phone.replace("-","").replace("+","").replace(" ","").replace("  ","");
//
//                try {
//                    sendInit.put("cmd", "checkPhone");
//                    sendInit.put("phone",phone);
//                    if(clientWebSocket.getConnection().getSocket()!=null) {
//                        clientWebSocket.getConnection().sendText(sendInit.toString());
//                    }
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//
//            @Override public void onLongItemClick(View view, int position) {
//                    }
//        })
//        );

list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        String strPhone = "";


        posit = position;
        if(isSearch) {
         strPhone = arrContactsSearched.get(position).getPhone();
            if( !arrContactsSearched.get(position).getStatus().equals("-2")) {
                arrContactsSearched.get(position).setStatus("-2");
                adapter.notifyDataSetChanged();
            }
          else   checkPhone(strPhone);
        }
        else   {
            strPhone = arrContacts.get(position).getPhone();
            if( !arrContacts.get(position).getStatus().equals("-2")) {
            arrContacts.get(position).setStatus("-2");
            adapter.notifyDataSetChanged();}
            else checkPhone(strPhone);
        }




    }
});



editSearch.addTextChangedListener(new TextWatcher() {
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if(!editSearch.getText().toString().isEmpty()) {
            isSearch = true;
            arrContactsSearched = new ArrayList<>();
            adapter = new AdapterAddFriend(getContext(), arrContactsSearched,0);
            list.setAdapter(adapter);
            for(int i=0; i<arrContacts.size(); i++){
                try {
                    String ph = (String) arrContacts.get(i).getPhone().toLowerCase();
                    String nm = (String) arrContacts.get(i).getName().toLowerCase();
                    if (ph.contains(editSearch.getText().toString().toLowerCase()) || nm.contains(editSearch.getText().toString().toLowerCase())) {
                        Log.e("ERA01","era");
                        arrContactsSearched.add(arrContacts.get(i));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                adapter = new AdapterAddFriend(getContext(), arrContactsSearched,0);
                list.setAdapter(adapter);


//                if(editSearch.getText().toString().contains(arrContacts.get(i).getName()) || editSearch.getText().toString().contains(arrContacts.get(i).getPhone()))
//                    arrContactsSearched.add(arrContacts.get(i));

            }
        }
        else {
            isSearch = false;

            for(int i=0;i<arrContacts.size(); i++){
                if(arrContacts.get(i).getName().equals(arrContactsSearched.get(posit).getName()) && arrContacts.get(i).getPhone().equals(arrContactsSearched.get(posit).getPhone())
                && !arrContacts.get(i).getStatus().equals("-2")) {
                    arrContacts.get(i).setStatus("-2");
                } }
            adapter = new AdapterAddFriend(getContext(), arrContacts,0);
            list.setAdapter(adapter);
        }
    }
});


        return v;
    }


    @OnClick({R.id.tvGroup, R.id.tvNext, R.id.imgBack})
    void onSaveClick(View view) {
        switch (view.getId()) {
            case R.id.tvGroup:
                Bundle b = new Bundle();
                b.putBoolean("newGroup", true);
                Fragment fragment = new AddFriendGroupFR();
                fragment.setArguments(b);
                FragmentTransaction transaction = fm.beginTransaction();
                transaction.replace(R.id.container, fragment);
                transaction.addToBackStack(null);
                transaction.commit();
                break;
            case R.id.imgBack:
                getActivity().startActivity(new Intent(getActivity(), Chat.class).addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION));
                Objects.requireNonNull(getActivity()).finish();
                break;
            case R.id.tvNext:
                int count = 0;
                arrContactsSelected = new ArrayList<>();

                if(arrContacts.size()>0) {
                    for (int i=0; i<arrContacts.size(); i++)
                    {
                        if(arrContacts.get(i).getStatus().equals("1")) {
                            count++;
                            arrContactsSelected.add(arrContacts.get(i));
                        }

                    }
                    if(count==0) Toast.makeText(getContext(), "Выберите хотя бы один контактный номер, который зарегистрирован в системе Indigo24", Toast.LENGTH_LONG).show();

                }


                break;

        }
    }


    private void checkPhone(String strPhone) {
        JSONObject sendInit = new JSONObject();
        StringBuffer sb = new StringBuffer(strPhone);
        if(strPhone.charAt(0) == '8')    sb.setCharAt(0, '7');
        phone = sb.toString();
        phone = phone.replaceAll("[()\\s-]+", "");
        phone = phone.replace("-","").replace("+","").replace(" ","").replace("  ","");


        try {
            sendInit.put("cmd", "checkPhone");
            sendInit.put("phone",phone);
            if(clientWebSocket.getConnection().getSocket()!=null) {
                clientWebSocket.getConnection().sendText(sendInit.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    void fillData() {

        arrContacts = new ArrayList<>();
         contscts = new JSONArray();
        Cursor phones = getContext().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);
        while (phones.moveToNext()) {

            String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            String ids = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID));

            User user = new User();
            user.setName(name);
            user.setPhone(phoneNumber);
            user.setStatus("-2");
            arrContacts.add(user);
            adapter = new AdapterAddFriend(getContext(), arrContacts,0);
            list.setAdapter(adapter);
            phoneNumber = phoneNumber.replaceAll("[()\\s-]+", "");

            // Enter Into Hash Map
            JSONObject cnt = new JSONObject();
            try {
                cnt.put("name", name);
                cnt.put("phone", phoneNumber);
            } catch (JSONException | NullPointerException e) {
                e.printStackTrace();
            }
            contscts.put(cnt);
            swipe.setRefreshing(false);
        }

        phones.close();
        swipe.setRefreshing(false);

    }


    @Override
    public void onSocketMessage(String message) {
    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return super.onOptionsItemSelected(item);

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
        Log.e("FFF Friend", text);
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
             JSONObject js = null;
            try {
            js = new JSONObject(text);
                String cabinetID;
                if(type==1 && js.has("cabinetID") && (js.has("cmd") && js.getString("cmd").equals("createCabinet")) || (js.has("cmd") && js.getString("cmd").equals("private"))) {
//                    if(js.has("cabinetID"))
                    cabinetID = js.getString("cabinetID");

                            Bundle bundle=new Bundle();
                            bundle.putString("cabinetID", cabinetID);
                            bundle.putString("type", "1");
                            bundle.putString("name", name);
                            bundle.putString("avatar", avatar);
                            Fragment fragment = new dialogUser();
                            fragment.setArguments(bundle);
                            FragmentTransaction transaction = fm.beginTransaction();
                            transaction.replace(R.id.container, fragment);
                            transaction.commit();


                }

            if(js.has("userID") && js.has("phone") && js.getString("phone").equals(phone)) {
                if(js.has("avatar")) {

                        name = js.getString("name");
                        avatar = js.getString("avatar");
                        JSONArray mJSONArray = new JSONArray();
                        mJSONArray.put(js.getInt("userID"));


                        JSONObject sendInit = new JSONObject();
                        sendInit.put("cmd","createCabinet");
                        sendInit.put("type",1);
                        sendInit.put("members", mJSONArray);
                        clientWebSocket.getConnection().sendText(sendInit.toString());
                        Log.e("ER99f", sendInit.toString());


                }
                else
                    Toast.makeText(Objects.requireNonNull(getActivity()).getApplicationContext(), "Извините, данный номер "+phone+" отсувствует в системе Indigo24", Toast.LENGTH_LONG).show();}

        } catch (JSONException e) {
            e.printStackTrace();
        } }
        }
        );

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

