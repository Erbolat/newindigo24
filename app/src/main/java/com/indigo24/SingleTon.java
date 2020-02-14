package com.indigo24;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;

import com.indigo24.activities.SplashActivity;
import com.indigo24.adapters.AdapterListChat;
import com.indigo24.objects.object;

import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketListener;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.enums.Opcode;
import org.java_websocket.enums.ReadyState;
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
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.EventListener;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

public class SingleTon  {
      public  static SingleTon mInstance ;
      public static  WebSocketClient mWebSocketClient;
      public  static boolean isConnected = false;
      public static URI uri = null;
      public static WebSocketListener sWebSocketListener;

      public static Context mContext;




    public static void initInstance(Context context) {
        Log.d("MY", "MySingleton::InitInstance()");
        if (mInstance == null) {
            try {
                uri = new URI("ws://indigo24.xyz:33080/");
            } catch (URISyntaxException e) {
                e.printStackTrace();
                return;
            }



            mWebSocketClient = new WebSocketClient(uri) {
                @Override
                public void onOpen(ServerHandshake handshakedata) {
                    JSONObject sendInit = new JSONObject();
                    try {
                        sendInit.put("cmd", "init");
                        sendInit.put("userID", Integer.parseInt(MainApp.userID));
                        sendInit.put("token", MainApp.unique);
                        mWebSocketClient.send(sendInit.toString());
                    } catch (JSONException | NullPointerException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onMessage(String message) {
                Log.e("ERA99", message);

                }

                @Override
                public void onClose(int code, String reason, boolean remote) {

                }

                @Override
                public void onError(Exception ex) {

                }
            };
            mWebSocketClient.connect();




        }
    }


}