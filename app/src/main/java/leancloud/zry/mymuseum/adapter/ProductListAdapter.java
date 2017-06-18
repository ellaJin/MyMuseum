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
import leancloud.zry.mymuseum.activity.ShowProductDetailsActivity;

/**
 * Created by Reene on 2017/5/19.
 */

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ViewHolder>{
    private List<AVObject> products;

    private Context mContext;

    public ProductListAdapter(List<AVObject> list) {
        products = list;
    }
    static class ViewHolder extends RecyclerView.ViewHolder{
        View productView;
        ImageView productImage;
        TextView productTitle;

        public ViewHolder(View view) {
            super(view);
            productView = view;

            productImage = (ImageView)view.findViewById(R.id.product_image);
            productTitle = (TextView)view.findViewById(R.id.product_tittle);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext == null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.product_cardview,parent,false);
        final ViewHolder holder = new ViewHolder(view);

        holder.productView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                AVObject product = products.get(position);
                Intent intent = new Intent(mContext,ShowProductDetailsActivity.class);
                intent.putExtra("productId",product.getObjectId());
                //             intent.putExtra(FruitActivity.FRUIT_IMAGE_ID,fruit.getImageId());
                mContext.startActivity(intent);
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        AVObject product = products.get(position);
        holder.productTitle.setText(product.get("title").toString());

        Glide.with(mContext).load(product.getAVFile("image").getUrl()).into(holder.productImage);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }
}
