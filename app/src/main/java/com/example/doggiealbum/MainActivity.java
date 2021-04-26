package com.example.doggiealbum;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends BaseApplication {
    private List<News> newsList = new ArrayList<>();
    RecycAdapter recycAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button button = (Button)findViewById(R.id.btn_new_img);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.re_view);
        ReboundScrollView reboundScrollView = findViewById(R.id.scroll_lout);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        //        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        //recyclerView.canScrollVertically(-1);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setNestedScrollingEnabled(false);

        ArrayList<String[]> list;
        list = FileManage.INSTANCE.getAllNews();
        initAlbum(list, recyclerView);

        ImageLoader imageLoader = new ImageLoader(newsList, recyclerView, recycAdapter);

        reboundScrollView.setOnReboundEndListener(new ReboundScrollView.OnReboundEndListener() {
            @Override
            public void onReboundTopComplete() {

            }

            @Override
            public void onReboundBottomComplete() {
                Log.d("TAG", "onReboundBottomComplete: " + "3232323");
                button.setVisibility(View.GONE);
                if(!imageLoader.getIsLoading()){
                    imageLoader.LoadNImage(8);
                }
            }
        });
        button.setOnClickListener(view -> imageLoader.LoadNImage(8));
        //imageLoader.LoadNImage();
//        initNews();
        //RecycAdapter recycAdapter = new RecycAdapter(newsList);
        //recyclerView.setAdapter(recycAdapter);
    }

    private void initAlbum(ArrayList<String[]> list, RecyclerView recyclerView){
        recycAdapter = new RecycAdapter(newsList, recyclerView);
        recyclerView.setAdapter(recycAdapter);
        for(int i = 0; i < list.size(); i ++){
            News first = new News("NONE", list.get(i)[0]);
            newsList.add(first);
            try {
                FileInputStream fis = new FileInputStream(list.get(i)[1]);
                LruCacheImg.INSTANCE.mMemoryCache.put(list.get(i)[0], BitmapFactory.decodeStream(fis));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
//            RecycAdapter recycAdapter = new RecycAdapter(newsList);
//            recyclerView.setAdapter(recycAdapter);
            recycAdapter.addFootItem();
        }
    }
}