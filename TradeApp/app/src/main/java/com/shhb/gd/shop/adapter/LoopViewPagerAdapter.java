package com.shhb.gd.shop.adapter;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.shhb.gd.shop.R;
import com.shhb.gd.shop.module.BannerInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aspsine on 2015/9/7.
 */
public class LoopViewPagerAdapter extends BaseLoopPagerAdapter {

    private final List<BannerInfo> bannerInfos;

    private final ViewGroup mIndicators;

    private int mLastPosition;

    public LoopViewPagerAdapter(ViewPager viewPager, ViewGroup indicators) {
        super(viewPager);
        mIndicators = indicators;
        bannerInfos = new ArrayList<>();
    }

    public void setList(List<BannerInfo> heroes) {
        bannerInfos.clear();
        bannerInfos.addAll(heroes);
        notifyDataSetChanged();
    }

    /**
     * oh shit! An indicator view is badly needed!
     * this shit have no animation at all.
     */
    private void initIndicators() {
        if (mIndicators.getChildCount() != bannerInfos.size() && bannerInfos.size() > 1) {
            mIndicators.removeAllViews();
            Resources res = mIndicators.getResources();
            int size = res.getDimensionPixelOffset(R.dimen.indicator_size);
            int margin = res.getDimensionPixelOffset(R.dimen.indicator_margin);
            for (int i = 0; i < getPagerCount(); i++) {
                ImageView indicator = new ImageView(mIndicators.getContext());
                indicator.setAlpha(180);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(size, size);
                lp.setMargins(margin, 0, 0, 0);
                lp.gravity = Gravity.CENTER;
                indicator.setLayoutParams(lp);
                Drawable drawable = res.getDrawable(R.drawable.selector_indicator);
                indicator.setImageDrawable(drawable);
                mIndicators.addView(indicator);
            }
        }
    }

    @Override
    public void notifyDataSetChanged() {
        initIndicators();
        super.notifyDataSetChanged();
    }

    @Override
    public int getPagerCount() {
        return bannerInfos.size();
    }

    @Override
    public BannerInfo getItem(int position) {
        return bannerInfos.get(position);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.banner_item, parent, false);
            holder = new ViewHolder();
            holder.ivBanner = (ImageView) convertView.findViewById(R.id.ivBanner);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), bannerInfos.get(position).getName(), Toast.LENGTH_SHORT).show();
            }
        });
        BannerInfo character = bannerInfos.get(position);
        Glide.with(parent.getContext())
                .load(character.getAvatar())
                .placeholder(R.mipmap.error_c)
                .error(R.mipmap.error_c)//加载出错的图片
                .priority(Priority.HIGH)//优先加载
                .diskCacheStrategy(DiskCacheStrategy.ALL)//设置缓存策略
                .into(holder.ivBanner);
        return convertView;
    }

    @Override
    public void onPageItemSelected(int position) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            mIndicators.getChildAt(mLastPosition).setActivated(false);
            mIndicators.getChildAt(position).setActivated(true);
        }
        mLastPosition = position;
    }

    public static class ViewHolder {
        ImageView ivBanner;
    }
}
