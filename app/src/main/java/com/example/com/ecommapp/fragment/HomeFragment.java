package com.example.com.ecommapp.fragment;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.com.ecommapp.R;
import com.example.com.ecommapp.activity.PhotoActivity;
import com.example.com.ecommapp.adapter.HomeAdapter;
import com.example.com.ecommapp.base.BaseFragment;
import com.example.com.ecommapp.module.recommand.RecommendModel;
import com.example.com.ecommapp.module.recommand.RecommendValue;
import com.example.com.ecommapp.network.http.HttpRequest;
import com.example.com.ecommapp.zxing.app.CaptureActivity;
import com.example.com.support.okhttp.listener.DisposeListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.example.com.ecommapp.activity.PhotoActivity.PHOTO_VIEW;
import static com.example.com.ecommapp.adapter.HomeAdapter.TYPE_CARD;
import static com.example.com.ecommapp.adapter.HomeAdapter.TYPE_CARD_MULTI;
import static com.example.com.ecommapp.adapter.HomeAdapter.TYPE_VEDIO;

/**
 * Created by rhm on 2017/10/31.
 */

public class HomeFragment extends BaseFragment  {
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

    private RadioGroup radioGroup;
    private RadioButton rbHome;
    private RadioButton rbMessage;
    private RadioButton rbMine;

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
        listView.setOnItemClickListener(itemClickListener);
        AnimationDrawable animationDrawable = (AnimationDrawable) loadView.getDrawable();
        animationDrawable.start();
        requestRecommend();
    }

    //todo HorizontalScrollView点击事件未完成
    private HomeAdapter.OnMultiClickListener multiClickListener = new HomeAdapter.OnMultiClickListener() {
        @Override
        public void onClick(List<String> url) {
            Intent intent = new Intent(getContext(), PhotoActivity.class);
            intent.putStringArrayListExtra(PHOTO_VIEW, (ArrayList<String>) url);
            startActivity(intent);
        }
    };



    private AdapterView.OnItemClickListener itemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            Toast.makeText(getContext(), "you click" + position,Toast.LENGTH_SHORT).show();
            RecommendValue value = (RecommendValue) adapter.getItem(position);
            if (value.type != TYPE_VEDIO) {
                Intent intent = new Intent(getContext(), PhotoActivity.class);
                intent.putStringArrayListExtra(PHOTO_VIEW, (ArrayList<String>) value.url);
                startActivity(intent);
            }
        }
    };

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
