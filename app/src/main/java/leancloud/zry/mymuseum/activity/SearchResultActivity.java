package leancloud.zry.mymuseum.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;

import java.util.ArrayList;
import java.util.List;

import leancloud.zry.mymuseum.R;
import leancloud.zry.mymuseum.adapter.SearchResultAdapter;

public class SearchResultActivity extends AppCompatActivity {
    // 搜索的类型 ：exhibit: 搜索展览 product: 搜索展品 type :点击种类进去显示的页面
    private String searchType;

    private RecyclerView searchRecyclerView;

    private SearchResultAdapter searchResultAdapter;

    private String adapterString;

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        //     Log.d("produtdetails","produtdetails");
        Toolbar toolbar = (Toolbar) findViewById(R.id.search_toolbar);
        toolbar.setTitle("搜索结果");
        toolbar.setNavigationIcon(R.mipmap.back);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }

        intent = getIntent();
        searchType = intent.getStringExtra("searchType");

        initData(searchType);



    }

    private void initData(String searchType) {
        switch (searchType) {
            case "type":
                String typeName = intent.getStringExtra("searchString");
                searchType(typeName);
                break;

            case "product":
                String productTitle = intent.getStringExtra("searchString");
                searchProduct(productTitle);
                break;
            case "exhibit":
                String exhibitTitle = intent.getStringExtra("searchString");
                searchExhibit(exhibitTitle);
                break;

        }
    }

    // 每种类型下的展品
    private void searchType(String searchString) {
        List<AVObject> lists = new ArrayList<>();
        AVQuery<AVObject> query = new AVQuery<>("Product");

        query.whereEqualTo("type", searchString);

        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                adapterString = "product";
                loadData(list);
            }
        });

    }

    private void searchProduct(String searchString) {
        List<AVObject> lists = new ArrayList<>();
        AVQuery<AVObject> query = new AVQuery<>("Product");

        query.whereContains("title", searchString);

        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                adapterString = "product";
                loadData(list);
            }
        });
    }

    private void searchExhibit(String searchString) {
        List<AVObject> lists = new ArrayList<>();
        AVQuery<AVObject> query = new AVQuery<>("Exhibit");

        query.whereContains("title", searchString);

        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                adapterString = "exhibit";
                loadData(list);
            }
        });
    }

    private void loadData(List<AVObject> list) {
        searchRecyclerView = (RecyclerView)findViewById(R.id.search_recycler_view);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 1);
        searchRecyclerView.setLayoutManager(layoutManager);

        intent.putExtra("searchType",searchType);
        searchResultAdapter  = new SearchResultAdapter(list,adapterString);
        searchRecyclerView.setAdapter(searchResultAdapter);
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


