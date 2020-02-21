package com.indigo24.fragments;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.devlomi.record_view.OnRecordClickListener;
import com.devlomi.record_view.OnRecordListener;
import com.devlomi.record_view.RecordButton;
import com.devlomi.record_view.RecordView;
import com.indigo24.ClientWebSocket;
import com.indigo24.R;
import com.indigo24.activities.Chat;
import com.indigo24.adapters.AdapterListDialog;
import com.indigo24.objects.User;
import com.indigo24.objects.Vibration;
import com.indigo24.objects.object;
import com.indigo24.requests.Interface;
import com.neovisionaries.ws.client.ThreadType;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFrame;
import com.neovisionaries.ws.client.WebSocketState;
import com.squareup.picasso.Picasso;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiPopup;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import jp.wasabeef.picasso.transformations.CropCircleTransformation;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import pl.droidsonroids.gif.GifImageView;


import static com.indigo24.requests.Interface.TAG;


public class dialogUser extends Fragment implements ClientWebSocket.MessageListener {
    ClientWebSocket clientWebSocket;
    ViewGroup rootView;
    EmojiPopup emojiPopup;
    JSONObject sendInit = null;

    ArrayList<object> arrDialog = new ArrayList<>();

    Handler mSeekbarUpdateHandler;
    Runnable mUpdateSeekbar;
    SeekBar seekbar_audio;
    AdapterListDialog adapter;
    @BindView(R.id.messageInput) public EmojiEditText editMessage;
    @BindView(R.id.swipe) SwipeRefreshLayout swipe;
    @BindView(R.id.record_button)
    RecordButton mRecordButton;
    @BindView(R.id.record_view)
    RecordView  mRecordView;
    @BindView(R.id.gifDwn) GifImageView gifDwn;
    @BindView(R.id.btnSendAudioFile) ImageButton btnSendAudioFile;
    @BindView(R.id.tvName) TextView tvName;
    @BindView(R.id.imgAvatar) ImageView imgAvatar;
//    @BindView(R.id.record_button) RecordButton btnRecord;
    @BindView(R.id.btnSend) ImageButton btnSend;
    @BindView(R.id.btnSmile) ImageButton btnSmile;
    @BindView(R.id.gifTyping) GifImageView gifTyping;
    @BindView(R.id.list) ListView list;
    @BindView(R.id.llCabinet) LinearLayout llCabinet;
    @BindView(R.id.imgBack) ImageView imgBack;


    private static final String AUDIO_RECORDER_FILE_EXT_3GP = ".3gp";
    private static final String AUDIO_RECORDER_FILE_EXT_MP4 = ".mp4";
    private static final String AUDIO_RECORDER_FOLDER = "AudioRecorder";
    private MediaRecorder recorder = null;
    private int currentFormat = 0;
    private int output_formats[] = { MediaRecorder.OutputFormat.MPEG_4,             MediaRecorder.OutputFormat.THREE_GPP };
    private String file_exts[] = { AUDIO_RECORDER_FILE_EXT_MP4, AUDIO_RECORDER_FILE_EXT_3GP };
    static  int start = 0;
    boolean get= false , teg = false, onTick = false;
    static boolean isWrite = false, isAudio = false;
    public static int step = 1;
    static String audioFileName = "";
    int page = 0, userID = 0;
    static int newPage=0;
    String fileName;
    SharedPreferences sPref;
    String type = "", cabinetID="", messageEdit="",unique, id = "",userAVA, userNAME;
    String myName = "", myAva = "";
    static  int msgType = 15;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_dialog_user, container, false);
        rootView = v.findViewById(R.id.activityRoot);
        ButterKnife.bind(this, v);
        newPage=0;  step = 1;
        emojiPopup = EmojiPopup.Builder.fromRootView(rootView).build(editMessage);
        SharedPreferences sPref = getActivity().getSharedPreferences("UserData",getActivity().MODE_PRIVATE);
        userID  = Integer.parseInt(sPref.getString("id",""));
        unique  = sPref.getString("unique","");
        myAva  = sPref.getString("avatar","");
        myName  = sPref.getString("name","");
        Bundle args = getArguments();
        sendInit = new JSONObject();
        if (args != null) {
            type = args.getString("type");
            cabinetID = args.getString("cabinetID");
            userAVA = args.getString("avatar");
            userNAME = args.getString("name");
            tvName.setText(userNAME);
            Picasso.get().load(Interface.baseAVATAR+  userAVA).transform(new CropCircleTransformation()).into(imgAvatar);
        }
        page = 0;
        clientWebSocket = new ClientWebSocket(this);
        if(clientWebSocket.getConnection().getSocket()!=null) {
                try {
                    if(type.equals("1"))
                    sendInit.put("cmd", "private");
                    else     sendInit.put("cmd", "group");
                    sendInit.put("cabinetID", Integer.parseInt(cabinetID));
                    sendInit.put("page", newPage);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            if(clientWebSocket.getConnection().getSocket()!=null) {
                clientWebSocket.getConnection().addListener(this);
                clientWebSocket.getConnection().sendText(sendInit.toString());
                arrDialog = new ArrayList<>();
        }
        }


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
                         if(type.equals("1"))  sendInit.put("cmd", "private");
                         else    sendInit.put("cmd", "group");
                         sendInit.put("cabinetID", Integer.parseInt(cabinetID));
                         sendInit.put("page", newPage);
                         clientWebSocket.getConnection().sendText(sendInit.toString());
                         teg=true;
                         swipe.setRefreshing(false);
                        } catch (JSONException | NullPointerException e) {
                            e.printStackTrace();
                        }
                }
            }});


        mRecordButton.setRecordView(mRecordView);

        mRecordView.setOnRecordListener(new OnRecordListener() {
                                            @Override
                                            public void onStart() {
                                                Log.e("WW","1");
                                                isAudio= false;
                                                btnSmile.setVisibility(View.VISIBLE);
                                                btnSend.setVisibility(View.GONE);
                                                //cameraButton.setVisibility(View.GONE);
                                                //buttonSelectTypeMessageAdd.setVisibility(View.GONE);
                                                Vibration vibr = new Vibration();
                                                vibr.vibro(getContext(), 50);
                                                fileName = getActivity().getExternalCacheDir().getAbsolutePath();
                                                fileName += "/" + System.currentTimeMillis() + ".aac";
                                                recorder = new MediaRecorder();
                                                recorder.setAudioSource(MediaRecorder.AudioSource.CAMCORDER);
                                                recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                                                recorder.setOutputFile(fileName);
                                                recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                                                recorder.setAudioChannels(1);
                                                recorder.setAudioSamplingRate(44100);
                                                recorder.setAudioEncodingBitRate(192000);
                                                try {
                                                    recorder.prepare();
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                                recorder.start();
                                            }

                                            @Override
                                            public void onCancel() {

                                                if(recorder != null){
                                                    recorder.stop();
                                                  }
                                                btnSend.setVisibility(View.GONE);
                                                mRecordButton.setVisibility(View.VISIBLE);
//                                                startRecord = false;
                                                btnSmile.setVisibility(View.VISIBLE);
                                                //cameraButton.setVisibility(View.VISIBLE);
//                                                buttonSelectTypeMessageAdd.setVisibility(View.VISIBLE);
                                                editMessage.setHint("Сообщение");
                                            }

                                            @Override
                                            public void onFinish(long recordTime) {
                                                Log.e("WW","3");
                                                isAudio = true;
                                                btnSend.setVisibility(View.VISIBLE);

//                                                btnSend.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.play));
                                                mRecordButton.setVisibility(View.GONE);
                                                if(recorder != null){
                                                    recorder.stop();
                                                    recorder.reset();
                                                    recorder.release();}
                                            }

                                            @Override
                                            public void onLessThanSecond() {

                                            }
                                        });


                mRecordView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {

                        // startRecord = true;
                        return true;
                    }
                });


        editMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(!editMessage.getText().toString().isEmpty())  isWrite = true;
                else isWrite=false;
                if(clientWebSocket.getConnection().getSocket()!=null && !editMessage.getText().toString().isEmpty() && isWrite && !onTick){
                    new CountDownTimer(6000, 1000) {
                        public void onTick(long millisUntilFinished) {
                            if(millisUntilFinished / 1000 == 5 ) {
                                onTick = true;
                                sendInit = new JSONObject();
                                try {
                                    sendInit.put("cmd", "sendStatus");
                                    sendInit.put("cabinetID", Integer.parseInt(cabinetID));
                                    sendInit.put("status", 1);
                                    clientWebSocket.getConnection().sendText(sendInit.toString());
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
                if(!editMessage.getText().toString().isEmpty()) {
                btnSend.setVisibility(View.VISIBLE);
                isAudio= false;
                mRecordButton.setVisibility(View.GONE); }
                else {
                    btnSend.setVisibility(View.GONE);
                    //btnSend.setImageDrawable(ContextCompat.getDrawable((Chat)getActivity(), R.drawable.ic_paper_plane));
                    isAudio= true;
                    mRecordButton.setVisibility(View.VISIBLE);
                }
            }

        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @OnClick({ R.id.btnSend, R.id.btnSmile, R.id.imgBack, R.id.llCabinet, R.id.btnSendAudioFile})
    void onSaveClick(View view) {
        switch (view.getId()) {

            case R.id.btnSendAudioFile:



                break;
            case R.id.btnSend:

                if(isAudio) {
                    Uri fil = Uri.parse(fileName);
                    File f = new File(fil.getPath());
                    msgType = 1;
                    if(f.exists()) {
                        String mimeType = URLConnection.guessContentTypeFromName(f.getName());


                        MediaType MEDIA_TYPE = MediaType.get(mimeType);
                        RequestBody requestBody = new MultipartBody.Builder()
                                .setType(MultipartBody.FORM)

                                .addFormDataPart("customerID", String.valueOf(userID))
                                .addFormDataPart("unique", unique)
                                .addFormDataPart("file", f.getName(), RequestBody.create(MEDIA_TYPE, f))
                                .build();



                        Request request = new Request.Builder()
                                .addHeader("Content-Type","application/x-www-form-urlencoded")
                                .url("https://api.indigo24.xyz/api/v2.1/upload")
                                .post(requestBody)
                                .build();
                        OkHttpClient client = new OkHttpClient();
                        client.newCall(request).enqueue(new Callback() {

                            @Override
                            public void onFailure(Call call, IOException e) {
                                e.printStackTrace();
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
//                                    sendAudioProgress.setVisibility(View.GONE);
                                        Toast.makeText(getContext(), "Не удалось загрузить файл!" + e.toString(), Toast.LENGTH_LONG).show();
                                    }
                                });
                            }

                            @Override
                            public void onResponse(Call call, final Response response) throws IOException {
//                            if (response.isSuccessful()) {
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {


//                                        progressLoadImage.setVisibility(View.GONE);
//                                        audioPlayerViwe.setVisibility(View.GONE);
//                                        linearLayout3.setVisibility(View.VISIBLE);
                                        try {
                                            JSONObject resp = new JSONObject(response.body().string());
                                            Log.e("EEE_RES", resp.toString());
                                            if (resp.getBoolean("success")) {
                                                audioFileName = resp.getString("fileName");
                                                JSONObject sended = new JSONObject();
                                                sended.put("cmd", "sendMessage");
                                                sended.put("message", resp.getString("fileName"));
                                                sended.put("cabinetID", Integer.valueOf(cabinetID));
                                                sended.put("type", Integer.valueOf(type));
                                                sended.put("messageType", 1);
                                                clientWebSocket.getConnection().sendText(sended.toString());

//
//                                                            else {
//                                                                Toast.makeText(getContext(), "Ошибка!", Toast.LENGTH_SHORT).show();
//                                                            }

                                            } else {
                                                try {
                                                    Toast.makeText(getContext(), resp.getString("message"), Toast.LENGTH_SHORT).show();
                                                } catch (JSONException | NullPointerException e) {
                                                    Toast.makeText(getContext(), "Не удалось загрузить файл!", Toast.LENGTH_LONG).show();
                                                }

                                            }
                                        } catch (JSONException | NullPointerException e) {
                                            e.printStackTrace();
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
//                            } else {
//                                JSONObject resp = null;
//                                try {
//                                    resp = new JSONObject(response.body().string());
//                                    try {
//                                        Toast.makeText(getContext(), resp.getString("message"), Toast.LENGTH_SHORT).show();
//                                    } catch (JSONException | NullPointerException e) {
//                                        Toast.makeText(getContext(), "Не удалось загрузить файл!", Toast.LENGTH_LONG).show();
//                                    }
//                                } catch (JSONException | NullPointerException e) {
//                                    Toast.makeText(getContext(), "Не удалось загрузить файл!", Toast.LENGTH_LONG).show();
//                                    e.printStackTrace();
//                                }
//
//                            }
                            }
                        });}

                }

                    else {






                if(!editMessage.getText().toString().isEmpty()) {
                     messageEdit = editMessage.getText().toString();
                     msgType = 15;
//                    btnSend.setVisibility(View.GONE);
//                    gifDwn.setVisibility(View.VISIBLE);
//
//                    CountDownTimer myCountDown = new CountDownTimer(450, 400) {
//                        public void onTick(long millisUntilFinished) {
//                        }
//
//                        public void onFinish() {
//
//                        }
//                    };
//                    myCountDown.start();

                    if(clientWebSocket.getConnection().getSocket()!=null){
                        sendInit = new JSONObject();
                        try {
                            sendInit.put("cmd", "sendMessage");
                            sendInit.put("message", messageEdit);
                            sendInit.put("messageType", 15);
                            sendInit.put("cabinetID", Integer.parseInt(cabinetID));
                            sendInit.put("type", Integer.parseInt(type));
                            clientWebSocket.getConnection().sendText(sendInit.toString());
                            gifDwn.setVisibility(View.GONE);

                        } catch (JSONException | NullPointerException e) {
                            e.printStackTrace();
                        }}

                list.clearFocus();
                list.post(new Runnable() {
                    @Override
                    public void run() {
                        list.setSelection(list.getCount());
                    }
                }); } }
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
                case R.id.llCabinet:
                    Bundle bundle=new Bundle();
                    bundle.putString("cabinetID", cabinetID+"");
                    bundle.putString("type", type);
                    bundle.putString("name",userNAME);
                    bundle.putString("avatar",userAVA);
                    if(type.equals("2")) {
                    Fragment fragment = new cabinetFR();
                    fragment.setArguments(bundle);
                    FragmentManager fm =  ((Chat) getContext()).getSupportFragmentManager();
                    FragmentTransaction transaction = fm.beginTransaction();
                    transaction.replace(R.id.container, fragment);
                    transaction.commit();
                    }
                    else {
                        Fragment fragment = new dialogCabinetFR();
                        fragment.setArguments(bundle);
                        FragmentManager fm = ((Chat)getContext()).getSupportFragmentManager();
                        FragmentTransaction transaction = fm.beginTransaction();
                        transaction.replace(R.id.container, fragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }

                break;





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

        final String message = text;
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if(!message.isEmpty()){
                    swipe.setRefreshing(true);
                    get = true;
                    try {
                        JSONObject js = new JSONObject(message);

                        if(js.has("cmd") && js.getString("cmd").equals("newStatus")){
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

                      else   if(js.has("cmd")  && js.has("insertedID") ){
                            object obj = new object();
                            if(msgType==15)
                            obj.setMsg(messageEdit);
                            else
                                obj.setMsg(audioFileName);
                            obj.setType(String.valueOf(msgType));
                            obj.setData(js.getString("date"));
                            obj.setId(js.getString("insertedID"));
                            obj.setUserID(String.valueOf(userID));
                            obj.setTitle(myName);
                            obj.setAvatar(myAva);


//                            if(arrUsers.size()>0) {
//                                for(int i=0; i<arrUsers.size(); i++){
//                                    if(arrUsers.get(i).getId().equals(String.valueOf(userID))){
//                                        obj.setAvatar(arrUsers.get(i).getAvatar());
//                                        obj.setTitle(arrUsers.get(i).getName());
//                                    }
//                                }
                                arrDialog.add(obj);
                                adapter.notifyDataSetChanged();
                                list.setSelection(list.getCount());
                                editMessage.setText("");
                                swipe.setRefreshing(false);


//                            if(arrUsers.size()>0 && type.equals("1")){
//                                obj.setAvatar(arrUsers.get(0).getAvatar());
//                            }

                        }

                      else   if(js.has("cmd") && js.getString("cmd").equals("online")) {
                            swipe.setRefreshing(false);
//                            for(int i=0; i<arrDialog.size(); i++){
//                                if(arrDialog.get(i).getUserID().equals(js.getString("userID")))
//                                    arrDialog.get(i).setStatus("online");
//                            }
//
//                            adapter.notifyDataSetChanged();

                        }
                   else      if(js.has("cmd") && js.getString("cmd").equals("offline")) {
//                            for(int i=0; i<arrDialog.size(); i++){
//                                if(arrDialog.get(i).getUserID().equals(js.getString("userID")))
//                                    arrDialog.get(i).setStatus("offline");
//                            }
                            swipe.setRefreshing(false);
//                            adapter.notifyDataSetChanged();
                        }
                      else   if(js.has("cmd")  && js.getString("cmd").equals("newMessage") ){
                            object obj = new object();
                            obj.setMsg(js.getString("message"));
                            obj.setData(js.getString("date"));
                            obj.setUserID(js.getString("fromID"));
                            obj.setId(js.getString("messageID"));
                            obj.setAvatar(js.getString("avatar"));
                            obj.setTitle(js.getString("name"));
                            obj.setType(js.getString("messageType"));

                            arrDialog.add(obj);
                            adapter.notifyDataSetChanged();
                            list.setSelection(list.getCount());
                            swipe.setRefreshing(false);
                        }

                     else    if(js.has("cmd")  && (js.has("members") || js.has("messages"))){

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
                                    obj.setType(jsArrayMessages.getJSONObject(i).getString("messageType"));
                                    obj.setId(jsArrayMessages.getJSONObject(i).getString("ID"));
                                    obj.setUserID(jsArrayMessages.getJSONObject(i).getString("userID"));
                                    obj.setTitle(jsArrayMessages.getJSONObject(i).getString("name"));
                                    obj.setAvatar(jsArrayMessages.getJSONObject(i).getString("avatar"));
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
                            }
                            swipe.setRefreshing(false);
                        }
                     else swipe.setRefreshing(false);

                    } catch (JSONException e) { e.printStackTrace();
                    }}}});
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
