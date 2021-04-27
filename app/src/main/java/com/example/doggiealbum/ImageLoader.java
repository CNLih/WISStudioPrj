package com.example.doggiealbum;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
    private boolean isLoading = false;
    private RecycAdapter recycAdapter;
    private BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();

    public ImageLoader(List<News> lists, RecyclerView recyclerView, RecycAdapter recycAdapter){
        this.lists = lists;
        this.recyclerView = recyclerView;
        this.recycAdapter = recycAdapter;
        LoadAtOneTime[0] = 6;
    }

    public ImageLoader(){

    }

    public boolean getIsLoading(){
        return isLoading;
    }

    public void LoadNImage(int num){
        if(!isLoading){
            if(!getNetworkState(BaseApplication.getmContext())){
                Toast.makeText(BaseApplication.getmContext(),"网络不可用",Toast.LENGTH_SHORT).show();
                return ;
            }
            setLoadOnce(num);

            bmpFactoryOptions.inSampleSize = 4;          //设置图片的压缩为原图的1/4
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

    private void getBitmap(String url){
        if(LruCacheImg.INSTANCE.mMemoryCache.get(url) == null){
            LruCacheImg.INSTANCE.mMemoryCache.put(url, getBitmapByUrl(url, true));
        }
    }

    public Bitmap getBitmapByUrl(String url, Boolean isZip){
        URL imgUrl;
        Bitmap bitmap = null;
        try {
            imgUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) imgUrl.openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            int size = conn.getContentLength();
            bmpFactoryOptions.inSampleSize = size / (256 * 1024);
            if(bmpFactoryOptions.inSampleSize > 1 && isZip){                    //这里想让news获取大小
                Log.d("TAG", "getBitmapByUrl: " + "overSize!");
                bitmap = BitmapFactory.decodeStream(is, null, bmpFactoryOptions);
            }
            else {
                bitmap = BitmapFactory.decodeStream(is);
            }
            is.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    //直接copy的
    public static boolean getNetworkState(Context context) {
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        // 获取NetworkInfo对象
        NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();
        // 遍历每一个对象
        for (NetworkInfo networkInfo : networkInfos) {
            if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                // debug信息
                Toast.makeText(context,"TypeName = " + networkInfo.getTypeName(),Toast.LENGTH_SHORT).show();
                // 网络状态可用
                return true;
            }
        }
        // 没有可用的网络
        return false;
    }

    public class UpdateRecyc extends AsyncTask<String, Integer, Void>{
        SynchronousQueue<List<String>> queue = new SynchronousQueue<>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isLoading = true;

            for(int i = 0; i < LoadAtOneTime[0]; i ++) {
                News news = new News();
                lists.add(news);
                recycAdapter.addFootItem();
            }
        }

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
                News news = lists.get(i + lists.size() - LoadAtOneTime[0]);
                news.setUrl(slists.get(i));
                getBitmap(slists.get(i));
                publishProgress(i + lists.size() - LoadAtOneTime[0]);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);

            recycAdapter.updataItem(values[0]);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            isLoading = false;
            Log.d("TAG", "onPostExecute: finished");
        }
    }
}
