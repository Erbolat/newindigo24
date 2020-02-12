package com.indigo24;

import android.app.Application;
import android.content.Context;

import androidx.room.Room;
import com.indigo24.room.AppDatabase;
import com.vanniktech.emoji.EmojiManager;
import com.vanniktech.emoji.ios.IosEmojiProvider;

public class MainApp extends Application  {
    private static MainApp mInstance;
    private static Context mAppContext;
    private AppDatabase database;
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


}
