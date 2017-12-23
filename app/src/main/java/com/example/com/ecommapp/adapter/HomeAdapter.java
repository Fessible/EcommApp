package com.example.com.ecommapp.adapter;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.com.ecommapp.R;
import com.example.com.ecommapp.module.recommand.RecommendValue;
import com.example.com.support.VideoManager;
import com.example.com.support.imageloader.ImageLoaderManager;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 首页ListView的适配器
 * Created by rhm on 2017/12/3.
 */

public class HomeAdapter extends BaseAdapter {
    //类型
    private final static int TYPE_COUNT = 4;
    public final static int TYPE_VEDIO = 0x00;
    public final static int TYPE_CARD = 0x02;
    public final static int TYPE_CARD_MULTI = 0x01;
    public final static int TYPE_VIEW_PAGER = 0x03;
    private List<RecommendValue> mData;
    private Context mContext;
    private LayoutInflater inflater;
    private ImageLoaderManager imageLoader;
    private HomeViewPagerAdapter adapter;
    private OnMultiClickListener multiClickListener;

    public HomeAdapter(Context context, List<RecommendValue> data) {
        this.mContext = context;
        this.mData = data;
        inflater = LayoutInflater.from(context);
        imageLoader = ImageLoaderManager.getInstance(context);
    }

    public void setMultiClickListener(OnMultiClickListener listener) {
        this.multiClickListener = listener;
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

    public void setData(List<RecommendValue> data) {
        this.mData = data;
    }

    /*************************init View*****************************************/
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int type = getItemViewType(position);
        ViewHolder mViewHolder = new ViewHolder();
        final RecommendValue value = (RecommendValue) getItem(position);
        View view = convertView;
        if (view == null) {
            switch (type) {
                case TYPE_VEDIO:
                    view = inflater.inflate(R.layout.item_video_layout, parent, false);
                    mViewHolder.mVieoContentLayout = (RelativeLayout)
                            view.findViewById(R.id.video_ad_layout);
                    mViewHolder.mShareView = (ImageView) view.findViewById(R.id.item_share_view);
                    break;
                case TYPE_CARD_MULTI:
                    view = inflater.inflate(R.layout.item_multi_photo, parent, false);
                    mViewHolder.multiProductLayout = view.findViewById(R.id.item_product_layout);
                    mViewHolder.mPriceView = view.findViewById(R.id.item_price_view);
                    mViewHolder.mFromView = view.findViewById(R.id.item_from_view);
                    mViewHolder.mZanView = view.findViewById(R.id.item_zan_view);
                    mViewHolder.mProductOneView = view.findViewById(R.id.product_view_one);
                    mViewHolder.mProductTwoView = view.findViewById(R.id.product_view_two);
                    mViewHolder.mProductThreeView = view.findViewById(R.id.product_view_three);
                    break;
                case TYPE_CARD:
                    view = inflater.inflate(R.layout.item_single_photo, parent, false);
                    mViewHolder.mProductView = view.findViewById(R.id.product_photo_view);
                    mViewHolder.mPriceView = view.findViewById(R.id.item_price_view);
                    mViewHolder.mFromView = view.findViewById(R.id.item_from_view);
                    mViewHolder.mZanView = view.findViewById(R.id.item_zan_view);
                    break;
                case TYPE_VIEW_PAGER:
                    view = inflater.inflate(R.layout.item_view_pager, parent, false);
                    mViewHolder.mViewPager = view.findViewById(R.id.item_view_pager);
                    break;
            }
            mViewHolder.mLogoView = view.findViewById(R.id.item_logo_view);
            mViewHolder.mTitleView = view.findViewById(R.id.item_title_view);
            mViewHolder.mInfoView = view.findViewById(R.id.item_info_view);
            mViewHolder.mFooterView = view.findViewById(R.id.item_footer_view);
            view.setTag(mViewHolder);
        }//有tag时
        else {
            mViewHolder = (ViewHolder) view.getTag();
        }
        //填充数据
        imageLoader.displayImage(mViewHolder.mLogoView, value.logo);
        mViewHolder.mTitleView.setText(value.title);
        mViewHolder.mInfoView.setText((mContext.getString(R.string.tian_qian, value.info)));
        mViewHolder.mFooterView.setText(value.text);
        switch (type) {
            case TYPE_VEDIO:
                String path = "android.resource://" + mContext.getPackageName() + "/" + R.raw.baishi;
                VideoManager videoManager = new VideoManager(mContext,mViewHolder.mVieoContentLayout, value.resource);
                break;
            case TYPE_CARD_MULTI:
                mViewHolder.mPriceView.setText(value.price);
                mViewHolder.mFromView.setText(value.from);
                mViewHolder.multiProductLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (multiClickListener != null) {
                            multiClickListener.onClick(value.url);
                        }
                    }
                });
                mViewHolder.mZanView.setText(mContext.getString(R.string.dian_zan).concat(String.valueOf(value.zan)));
                imageLoader.displayImage(mViewHolder.mProductOneView, value.url.get(0));
                imageLoader.displayImage(mViewHolder.mProductTwoView, value.url.get(1));
                imageLoader.displayImage(mViewHolder.mProductThreeView, value.url.get(2));
                break;
            case TYPE_CARD:
                mViewHolder.mPriceView.setText(value.price);
                mViewHolder.mFromView.setText(value.from);
                mViewHolder.mZanView.setText(mContext.getString(R.string.dian_zan).concat(String.valueOf(value.zan)));
                imageLoader.displayImage(mViewHolder.mProductView, value.url.get(0));
                break;
            case TYPE_VIEW_PAGER:
                List<String> list = value.url;
                adapter = new HomeViewPagerAdapter(mContext, list);
                mViewHolder.mViewPager.setAdapter(adapter);
                mViewHolder.mViewPager.setCurrentItem(list.size() * 100);
                break;
        }
        return view;
    }


    public interface OnMultiClickListener{
        void onClick(List<String> url);
    }

    static class ViewHolder {

        //所有Card共有属性

        CircleImageView mLogoView;

        TextView mTitleView;


        TextView mInfoView;


        TextView mFooterView;

        //Video Card特有属性

        RelativeLayout mVieoContentLayout;


        ImageView mShareView;

        //Video Card外所有Card具有属性

        TextView mPriceView;

        TextView mFromView;

        TextView mZanView;

        //Card Mutil

        HorizontalScrollView multiProductLayout;

        ImageView mProductOneView;

        ImageView mProductTwoView;

        ImageView mProductThreeView;


        ImageView mProductView;
        //View Pager属性

        ViewPager mViewPager;



    }
}
