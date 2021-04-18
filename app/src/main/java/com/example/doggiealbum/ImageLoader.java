package com.example.doggiealbum;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.SynchronousQueue;

public class ImageLoader {
    private List<News> lists;
    private RecyclerView recyclerView;
    public ImageLoader(List<News> lists, RecyclerView recyclerView){
        this.lists = lists;
        this.recyclerView = recyclerView;

        new UpdateRecyc().execute();
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
        URL imgUrl = null;
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
        SynchronousQueue<List<String>> queue = new SynchronousQueue<List<String>>();

        @Override
        protected Void doInBackground(String... strings) {
            UrlProcessor urlProcessor = new UrlProcessor();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        queue.put(urlProcessor.getUrls(6));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            List<String> slists = null;
            try {
                slists = queue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            for(int i = 0; i < slists.size(); i ++){
                News news = new News("ab", getBitmap(slists.get(i)), slists.get(i));
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
