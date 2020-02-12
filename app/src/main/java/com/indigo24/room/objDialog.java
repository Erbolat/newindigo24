package com.indigo24.room;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.json.JSONObject;

@Entity
public class objDialog {


    @PrimaryKey(autoGenerate = true)
    int id;
    int toID, fromID, msgID, statusMsg;
    String message, date;

    public objDialog(){

    }

    public objDialog(int toID, int fromID, int msgID, String message, String date, int statusMsg) {
        this.toID = toID;
        this.fromID = fromID;
        this.msgID = msgID;
        this.message = message;
        this.date = date;
        this.statusMsg = statusMsg;
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

    public int getMsgID() {
        return msgID;
    }

    public void setMsgID(int msgID) {
        this.msgID = msgID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getStatusMsg() {
        return statusMsg;
    }

    public void setStatusMsg(int statusMsg) {
        this.statusMsg = statusMsg;
    }
}