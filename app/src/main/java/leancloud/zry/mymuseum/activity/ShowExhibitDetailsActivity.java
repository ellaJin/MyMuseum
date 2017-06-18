package leancloud.zry.mymuseum.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.DeleteCallback;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.GetCallback;
import com.bumptech.glide.Glide;

import java.util.Arrays;
import java.util.List;

import leancloud.zry.mymuseum.R;

public class ShowExhibitDetailsActivity extends AppCompatActivity {
    private ImageView exhibitView;
    private  TextView exhibitContentText;
    private  TextView exhibitTitle;
    private  TextView exhibitShortDescribe;

    // 显示的四张（或小于四张）图片和文字

    private ImageView firstImage;
    private ImageView secondImage;
    private ImageView thirdImage;
    private ImageView forthImage;

    private TextView firstText;
    private TextView secondText;
    private TextView thirdText;
    private TextView dateTime;

    private FloatingActionButton like;

//    private Context mContext;

    // 有几张小图片
    private int count = 1;

    //最多三张
    private String firstProductId;
    private String secondProductId;
    private String thirdProductId;

    private  String exhibitId;

    private boolean isLiked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exhibit_main);

        Intent intent = getIntent();
        exhibitId = intent.getStringExtra("exhibitId");
    //    Toast.makeText(this,exhibitId , Toast.LENGTH_SHORT).show();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        // 初始化控件
        exhibitView = (ImageView) findViewById(R.id.exhibit_main_image);
        exhibitContentText = (TextView) findViewById(R.id.exhibit_content_text);
        exhibitTitle = (TextView)findViewById(R.id.exhibit_main_title);
        exhibitShortDescribe = (TextView)findViewById(R.id.exhibit_main_shortDescribr);
        like = (FloatingActionButton)findViewById(R.id.like_exhibit);
        dateTime = (TextView)findViewById(R.id.exhibit_time);

        firstImage = (ImageView)findViewById(R.id.first_image);
        secondImage = (ImageView)findViewById(R.id.second_image);
        thirdImage = (ImageView)findViewById(R.id.third_image);
        forthImage = (ImageView)findViewById(R.id.show_moreimages);
        firstText = (TextView)findViewById(R.id.first_text);
        secondText = (TextView)findViewById(R.id.second_text);
        thirdText = (TextView)findViewById(R.id.third_text);


        // 动态改变fabButton的图案
        checkIfLiked();
        /*
        if (isLiked) {
            Log.d("exhibitsss","settrue");
            like.setImageResource(R.mipmap.like_yes);
        } else {
            Log.d("exhibitsss","setno");
        }*/
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        collapsingToolbar.setTitle("展览详情");

        initData(exhibitId);
        setClick();
    }

    private void initData(String exhibitId) {
        AVQuery<AVObject> avQuery = new AVQuery<>("Exhibit");
  //     final AVObject exhibit = new AVObject();

        avQuery.getInBackground(exhibitId, new GetCallback<AVObject>() {
            @Override
            public void done(AVObject avObject, AVException e) {
                Log.d("exhibit", (String) avObject.get("title"));
                loadData(avObject);
            }
        });

    }

    private void loadData(AVObject exhibit) {
        exhibitContentText.setText(exhibit.get("describe").toString());
        exhibitTitle.setText(exhibit.get("title").toString());
        exhibitShortDescribe.setText(exhibit.get("shortDescribe").toString());
        Glide.with(this).load(exhibit.getAVFile("image").getUrl()).into(exhibitView);

        String begainTime = exhibit.get("startTime").toString();
        String endTime = exhibit.get("endTime").toString();
        String date = begainTime + "-" + endTime;
        Log.d("datetime",date);
        dateTime.setText(date);
        //     Glide.with(this).load(fruitImageId).into(exhibitView);

        getContainImages(exhibit);
    }

    private void getContainImages(AVObject exhibit) {

        AVQuery<AVObject> query = new AVQuery<>("Product");

        query.whereEqualTo("dependent", exhibit);

        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                for (AVObject product : list) {
                    Log.d("product", product.get("title").toString());
                    loadContainImages(list);
                }
            }
        });
    }

    // 设置显示的按钮控件 略麻烦
    private void loadContainImages(List<AVObject> list) {
        // 只有一张图
        if (list.size() == 1) {
            // 获得指定的图的id
            firstProductId = list.get(0).getObjectId();
            firstText.setText(list.get(0).get("title").toString());
            Glide.with(this).load(list.get(0).getAVFile("image").getUrl()).into(firstImage);
            secondImage.setImageResource(R.mipmap.more);


            firstImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ShowExhibitDetailsActivity.this,ShowProductDetailsActivity.class);
                    intent.putExtra("productId",firstProductId);
                    startActivity(intent);
                }
            });
            secondImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ShowExhibitDetailsActivity.this,ShowProductListActivity.class);
                    intent.putExtra("exhibitId",exhibitId);
                    startActivity(intent);
                }
            });

        } else if (list.size() == 2) {
            firstText.setText(list.get(0).get("title").toString());
            Glide.with(this).load(list.get(0).getAVFile("image").getUrl()).into(firstImage);
            secondText.setText(list.get(1).get("title").toString());
            Glide.with(this).load(list.get(1).getAVFile("image").getUrl()).into(secondImage);
            thirdImage.setImageResource(R.mipmap.more);

            firstProductId = list.get(0).getObjectId();
            secondProductId = list.get(1).getObjectId();

            firstImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ShowExhibitDetailsActivity.this,ShowProductDetailsActivity.class);
                    intent.putExtra("productId",firstProductId);
                    startActivity(intent);
                }
            });
            secondImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ShowExhibitDetailsActivity.this,ShowProductDetailsActivity.class);
                    intent.putExtra("productId",secondProductId);
                    startActivity(intent);
                }
            });
            thirdImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ShowExhibitDetailsActivity.this,ShowProductListActivity.class);
                    intent.putExtra("exhibitId",exhibitId);
                    startActivity(intent);
                }
            });
            count = 2;
        } else {
            firstText.setText(list.get(0).get("title").toString());
            Glide.with(this).load(list.get(0).getAVFile("image").getUrl()).into(firstImage);
            secondText.setText(list.get(1).get("title").toString());
            Glide.with(this).load(list.get(1).getAVFile("image").getUrl()).into(secondImage);
            thirdText.setText(list.get(2).get("title").toString());
            Glide.with(this).load(list.get(2).getAVFile("image").getUrl()).into(thirdImage);
            forthImage.setImageResource(R.mipmap.more);

            firstProductId = list.get(0).getObjectId();
            secondProductId = list.get(1).getObjectId();
            thirdProductId = list.get(2).getObjectId();

            firstImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ShowExhibitDetailsActivity.this,ShowProductDetailsActivity.class);
                    intent.putExtra("productId",firstProductId);
                    startActivity(intent);
                }
            });
            secondImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ShowExhibitDetailsActivity.this,ShowProductDetailsActivity.class);
                    intent.putExtra("productId",secondProductId);
                    startActivity(intent);
                }
            });
            thirdImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ShowExhibitDetailsActivity.this,ShowProductDetailsActivity.class);
                    intent.putExtra("productId",thirdProductId);
                    startActivity(intent);
                }
            });
            forthImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(ShowExhibitDetailsActivity.this,ShowProductListActivity.class);
                    intent.putExtra("exhibitId",exhibitId);
                    startActivity(intent);
                }
            });
            count = 3;
        }
    }

    private void setClick() {
        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 还没被收藏
                if (!isLiked) {
                    // 创建一个exhibit对象
                    Log.d("exhibitsss", "add");
                    addLiked();
                }
                else {
                    // 删除一个like对象
                    Log.d("exhibitsss","delete");
                    deleteLiked();
                }

            }
        });
    }

    private void addLiked() {
        AVObject myExhibit = AVObject.createWithoutData("Exhibit", exhibitId);
        like.setImageResource(R.mipmap.like_yes);

        AVObject likeExhibit = new AVObject("MyExhibits");// 选课表对象

        // 设置关联
        likeExhibit.put("exhibit", myExhibit);
        likeExhibit.put("user", AVUser.getCurrentUser());

        // 保存选课表对象
        likeExhibit.saveInBackground();
        Toast.makeText(this, "收藏成功！", Toast.LENGTH_SHORT).show();
        isLiked = true;
    }

    private void deleteLiked() {
        final AVQuery<AVObject> checkExhibit = new AVQuery<>("MyExhibits");

        AVObject myExhibit = AVObject.createWithoutData("Exhibit", exhibitId);
        checkExhibit.whereEqualTo("exhibit", myExhibit);
        /*
        checkExhibit.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    if (list.size() > 0) {
                        Log.d("exhibitsss","find!");
                    }else {
                        Log.d("exhibitsss","no find");
                    }
                }
            }
        });*/

        final AVQuery<AVObject> checkUser = new AVQuery<>("MyExhibits");
        checkUser.whereEqualTo("user", AVUser.getCurrentUser());
/*
        checkUser.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    if (list.size() > 0) {
                        Log.d("exhibitsss","find!u");
                    }else {
                        Log.d("exhibitsss","no findu");
                    }
                }
            }
        });*/

        AVQuery<AVObject> query = AVQuery.and(Arrays.asList(checkExhibit, checkUser));

        Log.d("exhibitsss","nnn");

        query.deleteAllInBackground(new DeleteCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    Log.d("exhibitsss","删除成功");
                    like.setImageResource(R.mipmap.like_no);
                    Toast.makeText(ShowExhibitDetailsActivity.this, "取消收藏了！", Toast.LENGTH_SHORT).show();
                    isLiked = false;
                } else {
                    Log.d("exhibitsss","删除失败！");
                }

            }
        });

    }

    private void checkIfLiked() {
        Log.d("exhibitsss","check");
/*
        HandleLiked handleLiked = new HandleLiked();
        if (handleLiked.checkIfLiked("MyExhibits",exhibitId)) {
            setIslieked();
        }*/

        final AVQuery<AVObject> checkExhibit = new AVQuery<>("MyExhibits");
        AVObject myExhibit = AVObject.createWithoutData("Exhibit", exhibitId);
        checkExhibit.whereEqualTo("exhibit", myExhibit);

        final AVQuery<AVObject> checkUser = new AVQuery<>("MyExhibits");
        checkUser.whereEqualTo("user", AVUser.getCurrentUser());

        AVQuery<AVObject> query = AVQuery.and(Arrays.asList(checkExhibit, checkUser));

        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (list.size() > 0) {
                  setIslieked();
                }
            }
        });
        /*
       if (HandleLiked.checkIfLiked("MyExhibits",exhibitId)){
           setIslieked();
       }*/
    }

    private void setIslieked() {
        Log.d("exhibitsss","set,true");
        isLiked = true;
        like.setImageResource(R.mipmap.like_yes);
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
}
