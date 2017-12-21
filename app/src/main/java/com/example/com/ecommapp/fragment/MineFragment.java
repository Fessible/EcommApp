package com.example.com.ecommapp.fragment;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.com.ecommapp.R;
import com.example.com.ecommapp.adapter.MineAdapter;
import com.example.com.ecommapp.base.BaseFragment;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by rhm on 2017/10/31.
 */

public class MineFragment extends BaseFragment {
    @BindView(R.id.login_layout)
    RelativeLayout loginLayout;

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @BindView(R.id.photo_view)
    CircleImageView imgPhoto;

    @BindView(R.id.login_info_view)
    TextView txtLoginInfo;

    @BindView(R.id.login_view)
    TextView txtLogin;

   @BindView(R.id.logined_layout)
   RelativeLayout loginedLayout;

   @BindView(R.id.user_photo_view)
   CircleImageView userPhoto;

   @BindView(R.id.username_view)
   TextView txtUserName;

   private MineAdapter adapter;

    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_mine_layout;
    }

    @Override
    protected void initView(View view, Bundle savedInstanceState) {
        adapter = new MineAdapter(getContext());
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setSmoothScrollbarEnabled(true);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClick(onItemClick);
    }

    private MineAdapter.onItemClick onItemClick = new MineAdapter.onItemClick() {
        @Override
        public void itemClick(int position) {
            Toast.makeText(getContext(),"you click "+position,Toast.LENGTH_SHORT).show();
        }
    };
}
