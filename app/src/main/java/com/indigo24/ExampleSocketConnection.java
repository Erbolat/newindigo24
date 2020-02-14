package com.indigo24;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;

import java.util.prefs.Preferences;

public class ExampleSocketConnection implements ClientWebSocket.MessageListener{
    private ClientWebSocket clientWebSocket;
    private Context context;
    public Gson gson = new Gson();
    private Handler socketConnectionHandler;

    private Runnable checkConnectionRunnable = () -> {
//        if (!clientWebSocket.getConnection().isOpen()) {
            openConnection();
//        }
        startCheckConnection();

    };

    private void startCheckConnection() {
        socketConnectionHandler.postDelayed(checkConnectionRunnable, 5000);
    }

    private void stopCheckConnection() {
        socketConnectionHandler.removeCallbacks(checkConnectionRunnable);
    }

    public ExampleSocketConnection(Context context) {
        this.context = context;
        socketConnectionHandler = new Handler();
    }

    public void openConnection() {


        initScreenStateListener();
        startCheckConnection();
    }

    public void closeConnection() {
        if (clientWebSocket != null) {
            clientWebSocket.close();
            clientWebSocket = null;
        }
        releaseScreenStateListener();
        stopCheckConnection();
    }


//    @Override
//    public void onSocketMessage(String message) {
//            Log.e("MESSAGEERA77", message);
//
//    }

    /**
     * Screen state listener for socket live cycle
     */
    private void initScreenStateListener() {
        context.registerReceiver(screenStateReceiver, new IntentFilter(Intent.ACTION_SCREEN_ON));
        context.registerReceiver(screenStateReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
    }

    private void releaseScreenStateListener() {
        try {
            context.unregisterReceiver(screenStateReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    private BroadcastReceiver screenStateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                Log.i("Websocket", "Screen ON");
                openConnection();
            } else if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                Log.i("Websocket", "Screen OFF");
                closeConnection();
            }
        }
    };

    public boolean isConnected() {
        return clientWebSocket != null &&
                clientWebSocket.getConnection() != null &&
                clientWebSocket.getConnection().isOpen();
    }

    @Override
    public void onSocketMessage(String message) {

    }
}