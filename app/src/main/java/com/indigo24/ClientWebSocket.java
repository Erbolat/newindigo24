package com.indigo24;

import android.util.Log;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFactory;
import com.neovisionaries.ws.client.WebSocketFrame;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;

public class ClientWebSocket {

    private static final String TAG = "Websocket";
    private MessageListener listener;
    private String request;
    public static WebSocket ws = null;
    String open="";
    public ClientWebSocket(MessageListener listener, String request) {
        this.listener = listener;
        this.request = request;
//        if(ws== null) {
//            Log.e("ERA00","ERA00");
//            connect();
//        }
//        else {
//            Log.e("ERA11","ERA11");
//        }


    }

    public ClientWebSocket(String open){
        this.open = open;
//        if(ws==null)
            connect();
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
            if(open.equals("1")) {
            JSONObject sendInit = new JSONObject();
            String request = null;
//            Log.e("PP",Integer.parseInt(MainApp.userID)+"    11111111");
//            Log.e("PP2",unique+" 111111111111111111");
            try {
                sendInit.put("cmd", "init");
                sendInit.put("userID", Integer.parseInt(MainApp.userID));
                sendInit.put("token", MainApp.unique);
                request = sendInit.toString();
            } catch (JSONException | NullPointerException e) {
                e.printStackTrace();
            }
            Log.e("RE",request);
            ws.sendText(request);}
        }

        public void onTextMessage(WebSocket websocket, String message) {
            Log.e(TAG, "Message --> " + message);
            listener.onSocketMessage(message);

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


    public interface MessageListener {
        void onSocketMessage(String message);
    }




}
