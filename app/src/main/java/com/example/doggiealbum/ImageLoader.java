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
    List<News> lists;
    RecyclerView recyclerView;
    public ImageLoader(List<News> lists, RecyclerView recyclerView){
        this.lists = lists;
        this.recyclerView = recyclerView;

        new UpdateRecyc().execute();
    }

    public List<News> LoadNImage(){
        SynchronousQueue<List<String>> queue = new SynchronousQueue<List<String>>();

        new Thread(new Runnable() {
            @Override
            public void run() {
                UrlProcessor urlProcessor = new UrlProcessor();
                try {
                    queue.put(urlProcessor.getUrls(6));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        new Thread(new Runnable() {
            @Override
            public void run() {
                List<String> slists = null;
                try {
                    slists = queue.take();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                for(int i = 0; i < slists.size(); i ++){
                    News news = new News("ab", getBitmapByUrl(slists.get(i)));
                    lists.add(news);
                }
            }
        }).start();
        return lists;
    }

    public static Bitmap getBitmapByUrl(String url){
        URL imgUrl = null;
        Bitmap bitmap = null;
        try {
            imgUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) imgUrl.openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            bitmap = BitmapFactory.decodeStream(is);
            is.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public class UpdateRecyc extends AsyncTask<String, Void, List<News>>{
        SynchronousQueue<List<String>> queue = new SynchronousQueue<List<String>>();

        @Override
        protected List<News> doInBackground(String... strings) {
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
                News news = new News("ab", getBitmapByUrl(slists.get(i)));
                lists.add(news);
            }

            return lists;
        }

        @Override
        protected void onPostExecute(List<News> news) {
            super.onPostExecute(news);
            Log.d("TAG", "onPostExecute:   executed");
            RecycAdapter recycAdapter = new RecycAdapter(news);
            recyclerView.setAdapter(recycAdapter);
        }
    }
}
