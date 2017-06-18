package leancloud.zry.mymuseum;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import leancloud.zry.mymuseum.fragments.FirstFragment;
import leancloud.zry.mymuseum.fragments.SecondFragment;
import leancloud.zry.mymuseum.fragments.ThirdFragment;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView topBarText;
    private TextView tabDeal;
    private TextView main;
    private TextView list;
    private TextView my;

    private Fragment f1,f2,f3;

    private FrameLayout ly_content;

    private FragmentTransaction transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d("wtf","wwww");

        bindView();
        showFirst();

    }

    public void showFirst() {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        //    f1 = new FirstFragment("第一个Fragment");
        f1 = new FirstFragment();
        main.setSelected(true);
        transaction.add(R.id.fragment_container,f1);
        transaction.commit();
    }

    //UI组件初始化与事件绑定
    private void bindView() {
        topBarText = (TextView)this.findViewById(R.id.txt_top);
      //  tabDeal = (TextView)this.findViewById(R.id.txt_deal);
        main = (TextView)this.findViewById(R.id.bottom_main);
        list = (TextView)this.findViewById(R.id.bottom_list);
        my = (TextView)this.findViewById(R.id.bottom_my);
        ly_content = (FrameLayout) findViewById(R.id.fragment_container);

  //      tabDeal.setOnClickListener(this);
        main.setOnClickListener(this);
        list.setOnClickListener(this);
        my.setOnClickListener(this);

    }

    //重置所有文本的选中状态
    public void selected(){
    //    tabDeal.setSelected(false);
        main.setSelected(false);
        list.setSelected(false);
        my.setSelected(false);
    }

    //隐藏所有Fragment
    public void hideAllFragment(FragmentTransaction transaction){

        if(f1!=null){
            transaction.hide(f1);
        }
        if(f2!=null){
            transaction.hide(f2);
        }

        if(f3!=null){
            transaction.hide(f3);
        }
/*
        if (f4 != null) {
            transaction.hide(f4);
        }*/
    }

    @Override
    public void onClick(View v) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        hideAllFragment(transaction);
        switch(v.getId()){
            case R.id.bottom_main:
                selected();
                topBarText.setText("展览列表");
                main.setSelected(true);
                if(f1==null){
                    f1 = new FirstFragment();
                    transaction.add(R.id.fragment_container,f1);
                }else{
                    transaction.show(f1);
                }
                break;
            case R.id.bottom_list:
                selected();
                topBarText.setText("展品搜索");
                list.setSelected(true);
                if(f2==null){
                    f2 = new SecondFragment();
                    transaction.add(R.id.fragment_container,f2);
                }else{
                    transaction.show(f2);
                }
                break;
            case R.id.bottom_my:
                selected();
                topBarText.setText("个人资料");
                my.setSelected(true);
                if(f3==null){
                    f3 = new ThirdFragment();
                    transaction.add(R.id.fragment_container,f3);
                }else{
                    transaction.show(f3);
                }
                break;


        }

        transaction.commit();
    }
}
