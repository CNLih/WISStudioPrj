package com.example.doggiealbum;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecycAdapter extends RecyclerView.Adapter<RecycAdapter.VH> {
    private List<News> newsList;

    public RecycAdapter(List<News> newsList){
        this.newsList = newsList;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyc_item, parent, false);
        VH vh = new VH(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        News news = newsList.get(position);
        holder.tv1.setText(news.getTitle());
        holder.tv2.setText(news.getContent());
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tv1;
        TextView tv2;

        public VH(@NonNull View itemView) {
            super(itemView);
            tv1 = itemView.findViewById(R.id.tv1);
            tv2 = itemView.findViewById(R.id.tv2);
        }
    }
}
