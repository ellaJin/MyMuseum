package leancloud.zry.mymuseum;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;

import com.avos.avoscloud.AVObject;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import leancloud.zry.mymuseum.activity.ShowExhibitDetailsActivity;

/**
 * Created by Reene on 2017/5/27.
 */

public class BannerView {
    private Context context;
    private View view;

    private final int MAX_SIZE = 5;

    public static String IMAGE_CACHE_PATH = "imageloader/Cache"; // 图片缓存路径

    private ViewPager adViewPager;
    private List<ImageView> imageViews;// 滑动的图片集合

    private List<View> dots; // 图片标题正文的那些点
    private List<View> dotList;

    private TextView tv_date;
    private TextView tv_title;
    private TextView tv_topic_from;
    private int currentItem = 0; // 当前图片的索引号
    // 定义的五个指示点
    private View dot0;
    private View dot1;
    private View dot2;
    private View dot3;
    private View dot4;

    private ScheduledExecutorService scheduledExecutorService;

    // 异步加载图片
    private ImageLoader mImageLoader;
    private DisplayImageOptions options;

    private List<AVObject> exhibits;

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            adViewPager.setCurrentItem(currentItem);
        };
    };

    public BannerView(Context context, View view, List<AVObject> list) {
        this.context = context;
        this.view = view;
        exhibits = getNewList(list);

        initImageLoader();

        // 获取图片加载实例
        mImageLoader = ImageLoader.getInstance();
        options = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.top_banner_android)
                .showImageForEmptyUri(R.drawable.top_banner_android)
                .showImageOnFail(R.drawable.top_banner_android)
                .cacheInMemory(true).cacheOnDisc(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .imageScaleType(ImageScaleType.EXACTLY).build();

        initAdData();

        startAd();
    }

    private List<AVObject> getNewList(List<AVObject> list) {
        int maxIndex = list.size();
        List<AVObject> newList = new ArrayList<>();
        for (int i = 1; i < 6; i++) {
            newList.add(list.get(maxIndex - i));
        }
        return newList;
    }

    private void initImageLoader() {
        File cacheDir = com.nostra13.universalimageloader.utils.StorageUtils
                .getOwnCacheDirectory(context.getApplicationContext(),
                        IMAGE_CACHE_PATH);

        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true).cacheOnDisc(true).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                context).defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new LruMemoryCache(12 * 1024 * 1024))
                .memoryCacheSize(12 * 1024 * 1024)
                .discCacheSize(32 * 1024 * 1024).discCacheFileCount(100)
                .discCache(new UnlimitedDiscCache(cacheDir))
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .tasksProcessingOrder(QueueProcessingType.LIFO).build();

        ImageLoader.getInstance().init(config);
    }

    private void initAdData() {
        imageViews = new ArrayList<ImageView>();

        // 点
        dots = new ArrayList<View>();
        dotList = new ArrayList<View>();
        dot0 = view.findViewById(R.id.v_dot0);
        dot1 = view.findViewById(R.id.v_dot1);
        dot2 = view.findViewById(R.id.v_dot2);
        dot3 = view.findViewById(R.id.v_dot3);
        dot4 = view.findViewById(R.id.v_dot4);
        dots.add(dot0);
        dots.add(dot1);
        dots.add(dot2);
        dots.add(dot3);
        dots.add(dot4);

        addDynamicView();
        tv_date = (TextView) view.findViewById(R.id.tv_date);
        tv_title = (TextView) view.findViewById(R.id.tv_title);
        tv_topic_from = (TextView) view.findViewById(R.id.tv_topic_from);

        adViewPager = (ViewPager) view.findViewById(R.id.vp);
        adViewPager.setAdapter(new MyAdapter());// 设置填充ViewPager页面的适配器
        // 设置一个监听器，当ViewPager中的页面改变时调用
        adViewPager.setOnPageChangeListener(new MyPageChangeListener());

    }

    private void addDynamicView() {
        // 动态添加图片和下面指示的圆点
        // 初始化图片资源
        for (int i = 0; i < MAX_SIZE; i++) {
            ImageView imageView = new ImageView(context);
            // 异步加载图片

//            mImageLoader.displayImage(adList.get(i).getImgUrl(), imageView,
//                    options);

//            Log.d("banner",exhibits.get(i).getAVFile("image").getUrl());
            mImageLoader.displayImage(exhibits.get(i).getAVFile("image").getUrl(), imageView,
                    options);
            imageView.setScaleType(ScaleType.CENTER_CROP);
            imageViews.add(imageView);
            dots.get(i).setVisibility(View.VISIBLE);
            dotList.add(dots.get(i));
        }
    }

/*
    @Override
    protected void onResume() {
        super.onResume();
    }*/

    private void startAd() {
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        // 当Activity显示出来后，每两秒切换一次图片显示
        scheduledExecutorService.scheduleAtFixedRate(new ScrollTask(), 1, 2,
                TimeUnit.SECONDS);
    }

    private class ScrollTask implements Runnable {

        @Override
        public void run() {
            synchronized (adViewPager) {
                currentItem = (currentItem + 1) % imageViews.size();
                handler.obtainMessage().sendToTarget();
            }
        }
    }

    /*
    @Override
    protected void onStop() {
        super.onStop();
        // 当Activity不可见的时候停止切换
        scheduledExecutorService.shutdown();
    }*/

    private class MyPageChangeListener implements ViewPager.OnPageChangeListener {

        private int oldPosition = 0;

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageSelected(int position) {
            currentItem = position;

            AVObject exhibit = exhibits.get(position);
            tv_title.setText(exhibit.get("title").toString());

            String begainTime = exhibit.get("startTime").toString();
            String endTime = exhibit.get("endTime").toString();
            String date = begainTime + "-" + endTime;
            Log.d("datetime2",date);
            tv_date.setText(date);
            tv_topic_from.setText(exhibit.get("shortDescribe").toString());
            dots.get(oldPosition).setBackgroundResource(R.drawable.dot_normal);
            dots.get(position).setBackgroundResource(R.drawable.dot_focused);
            oldPosition = position;
        }
    }

    private class MyAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return MAX_SIZE;
        }

        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            ImageView iv = imageViews.get(position);
            ((ViewPager) container).addView(iv);
            // 在这个方法里面设置图片的点击事件
            iv.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // 处理跳转逻辑
                    AVObject exhibit = exhibits.get(position);
                    Intent intent = new Intent(context,ShowExhibitDetailsActivity.class);
                    intent.putExtra("exhibitId",exhibit.getObjectId());
                    //             intent.putExtra(FruitActivity.FRUIT_IMAGE_ID,fruit.getImageId());
                    context.startActivity(intent);
                }
            });
            return iv;
        }

        @Override
        public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView((View) arg2);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void restoreState(Parcelable arg0, ClassLoader arg1) {

        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(View arg0) {

        }

        @Override
        public void finishUpdate(View arg0) {

        }

    }
}
