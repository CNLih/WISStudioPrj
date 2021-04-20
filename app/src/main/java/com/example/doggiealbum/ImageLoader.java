package com.example.doggiealbum;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.SynchronousQueue;

public class ImageLoader {
    private List<News> lists;
    private RecyclerView recyclerView;
    private final int[] LoadAtOneTime = new int[2];
    private final boolean isLoading = false;

    public ImageLoader(List<News> lists, RecyclerView recyclerView){
        this.lists = lists;
        this.recyclerView = recyclerView;
        LoadAtOneTime[0] = 6;
    }

    public void LoadNImage(int num){
        if(!isLoading){
            setLoadOnce(num);
            new UpdateRecyc().execute();
        }
    }

    public void setLoadOnce(int num){
        if(num < 1) {
            Toast.makeText(BaseApplication.getmContext(), "至少每次更新1张图片", Toast.LENGTH_SHORT).show();
            return ;
        }
        this.LoadAtOneTime[0] = num;
    }

    public int getLoadOnce(int num){
        return this.LoadAtOneTime[0];
    }

    private static Bitmap getBitmap(String url){
        Bitmap bitmap;
        if((bitmap = LruCacheImg.INSTANCE.mMemoryCache.get(url)) != null){
            return bitmap;
        }else{
            return getBitmapByUrl(url);
        }
    }

    private static Bitmap getBitmapByUrl(String url){
        URL imgUrl;
        Bitmap bitmap = null;
        try {
            imgUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) imgUrl.openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            LruCacheImg.INSTANCE.mMemoryCache.put(url, bitmap);
            is.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public class UpdateRecyc extends AsyncTask<String, Void, Void>{
        SynchronousQueue<List<String>> queue = new SynchronousQueue<>();

        @Override
        protected Void doInBackground(String... strings) {
            UrlProcessor urlProcessor = new UrlProcessor();
            new Thread(() -> {
                try {
                    queue.put(urlProcessor.getUrls(LoadAtOneTime[0]));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();

            List<String> slists = null;
            try {
                slists = queue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            for(int i = 0; i < Objects.requireNonNull(slists).size(); i ++){
                News news = new News("ab", slists.get(i));
                getBitmap(slists.get(i));
                lists.add(news);
                publishProgress();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
            RecycAdapter recycAdapter = new RecycAdapter(lists);
            recyclerView.setAdapter(recycAdapter);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.d("TAG", "onPostExecute: finished");
        }
    }
}
