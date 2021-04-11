package com.example.doggiealbum;

public class News {
    private String title;
    private int content;

    public void setTitle(String title) {
        this.title = title;
    }

    public void setContent(int content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public int getContent() {
        return content;
    }

    public News(String title, int content){
        this.title = title;
        this.content = content;
    }
}
