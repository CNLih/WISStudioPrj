package com.example.doggiealbum;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
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
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        initNews();
        RecycAdapter recycAdapter = new RecycAdapter(newsList);
        recyclerView.setAdapter(recycAdapter);
    }

    private void initNews(){
        for(int i = 0; i < 10; i ++){
            News first = new News("ABC", "abc");
            newsList.add(first);
        }
    }
}