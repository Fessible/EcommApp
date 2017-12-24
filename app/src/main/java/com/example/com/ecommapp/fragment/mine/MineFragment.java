package com.example.com.ecommapp.fragment.mine;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.com.ecommapp.R;
import com.example.com.ecommapp.activity.LoginActivity;
import com.example.com.ecommapp.activity.QrcodeActivity;
import com.example.com.ecommapp.adapter.MineAdapter;
import com.example.com.ecommapp.application.EcomAplication;
import com.example.com.ecommapp.base.BaseFragment;
import com.example.com.ecommapp.module.user.User;
import com.example.com.ecommapp.util.IntentUtil;
import com.example.com.ecommapp.zxing.util.Util;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.Hashtable;

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
                    Bitmap b = createQRcode();
                    Intent intent = new Intent(getActivity(), QrcodeActivity.class);
                    intent.putExtra(QRCODE, b);
                    startActivity(intent);
                    break;
                case STATE_VERSION:
                    break;
            }
        }
    };

    public Bitmap createQRcode() {
        int QR_WIDTH = 300;
        int QR_HEIGHT = 300;

        try {
            // 需要引入core包
            QRCodeWriter writer = new QRCodeWriter();
            //todo 电话号码
            String text = "15000000";
            if (text == null || "".equals(text) || text.length() < 1) {
                return null;
            }
            // 把输入的文本转为二维码
            BitMatrix martix = writer.encode(text, BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT);

            System.out.println("w:" + martix.getWidth() + "h:" + martix.getHeight());

            Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            BitMatrix bitMatrix = new QRCodeWriter().encode(text, BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints);
            int[] pixels = new int[QR_WIDTH * QR_HEIGHT];
            for (int y = 0; y < QR_HEIGHT; y++) {
                for (int x = 0; x < QR_WIDTH; x++) {
                    if (bitMatrix.get(x, y)) {
                        pixels[y * QR_WIDTH + x] = 0xff000000;
                    } else {
                        pixels[y * QR_WIDTH + x] = 0xffffffff;
                    }
                }
            }
            // cheng chen de er wei ma
            Bitmap bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);

            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
        }
        return null;
    }

    @OnClick(R.id.login_view)
    public void Login(View view) {
        Intent intent = new Intent(getContext(), LoginActivity.class);
        startActivity(intent);
    }
}
