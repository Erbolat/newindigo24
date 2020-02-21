package com.indigo24;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.util.Log;

import com.indigo24.activities.SplashActivity;
import com.indigo24.requests.Interface;
import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;
import com.neovisionaries.ws.client.WebSocketListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;

import static com.indigo24.MainApp.arrOnlines;
import static com.indigo24.requests.Interface.TAG;

public class ClientWebSocket {


    private MessageListener listener;
    private String request;
    public static   WebSocket ws = null;

    public ClientWebSocket(MessageListener listener) {
        this.listener = listener;
    }
    public void connect() {
        new Thread(() -> {
            if (ws != null) {
                reconnect();
            } else {
                try {
                    WebSocketFactory factory = new WebSocketFactory();
                    SSLContext context = NaiveSSLContext.getInstance("TLS");
                    factory.setSSLContext(context);
                    ws = factory.createSocket("ws://indigo24.xyz:33080/");
                    ws.addListener(new SocketListener());
                    ws.connect();
                } catch (WebSocketException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    private void reconnect() {
        try {
            ws = ws.recreate().connect();
        } catch (WebSocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public WebSocket getConnection() {
        return ws;
    }

    public void close() {
        ws.disconnect();
    }

    public class SocketListener extends WebSocketAdapter {

        @Override
        public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
            super.onConnected(websocket, headers);
            Log.i(TAG, "onConnected");

            JSONObject sendInit = new JSONObject();
            try {
                sendInit.put("cmd", "init");
                sendInit.put("userID", Integer.parseInt(MainApp.userID));
                sendInit.put("token", MainApp.unique);
                request = sendInit.toString();
            } catch (JSONException | NullPointerException e) {
                e.printStackTrace();
            }
            ws.sendText(request);

        }

        @SuppressLint("LongLogTag")
        public void onTextMessage(WebSocket websocket, String message) {

            listener.onSocketMessage(message);
            try {
                JSONObject js = new JSONObject(message);
                if(js.has("cmd") && (js.getString("cmd").equals("online")) || js.getString("cmd").equals("offline")) {
                    if(arrOnlines.size()>0) {
                        for (int i=0; i<arrOnlines.size(); i++) {
                            if(!arrOnlines.get(i).equals(js.getString("userID")) && js.getString("cmd").equals("online"))
                            arrOnlines.add(js.getString("userID"));
                            else  if(arrOnlines.get(i).equals(js.getString("userID")) && js.getString("cmd").equals("offline"))
                                arrOnlines.remove(i);
                        }
                    }
                    else  if(js.getString("cmd").equals("online"))
                        arrOnlines.add(js.getString("userID"));


                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

//            Log.e(TAG+"class ClientWebsocket ", "Message --> " + message);

        }

        @Override
        public void onError(WebSocket websocket, WebSocketException cause) {
            Log.i(TAG, "Error -->" + cause.getMessage());

            reconnect();
        }

        @Override
        public void onDisconnected(WebSocket websocket,
                                   WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame,
                                   boolean closedByServer) {
            Log.i(TAG, "onDisconnected");
            if (closedByServer) {
                reconnect();
            }
        }

        @Override
        public void onUnexpectedError(WebSocket websocket, WebSocketException cause) {
            Log.i(TAG, "Error -->" + cause.getMessage());
            reconnect();
        }

        @Override
        public void onPongFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
            super.onPongFrame(websocket, frame);
            websocket.sendPing("Are you there?");
        }


    }


    public interface MessageListener extends WebSocketListener {
        void onSocketMessage(String message);
    }



}
