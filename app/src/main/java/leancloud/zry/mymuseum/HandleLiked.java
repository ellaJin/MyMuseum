package leancloud.zry.mymuseum;

import android.util.Log;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Reene on 2017/5/22.
 */

public class HandleLiked {
    private  boolean checkIsLiked = false;


    public boolean checkIfLiked(String className, String objectId) {
        AVQuery<AVObject> checkLiked = new AVQuery<>(className);
        AVObject myItem;

        if (className.equals("MyExhibits")) {
            Log.d("className","MyExhibits");
            myItem = AVObject.createWithoutData("Exhibit", objectId);
            checkLiked.whereEqualTo("exhibit", myItem);
        } else if (className.equals("MyProducts")) {
            Log.d("className","MyProducts");
            myItem = AVObject.createWithoutData("Product", objectId);
            checkLiked.whereEqualTo("product", myItem);
        }
 //       checkExhibit.whereEqualTo("exhibit", myExhibit);

  //      final AVQuery<AVObject> checkUser = new AVQuery<>("MyExhibits");
        AVQuery<AVObject> checkUser = new AVQuery<>(className);
        checkUser.whereEqualTo("user", AVUser.getCurrentUser());

        AVQuery<AVObject> query = AVQuery.and(Arrays.asList(checkLiked, checkUser));


        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    if (list.size() > 0) {
                        Log.d("handle","sethandletrue");
                       checkIsLiked = true;
                //        return;
                    } else {
                        Log.d("handle","sethandlefalse");
                    }
                } else {
                    Log.d("handle","查不到");
                }

            }
        });
/*
        if (className.equals("MyExhibits")) {
            final AVQuery<AVObject> checkExhibit = new AVQuery<>("MyExhibits");
            AVObject myExhibit = AVObject.createWithoutData("Exhibit", objectId);
            checkExhibit.whereEqualTo("exhibit", myExhibit);

            final AVQuery<AVObject> checkUser = new AVQuery<>("MyExhibits");
            checkUser.whereEqualTo("user", AVUser.getCurrentUser());

            AVQuery<AVObject> query = AVQuery.and(Arrays.asList(checkExhibit, checkUser));

            query.findInBackground(new FindCallback<AVObject>() {
                @Override
                public void done(List<AVObject> list, AVException e) {
                    if (list.size() > 0) {
                        isliked = true;
                    }
                }
            });
        }*/

        if (checkIsLiked) {
            Log.d("handle","trueeeeee");
        } else {
            Log.d("handle","falseeeee");
        }
        return checkIsLiked;
    }

    public static boolean addItem(String className, String objectId) {
        if (className.equals("MyExhibits")){
            AVObject myExhibit = AVObject.createWithoutData("Exhibit", objectId);
        }
        return false;
    }
}
