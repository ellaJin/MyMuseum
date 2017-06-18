package leancloud.zry.mymuseum.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.DeleteCallback;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import leancloud.zry.mymuseum.R;
import leancloud.zry.mymuseum.activity.ShowExhibitDetailsActivity;
import leancloud.zry.mymuseum.activity.ShowLikedActivity;
import leancloud.zry.mymuseum.activity.ShowProductDetailsActivity;

/**
 * Created by Reene on 2017/5/22.
 */

public class LikedAdapter extends RecyclerView.Adapter<LikedAdapter.ViewHolder>{
    private List<AVObject> results;

 //   private List<AVObject> myLikeds;

    private Context mContext;

    private String likeType;

    private String entity;


    public LikedAdapter(List<AVObject> list, String searchType, final String entity) {
        results = new ArrayList<>();
  //      myLikeds = new ArrayList<>();
        results = list;
        Log.d("like",entity);

        likeType = searchType;
        this.entity = entity;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        View likedView;
        ImageView likedImage;
        TextView likedTitle;
        ImageView likeButton;


        public ViewHolder(View view) {
            super(view);
            likedView = view;

            likedImage = (ImageView)view.findViewById(R.id.liked_image);
            likedTitle = (TextView)view.findViewById(R.id.liked_title);
            likeButton = (ImageView)view.findViewById(R.id.liked_button);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.show_liked_cardview,parent,false);
        final ViewHolder holder = new ViewHolder(view);

  //      Log.d("result","check");

        /*
        for (AVObject object: results) {
            Log.d("result", object.get("title").toString());
            Log.d("result", object.get("describe").toString());
        }*/

        // 展示对应的展览/展品详情
        holder.likedImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                AVObject result = results.get(position);
                if (entity.equals("Product")) {
                    Intent intent = new Intent(mContext, ShowProductDetailsActivity.class);
                    intent.putExtra("productId",result.getObjectId());
                    mContext.startActivity(intent);
         //           ((Activity)mContext).finish();
                } else if (entity.equals("Exhibit")) {
                    Intent intent = new Intent(mContext, ShowExhibitDetailsActivity.class);
                    intent.putExtra("exhibitId",result.getObjectId());
                    mContext.startActivity(intent);
            //        ((Activity)mContext).finish();
                }
            }
        });

        holder.likedTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                AVObject result = results.get(position);
                if (entity.equals("Product")) {
                    Intent intent = new Intent(mContext, ShowProductDetailsActivity.class);
                    intent.putExtra("productId",result.getObjectId());
                    ((Activity)mContext).finish();
         //           mContext.startActivity(intent);
                } else if (entity.equals("Exhibit")) {
                    Intent intent = new Intent(mContext, ShowExhibitDetailsActivity.class);
                    intent.putExtra("exhibitId",result.getObjectId());
                    ((Activity)mContext).finish();
          //          mContext.startActivity(intent);
                }
            }
        });

        // 取消收藏
        holder.likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                // exhibitObject and user  (MyExhibit Class)
                AVObject result = results.get(position);

                AVQuery<AVObject> checkEntity = new AVQuery<>(likeType);
     //           Log.d("likebutton",likeType);

                // 根据id创建实体
                AVObject myObject = AVObject.createWithoutData(entity, result.getObjectId());
                checkEntity.whereEqualTo(entity.toLowerCase(), myObject);

                AVQuery<AVObject> checkUser = new AVQuery<>(likeType);
                checkUser.whereEqualTo("user", AVUser.getCurrentUser());

                AVQuery<AVObject> deleteQuery = AVQuery.and(Arrays.asList(checkEntity, checkUser));

                deleteQuery.deleteAllInBackground(new DeleteCallback() {
                    @Override
                    public void done(AVException e) {
                        if (e == null) {
                   //         Log.d("productsss","删除成功");
                            Toast.makeText(mContext, "取消收藏了！", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(mContext, ShowLikedActivity.class);
                            intent.putExtra("likeType",likeType);
                            intent.putExtra("entity",entity);
                            ((Activity)mContext).finish();
                            mContext.startActivity(intent);

                        } else {
                            Log.d("productsss","删除失败！");
                        }

                    }
                });

            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
 //       Log.d("likeo22","create");
        AVObject result = results.get(position);
        holder.likedTitle.setText(result.get("title").toString());

        Glide.with(mContext).load(result.getAVFile("image").getUrl()).into(holder.likedImage);
    }

    @Override
    public int getItemCount() {
        return results.size();
    }
}
