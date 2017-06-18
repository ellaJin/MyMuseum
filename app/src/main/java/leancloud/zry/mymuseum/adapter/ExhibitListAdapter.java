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

/**
 * Created by Reene on 2017/5/18.
 */

public class ExhibitListAdapter extends RecyclerView.Adapter<ExhibitListAdapter.ViewHolder>{

    private List<AVObject> exhibits;

    private Context mContext;

    public ExhibitListAdapter(List<AVObject> list) {
        exhibits = list;
    }


    static class ViewHolder extends RecyclerView.ViewHolder{
        View exhibitView;
        ImageView exhibitImage;
        TextView exhibitTitle;
        TextView exhibitShortDescribe;

        public ViewHolder(View view) {
            super(view);
            exhibitView = view;

            exhibitImage = (ImageView)view.findViewById(R.id.exhibit_image);
            exhibitTitle = (TextView)view.findViewById(R.id.exhibit_tittle);
            exhibitShortDescribe = (TextView)view.findViewById(R.id.exhibit_shortDescribe);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.exhibit_cardview,parent,false);
        final ViewHolder holder = new ViewHolder(view);
        holder.exhibitView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                AVObject exhibit = exhibits.get(position);
                Intent intent = new Intent(mContext,ShowExhibitDetailsActivity.class);
                intent.putExtra("exhibitId",exhibit.getObjectId());
   //             intent.putExtra(FruitActivity.FRUIT_IMAGE_ID,fruit.getImageId());
                mContext.startActivity(intent);

        //        Toast.makeText(mContext, "you clicked view " + exhibit.get("title").toString(), Toast.LENGTH_SHORT).show();
            }
        });

        holder.exhibitImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                AVObject exhibit = exhibits.get(position);
                Intent intent = new Intent(mContext,ShowExhibitDetailsActivity.class);
                intent.putExtra("exhibitId",exhibit.getObjectId());
                mContext.startActivity(intent);
       //         Toast.makeText(mContext, "you clicked image " + exhibit.get("image").toString(), Toast.LENGTH_SHORT).show();
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        AVObject exhibit = exhibits.get(position);
  //      Fruit fruit = fruitList.get(position);
  //      holder.fruitImage.setImageResource(fruit.getImageId());
  //      holder.fruitName.setText(fruit.getName());
        holder.exhibitTitle.setText(exhibit.get("title").toString());
        holder.exhibitShortDescribe.setText(exhibit.get("shortDescribe").toString());

       /* Picasso.with(mContext).load(mList.get(position).getAVFile("image") == null ?
                "www" : mList.get(position).getAVFile("image").getUrl()).transform(new RoundedTransformation(9, 0)).into(holder.mPicture);*/

  //      Glide.with(mContext).load(exhibit.get("image").toString()).into(holder.exhibitImage);
        Glide.with(mContext).load(exhibit.getAVFile("image").getUrl()).into(holder.exhibitImage);
    }

    @Override
    public int getItemCount() {
        return exhibits.size();
    }

}
