package com.example.com.ecommapp.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.example.com.ecommapp.R;
import com.example.com.ecommapp.base.BaseActivity;

import butterknife.BindView;
import butterknife.OnClick;

import static com.example.com.ecommapp.zxing.app.CaptureActivity.QRCODE;

/**
 * Created by rhm on 2017/12/10.
 */

public class QrcodeActivity extends BaseActivity {
    @BindView(R.id.img_qrcode)
    ImageView imgQrcode;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onViewCreated(Bundle savedInstanceState) {
        Intent intent = getIntent();
        Bitmap bitmap = intent.getParcelableExtra(QRCODE);
        imgQrcode.setImageBitmap(bitmap);
        toolbar.setNavigationOnClickListener(navigationOnClickListener);
    }

    @Override
    protected int getActivityResId() {
        return R.layout.activity_create_qrcode;
    }

    private View.OnClickListener navigationOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };
}
