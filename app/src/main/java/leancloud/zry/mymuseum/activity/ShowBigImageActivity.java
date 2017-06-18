package leancloud.zry.mymuseum.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import leancloud.zry.mymuseum.R;

/**
 * Created by Reene on 2017/5/29.
 */

public class ShowBigImageActivity extends AppCompatActivity {
    private ImageView bigImage;

    private Intent intent;

    private String imageUrl;

    private Bitmap savePic;


    protected void onCreate(final Bundle savedInstanceState) {
        setContentView(R.layout.show_big_image);
        super.onCreate(savedInstanceState);


        bigImage = (ImageView) findViewById(R.id.bigImage);

        intent = getIntent();
        imageUrl = intent.getStringExtra("imageUrl");
        Glide.with(this).load(imageUrl).into(bigImage);
        Glide.with(this).load(imageUrl).asBitmap().into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                savePic = resource;
            }
        });


        bigImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowBigImageActivity.this.finish();
            }
        });

        bigImage.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ShowBigImageActivity.this);
                builder.setItems(new String[]{"保存图片"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
             //           saveCroppedImage(savePic);
                        /*
                        saveImage(savePic);
                        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        File file = saveImage(savePic);
                        Uri uri = Uri.fromFile(file);
                        intent.setData(uri);
                        sendBroadcast(intent);*/
                        /*
                        final String saveAs = "/storage/emulated/0/testtest" + System.currentTimeMillis() + "_add.png";
                        Uri contentUri = Uri.fromFile(new File(saveAs));
                        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,contentUri);
                        sendBroadcast(mediaScanIntent);
                        Uri uri = mediaScanIntent.getData();
                        String path = uri.getPath();
                        String externalStoragePath = Environment.getExternalStorageDirectory().getPath();
                        Log.i("LOGTAG", "Androidyue onReceive intent= " + mediaScanIntent
                                + ";path=" + path + ";externalStoragePath=" +
                                externalStoragePath);*/


                        saveImageToGallery(ShowBigImageActivity.this,savePic);

                        Toast.makeText(ShowBigImageActivity.this, "保存成功！", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();
                return true;
            }
        });
    }

    public  void saveImageToGallery(Context context, Bitmap bmp) {
        // 首先保存图片
        //内置存储
        String externalStoragePath = Environment.getExternalStorageDirectory().getPath();

        File appDir = new File(externalStoragePath, "MyMuseum");
        Uri contentUri = Uri.fromFile(appDir);



        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

/*
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,contentUri);
        sendBroadcast(mediaScanIntent);
        Uri uri = mediaScanIntent.getData();
        String path = uri.getPath();
        Log.i("LOGTAG", "Androidyue onReceive intent= " + mediaScanIntent
                + ";path=" + path + ";externalStoragePath=" +
                externalStoragePath);*/
/*
        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), fileName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }*/
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + "/storage/emulated/0/MyMuseum/" + fileName)));
    }



}
