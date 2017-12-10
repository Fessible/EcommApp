package com.example.com.ecommapp.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.example.com.ecommapp.R;
import com.example.com.ecommapp.base.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.com.ecommapp.zxing.app.CaptureActivity.QRCODE;

/**
 * Created by rhm on 2017/12/10.
 */

public class QrcodeActivity extends BaseActivity {
    @BindView(R.id.img_qrcode)
    ImageView imgQrcode;

    @Override
    protected void onViewCreated(Bundle savedInstanceState) {
        Intent intent = getIntent();
        Bitmap bitmap = intent.getParcelableExtra(QRCODE);
        imgQrcode.setImageBitmap(bitmap);
    }

    @Override
    protected int getActivityResId() {
        return R.layout.activity_create_qrcode;
    }

    @OnClick(R.id.button_back)
    public void onBack(View view) {
        finish();
    }

}
