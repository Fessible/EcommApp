package com.example.com.ecommapp.view.home.fragment;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.com.ecommapp.R;
import com.example.com.ecommapp.adapter.HomeAdapter;
import com.example.com.ecommapp.listener.DisposeDataListener;
import com.example.com.ecommapp.module.recommand.RecommendModel;
import com.example.com.ecommapp.module.recommand.RecommendValue;
import com.example.com.ecommapp.network.http.HttpConstants;
import com.example.com.ecommapp.network.http.HttpRequest;
import com.example.com.ecommapp.network.okhttp.CommonOkHttpClient;
import com.example.com.ecommapp.network.okhttp.request.CommRequest;
import com.example.com.ecommapp.view.home.BaseFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rhm on 2017/10/31.
 */

public class HomeFragment extends BaseFragment {
    private final static String TAG = "HomeFragment";
    private Context mContext;
    private View mContentView;
    private TextView txtScan;
    private TextView txtCategory;
    private TextView txtSearch;
    private ImageView loadView;
    private ListView listView;
    private HomeAdapter adapter;
    private RecommendModel recommendModel;
    private List<RecommendValue> mData = new ArrayList<>();

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_home_layout;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        loadView = view.findViewById(R.id.loading_view);
        txtScan = view.findViewById(R.id.qrcode_view);
        txtCategory = view.findViewById(R.id.category_view);
        listView = view.findViewById(R.id.list_view);
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
        HttpRequest.requestRecommendData(new DisposeDataListener() {
            @Override
            public void onSuccess(Object responseObj) {
                Log.d(TAG, "Success: " );
                if (responseObj != null) {
                    recommendModel = (RecommendModel) responseObj;
                    showSuccessView(recommendModel);
                }
            }

            @Override
            public void onFailure(Object responObj) {
                Log.d(TAG, "onFailure: " + responObj.toString());
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
}
