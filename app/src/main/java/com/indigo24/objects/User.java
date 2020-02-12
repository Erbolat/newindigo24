package com.indigo24.objects;

public class User {
    String name, id, avatar;

    public User( String id, String name, String avatar) {
        this.name = name;
        this.id = id;
        this.avatar = avatar;
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


}
