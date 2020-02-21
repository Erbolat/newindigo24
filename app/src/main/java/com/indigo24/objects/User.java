package com.indigo24.objects;

public class User {
    String name, id, avatar, cabinetID, phone, status, admin;

    public User( String id, String name, String avatar) {
        this.name = name;
        this.id = id;
        this.avatar = avatar;
    }

    public User(String name, String id, String avatar, String cabinetID, String phone, String status, String admin) {
        this.name = name;
        this.id = id;
        this.avatar = avatar;
        this.cabinetID = cabinetID;
        this.phone = phone;
        this.status = status;
        this.admin = admin;
    }

    public User() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getCabinetID() {
        return cabinetID;
    }

    public void setCabinetID(String cabinetID) {
        this.cabinetID = cabinetID;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus2() {
        return admin;
    }

    public void setStatus2(String status2) {
        this.admin = status2;
    }

    public String getAdmin() {
        return admin;
    }

    public void setAdmin(String admin) {
        this.admin = admin;
    }
}
