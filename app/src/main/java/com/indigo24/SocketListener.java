package com.indigo24;

import android.util.Log;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketAdapter;
import com.neovisionaries.ws.client.WebSocketException;
import com.neovisionaries.ws.client.WebSocketFrame;

import java.util.List;
import java.util.Map;

public class SocketListener extends WebSocketAdapter {
    boolean isCon = false;
    String  requests;
    public SocketListener(String request) {
        requests = request;
    }

    @Override
    public void onConnected(WebSocket websocket, Map<String, List<String>> headers) throws Exception {
        super.onConnected(websocket, headers);




    }

    public void onTextMessage(WebSocket websocket, String message) {
//            listener.onSocketMessage(message);
        Log.e("MESSAGE! - ", message);
    }

    @Override
    public void onError(WebSocket websocket, WebSocketException cause) {
//        Log.i(TAG, "Error -->" + cause.getMessage());
//
//        reconnect();
    }

    @Override
    public void onDisconnected(WebSocket websocket,
                               WebSocketFrame serverCloseFrame, WebSocketFrame clientCloseFrame,
                               boolean closedByServer) {
//        Log.i(TAG, "onDisconnected");
//        if (closedByServer) {
//            reconnect();
//        }
    }

    @Override
    public void onUnexpectedError(WebSocket websocket, WebSocketException cause) {
//        Log.i(TAG, "Error -->" + cause.getMessage());
//        reconnect();
    }

    @Override
    public void onPongFrame(WebSocket websocket, WebSocketFrame frame) throws Exception {
        super.onPongFrame(websocket, frame);
        websocket.sendPing("Are you there?");
    }
}