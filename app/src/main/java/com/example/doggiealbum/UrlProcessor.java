package com.example.doggiealbum;

import org.json.JSONArray;
import org.json.JSONException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class UrlProcessor {

    public List<String> getUrls(int n) {
        String OriginUrl;
        OriginUrl = "http://shibe.online/api/shibes";
        String jsonUrl = OriginUrl + "?count=" + n;

        List<String> lists = new ArrayList<>();        //将解析好的url加入lists
        try{
            String jsonData = readStream(new URL(jsonUrl).openStream());
            JSONArray jsonArray = new JSONArray(jsonData);
            for(int i = 0; i < jsonArray.length(); i ++){
                lists.add(jsonArray.getString(i));
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
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