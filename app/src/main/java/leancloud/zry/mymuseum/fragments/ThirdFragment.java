package leancloud.zry.mymuseum.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.bumptech.glide.Glide;

import java.util.List;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.CropCircleTransformation;
import leancloud.zry.mymuseum.LoginActivity;
import leancloud.zry.mymuseum.R;
import leancloud.zry.mymuseum.activity.ChangePersonalImageActivity;
import leancloud.zry.mymuseum.activity.SetPasswordActivity;
import leancloud.zry.mymuseum.activity.ShowLikedActivity;


/**
 * Created by Reene on 2017/5/18.
 */

public class ThirdFragment extends Fragment{
    private ImageView imageView;

    private ImageView imageViewSmall;

    private ImageView myProducts;

    private ImageView myExhibits;

    private ImageView resetPassword;

    private TextView personalName;

    private Button logout;

    private Context context;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.third_fragment,container,false);
        imageView = (ImageView)view.findViewById(R.id.personal_image);
        imageViewSmall = (ImageView)view.findViewById(R.id.personal_image_small);
        myProducts = (ImageView)view.findViewById(R.id.myproducts);
        myExhibits = (ImageView)view.findViewById(R.id.myexhibits);
        personalName = (TextView)view.findViewById(R.id.personal_username) ;
        resetPassword = (ImageView)view.findViewById(R.id.reset_password);

        logout = (Button) view.findViewById(R.id.logout);
        context = view.getContext();

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AVUser.getCurrentUser().logOut();
                startActivity(new Intent(getActivity(), LoginActivity.class));
                getActivity().finish();
            }
        });
        setClick();
        setPersonalBlurImage();
        return view;
    }

    private void setPersonalBlurImage() {
    //    String objectId = AVUser.getCurrentUser().getObjectId();
        /*
           AVQuery<AVObject> query = new AVQuery<>(likeType);

        query.whereEqualTo("user",AVUser.getCurrentUser());

        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
      //          likeList.addAll(list);
     //           handleList(list);
                loadData(list);
            }
        });
         */

        AVQuery<AVObject> query = new AVQuery<>("PersonalImage");
        query.whereEqualTo("user",AVUser.getCurrentUser());
        personalName.setText(AVUser.getCurrentUser().get("username").toString());
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (list.size() > 0) {
                    Glide.with(context).load(list.get(0).getAVFile("image").getUrl()).bitmapTransform(new BlurTransformation(context, 25)).crossFade(1000).into(imageView);
                    Glide.with(context).load(list.get(0).getAVFile("image").getUrl()).bitmapTransform(new CropCircleTransformation(context)).crossFade(1000).into(imageViewSmall);

                }
                else {
                    Glide.with(context).load(R.mipmap.luffy).bitmapTransform(new BlurTransformation(context, 25)).crossFade(1000).into(imageView);
                    Glide.with(context).load(R.mipmap.luffy).bitmapTransform(new CropCircleTransformation(context)).crossFade(1000).into(imageViewSmall);
                }

            }
        });

    }

    private void setClick() {
        myExhibits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("third","myexhibitclick");
                Intent intent = new Intent(context, ShowLikedActivity.class);
                intent.putExtra("likeType","MyExhibits");
                intent.putExtra("entity","Exhibit");

                startActivity(intent);
            }
        });

        myProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("third","myproductclick");
                Intent intent = new Intent(context, ShowLikedActivity.class);
                intent.putExtra("likeType","MyProducts");
                intent.putExtra("entity","Product");
                startActivity(intent);
            }
        });

        resetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SetPasswordActivity.class);
                intent.putExtra("userId",AVUser.getCurrentUser().getObjectId());
                startActivity(intent);
            }
        });

        imageViewSmall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ChangePersonalImageActivity.class);
    //            ((Activity)context).finish();
                startActivity(intent);
            }
        });
    }


}
