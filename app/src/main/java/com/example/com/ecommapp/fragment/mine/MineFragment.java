package com.example.com.ecommapp.fragment.mine;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.com.ecommapp.R;
import com.example.com.ecommapp.activity.QrcodeActivity;
import com.example.com.ecommapp.adapter.MineAdapter;
import com.example.com.ecommapp.base.BaseFragment;
import com.example.com.ecommapp.module.version.VersionModel;
import com.example.com.ecommapp.network.http.HttpRequest;
import com.example.com.ecommapp.util.IntentUtil;
import com.example.com.ecommapp.util.Utils;
import com.example.com.support.okhttp.listener.DisposeListener;

import butterknife.BindView;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.com.ecommapp.adapter.MineAdapter.STATE_QCODE;
import static com.example.com.ecommapp.adapter.MineAdapter.STATE_SHARE;
import static com.example.com.ecommapp.adapter.MineAdapter.STATE_VERSION;
import static com.example.com.ecommapp.adapter.MineAdapter.STATE_VIDEO_SET;
import static com.example.com.ecommapp.zxing.app.CaptureActivity.QRCODE;

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
            switch (position) {
                case STATE_VIDEO_SET:
                    IntentUtil.startTemplateActivity(MineFragment.this, SettingFragment.class, SettingFragment.TAG_SETTING_FRAGMENT);
                    break;
                case STATE_SHARE:
                    break;
                case STATE_QCODE:
                    Bitmap b = Utils.createQRCode(300, 300, "15500");
                    Intent intent = new Intent(getActivity(), QrcodeActivity.class);
                    intent.putExtra(QRCODE, b);
                    startActivity(intent);
                    break;
                case STATE_VERSION:
                    checkVersion();
                    break;
            }
        }

    };

    /**
     * 检查版本更新
     */
    private void checkVersion() {
        HttpRequest.versionRequest(new DisposeListener() {
            @Override
            public void onSuccess(Object responseObj) {
                if (responseObj != null) {
                    VersionModel versionModel = (VersionModel) responseObj;
                    //当前版本小于服务器版本，更新
                    if (Utils.getVersionCode(getContext()) < versionModel.version) {
                        new AlertDialog.Builder(getContext())
                                .setTitle(R.string.update_new_version)
                                .setMessage(R.string.update_title)
                                .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                })
                                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                })
                                .show();
                    } else {
                        showShortToast(getContext().getString(R.string.no_new_version_msg));
                    }
                }
            }

            @Override
            public void onFailure(String msg) {
                showShortToast(msg);
            }
        });

    }

    @OnClick(R.id.login_view)
    public void Login(View view) {
        IntentUtil.startTemplateActivity(MineFragment.this, LoginFragment.class, LoginFragment.TAG_Login_FRAGMENT);
    }
}
