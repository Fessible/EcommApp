package com.example.com.ecommapp.share;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.DisplayMetrics;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.com.ecommapp.R;
import com.example.com.ecommapp.zxing.decode.Intents;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;

import static android.view.Gravity.BOTTOM;

/**
 * Created by rhm on 2018/1/8.
 */

public class ShareDialog extends Dialog {

    Unbinder unbinder;
    private DisplayMetrics displayMetrics;
    private int shareType;
    private String shareTitle;
    private String shareText;
    private String titlUrl;
    private String siteUrl;
    private String url;
    private String sharePhoto;
    private String shareSite;


    public ShareDialog(@NonNull Context context) {
        super(context, R.style.Alert);
        displayMetrics = context.getResources().getDisplayMetrics();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_share_layout);
        unbinder = ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        Window dialogWindow = getWindow();
        dialogWindow.setGravity(BOTTOM);//设置dialog的显示位置
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = displayMetrics.widthPixels;//屏幕宽度
        dialogWindow.setAttributes(lp);
    }

    public void shareData(ShareManager.PlatofrmType type) {
        ShareData data = new ShareData();
        Platform.ShareParams params = new Platform.ShareParams();
        params.setTitle(shareTitle);
        params.setTitleUrl(titlUrl);
        params.setSite(shareSite);
        params.setText(shareText);
        params.setImagePath(sharePhoto);
        params.setUrl(url);
        data.mShareParams = params;
        data.mPlatformType = type;
        ShareManager.getManager().shareData(data, listener);
    }

    private PlatformActionListener listener = new PlatformActionListener() {
        @Override
        public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
            Toast.makeText(getContext(),getContext().getResources().getString(R.string.share_complete),Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onError(Platform platform, int i, Throwable throwable) {
            Toast.makeText(getContext(),getContext().getResources().getString(R.string.share_error),Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(Platform platform, int i) {
            Toast.makeText(getContext(),getContext().getResources().getString(R.string.share_cancel),Toast.LENGTH_SHORT).show();
        }
    };

    @OnClick(R.id.weixin_layout)
    void weixin() {
        shareData(ShareManager.PlatofrmType.WeChat);
    }

    @OnClick(R.id.weibo_layout)
    void weibo() {
        shareData(ShareManager.PlatofrmType.Weibo);
    }

    @OnClick(R.id.moment_layout)
    void moment() {
        shareData(ShareManager.PlatofrmType.Comment);

    }

    @OnClick(R.id.qq_layout)
    void qq() {
        shareData(ShareManager.PlatofrmType.QQ);

    }

    @OnClick(R.id.qzone_layout)
    void qzone() {
        shareData(ShareManager.PlatofrmType.Qzone);

    }

    @OnClick(R.id.cancel_view)
    void cancelView() {
        dismiss();
    }


    public void destroy() {
        unbinder.unbind();
    }

    public void setShareSite(String shareSite) {
        this.shareSite = shareSite;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setShareType(int shareType) {

        this.shareType = shareType;
    }

    public void setShareTitle(String shareTitle) {
        this.shareTitle = shareTitle;
    }

    public void setShareText(String shareText) {
        this.shareText = shareText;
    }

    public void setTitlUrl(String titlUrl) {
        this.titlUrl = titlUrl;
    }

    public void setSiteUrl(String siteUrl) {
        this.siteUrl = siteUrl;
    }

    public void setSharePhoto(String sharePhoto) {
        this.sharePhoto = sharePhoto;
    }
}
