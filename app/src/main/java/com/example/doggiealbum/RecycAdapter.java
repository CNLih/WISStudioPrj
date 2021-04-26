package com.example.doggiealbum;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RecycAdapter extends RecyclerView.Adapter<RecycAdapter.VH> {
    public static String EXTRA_IMAGE_TRANSITION_NAME = "extra_transition";
    public static String EXTRA_IMAGE_URL = "extra_url";
    private List<News> newsList;
    private boolean mIsSet;
    private RecyclerView mRecyclerView;
    private View mView;

    public RecycAdapter(List<News> newsList, RecyclerView recyclerView){
        this.newsList = newsList;
        this.mRecyclerView = recyclerView;
        mIsSet=false;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyc_item, parent, false);
        mView=view;
        return new VH(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        News news = newsList.get(position);
        if(news.getUrl() == null){
            return ;
        }
        holder.tv1.setText(news.getTitle());
        holder.img1.setImageBitmap(LruCacheImg.INSTANCE.mMemoryCache.get(news.getUrl()));
        holder.transitionName = "shared" + position;
        ViewCompat.setTransitionName(holder.img1, "shared" + holder.transitionName);
        holder.img1.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), ImageShrink.class);
            Log.d("TAG", "onClick: " + holder.transitionName);
            intent.putExtra(EXTRA_IMAGE_TRANSITION_NAME, holder.transitionName);
            intent.putExtra(EXTRA_IMAGE_URL, news.getUrl());
            view.getContext().startActivity(
                    intent,
                    ActivityOptionsCompat.makeSceneTransitionAnimation(
                            (Activity) view.getContext(), holder.img1, holder.transitionName
                    ).toBundle());
        });
        setRecyclerViewHeight();
    }

    public void addFootItem(){
        notifyItemInserted(getItemCount());
    }

    public void updataItem(int pos){
        notifyItemChanged(pos);
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tv1;
        ImageView img1;
        String transitionName;

        public VH(@NonNull View itemView) {
            super(itemView);
            tv1 = itemView.findViewById(R.id.tv1);
            img1 = itemView.findViewById(R.id.img1);
        }
    }

    private void setRecyclerViewHeight(){
        if(mIsSet||mRecyclerView==null) return;
        mIsSet=true;
        //不可以用View.getHeight()方法获取高度，因为这个时候控件高度还没有被度量，要在onCreate执行后才会被度量，因此我们需要直接通过获取属性来获取高度！
        /*注意，所有的getLayoutParams方法，获取的都是它在父view中的属性，所以不难理解这两个强制类型转换*/
        RecyclerView.LayoutParams rparam=(RecyclerView.LayoutParams)mView.getLayoutParams();
        int Height=getItemCount()*rparam.height;
        Log.w("length","mview's height is "+rparam.height);
        ConstraintLayout.LayoutParams layoutParams= (ConstraintLayout.LayoutParams) mRecyclerView.getLayoutParams();
        layoutParams.height=Height;
        mRecyclerView.setLayoutParams(layoutParams);
    }
}
