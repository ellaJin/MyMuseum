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
import leancloud.zry.mymuseum.activity.SearchResultActivity;

/**
 * Created by Reene on 2017/5/20.
 */

public class TypeListAdapter extends RecyclerView.Adapter<TypeListAdapter.ViewHolder>{
    private List<AVObject> types;

    private Context mContext;

    public TypeListAdapter(List<AVObject> list) {
        types = list;
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        View typeView;
        ImageView typeImage;
        TextView typeName;

        public ViewHolder(View view) {
            super(view);
            typeView = view;

            typeImage = (ImageView)view.findViewById(R.id.type_image);
            typeName = (TextView)view.findViewById(R.id.type_name);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.type_cardview,parent,false);
        final ViewHolder holder = new ViewHolder(view);

        holder.typeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                AVObject type = types.get(position);
                Intent intent = new Intent(mContext,SearchResultActivity.class);

                intent.putExtra("searchType","type");
                intent.putExtra("searchString",type.get("name").toString());
                //             intent.putExtra(FruitActivity.FRUIT_IMAGE_ID,fruit.getImageId());
                mContext.startActivity(intent);
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        AVObject type = types.get(position);
        holder.typeName.setText(type.get("chineseName").toString());

        Glide.with(mContext).load(type.getAVFile("image").getUrl()).into(holder.typeImage);
    }

    @Override
    public int getItemCount() {
        return types.size();
    }
}
