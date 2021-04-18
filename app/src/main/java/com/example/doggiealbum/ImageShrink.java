package com.example.doggiealbum;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.FileOutputStream;

public class ImageShrink extends BaseApplication {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_shrink);
        supportPostponeEnterTransition();

        String imageTransitionName = getIntent().getExtras().getString(RecycAdapter.EXTRA_IMAGE_TRANSITION_NAME);
        String imageUrl = getIntent().getExtras().getString(RecycAdapter.EXTRA_IMAGE_URL);

        ImageView bigImage;
        bigImage = (ImageView)findViewById(R.id.big_img);
        bigImage.setImageBitmap(LruCacheImg.INSTANCE.mMemoryCache.get(imageUrl));
        //不清楚为何transitionname设置了，打开活动也没有画面，所以重新获取bitmap
//        bigImage.setTransitionName(imageTransitionName);
        ViewCompat.setTransitionName(bigImage, imageTransitionName);
        Log.d("TAG", "onCreate: " + imageTransitionName);
        supportStartPostponedEnterTransition();
        bigImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ActivityCompat.finishAfterTransition(ImageShrink.this);
            }
        });
        bigImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //动态获取权限
                if(ContextCompat.checkSelfPermission(BaseApplication.getmContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(ImageShrink.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }
                FileManage.INSTANCE.putNews(imageUrl);
                return false;
            }
        });
    }
}