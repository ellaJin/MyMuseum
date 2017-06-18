package leancloud.zry.mymuseum.activity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import leancloud.zry.mymuseum.MainActivity;
import leancloud.zry.mymuseum.R;

public class ChangePersonalImageActivity extends AppCompatActivity {
    private Button select_button;

    private ImageView select_image;

    private Button sumbmit;

    private Toolbar toolbar;

    private byte[] mImageBytes = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_personal_image);

        toolbar = (Toolbar) findViewById(R.id.change_img_toolbar);
        toolbar.setNavigationIcon(R.mipmap.back);

        select_button = (Button)findViewById(R.id.button_select_publish);
        select_image = (ImageView)findViewById(R.id.imageview_select_publish);
        sumbmit = (Button)findViewById(R.id.img_select_submit) ;
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }

        select_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 42);
            }
        });

        sumbmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("change","done1");
                if (mImageBytes == null) {
                    Toast.makeText(ChangePersonalImageActivity.this, "请选择一张照片", Toast.LENGTH_SHORT).show();
                    return;
                }

                AVQuery<AVObject> query = new AVQuery<>("PersonalImage");
                query.whereEqualTo("user",AVUser.getCurrentUser());
                query.findInBackground(new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException e) {
                        // 修改图片
                        if (list.size() > 0) {
                            AVObject change = list.get(0);
                            change.put("image",new AVFile("myImage", mImageBytes));
                            change.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(AVException e) {
                                    if (e == null){
                                        ChangePersonalImageActivity.this.finish();
                                        Intent intent = new Intent(ChangePersonalImageActivity.this, MainActivity.class);
                                        startActivity(intent);
                                    }else {
                                        Log.d("change","修改失败");
                                        e.printStackTrace();
                                    }
                                }
                            });

                        }
                        // 创建一行
                        else {
                            AVObject addPic = new AVObject("PersonalImage");
                            addPic.put("user",AVUser.getCurrentUser());
                            addPic.put("image",new AVFile("myImage", mImageBytes));
                            addPic.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(AVException e) {
                                    if (e == null){
                                        ChangePersonalImageActivity.this.finish();
                                        Intent intent = new Intent(ChangePersonalImageActivity.this, MainActivity.class);
                                        startActivity(intent);
                                    }else {
                                        Log.d("change","修改失败");
                                        e.printStackTrace();
                                    }
                                }
                            });
                        }

                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 42 && resultCode == RESULT_OK) {
            try {
                select_image.setImageBitmap(MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData()));
                mImageBytes = getBytes(getContentResolver().openInputStream(data.getData()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, len);
        }
        return byteArrayOutputStream.toByteArray();
    }

    @Override
    protected void onPause() {
        super.onPause();
        AVAnalytics.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        AVAnalytics.onResume(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
