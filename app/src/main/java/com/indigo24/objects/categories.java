package com.indigo24.objects;

public class categories {
    String id, logo, title, comission;

    public categories(String id, String logo, String title, String comission) {
        this.id = id;
        this.logo = logo;
        this.title = title;
        this.comission = comission;
    }

    public categories() {
        this.id = id;
        this.logo = logo;
        this.title = title;
        this.comission = comission;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getComission() {
        return comission;
    }

    public void setComission(String comission) {
        this.comission = comission;
    }
}
