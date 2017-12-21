package com.example.com.ecommapp.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.com.ecommapp.R;
import com.example.com.ecommapp.base.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by rhm on 2017/12/21.
 */

public class MineAdapter extends RecyclerView.Adapter<MineAdapter.ViewHolder> {
    private List<String> titleList = new ArrayList<>();
    private Context context;
    private onItemClick onItemClick;

    public MineAdapter(Context context) {
        this.context = context;
        initData();
    }

    //添加数据
    private void initData() {
        titleList.clear();
        titleList.add(getString(R.string.video_play_setting));
        titleList.add(getString(R.string.share));
        titleList.add(getString(R.string.qrcode_me));
        titleList.add(getString(R.string.update_info));
    }

    //获取资源数据
    private String getString(int ResId) {
        return context.getString(ResId);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(context, parent);
    }

    public void setOnItemClick(onItemClick onItemClick) {
        this.onItemClick = onItemClick;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindViewHolder(titleList, position, onItemClick);
    }

    @Override
    public int getItemCount() {
        return titleList.size();
    }

    public static class ViewHolder extends BaseViewHolder {
        @BindView(R.id.txt_title)
        TextView txtTitle;

        public ViewHolder(Context context, ViewGroup parent) {
            super(context, parent, R.layout.item_mine_layout);
        }

        public void bindViewHolder(List<String> titleList, final int position, final onItemClick onItemClick) {
            txtTitle.setText(titleList.get(position));
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) { //item点击事件
                    if (onItemClick != null) {
                        onItemClick.itemClick(position);
                    }
                }
            });
        }
    }

    public interface onItemClick {
        void itemClick(int position);
    }
}
