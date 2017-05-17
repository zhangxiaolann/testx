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
    private List<Integer> types = new ArrayList<>();
    /** banner的数据 */
    private final List<BannerInfo> bannerInfos;
    /** recycler的数据 */
    private List<Map<String, Object>> listMap;
    private static OnShareClickListener mShareClickListener;
    private static OnItemClickListener mItemClickListener;


    public RecyclerViewAdapter(int type) {
        this.mType = type;
        bannerInfos = new ArrayList<>();
    }

    /** 通过异步请求将数据填充到Adapter */
    public void addData(List<Map<String, Object>> listMap) {
        if(null == listMap || listMap.isEmpty()){
            return;
        }
        if(null == this.listMap){
            this.listMap = new ArrayList<>();
        }
        this.listMap.addAll(listMap);
    }

    @Override
    public int getItemCount() {
        if(null == this.listMap){
            this.listMap = new ArrayList<>();
        }
        return listMap.size();
    }

    /**
     * 不同的Fragment给出不同的标识
     * @param position
     * @return
     */
//    @Override
//    public int getItemViewType(int position) {
//        Log.e("getItemViewType的值",position+"");
//        if (position == mType) {
//            return TYPE_GROUP;
//        } /*else if (position == 1) {
//            return TYPE_GROUP;
//        }*/ else {
//            return TYPE_RECYCLER;
//        }
//    }

    /** 初始化布局包括布局的位置和大小 */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        int type = getItemViewType(i);
        View itemView = null;
        switch (type){
            case 0:
                itemView = inflate(viewGroup,R.layout.recycler_item);
                return new RecyclerHolder(itemView);
//            case TYPE_BANNER:
//                if(mType == 0){
//                    itemView = inflate(viewGroup,R.layout.banner_view);
//                    return new BannerHolder(itemView);
//                }
//            case TYPE_GROUP:
//                if(mType == 0){
//                    itemView = inflate(viewGroup,R.layout.group_view);
//                    return new GroupHolder(itemView);
//                }
//            case TYPE_RECYCLER:
//                itemView = inflate(viewGroup,R.layout.recycler_item);
//                return new RecyclerHolder(itemView);
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

    static class BannerHolder extends RecyclerView.ViewHolder {
        CustomViewPager viewPager;
        ViewGroup indicators;

        /** 获取到banner中的每一个View */
        public BannerHolder(View itemView) {
            super(itemView);
            viewPager = (CustomViewPager) itemView.findViewById(R.id.viewPager);
            indicators = (ViewGroup) itemView.findViewById(R.id.indicators);
        }
    }

    static class GroupHolder extends RecyclerView.ViewHolder{
        private LinearLayout groupMain;

        /** 获取到中间区域中的每一个View */
        public GroupHolder(View itemView){
            super(itemView);
            groupMain = (LinearLayout) itemView.findViewById(R.id.group_main);
//            groupMain.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
        }
    }

    class RecyclerHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private LinearLayout goodsMain;
        private ImageView goodsImg,shareImg;
        private RelativeLayout goodsView;
        private TextView type,title,cPrice,oPrice,bNum,rebate;

        /** 获取到recycler中的每一个View */
        public RecyclerHolder(View itemView) {
            super(itemView);
            int width = (int) (BaseTools.getWindowsWidth((Activity) itemView.getContext()) / 2);
            goodsMain = (LinearLayout) itemView.findViewById(R.id.goods_main);
//            goodsMain.setLayoutParams(new LayoutParams(width, LayoutParams.WRAP_CONTENT));
            goodsView = (RelativeLayout) itemView.findViewById(R.id.goods_view);
//            goodsView.setLayoutParams(new LayoutParams(width, width));
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
                    mShareClickListener.onShareClick(view,getPosition(),listMap);
                    break;
                default:
                    mItemClickListener.onItemClick(view, getPosition(),listMap);
                    break;
            }
        }
    }

    /** 定义分享的单击接口 */
    public interface OnShareClickListener {
        void onShareClick(View view, int position,List<Map<String,Object>> listMap);
    }

    /** 定义item的单击接口 */
    public interface OnItemClickListener {
        void onItemClick(View view, int position,List<Map<String,Object>> listMap);
    }

    /** 分享按钮单击事件 */
    public void setShareClickListener(final  OnShareClickListener mShareClickListener){
        this.mShareClickListener = mShareClickListener;
    }

    /** item按钮单击事件 */
    public void setOnItemClickListener(final OnItemClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
    }

    /** 将数据绑定到对应的View上 */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        int type = getItemViewType(position);
        switch (type) {
            case 0:
                onBindRecyclerHolder((RecyclerHolder) viewHolder,position);
                break;
//            case TYPE_BANNER:
////                onBindBannerHolder((BannerHolder) viewHolder);
//                break;
//            case TYPE_GROUP:
////                onBindGroupHolder((GroupHolder) viewHolder, getGroupPosition(position));
//                break;
//            case TYPE_RECYCLER:
//                onBindRecyclerHolder((RecyclerHolder) viewHolder,position);
//                break;
        }
    }

    /**
     * 将数据填充到banner上
     * @param viewHolder
     */
    private void onBindBannerHolder(BannerHolder viewHolder) {

    }

    /**
     * 将数据填充到recycler上
     * @param viewHolder
     * @param position
     */
    private void onBindRecyclerHolder(RecyclerHolder viewHolder, int position) {
        int width = (int) (BaseTools.getWindowsWidth((Activity) viewHolder.itemView.getContext()) / 2);
        width = width - (width % 10);
        String url = listMap.get(position).get("imgUrl") + "_" + width + "x" + width + ".jpg";
//        String url = listMap.get(position).get("imgUrl")+"";
        Log.e("图片的地址为：", url);
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
}
