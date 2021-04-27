package com.example.doggiealbum;

import android.graphics.Bitmap;
import android.util.LruCache;

public enum LruCacheImg {
    INSTANCE;
    private final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);
    private final int lruMemory = maxMemory / 2;         //设置分配给图片缓冲的大熊啊
    LruCache<String, Bitmap> mMemoryCache = new LruCache<String, Bitmap>(lruMemory) {
        @Override
        protected int sizeOf(String key, Bitmap value) {
            return value.getRowBytes() * value.getHeight() / 1024;
        }
    };
}
