package com.example.doggiealbum;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.SynchronousQueue;

public class ImageShrink extends BaseApplication {
    private ImageView bigImage;
    Bitmap originImg = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_shrink);
        supportPostponeEnterTransition();

        String imageTransitionName = getIntent().getExtras().getString(RecycAdapter.EXTRA_IMAGE_TRANSITION_NAME);
        String imageUrl = getIntent().getExtras().getString(RecycAdapter.EXTRA_IMAGE_URL);

        bigImage = (ImageView)findViewById(R.id.big_img);
        bigImage.setImageBitmap(LruCacheImg.INSTANCE.mMemoryCache.get(imageUrl));
        //不清楚为何transitionname设置了，打开活动也没有画面，所以重新获取bitmap
//        bigImage.setTransitionName(imageTransitionName);
        ViewCompat.setTransitionName(bigImage, imageTransitionName);
        Log.d("TAG", "onCreate: " + imageTransitionName);
        supportStartPostponedEnterTransition();

        new UpdateImageView().execute(imageUrl);

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
                if(originImg == null){
                    Toast.makeText(BaseApplication.getmContext(), "稍等..加载原图ing", Toast.LENGTH_SHORT).show();
                    return false;
                }
                FileManage.INSTANCE.putNews(imageUrl, originImg);
                return true;
            }
        });
    }

    private class UpdateImageView extends AsyncTask<String, Void, Void>{

        @Override
        protected Void doInBackground(String... strings) {
            originImg = new ImageLoader().getBitmapByUrl(strings[0], false);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(BaseApplication.getmContext(), "加载原图", Toast.LENGTH_SHORT).show();
            Log.d("TAG", "onPostExecute: " + "加载原图");
            bigImage.setImageBitmap(originImg);
        }
    }
}