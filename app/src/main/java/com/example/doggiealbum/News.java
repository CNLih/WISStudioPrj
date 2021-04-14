package com.example.doggiealbum;

import android.graphics.Bitmap;

public class News {
    private String title;
    private Bitmap bitmap;

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

    public News(String title, Bitmap bitmap){
        this.title = title;
        this.bitmap = bitmap;
    }
}
