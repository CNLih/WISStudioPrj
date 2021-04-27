package com.example.doggiealbum;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

//用于给工具类提供Context
public class BaseApplication extends AppCompatActivity {
    private static Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getApplicationContext();
    }

    public static Context getmContext(){
        return mContext;
    }
}
