package com.example.com.ecommapp.view.home.fragment;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.com.ecommapp.R;
import com.example.com.ecommapp.adapter.HomeAdapter;
import com.example.com.ecommapp.module.recommand.RecommendModel;
import com.example.com.ecommapp.module.recommand.RecommendValue;
import com.example.com.ecommapp.network.http.HttpRequest;
import com.example.com.ecommapp.base.BaseFragment;
import com.example.com.ecommapp.zxing.app.CaptureActivity;
import com.example.com.support.okhttp.listener.DisposeListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by rhm on 2017/10/31.
 */

public class HomeFragment extends BaseFragment {
    private final static String TAG = "HomeFragment";

    @BindView(R.id.category_view)
    TextView txtCategory;

    @BindView(R.id.qrcode_view)
    TextView txtScan;

    @BindView(R.id.loading_view)
    ImageView loadView;

    @BindView(R.id.list_view)
    ListView listView;

    @BindView(R.id.search_view)
    TextView txtSearch;

    private HomeAdapter adapter;
    private RecommendModel recommendModel;
    private List<RecommendValue> mData = new ArrayList<>();

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_home_layout;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        adapter = new HomeAdapter(getActivity(), mData);
        listView.setAdapter(adapter);
        AnimationDrawable animationDrawable = (AnimationDrawable) loadView.getDrawable();
        animationDrawable.start();
        requestRecommend();
    }


    /**
     * 请求首页数据
     */
    private void requestRecommend() {
        HttpRequest.HomeRequest(new DisposeListener() {
            @Override
            public void onSuccess(Object responseObj) {
                RecommendModel recommendModel = (RecommendModel) responseObj;
                showSuccessView(recommendModel);
            }

            @Override
            public void onFailure(String msg) {
                showShortToast(msg);
            }
        });
    }

    /**
     * 请求成功执行
     *
     * @param recommendModel
     */
    private void showSuccessView(RecommendModel recommendModel) {
        //判断数据是否为空
        if (recommendModel.data != null && recommendModel.data.size() > 0) {
            mData = recommendModel.data;
            loadView.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
            adapter.setData(mData);
            adapter.notifyDataSetChanged();
        } else {
            showErrorView();
        }
    }

    /**
     * 请求失败执行
     */
    private void showErrorView() {
    }

    @OnClick(R.id.qrcode_view)
    public void clickQrcode(View view){
        Intent intent = new Intent(getActivity(), CaptureActivity.class);
        startActivity(intent);
    }
}
