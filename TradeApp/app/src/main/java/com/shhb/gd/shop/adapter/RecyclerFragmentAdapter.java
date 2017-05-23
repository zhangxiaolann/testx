package com.shhb.gd.shop.adapter;

import android.app.Activity;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.shhb.gd.shop.R;
import com.shhb.gd.shop.tools.BaseTools;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RecyclerFragmentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final int fType;
    /** recycler的数据 */
    private final List<Map<String, Object>> listMap;
    private static OnClickListener onClickListener;
    private int width;

    public RecyclerFragmentAdapter(int type){
        fType = type;
        listMap = new ArrayList<>();
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

    @Override
    public int getItemCount() {
        return listMap.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = inflate(viewGroup, R.layout.recycler_item);
        return new RecyclerHolder(itemView);
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

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int i) {
        onBindRecyclerHolder((RecyclerHolder) viewHolder,i);
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
        if(fType == 1){
            viewHolder.rebate.setText("约返现" + listMap.get(position).get("rebate") + "元");
        } else {
            viewHolder.rebate.setText(listMap.get(position).get("reduce") + "元优惠券");
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
            if(fType == 1){
                rebate.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.holo_green_dark));
            } else {
                rebate.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.holo_red_dark));
                rebate.setBackgroundResource(R.drawable.volume_view_bg);
            }
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
    }

    /** 所有单击事件的处理方法 */
    public void setOnClickListener(final OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }
}
