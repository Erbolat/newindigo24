package com.indigo24;

import androidx.appcompat.app.AppCompatActivity;

import com.neovisionaries.ws.client.WebSocket;
import com.neovisionaries.ws.client.WebSocketListener;

import java.net.URI;

public abstract class Example extends AppCompatActivity {

    @Override
    protected void onStart() {



        super.onStart();

    }

    @Override
    protected void onStop() {
//        EventBus.getDefault().unregister(this);
        super.onStop();
    }

//    @Subscribe
//    public void handleRealTimeMessage(RealTimeEvent event) {
//        // processing of all real-time events
//    }
}