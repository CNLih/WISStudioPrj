package com.example.doggiealbum;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.SynchronousQueue;

public class UrlProcessor {

    public List<String> getUrls(int n) {
        String OriginUrl;
        OriginUrl = "http://shibe.online/api/shibes";
        String jsonUrl = OriginUrl + "?count=" + n;

        List<String> lists = new ArrayList<>();
        Log.d("TAG", "getUrls: " + jsonUrl);
        try{
            String jsonData = readStream(new URL(jsonUrl).openStream());
            Log.d("TAG", "getUrls: " + jsonData);
            JSONArray jsonArray = new JSONArray(jsonData);
            for(int i = 0; i < jsonArray.length(); i ++){
                Log.d("TAG", "getUrls: " + jsonArray.getString(i));
                lists.add(jsonArray.getString(i));
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("TAG", "getUrls: " + lists);
        return lists;
    }

    //通过InputStream解析网页返回的数据
    private String readStream(InputStream is) {
        InputStreamReader isr = null;
        String result = "";
        try {
            isr = new InputStreamReader(is, "utf8");  //将字节流转为字符流
            BufferedReader br = new BufferedReader(isr);  //通过BufferReader读取
            String line = "";
            while ((line = br.readLine()) != null) {
                result += line;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }
}

//    //实现网页的异步访问
//    class NewsAsynTask extends AsyncTask<String, Void, List<String>> {
//        @Override
//        protected List<String> doInBackground(String... urls) {
//            return getUrls(5);
//        }
//
//        @Override
//        protected void onPostExecute(List<String> newsBeans) {
//            super.onPostExecute(newsBeans);
//            //构建数据源
//            //NewsAdapter newsAdapter = new NewsAdapter(MainActivity.this, newsBeans);
//            //mListView.setAdapter(newsAdapter);
//        }
//    }
