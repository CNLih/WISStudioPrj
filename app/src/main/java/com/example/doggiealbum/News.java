package com.example.doggiealbum;

import android.graphics.Bitmap;

import java.io.Serializable;

public class News {
    private String title;
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public News(){
        this.title = null;
        this.url = null;
    }

    public News(String title, String url){
        this.title = title;
        this.url = url;
    }

    public News(String url){
        this.title = "";
        this.url = url;
    }
}
