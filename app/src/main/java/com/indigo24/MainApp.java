package com.indigo24;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.room.Room;
import com.indigo24.room.AppDatabase;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.ios.IosEmojiProvider;

import org.json.JSONException;
import org.json.JSONObject;

public class MainApp extends Application  implements ClientWebSocket.MessageListener {
    private static MainApp mInstance;
    private static Context mAppContext;
    private AppDatabase database;
    public  static  String userID, unique;
    public  static ClientWebSocket mClientWebSocket;

    private ExampleSocketConnection exampleSocketConnection;
    String msg;
    static  boolean isCon = false;
    ClientWebSocket.MessageListener listener;
    @Override
    public void onCreate() {
        super.onCreate();


    }

    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        mInstance = this;
        database = Room.databaseBuilder(this, AppDatabase.class, "database").fallbackToDestructiveMigration().allowMainThreadQueries()
                .build();
        EmojiManager.install(new IosEmojiProvider());

//        SharedPreferences sPref = getSharedPreferences("UserData",MODE_PRIVATE);
//        userID  = sPref.getString("id","");
//        unique  = sPref.getString("unique","");
//        SingleTon.initInstance(getApplicationContext());
//        exampleSocketConnection = new ExampleSocketConnection(this);
//        BackgroundManager.get(this).registerListener(appActivityListener);



//        mClientWebSocket = new ClientWebSocket("1");


        this.setAppContext(getApplicationContext());

    }
    public AppDatabase getDatabase() {
        return database;
    }
    public static MainApp getInstance() {
        return mInstance;
    }

    public static Context getAppContext() {
        return mAppContext;
    }

    public void setAppContext(Context mAppContext) {
        this.mAppContext = mAppContext;
    }

    public void closeSocketConnection() {
        exampleSocketConnection.closeConnection();
    }

    public void openSocketConnection() {
        exampleSocketConnection.openConnection();
    }

    public boolean isSocketConnected() {
        return exampleSocketConnection.isConnected();
    }

    public void reconnect() {
        exampleSocketConnection.openConnection();
    }

    private BackgroundManager.Listener appActivityListener = new BackgroundManager.Listener() {

        public void onBecameForeground() {
            openSocketConnection();

            Log.i("Websocket", "Became Foreground");
        }

        public void onBecameBackground() {
            closeSocketConnection();
            Log.i("Websocket", "Became Background");
        }
    };

    @Override
    public void onSocketMessage(String message) {
        Log.e("ERA55555", message);
    }



    public void setMsg(String msg) {
        this.msg = msg;
    }

    public   String getMsg() {
        return msg;
    }
}
