package leancloud.zry.mymuseum.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;

import java.util.ArrayList;
import java.util.List;

import leancloud.zry.mymuseum.R;
import leancloud.zry.mymuseum.adapter.LikedAdapter;

public class ShowLikedActivity extends AppCompatActivity {
    private Toolbar toolbar;

    private Intent intent;

    private String likeType;
    private String entity;

    private RecyclerView recyclerView;

    private List<AVObject> likeList;

    private LikedAdapter likedAdapter;

    private SwipeRefreshLayout swipeRefreshLayout;

 //   private List<AVObject> newList;

    private List<String> objectsIds;

    private List<AVObject> oobjects;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("third","showlikedA");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        intent = getIntent();
        likeType = intent.getStringExtra("likeType");
        entity = intent.getStringExtra("entity");
        Log.d("third",likeType);
        likeList = new ArrayList<>();
  //      newList = new ArrayList<>();
        objectsIds = new ArrayList<>();

        toolbar = (Toolbar) findViewById(R.id.search_toolbar);
        toolbar.setTitle("我的收藏");
        toolbar.setNavigationIcon(R.mipmap.back);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }

 //       new Thread(networkTask).start();

        initData(likeType);

    }


    /**
     * 比如把MyExhibit表里的，对应user=ella的行全都加入一个新的列表
     * @param likeType
     */
    private void initData(String likeType) {
        AVQuery<AVObject> query = new AVQuery<>(likeType);

        query.whereEqualTo("user",AVUser.getCurrentUser());
        query.orderByDescending("createdAt");

        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
      //          likeList.addAll(list);
     //           handleList(list);
                loadData(list);
            }
        });
    }

    /**
     * 获得每一行数据里对应的Exhibit/Product的objectId,entity=Exhibit/Product
     * @param list
     */
    private void loadData(List<AVObject> list) {

        for (AVObject object: list) {
            objectsIds.add(object.getAVObject(entity.toLowerCase()).getObjectId());
 //           Log.d("load",object.getAVObject(entity.toLowerCase()).getObjectId());
        }

        /*
        for (String s: objectsIds){
            Log.d("loads",s);
        }*/

        getNewObjects();
    }

    private void setAdapter(List<AVObject> list) {
        recyclerView = (RecyclerView)findViewById(R.id.search_recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(layoutManager);

        //   intent.putExtra("likedTypeAdapter",likeType);
        likedAdapter  = new LikedAdapter(list,likeType,entity);
        recyclerView.setAdapter(likedAdapter);

        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh_like);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh() {
                refreshFruits();
            }
        });
    }

    private void refreshFruits() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                }catch (InterruptedException e) {
                    e.printStackTrace();
                }
               runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //          initCardView(view1);
                        objectsIds.clear();
                        oobjects.clear();
                        initData(likeType);
                        likedAdapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        }).start();
    }

    /**
     * 通过一组objectId得到新的一组对应实体对象列表
     */
    private void getNewObjects() {
        oobjects = new ArrayList<>();
        // 新开线程进行网络请求
        new Thread(networkTask).start();
    //    return oobjects;
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String val = data.getString("value");
            Log.d("mylog", "请求结果为-->" + val);
            // TODO
            // UI界面的更新等相关操作
//            initData(likeType);
//
//            for (AVObject object:oobjects) {
//                Log.d("handle",object.get("title").toString());
//            }

            setAdapter(oobjects);

        }
    };

    /**
     * 网络操作相关的子线程
     */
    Runnable networkTask = new Runnable() {

        @Override
        public void run() {
            // TODO
            // 在这里进行 http request.网络请求相关操作
            Message msg = new Message();

            Bundle data = new Bundle();
            // 获得对应表的所有数据
            AVQuery<AVObject> query = new AVQuery<>(entity);

            try {
                // 通过id把需要的实体筛选出来，加到新的列表
                for (String s : objectsIds) {
                    oobjects.add(query.get(s));
                }

            }catch (AVException e) {
                e.printStackTrace();
            }
            data.putString("value", "请求结果");
            msg.setData(data);
            handler.sendMessage(msg);
        }
    };

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
