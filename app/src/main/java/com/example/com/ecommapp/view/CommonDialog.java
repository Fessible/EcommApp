package com.example.com.ecommapp.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.com.ecommapp.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by rhm on 2017/12/25.
 */

public class CommonDialog extends Dialog {
    public final static int TYPE_SINGLE = 0;
    public final static int TYPE_ALL = 1;

    @IntDef({TYPE_SINGLE, TYPE_ALL})
    @Retention(RetentionPolicy.SOURCE)
    @interface TYPE {
    }


    @BindView(R.id.all_layout)
    LinearLayout allLayout;

    @BindView(R.id.single_layout)
    LinearLayout singleLayout;

    @BindView(R.id.title)
    TextView txtTitle;

    @BindView(R.id.message)
    TextView txtMessage;

    @BindView(R.id.confirm_btn)
    TextView confirmBtn;

    @BindView(R.id.cancel_btn)
    TextView cancelBtn;

    private ClickListener listener;
    private Unbinder unbinder;
    private ConfirmListener confirmListener;
    private int type;

    public CommonDialog(@NonNull Context context, @TYPE int type) {
        super(context, R.style.dialog_custom);
        this.type = type;
        setContentView(R.layout.common_dialog_layout);

        unbinder = ButterKnife.bind(this);
        if (type == TYPE_SINGLE) {
            setLayoutVisible(true);
        } else if (type == TYPE_ALL) {
            setLayoutVisible(false);
        }
    }

    public void setLayoutVisible(boolean isSingle) {
        singleLayout.setVisibility(isSingle ? View.VISIBLE : View.GONE);
        allLayout.setVisibility(isSingle ? View.GONE : View.VISIBLE);
    }

    public void setConformListener(ConfirmListener listener) {
        this.confirmListener = listener;
    }

    @OnClick(R.id.confirm_btn)
    public void onConfirm(View view) {
        listener.onPositiveClick();
    }

    @OnClick(R.id.cancel_btn)
    public void onCancel(View view) {
        listener.onNegativeClick();
    }

    @OnClick(R.id.signal_confirm_btn)
    public void onSingleConfim(View view) {
        confirmListener.onClick();
    }

    @Override
    public void dismiss() {

        super.dismiss();
        unbinder.unbind();
    }

    public void setOnClickListener(ClickListener clickListener) {
        this.listener = clickListener;
    }

    public void setTitle(String title) {
        txtTitle.setText(title);
    }

    public void setMessage(String message) {
        txtMessage.setText(message);
    }

    public void setPositiveTxt(String txt) {
        confirmBtn.setText(txt);
    }

    public void setNegativeTxt(String txt) {
        cancelBtn.setText(txt);
    }

    public void setParams(int width, int height) {
        WindowManager.LayoutParams dialogParams = this.getWindow().getAttributes();
        dialogParams.width = width;
        dialogParams.height = height;
        this.getWindow().setAttributes(dialogParams);
    }

    public interface ClickListener {
        void onPositiveClick();

        void onNegativeClick();
    }

    public interface ConfirmListener {
        void onClick();
    }
}
