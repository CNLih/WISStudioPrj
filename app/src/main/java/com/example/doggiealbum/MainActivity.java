package com.example.doggiealbum;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseApplication {
    private List<News> newsList = new ArrayList<>();
    private BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
    RecycAdapter recycAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageLoader.getNetworkState();

        //View
        Button button = (Button)findViewById(R.id.btn_new_img);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.re_view);
        ReboundScrollView reboundScrollView = findViewById(R.id.scroll_lout);

        //Manager
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);

        //为FileManage获取权限
        if(ContextCompat.checkSelfPermission(BaseApplication.getmContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
        //首先检测本地文件，并进行初始化
        ArrayList<String[]> list;
        list = FileManage.INSTANCE.getAllNews();
        initAlbum(list, recyclerView);
        ImageLoader imageLoader = new ImageLoader(newsList, recyclerView, recycAdapter);
        if(list.size() < 6){
            imageLoader.LoadNImage(6 - list.size());
        }

        reboundScrollView.setOnReboundEndListener(new ReboundScrollView.OnReboundEndListener() {
            @Override
            public void onReboundTopComplete() {
                FileManage.INSTANCE.getAllNews();   //从db中移除一些本地删除的图片
            }

            @Override
            public void onReboundBottomComplete() {
                if(!imageLoader.getIsLoading()){
                    imageLoader.LoadNImage(6);
                }
            }
        });
        button.setOnClickListener(view -> imageLoader.LoadNImage(6));
    }

    private void initAlbum(ArrayList<String[]> list, RecyclerView recyclerView){
        recycAdapter = new RecycAdapter(newsList, recyclerView);
        recyclerView.setAdapter(recycAdapter);
        for(int i = 0; i < list.size(); i ++){
            News first = new News("NONE", list.get(i)[0]);
            newsList.add(first);
            try {
                FileInputStream fis = new FileInputStream(list.get(i)[1]);
                int size;
                size = fis.available();
                if(size >= 1024 * 256){           //大小在256k以上的图片压缩放到缓冲区
                    LruCacheImg.INSTANCE.mMemoryCache.put(list.get(i)[0], BitmapFactory.decodeStream(fis, null, bmpFactoryOptions));
                }
                else{
                    LruCacheImg.INSTANCE.mMemoryCache.put(list.get(i)[0], BitmapFactory.decodeStream(fis));
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            recycAdapter.addFootItem();           //recyclerView尾部动态添加新元素
        }
    }
}