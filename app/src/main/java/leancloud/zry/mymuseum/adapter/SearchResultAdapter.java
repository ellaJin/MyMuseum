package leancloud.zry.mymuseum.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVObject;
import com.bumptech.glide.Glide;

import java.util.List;

import leancloud.zry.mymuseum.R;
import leancloud.zry.mymuseum.activity.ShowExhibitDetailsActivity;
import leancloud.zry.mymuseum.activity.ShowProductDetailsActivity;

/**
 * Created by Reene on 2017/5/20.
 */

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ViewHolder>{
    private List<AVObject> results;

    private Context mContext;

    private String searchType;


    public SearchResultAdapter(List<AVObject> list,String searchType) {
        results = list;
        this.searchType = searchType;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        View resultView;
        ImageView resultImage;
        TextView resultTitle;



        public ViewHolder(View view) {
            super(view);
            resultView = view;

            resultImage = (ImageView)view.findViewById(R.id.search_image);
            resultTitle = (TextView)view.findViewById(R.id.search_title);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.search_result_cardview,parent,false);
        final ViewHolder holder = new ViewHolder(view);


        holder.resultView.setOnClickListener(new View.OnClickListener() {
            Intent intent;
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                AVObject result = results.get(position);

                if (searchType.equals("exhibit")) {
                    intent = new Intent(mContext,ShowExhibitDetailsActivity.class);
                    intent.putExtra("exhibitId",result.getObjectId());
                } else if (searchType.equals("product")){
                    intent = new Intent(mContext,ShowProductDetailsActivity.class);
                    intent.putExtra("productId",result.getObjectId());
                }

                mContext.startActivity(intent);

            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        AVObject result = results.get(position);
        holder.resultTitle.setText(result.get("title").toString());

        Glide.with(mContext).load(result.getAVFile("image").getUrl()).into(holder.resultImage);
    }

    @Override
    public int getItemCount() {
        return results.size();
    }

}
