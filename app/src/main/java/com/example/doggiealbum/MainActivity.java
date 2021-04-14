package com.example.doggiealbum;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<News> newsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.re_view);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);

        ImageLoader imageLoader = new ImageLoader(newsList, recyclerView);
        //imageLoader.LoadNImage();
//        initNews();
        //RecycAdapter recycAdapter = new RecycAdapter(newsList);
        //recyclerView.setAdapter(recycAdapter);
    }

    private void initNews(){
//        for(int i = 0; i < 10; i ++){
//            News first = new News("ABC", );
//            newsList.add(first);
//        }
    }
}