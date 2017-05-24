package com.shhb.gd.shop.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ali.auth.third.ui.context.CallbackContext;
import com.shhb.gd.shop.R;
import com.shhb.gd.shop.adapter.MainAdapter;
import com.shhb.gd.shop.fragment.Fragment5;
import com.shhb.gd.shop.fragment.RecyclerFragment;
import com.shhb.gd.shop.listener.ShareOrShowBReceiver;
import com.shhb.gd.shop.module.Constants;
import com.shhb.gd.shop.view.CustomViewPager;
import com.umeng.socialize.UMShareAPI;

import java.util.ArrayList;

/**
 * Created by superMoon on 2017/5/22.
 */

public class RecyclerActivity extends BaseActivity implements View.OnClickListener{
    private int type;
    private TextView title;
    private LinearLayout onBack;
    private ArrayList<Fragment> fragments = new ArrayList<>();
    private MainAdapter viewPagerAdapter;
    private CustomViewPager viewPager;
    /** Fragment中单击分享的广播接收器*/
    private ShareOrShowBReceiver shareOrShowBR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_activity);
        type = Integer.parseInt(getIntent().getStringExtra("type"));
        initView();
        intiShareBReceiver();
    }

    private void initView() {
        viewPager = (CustomViewPager) findViewById(R.id.viewPager);
        viewPager.setCurrentItem(0,false);
        viewPager.setScanScroll(true);
        onBack = (LinearLayout) findViewById(R.id.onBack);
        onBack.setOnClickListener(this);
        title = (TextView) findViewById(R.id.webView_title);
        if(1 == type){
            title.setText("品牌馆");
            fragments.add(Fragment5.newInstance(type));
        } else if(2 == type) {
            title.setText("超值优惠卷");
            fragments.add(Fragment5.newInstance(type));
        } else {
            title.setText("新品特惠");
            fragments.add(RecyclerFragment.newInstance(type+""));
        }
        viewPager.addOnPageChangeListener(pageChangeListener);
        viewPagerAdapter = new MainAdapter(getSupportFragmentManager(),fragments);
        viewPager.setAdapter(viewPagerAdapter);
    }

    /**
     * Fragment滑动事件
     */
    private ViewPager.OnPageChangeListener pageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode){
            case KeyEvent.KEYCODE_BACK:
                finish();
                break;
        }
        return true;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.onBack:
                finish();
                break;
        }
    }

    /**
     * 创建分享的监听
     */
    private void intiShareBReceiver() {
        IntentFilter intentFilter = new IntentFilter(Constants.SENDMSG_SHARE);
        shareOrShowBR = new ShareOrShowBReceiver();
        registerReceiver(shareOrShowBR, intentFilter);
    }

    /**
     * 淘宝和友盟的回调
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        CallbackContext.onActivityResult(requestCode, resultCode, data);//阿里的回调
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);//友盟精简版的回调
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(shareOrShowBR);
        super.onDestroy();
    }
}
