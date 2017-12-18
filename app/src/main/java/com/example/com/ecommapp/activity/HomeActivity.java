package com.example.com.ecommapp.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.example.com.ecommapp.R;
import com.example.com.ecommapp.base.BaseActivity;
import com.example.com.ecommapp.view.home.fragment.HomeFragment;
import com.example.com.ecommapp.view.home.fragment.MessageFragment;
import com.example.com.ecommapp.view.home.fragment.MineFragment;

import butterknife.BindView;

/**
 * Created by rhm on 2017/10/31.
 */

public class HomeActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener {
    private final static int HOME_INDEX = 0;
    private final static int Message_INDEX = 1;
    private final static int MINE_INDEX = 2;

    @BindView(R.id.radioGroup)
    RadioGroup radioGroup;

    @BindView(R.id.rb_home)
    RadioButton rbHome;

    @BindView(R.id.rb_message)
    RadioButton rbMessage;

    @BindView(R.id.rb_mine)
    RadioButton rbMine;

    private HomeFragment mHomeFragment;
    private MessageFragment mMessageFragment;
    private MineFragment mMineFragment;
    private Fragment[] mFragments;
    private int mIndex;

    @Override
    protected void onViewCreated(Bundle savedInstanceState) {
        radioGroup.setOnCheckedChangeListener(this);
        initFragment();
    }

    private void initFragment() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        //初始化数组
        mHomeFragment = new HomeFragment();
        mMessageFragment = new MessageFragment();
        mMineFragment = new MineFragment();
        mFragments = new Fragment[]{mHomeFragment, mMessageFragment, mMineFragment};
        //添加事务
        fragmentTransaction.replace(R.id.content_layout, mHomeFragment).commit();
        mIndex = HOME_INDEX;
    }


    @Override
    protected int getActivityResId() {
        return R.layout.activity_home_layout;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.rb_home:
                setIndexSelected(HOME_INDEX);
                break;
            case R.id.rb_message:
                setIndexSelected(Message_INDEX);
                break;
            case R.id.rb_mine:
                setIndexSelected(MINE_INDEX);
                break;
        }
    }

    private void setIndexSelected(int index) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        //如果点击仍然是当前位置直接返回
        if (mIndex == index) {
            return;
        }
        fragmentTransaction.hide(mFragments[mIndex]);//隐藏上一个位置的内容

        if (!mFragments[index].isAdded()) {//未添加则添加到事务中
            fragmentTransaction.add(R.id.content_layout, mFragments[index]).show(mFragments[index]);
        } else {//直接显示
            fragmentTransaction.show(mFragments[index]);
        }
        fragmentTransaction.commit();
        mIndex = index;
    }


}
