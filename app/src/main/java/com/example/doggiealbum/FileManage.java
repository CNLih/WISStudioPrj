package com.example.doggiealbum;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Trace;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public enum FileManage {
    INSTANCE;

    private SQLiteDatabase db;
    private static String DEFAULT_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath();

    FileManage() {

        SQLiteOpenHelper dbHelper = new MyDatabaseHelper(BaseApplication.getmContext(),
                "ALBUM.db", null, 1);
        db = dbHelper.getReadableDatabase();
    }

    private Handler handler = new Handler(){
        public void handleMessage(Message msg){
            Toast.makeText(BaseApplication.getmContext(), msg.obj.toString(), Toast.LENGTH_SHORT).show();
        }
    };

    public boolean fileIsExists(String strFile)
    {
        try {
            File f=new File(strFile);
            if(!f.exists()) {
                return false;
            }
        }
        catch (Exception e) {
            return false;
        }
        return true;
    }

    //return : String[2], 0 -- url, 1 -- path
    public ArrayList<String[]> getAllNews(){
        Cursor cursor = db.query("Album", null, null, null, null, null, null);
        ArrayList<String[]> rtn = new ArrayList<>();
        if(cursor.moveToFirst()){
            do{
                String url = cursor.getString(cursor.getColumnIndex("url"));
                String path = cursor.getString(cursor.getColumnIndex("path"));
                String[] news = new String[2];
                news[0] = url;
                news[1] = path;
                if(fileIsExists(path)){
                    rtn.add(news);
                }else{
                    db.delete("Album", "path = ?", new String[]{path});
                }
            }while (cursor.moveToNext());
        }
        cursor.close();
        for(int i = 0; i < rtn.size(); i ++){
            Log.d("TAG", "getAllNews: " + rtn.get(i)[0] + rtn.get(i)[1]);
        }
        return rtn;
    }

    public void putNews(String url, Bitmap originBitmap) {
        //db存放路径
        ContentValues values = new ContentValues();
        //获取db中的最大id值+1作为新的id
        Cursor cursor = db.rawQuery("select max(id) AS maxId from Album", null);
        final int[] curId = new int[2];
        //curId[0] = 0;   //by default
        if(cursor !=null && cursor.moveToFirst() && cursor.getCount()>0) {
            curId[0] = cursor.getInt(cursor.getColumnIndex("maxId"));
            Log.d("TAG", "putNews: has" + curId[0]);
        }
        cursor.close();
        values.put("url", url);
        values.put("path", DEFAULT_PATH + "/" +(curId[0] + 1) + ".jpg");

        if(db.insert("Album", null, values) == -1){
            Log.d("TAG", "putNews: " + "already exit");
            Toast.makeText(BaseApplication.getmContext(), "图片已经存在于目录中", Toast.LENGTH_SHORT).show();
            return ;
        }
        Log.d("TAG", "putNews: put into db successfully");
        //保存图片到物理位置
        new Thread(() -> {
            Bitmap bitmap;
            if(originBitmap == null){
                bitmap = LruCacheImg.INSTANCE.mMemoryCache.get(url);
            }
            else {
                bitmap = originBitmap;
            }
            File file = new File(DEFAULT_PATH + "/" +(curId[0] + 1) + ".jpg");
            Message message = new Message();
            try{
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                fileOutputStream.flush();  //关闭写入流
                fileOutputStream.close();
                message.what = 1;
                message.obj = DEFAULT_PATH + "/" +(curId[0] + 1) + ".jpg";
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                message.what = 2;
                message.obj = e.getMessage();
            } catch (IOException e) {
                e.printStackTrace();
                message.what = 2;
                message.obj = e.getMessage();
            }
            handler.sendMessage(message);
        }).start();
    }

    //先加载本地图片
    //public Bitmap putNews

    public class MyDatabaseHelper extends SQLiteOpenHelper{
        public static final String CREATE_TABLE = "create table Album("
                + "id integer primary key autoincrement, "
                + "url text unique, "
                + "path text)";
        private Context mContext;

        public MyDatabaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
            mContext = context;
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(CREATE_TABLE);
            Log.d("TAG", "onCreate: CREATE TABLE!");
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        }
    }
}
