package com.indigo24.room;

import android.graphics.drawable.Drawable;
import android.transition.Slide;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.json.JSONObject;

@Entity
public class objList {

    @PrimaryKey(autoGenerate = true)
    public int id;
    public int toID;
    public int fromID;
    public int msgID;
    public String date;
    public String name;
    public String phone;
    public String avatar;
    public String lastMsg;
    public String countUnread;

    public objList() {

    }

    public objList(int id, int toID, int fromID, int msgID, String date, String name, String phone, String avatar, String lastMsg, String countUnread) {
        this.id = id;
        this.toID = toID;
        this.fromID = fromID;
        this.msgID = msgID;
        this.date = date;
        this.name = name;
        this.phone = phone;
        this.avatar = avatar;
        this.lastMsg = lastMsg;
        this.countUnread = countUnread;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getToID() {
        return toID;
    }

    public void setToID(int toID) {
        this.toID = toID;
    }

    public int getFromID() {
        return fromID;
    }

    public void setFromID(int fromID) {
        this.fromID = fromID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(String lastMsg) {
        this.lastMsg = lastMsg;
    }

    public String getCountUnread() {
        return countUnread;
    }

    public void setCountUnread(String countUnread) {
        this.countUnread = countUnread;
    }
}