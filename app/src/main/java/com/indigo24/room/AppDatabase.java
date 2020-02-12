package com.indigo24.room;


import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {objList.class, objDialog.class}, version = 1)
    public abstract class AppDatabase extends RoomDatabase {
        public abstract intList mIntList();
        public abstract intDialog mIntDialog();


    }



