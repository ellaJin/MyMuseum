package leancloud.zry.mymuseum.dao;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Reene on 2017/5/28.
 */

public class GetData {
    private String className;

    private List<AVObject> list;

    public GetData() {
    }

    public GetData(String className) {
        this.className = className;
        list = new ArrayList<>();
        new Thread(findData).start();
    }


    Runnable findData = new Runnable() {

        @Override
        public void run() {
            // TODO
            // 在这里进行 http request.网络请求相关操作
            Message msg = new Message();

            Bundle data = new Bundle();
            AVQuery<AVObject> query = new AVQuery<>(className);

            try {
               list = query.find();

            }catch (AVException e) {
                e.printStackTrace();
            }
            data.putString("value", "请求结果");
            msg.setData(data);
            handlerData.sendMessage(msg);
        }
    };

    Handler handlerData = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Bundle data = msg.getData();
            String val = data.getString("value");
            Log.d("mylog", "请求结果为-->" + val);
            // TODO
            // UI界面的更新等相关操作
//            initData(likeType);
//
//            for (AVObject object:oobjects) {
//                Log.d("handle",object.get("title").toString());
//            }

            for (AVObject avObject:list) {
                Log.d("getdata",avObject.get("title").toString());
            }

        }
    };

    public List<AVObject> getList() {
        return list;
    }

    public void setList(List<AVObject> list) {
        this.list = list;
    }
}
