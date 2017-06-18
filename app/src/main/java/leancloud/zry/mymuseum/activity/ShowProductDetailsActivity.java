package leancloud.zry.mymuseum.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
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

public class ShowProductDetailsActivity extends AppCompatActivity {
    private String productId;

    private ImageView productImage;
    private TextView productTitle;
    private TextView productMaker;
    private TextView productMaterial;
    private TextView productSize;
    private TextView productDescribe;

    private Toolbar toolbar;

    private boolean productIsLiked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        productId = intent.getStringExtra("productId");
        setContentView(R.layout.activity_show_product);

        toolbar = (Toolbar) findViewById(R.id.product_toolbar);
        checkIfLiked();
        toolbar.setTitle("展品详情");
        toolbar.setNavigationIcon(R.mipmap.back);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }


        bindView();
        initData(productId);
    }

    private void bindView() {
        productImage = (ImageView)findViewById(R.id.product_detail_image);
        productTitle = (TextView)findViewById(R.id.product_detail_tittle);
        productMaker = (TextView)findViewById(R.id.product_detail_maker);
        productMaterial = (TextView)findViewById(R.id.product_detail_material);
        productSize = (TextView)findViewById(R.id.product_detail_size);
        productDescribe = (TextView)findViewById(R.id.product_detail_describe);
    }
    private void initData(String productId) {
        AVQuery<AVObject> avQuery = new AVQuery<>("Product");
        //     final AVObject exhibit = new AVObject();

        avQuery.getInBackground(productId, new GetCallback<AVObject>() {
            @Override
            public void done(AVObject avObject, AVException e) {
       //         Log.d("exhibit", (String) avObject.get("title"));
                loadData(avObject);
            }
        });
    }


    private void loadData(AVObject product) {
        /*
        exhibitContentText.setText(exhibit.get("describe").toString());
        exhibitTitle.setText(exhibit.get("title").toString());
        exhibitShortDescribe.setText(exhibit.get("shortDescribe").toString());*/

        productTitle.setText(product.get("title").toString());
//        Toast.makeText(this, product.get("title").toString(), Toast.LENGTH_SHORT).show();
        productDescribe.setText(product.get("describe").toString());

        if (product.get("maker")!= null) {
            productMaker.setText(product.get("maker").toString());
        }
        if (product.get("material") != null) {
            productMaterial.setText(product.get("material").toString());
        }
        if (product.get("size") != null) {
            productSize.setText(product.get("size").toString());
        }

        final  String imageUrl = product.getAVFile("image").getUrl();
        Glide.with(this).load(imageUrl).into(productImage);
        //     Glide.with(this).load(fruitImageId).into(exhibitView);
        productImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(ShowProductDetailsActivity.this,ShowBigImageActivity.class);
                intent1.putExtra("imageUrl",imageUrl);
                startActivity(intent1);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.like_product:
                if (productIsLiked) {
                    deleteLiked(item);
                } else {
                    addLiked(item);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkIfLiked() {
        final AVQuery<AVObject> checkProduct = new AVQuery<>("MyProducts");
        AVObject myProduct = AVObject.createWithoutData("Product", productId);
        checkProduct.whereEqualTo("product", myProduct);

        final AVQuery<AVObject> checkUser = new AVQuery<>("MyProducts");
        checkUser.whereEqualTo("user", AVUser.getCurrentUser());

        AVQuery<AVObject> query = AVQuery.and(Arrays.asList(checkProduct, checkUser));

        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    if (list.size() > 0) {
                        setIslieked();
                    } else {
                        Log.d("product","空");
                    }
                } else {
                    Log.d("product","出错！");
                }

            }
        });
    }

    private void setIslieked() {
        Log.d("productsss","set,true");
        productIsLiked = true;
        toolbar.getMenu().getItem(0).setIcon(R.mipmap.like_yes);

    }

    private void deleteLiked(final MenuItem item) {
        AVQuery<AVObject> checkProduct = new AVQuery<>("MyProducts");

        AVObject myProdut = AVObject.createWithoutData("Product", productId);
        checkProduct.whereEqualTo("product", myProdut);
        AVQuery<AVObject> checkUser = new AVQuery<>("MyProducts");
        checkUser.whereEqualTo("user", AVUser.getCurrentUser());

        AVQuery<AVObject> query = AVQuery.and(Arrays.asList(checkProduct, checkUser));

        query.deleteAllInBackground(new DeleteCallback() {
            @Override
            public void done(AVException e) {
                if (e == null) {
                    Log.d("productsss","删除成功");
                    item.setIcon(R.mipmap.like_no);
                    Toast.makeText(ShowProductDetailsActivity.this, "取消收藏了！", Toast.LENGTH_SHORT).show();
                    productIsLiked = false;
                } else {
                    Log.d("productsss","删除失败！");
                }

            }
        });

    }

    private void addLiked(MenuItem item) {
        AVObject myProduct = AVObject.createWithoutData("Product", productId);
        item.setIcon(R.mipmap.like_yes);

        AVObject likeProduct = new AVObject("MyProducts");

        // 设置关联
        likeProduct.put("product", myProduct);
        likeProduct.put("user", AVUser.getCurrentUser());

        // 保存选课表对象
        likeProduct.saveInBackground();
        Toast.makeText(this, "收藏成功！", Toast.LENGTH_SHORT).show();
        productIsLiked = true;
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
