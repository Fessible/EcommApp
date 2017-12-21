package com.example.com.ecommapp.base;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import butterknife.ButterKnife;

/**
 * Created by rhm on 2017/12/21.
 */

public abstract class BaseViewHolder extends RecyclerView.ViewHolder {
    public BaseViewHolder(Context context, ViewGroup parent, @LayoutRes int adapterLayoutResId) {
        super(LayoutInflater.from(context).inflate(adapterLayoutResId, parent, false));
        ButterKnife.bind(this, itemView);
    }
}
