package com.example.com.ecommapp.adapter;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.com.ecommapp.R;
import com.example.com.ecommapp.module.recommand.RecommendValue;
import com.example.com.imageloader.ImageLoaderManager;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.utils.L;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 首页ListView的适配器
 * Created by rhm on 2017/12/3.
 */

public class HomeAdapter extends BaseAdapter {
    //三种类型
    private final static int TYPE_COUNT = 3;
    private final static int TYPE_VEDIO = 0x00;
    private final static int CARD_TYPE = 0x01;
    private final static int CARD_TYPE_MULTI = 0x02;
    private List<RecommendValue> mData;
    private Context mContext;
    private LayoutInflater inflater;
    private ImageLoaderManager imageLoader;

    public HomeAdapter(Context context, List<RecommendValue> data) {
        this.mContext = context;
        this.mData = data;
        inflater = LayoutInflater.from(context);
        imageLoader = ImageLoaderManager.getInstatnce(context);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    //获取类型
    @Override
    public int getItemViewType(int position) {
        return mData.get(position).type;
    }

    //获取类型数
    @Override
    public int getViewTypeCount() {
        return TYPE_COUNT;
    }

    /*************************init View*****************************************/
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);
        ViewHolder mViewHolder = new ViewHolder();
        final RecommendValue value = (RecommendValue) getItem(position);
        if (convertView == null) {
            switch (type) {
                case TYPE_VEDIO:
                    break;
                case CARD_TYPE:
                    mViewHolder = new ViewHolder();
                    convertView = inflater.inflate(R.layout.item_product_card_one_layout, parent, false);
                    mViewHolder.mPriceView = (TextView) convertView.findViewById(R.id.item_price_view);
                    mViewHolder.mFromView = (TextView) convertView.findViewById(R.id.item_from_view);
                    mViewHolder.mZanView = (TextView) convertView.findViewById(R.id.item_zan_view);
                    mViewHolder.mProductOneView = (ImageView) convertView.findViewById(R.id.product_view_one);
                    mViewHolder.mProductTwoView = (ImageView) convertView.findViewById(R.id.product_view_two);
                    mViewHolder.mProductThreeView = (ImageView) convertView.findViewById(R.id.product_view_three);
                    break;
                case CARD_TYPE_MULTI:
                    mViewHolder = new ViewHolder();
                    convertView = inflater.inflate(R.layout.item_product_card_two_layout, parent, false);
                    mViewHolder.mProductView = (ImageView) convertView.findViewById(R.id.product_photo_view);
                    mViewHolder.mPriceView = (TextView) convertView.findViewById(R.id.item_price_view);
                    mViewHolder.mFromView = (TextView) convertView.findViewById(R.id.item_from_view);
                    mViewHolder.mZanView = (TextView) convertView.findViewById(R.id.item_zan_view);
                    break;
            }
            mViewHolder.mLogoView = (CircleImageView) convertView.findViewById(R.id.item_logo_view);
            mViewHolder.mTitleView = (TextView) convertView.findViewById(R.id.item_title_view);
            mViewHolder.mInfoView = (TextView) convertView.findViewById(R.id.item_info_view);
            mViewHolder.mFooterView = (TextView) convertView.findViewById(R.id.item_footer_view);
            convertView.setTag(mViewHolder);
        }//有tag时
        else {
            convertView.getTag();
        }
        //填充数据
        imageLoader.displayImage(mViewHolder.mLogoView, value.logo);
        mViewHolder.mTitleView.setText(value.title);
        mViewHolder.mInfoView.setText(value.info);
        mViewHolder.mFooterView.setText(value.text);
        switch (type) {
            case TYPE_VEDIO:
                break;
            case CARD_TYPE:
                mViewHolder.mPriceView.setText(value.price);
                mViewHolder.mFooterView.setText(value.from);
                mViewHolder.mZanView.setText(mContext.getString(R.string.dian_zan).concat(String.valueOf(value.zan)));
                imageLoader.displayImage(mViewHolder.mProductOneView,value.url.get(0));
                imageLoader.displayImage(mViewHolder.mProductTwoView,value.url.get(1));
                imageLoader.displayImage(mViewHolder.mProductThreeView, value.url.get(2));
                break;
            case CARD_TYPE_MULTI:
                mViewHolder.mPriceView.setText(value.price);
                mViewHolder.mFromView.setText(value.from);
                mViewHolder.mZanView.setText(mContext.getString(R.string.dian_zan).concat(String.valueOf(value.zan)));
                imageLoader.displayImage(mViewHolder.mProductView, value.url.get(0));
                break;
        }
        return convertView;
    }


    private static class ViewHolder {
        //所有Card共有属性
        private CircleImageView mLogoView;
        private TextView mTitleView;
        private TextView mInfoView;
        private TextView mFooterView;
        //Video Card特有属性
        private RelativeLayout mVieoContentLayout;
        private ImageView mShareView;

        //Video Card外所有Card具有属性
        private TextView mPriceView;
        private TextView mFromView;
        private TextView mZanView;
        //Card One特有属性
        private ImageView mProductOneView;
        private ImageView mProductTwoView;
        private ImageView mProductThreeView;
        //Card Two特有属性
        private ImageView mProductView;
    }
}
