package com.example.doggiealbum;

import android.graphics.Bitmap;

import java.io.Serializable;

public class News {
    private String title;
    private Bitmap bitmap;
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

    public void setBitmap(Bitmap bitmap){
        this.bitmap = bitmap;
    }

    public String getTitle() {
        return title;
    }

    public Bitmap getBitmap(){
        return bitmap;
    }

    public News(String title, Bitmap bitmap, String url){
        this.title = title;
        this.bitmap = bitmap;
        this.url = url;
    }
}
