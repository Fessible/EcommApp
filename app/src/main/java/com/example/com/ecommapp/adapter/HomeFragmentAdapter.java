package com.example.com.ecommapp.adapter;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.IntDef;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.com.ecommapp.R;
import com.example.com.ecommapp.base.BaseViewHolder;
import com.example.com.ecommapp.module.recommand.RecommendValue;
import com.example.com.support.imageloader.ImageLoaderManager;
import com.example.com.support.video.NiceVideoPlayer;
import com.example.com.support.video.TxVideoPlayerController;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import cn.jzvd.JZVideoPlayerStandard;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.com.ecommapp.adapter.HomeFragmentAdapter.Item.TYPE_CARD;
import static com.example.com.ecommapp.adapter.HomeFragmentAdapter.Item.TYPE_CARD_MULTI;
import static com.example.com.ecommapp.adapter.HomeFragmentAdapter.Item.TYPE_VIDEO;
import static com.example.com.ecommapp.adapter.HomeFragmentAdapter.Item.TYPE_VIEW_PAGER;

/**
 * 主页适配器
 * Created by rhm on 2018/1/19.
 */

public class HomeFragmentAdapter extends RecyclerView.Adapter<HomeFragmentAdapter.HomeHolder> {

    private Context context;
    private final List<Item> itemList = new ArrayList<>();
    private OnMultiClickListener onMultiClickListener;
    private List<RecommendValue> recommendValueList = new ArrayList<>();
    private NiceVideoPlayer niceVideoPlayer;

    public HomeFragmentAdapter(Context context) {
        this.context = context;
        registerAdapterDataObserver(observer);
    }

    public void setData(List<RecommendValue> recommendValueList) {
        this.recommendValueList = recommendValueList;
    }

    public void setMultiClickListener(OnMultiClickListener listener) {
        this.onMultiClickListener = listener;
    }

    public NiceVideoPlayer getNiceVideoPlayer() {
        return niceVideoPlayer;
    }

    private RecyclerView.AdapterDataObserver observer = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            itemList.clear();
            if (recommendValueList != null && !recommendValueList.isEmpty()) {
                for (RecommendValue value : recommendValueList) {
                    switch (value.type) {
                        case TYPE_CARD:
                            itemList.add(new CardItem(value));
                            break;
                        case TYPE_CARD_MULTI:
                            itemList.add(new MultiCardItem(value));
                            break;
                        case TYPE_VIDEO:
                            itemList.add(new VideoItem(value));
                            break;
                        case TYPE_VIEW_PAGER:
                            itemList.add(new ViewPagerItem(value));
                            break;
                    }
                }
            }
        }
    };

    @Override
    public int getItemViewType(int position) {
        return itemList.get(position).getViewType();
    }

    @Override
    public HomeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        HomeHolder holder = null;
        switch (viewType) {
            case TYPE_CARD:
                holder = new CardHolder(context, parent);
                break;
            case TYPE_CARD_MULTI:
                holder = new MultiHolder(context, parent);
                break;
            case TYPE_VIDEO:
                holder = new VideoHolder(context, parent);
                break;
            case TYPE_VIEW_PAGER:
                holder = new PagerHolder(context, parent);
                break;
        }
        return holder;
    }

    @Override
    public void onBindViewHolder(HomeHolder holder, int position) {
        holder.bindHolder(context, itemList.get(position));
    }


    @Override
    public int getItemCount() {
        return itemList.size();
    }

    //单图
    class CardHolder extends HomeHolder<CardItem> {
        @BindView(R.id.item_logo_view)
        CircleImageView mLogoView;

        @BindView(R.id.item_title_view)
        TextView mTitleView;

        @BindView(R.id.item_info_view)
        TextView mInfoView;

        @BindView(R.id.item_footer_view)
        TextView mFooterView;

        @BindView(R.id.product_photo_view)
        ImageView mProductView;

        @BindView(R.id.item_price_view)
        TextView mPriceView;

        @BindView(R.id.item_from_view)
        TextView mFromView;

        @BindView(R.id.item_zan_view)
        TextView mZanView;

        public CardHolder(Context context, ViewGroup parent) {
            super(context, parent, R.layout.item_single_photo);
        }

        @Override
        public void bindHolder(Context context, CardItem item) {
            RecommendValue value = item.value;
            ImageLoaderManager.getInstance(context).displayImage(mLogoView, value.logo);
            mTitleView.setText(value.title);
            mInfoView.setText((context.getString(R.string.tian_qian, value.info)));
            mFooterView.setText(value.text);

            mPriceView.setText(value.price);
            mFromView.setText(value.from);
            mZanView.setText(context.getString(R.string.dian_zan).concat(String.valueOf(value.zan)));
            ImageLoaderManager.getInstance(context).displayImage(mProductView, value.url.get(0));

        }
    }

    //多图
    class MultiHolder extends HomeHolder<MultiCardItem> {

        @BindView(R.id.item_product_layout)
        HorizontalScrollView multiProductLayout;

        @BindView(R.id.product_view_one)
        ImageView mProductOneView;

        @BindView(R.id.product_view_two)
        ImageView mProductTwoView;

        @BindView(R.id.product_view_three)
        ImageView mProductThreeView;

        @BindView(R.id.item_price_view)
        TextView mPriceView;

        @BindView(R.id.item_from_view)
        TextView mFromView;

        @BindView(R.id.item_zan_view)
        TextView mZanView;

        @BindView(R.id.item_logo_view)
        CircleImageView mLogoView;

        @BindView(R.id.item_title_view)
        TextView mTitleView;

        @BindView(R.id.item_info_view)
        TextView mInfoView;

        @BindView(R.id.item_footer_view)
        TextView mFooterView;

        public MultiHolder(Context context, ViewGroup parent) {
            super(context, parent, R.layout.item_multi_photo);
        }

        @Override
        public void bindHolder(Context context, MultiCardItem item) {
            RecommendValue value = item.value;
            ImageLoaderManager.getInstance(context).displayImage(mLogoView, value.logo);
            mTitleView.setText(value.title);
            mInfoView.setText((context.getString(R.string.tian_qian, value.info)));
            mFooterView.setText(value.text);

            mPriceView.setText(value.price);
            mFromView.setText(value.from);
            mZanView.setText(context.getString(R.string.dian_zan).concat(String.valueOf(value.zan)));
            ImageLoaderManager imageLoader = ImageLoaderManager.getInstance(context);
            imageLoader.displayImage(mProductOneView, value.url.get(0));
            imageLoader.displayImage(mProductTwoView, value.url.get(1));
            imageLoader.displayImage(mProductThreeView, value.url.get(2));
        }
    }

    public void setNiceVideoPlayer(NiceVideoPlayer niceVideoPlayer) {
        this.niceVideoPlayer = niceVideoPlayer;
    }

    //视频
    class VideoHolder extends HomeHolder<VideoItem> {

//        @BindView(R.id.video_view)
//        NiceVideoPlayer niceVideoPlayer;
        @BindView(R.id.videoplayer)
        JZVideoPlayerStandard jzVideoPlayerStandard;

        @BindView(R.id.item_logo_view)
        ImageView logo;

        public VideoHolder(Context context, ViewGroup parent) {

            super(context, parent, R.layout.item_video_layout);
        }

        @Override
        public void bindHolder(Context context, VideoItem item) {
            jzVideoPlayerStandard.setUp("http://jzvd.nathen.cn/c6e3dc12a1154626b3476d9bf3bd7266/6b56c5f0dc31428083757a45764763b0-5287d2089db37e62345123a1be272f8b.mp4"
                    , JZVideoPlayerStandard.SCREEN_WINDOW_NORMAL, "饺子闭眼睛");
            jzVideoPlayerStandard.thumbImageView.setImageURI(Uri.parse("http://p.qpic.cn/videoyun/0/2449_43b6f696980311e59ed467f22794e792_1/640"));
//            setNiceVideoPlayer(niceVideoPlayer);
//            RecommendValue value = item.value;
//            ImageLoaderManager.getInstance(context).displayImage(logo, value.logo);
//            TxVideoPlayerController controller = new TxVideoPlayerController(context);
//            controller.setUrl(value.resource);
//            controller.setTitle("");
//            Glide.with(context).load(value.url).into(controller.imageView());
//            niceVideoPlayer.setController(controller);
        }
    }

    //循环轮播
    class PagerHolder extends HomeHolder<ViewPagerItem> {
        @BindView(R.id.item_logo_view)
        CircleImageView mLogoView;

        @BindView(R.id.item_title_view)
        TextView mTitleView;

        @BindView(R.id.item_info_view)
        TextView mInfoView;

        @BindView(R.id.item_footer_view)
        TextView mFooterView;

        @BindView(R.id.item_view_pager)
        ViewPager viewPager;

        public PagerHolder(Context context, ViewGroup parent) {
            super(context, parent, R.layout.item_view_pager);
        }

        @Override
        public void bindHolder(Context context, ViewPagerItem item) {
            RecommendValue value = item.value;
            ImageLoaderManager.getInstance(context).displayImage(mLogoView, value.logo);
            mTitleView.setText(value.title);
            mInfoView.setText((context.getString(R.string.tian_qian, value.info)));
            mFooterView.setText(value.text);

            List<String> list = value.url;
            HomeViewPagerAdapter adapter = new HomeViewPagerAdapter(context, list);
            viewPager.setAdapter(adapter);
            viewPager.setCurrentItem(list.size() * 100);
        }
    }


    class VideoItem implements Item {
        private RecommendValue value;

        public VideoItem(RecommendValue value) {
            this.value = value;
        }

        @Override
        public int getViewType() {
            return TYPE_VIDEO;
        }
    }

    class CardItem implements Item {
        private RecommendValue value;

        public CardItem(RecommendValue value) {
            this.value = value;
        }

        @Override
        public int getViewType() {
            return TYPE_CARD;
        }
    }

    class MultiCardItem implements Item {
        private RecommendValue value;

        public MultiCardItem(RecommendValue value) {
            this.value = value;
        }

        @Override
        public int getViewType() {
            return TYPE_CARD_MULTI;
        }
    }

    class ViewPagerItem implements Item {

        private RecommendValue value;

        public ViewPagerItem(RecommendValue value) {
            this.value = value;
        }

        @Override
        public int getViewType() {
            return TYPE_VIEW_PAGER;
        }
    }

    interface Item {
        int TYPE_VIDEO = 0x00;
        int TYPE_CARD = 0x02;
        int TYPE_CARD_MULTI = 0x01;
        int TYPE_VIEW_PAGER = 0x03;

        @IntDef({TYPE_VIDEO,
                TYPE_CARD,
                TYPE_CARD_MULTI,
                TYPE_VIEW_PAGER})

        @Retention(RetentionPolicy.SOURCE)
        @interface ViewType {
        }

        @ViewType
        int getViewType();
    }

    public abstract class HomeHolder<II extends Item> extends BaseViewHolder {
        public HomeHolder(Context context, ViewGroup parent, int adapterLayoutResId) {
            super(context, parent, adapterLayoutResId);
        }

        public abstract void bindHolder(Context context, II item);
    }

    public interface OnMultiClickListener {
        void onClick(List<String> url);
    }


}
