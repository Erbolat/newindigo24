package com.indigo24.objects;

public class objCountry {

    int length;
    String code, icon, mask, prefix, title;

    public objCountry() {
    }

    public objCountry(int length, String code, String icon, String mask, String prefix, String title) {
        this.length = length;
        this.code = code;
        this.icon = icon;
        this.mask = mask;
        this.prefix = prefix;
        this.title = title;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getMask() {
        return mask;
    }

    public void setMask(String mask) {
        this.mask = mask;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
