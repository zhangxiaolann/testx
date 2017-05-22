package com.shhb.gd.shop.adapter;

import android.app.Activity;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.shhb.gd.shop.R;
import com.shhb.gd.shop.module.BannerInfo;
import com.shhb.gd.shop.tools.BaseTools;
import com.shhb.gd.shop.view.CustomViewPager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    /** 哪一个Fragment*/
    private final int mType;
    /** banner的View标识 */
    private static final int TYPE_BANNER = 0;
    /** 中间区域的View标识 */
    private static final int TYPE_GROUP = 1;
    /** recycler的ItemView标识 */
    private static final int TYPE_RECYCLER = 2;
    /** banner的数据 */
    private final List<BannerInfo> bannerInfos;
    /** recycler的数据 */
    private final List<Map<String, Object>> listMap;
    private static OnClickListener onClickListener;
    private LoopViewPagerAdapter mPagerAdapter;
    private int width;


    public RecyclerViewAdapter(int type) {
        mType = type;
        listMap = new ArrayList<>();
        bannerInfos = new ArrayList<>();
    }

    /**
     * 通过异步请求将列表的数据填充到Adapter
     * @param datas
     * @param mPageIndex 1是刷新
     */
    public void addRecyclerData(List<Map<String, Object>> datas, int mPageIndex) {
        if(mPageIndex == 1){
            listMap.clear();
        }
        listMap.addAll(datas);
        notifyDataSetChanged();
    }

    /** 通过异步请求将Banner的数据填充到Adapter */
    public void addBannerData(List<BannerInfo> datas) {
        bannerInfos.clear();
        bannerInfos.addAll(datas);
    }

    @Override
    public int getItemCount() {
        return listMap.size();
    }

    /**
     * 不同的Fragment给出不同的标识
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        if (position == 0 && mType == 0){
            return TYPE_BANNER;
        } else if(position == 1 && mType == 0){
            return TYPE_GROUP;
        } else {
            return TYPE_RECYCLER;
        }
    }

    /** 初始化布局包括布局的位置和大小 */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        int type = getItemViewType(i);
        View itemView;
        switch (type){
            case TYPE_BANNER:
                itemView = inflate(viewGroup, R.layout.banner_view);
                return new BannerHolder(itemView);
            case TYPE_GROUP:
                itemView = inflate(viewGroup, R.layout.group_view);
                return new GroupHolder(itemView);
            case TYPE_RECYCLER:
                itemView = inflate(viewGroup, R.layout.recycler_item);
                width = (BaseTools.getWindowsWidth((Activity) itemView.getContext()) / 2);
                itemView.setLayoutParams(new LinearLayout.LayoutParams(width,LinearLayout.LayoutParams.WRAP_CONTENT));
                return new RecyclerHolder(itemView);
        }
        throw new IllegalArgumentException("Wrong type!");
    }

    /**
     * 填充页面的方法
     * @param parent
     * @param layoutRes
     * @return
     */
    private View inflate(ViewGroup parent, int layoutRes) {
        return LayoutInflater.from(parent.getContext()).inflate(layoutRes, parent, false);
    }

    /** 将数据绑定到对应的View上 */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        int type = getItemViewType(i);
        switch (type) {
            case TYPE_BANNER:
                onBindBannerHolder((BannerHolder) viewHolder,i);
                break;
            case TYPE_GROUP:
//                onBindGroupHolder((GroupHolder) viewHolder);
                break;
            case TYPE_RECYCLER:
                onBindRecyclerHolder((RecyclerHolder) viewHolder,i);
                break;
        }
    }

    /**
     * 将数据填充到banner上
     * @param viewHolder
     * @param position
     */
    private void onBindBannerHolder(BannerHolder viewHolder, int position) {
        if (viewHolder.viewPager.getAdapter() == null) {
            mPagerAdapter = new LoopViewPagerAdapter(viewHolder.viewPager, viewHolder.indicators);
            viewHolder.viewPager.setAdapter(mPagerAdapter);
            viewHolder.viewPager.addOnPageChangeListener(mPagerAdapter);
            mPagerAdapter.setList(bannerInfos);
        } else {
            mPagerAdapter.setList(bannerInfos);
        }
    }

    /**
     * 将数据填充到recycler上
     * @param viewHolder
     * @param position
     */
    private void onBindRecyclerHolder(RecyclerHolder viewHolder, int position) {
        width = (BaseTools.getWindowsWidth((Activity) viewHolder.itemView.getContext()) / 2);
        width = width - (width % 10);
        String url = listMap.get(position).get("imgUrl") + "_" + width + "x" + width + ".jpg";
        Glide.with(viewHolder.itemView.getContext())
                .load(url)
                .placeholder(R.mipmap.error_z)
                .error(R.mipmap.error_z)//加载出错的图片
                .priority(Priority.HIGH)//优先加载
                .diskCacheStrategy(DiskCacheStrategy.ALL)//设置缓存策略
                .into(viewHolder.goodsImg);
        String type = listMap.get(position).get("type") + "";
        if(TextUtils.equals(type,"0")){
            viewHolder.type.setText("淘宝");
        } else {
            viewHolder.type.setText("天猫");
        }
        viewHolder.title.setText("\u3000\u3000 "+listMap.get(position).get("title"));
        viewHolder.cPrice.setText("￥" + listMap.get(position).get("cPrice") + "");
        viewHolder.oPrice.setText(listMap.get(position).get("oPrice") + "");
        viewHolder.bNum.setText("(" + listMap.get(position).get("bNum") + "人已购买)");
        viewHolder.rebate.setText("约返现" + listMap.get(position).get("rebate") + "元");
    }

    static class BannerHolder extends RecyclerView.ViewHolder {
        CustomViewPager viewPager;
        ViewGroup indicators;

        /** 获取到banner中的每一个View */
        public BannerHolder(View itemView) {
            super(itemView);
            viewPager = (CustomViewPager) itemView.findViewById(R.id.viewPager);
            viewPager.setScanScroll(true);
            indicators = (ViewGroup) itemView.findViewById(R.id.indicators);
        }
    }

    class GroupHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView group11,group12,group13,group14;
        private RelativeLayout group21,group22;

        /** 获取到中间区域中的每一个View */
        public GroupHolder(View itemView){
            super(itemView);
            group11 = (TextView) itemView.findViewById(R.id.group_1_1);
            group12 = (TextView) itemView.findViewById(R.id.group_1_2);
            group13 = (TextView) itemView.findViewById(R.id.group_1_3);
            group14 = (TextView) itemView.findViewById(R.id.group_1_4);
            group21 = (RelativeLayout) itemView.findViewById(R.id.group_2_1);
            group22 = (RelativeLayout) itemView.findViewById(R.id.group_2_2);
            group11.setOnClickListener(this);
            group12.setOnClickListener(this);
            group13.setOnClickListener(this);
            group14.setOnClickListener(this);
            group21.setOnClickListener(this);
            group22.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.group_1_1:
                    onClickListener.onClick(view);
                    break;
                case R.id.group_1_2:
                    onClickListener.onClick(view);
                    break;
                case R.id.group_1_3:
                    onClickListener.onClick(view);
                    break;
                case R.id.group_1_4:
                    onClickListener.onClick(view);
                    break;
                case R.id.group_2_1:
                    onClickListener.onClick(view);
                    break;
                case R.id.group_2_2:
                    onClickListener.onClick(view);
                    break;
            }
        }
    }

    class RecyclerHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private LinearLayout goodsMain;
        private ImageView goodsImg,shareImg;
        private TextView type,title,cPrice,oPrice,bNum,rebate;

        /** 获取到recycler中的每一个View */
        public RecyclerHolder(View itemView) {
            super(itemView);
            int width = (int) (BaseTools.getWindowsWidth((Activity) itemView.getContext()) / 2);
            goodsMain = (LinearLayout) itemView.findViewById(R.id.goods_main);
//            goodsMain.setLayoutParams(new LinearLayout.LayoutParams(width, LinearLayout.LayoutParams.WRAP_CONTENT));
            goodsImg = (ImageView) itemView.findViewById(R.id.goods_img);
            shareImg = (ImageView) itemView.findViewById(R.id.share_img);
            type = (TextView) itemView.findViewById(R.id.goods_type);
            title = (TextView) itemView.findViewById(R.id.goods_title);
            cPrice = (TextView) itemView.findViewById(R.id.cPrice);
            oPrice = (TextView) itemView.findViewById(R.id.oPrice);
            oPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG | Paint.ANTI_ALIAS_FLAG);
            bNum = (TextView) itemView.findViewById(R.id.bNum);
            rebate = (TextView) itemView.findViewById(R.id.rebate);
            shareImg.setOnClickListener(this);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.share_img:
                    onClickListener.onClick(view,getPosition(),listMap);
                    break;
                default:
                    onClickListener.onClick(view,getPosition(),listMap);
                    break;
            }
        }
    }

    /** 定义所有单击事件接口 */
    public interface OnClickListener {
        void onClick(View view,int position,List<Map<String,Object>> listMap);
        void onClick(View view);
    }

    /** 所有单击事件的处理方法 */
    public void setOnClickListener(final OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }


}
