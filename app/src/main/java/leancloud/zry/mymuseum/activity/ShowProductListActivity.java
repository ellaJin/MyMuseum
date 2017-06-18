package leancloud.zry.mymuseum.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.avos.avoscloud.AVAnalytics;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;

import java.util.ArrayList;
import java.util.List;

import leancloud.zry.mymuseum.R;
import leancloud.zry.mymuseum.adapter.ProductListAdapter;

public class ShowProductListActivity extends AppCompatActivity {

    private List<AVObject> producList;

    private String exhibitId;

    private RecyclerView recyclerView;

    private ProductListAdapter productListAdapter;

    private EditText searchEditText;

    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_product_list);

        Intent intent = getIntent();
        exhibitId = intent.getStringExtra("exhibitId");
        searchEditText = (EditText)findViewById(R.id.search_product_in_exhibit);
        searchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {//EditorInfo.IME_ACTION_SEARCH、EditorInfo.IME_ACTION_SEND等分别对应EditText的imeOptions属性
                    String searchText = searchEditText.getText().toString();
                    search(searchText);
                }
                return false;
            }
        });

        initData();
    }

    private void initData() {
        producList = new ArrayList<>();
        // 假设 GuangDong 的 objectId 为 56545c5b00b09f857a603632
        AVObject exhibit = AVObject.createWithoutData("Exhibit", exhibitId);

        AVQuery<AVObject> query = new AVQuery<>("Product");

        query.whereEqualTo("dependent", exhibit);

        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
               loadData(list);
                producList = list;
            }
        });
    }

    private void loadData(List<AVObject> products) {
        recyclerView = (RecyclerView)findViewById(R.id.product_list_recycler_view);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        productListAdapter  = new ProductListAdapter(products);
        recyclerView.setAdapter(productListAdapter);

        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe_refresh_product_list);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh() {
                refreshFruits();
            }
        });
    }

    // 查询标题包含输入字段的展品
    private void search(String searchText) {
       List<AVObject> searchResults = new ArrayList<>();
        for (AVObject product : producList) {
            String temp = product.get("title").toString();

            if (temp.contains(searchText)) {
                searchResults.add(product);
            }
        }

        // 更新数据
        loadData(searchResults);
    }

    // 刷新数据
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
                        initData();
                        productListAdapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        }).start();
        Log.d("TestFragment","refresh");
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
