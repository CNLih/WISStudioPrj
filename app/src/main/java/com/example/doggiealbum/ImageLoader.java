package com.example.doggiealbum;

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
import java.util.List;
import java.util.Objects;
import java.util.concurrent.SynchronousQueue;

public class ImageLoader {
    private List<News> lists;
    private RecyclerView recyclerView;
    private final int[] LoadAtOneTime = new int[2];
    private boolean isLoading = false;
    private RecycAdapter recycAdapter;

    public ImageLoader(List<News> lists, RecyclerView recyclerView, RecycAdapter recycAdapter){
        this.lists = lists;
        this.recyclerView = recyclerView;
        this.recycAdapter = recycAdapter;
    }

    public boolean getIsLoading(){
        return isLoading;
    }

    public void LoadNImage(int num){
        if(!isLoading){
            if(!getNetworkState()){
                Toast.makeText(BaseApplication.getmContext(),"网络不可用",Toast.LENGTH_SHORT).show();
                return ;
            }
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

    private void getBitmap(String url){
        if(LruCacheImg.INSTANCE.mMemoryCache.get(url) == null){
            LruCacheImg.INSTANCE.mMemoryCache.put(url, getBitmapByUrl(url, true));
        }
    }

    public static Bitmap getBitmapByUrl(String url, Boolean isZip){
        URL imgUrl;
        Bitmap bitmap = null;
        try {
            imgUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) imgUrl.openConnection();
            conn.setDoInput(true);
            conn.connect();
            InputStream is = conn.getInputStream();
            int size = conn.getContentLength();       //网络中获取bitmap大小的方式
            BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
            bmpFactoryOptions.inSampleSize = size / (256 * 1024);               //让大于256k的图片通过sampleSize尽量贴近256k
            if(bmpFactoryOptions.inSampleSize > 1 && isZip){
//                Log.d("TAG", "getBitmapByUrl: " + "overSize!");
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
    public static boolean getNetworkState() {
        // 获取手机所有连接管理对象（包括对wi-fi,net等连接的管理）
        ConnectivityManager connectivityManager = (ConnectivityManager) BaseApplication.getmContext().getSystemService(BaseApplication.getmContext().CONNECTIVITY_SERVICE);
        // 获取NetworkInfo对象
        NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();
        // 遍历每一个对象
        for (NetworkInfo networkInfo : networkInfos) {
            if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                return true;
            }
        }
        // 没有可用的网络
        return false;
    }

    public class UpdateRecyc extends AsyncTask<Void, Integer, Void>{
        SynchronousQueue<List<String>> queue = new SynchronousQueue<>();   //锁，处理完url才加载图片

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isLoading = true;        //锁，避免一次申请多个加载请求

            for(int i = 0; i < LoadAtOneTime[0]; i ++) {
                News news = new News();
                lists.add(news);
                recycAdapter.addFootItem();
            }
        }

        @Override
        protected Void doInBackground(Void... void0) {
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

            recycAdapter.updateItem(values[0]);        //这里更新缓冲好的图片
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            isLoading = false;
        }
    }
}
