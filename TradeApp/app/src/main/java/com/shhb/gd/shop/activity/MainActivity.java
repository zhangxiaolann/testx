package com.shhb.gd.shop.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.ashokvarma.bottomnavigation.BottomNavigationBar;
import com.ashokvarma.bottomnavigation.BottomNavigationItem;
import com.jaeger.library.StatusBarUtil;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.shhb.gd.shop.R;
import com.shhb.gd.shop.adapter.MainAdapter;
import com.shhb.gd.shop.application.MainApplication;
import com.shhb.gd.shop.fragment.Fragment1;
import com.shhb.gd.shop.fragment.Fragment2;
import com.shhb.gd.shop.fragment.Fragment3;
import com.shhb.gd.shop.fragment.Fragment4;
import com.shhb.gd.shop.module.Constants;
import com.shhb.gd.shop.module.UMShare;
import com.shhb.gd.shop.tools.PrefShared;
import com.shhb.gd.shop.view.CustomViewPager;
import com.umeng.socialize.UMShareAPI;

import java.util.ArrayList;

/**
 * Created by superMoon on 2017/4/26.
 */

public class MainActivity extends BaseActivity{

    private View mViewNeedOffset;
    private RelativeLayout mainView;
    /** 底部按钮的ViewPager */
    private static CustomViewPager buttonView;
    /** 底部按钮*/
    private static BottomNavigationBar navigationBar;
    /** 去掉首页ViewPager点击动画*/
    private boolean isAnimation = true;
    private ArrayList<Fragment> fragments;
    private MainAdapter viewPagerAdapter;
    /** Fragment中单击分享或WebView加载完毕的广播接收器*/
    private ShareOrShowBReceiver shareOrShowBR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        initView();
        initBtn();
        initFragments();
        setCurrentPage(0);
    }

    @Override
    protected void createLoading() {
        super.createLoading();
        failureHud = KProgressHUD.create(context).setCustomView(new ImageView(context));
    }

    private void initView() {
        mViewNeedOffset = findViewById(R.id.view_need_offset);
        mainView = (RelativeLayout) findViewById(R.id.main);
        buttonView = (CustomViewPager) findViewById(R.id.viewPager);
        buttonView.setScanScroll(true);
        buttonView.setOffscreenPageLimit(4);
        navigationBar = (BottomNavigationBar) findViewById(R.id.navigation_bar);
        intiShareBReceiver();
    }

    /**
     * 初始化底部菜单按钮
     */
    private void initBtn() {
        navigationBar
                .setInActiveColor(R.color.gray)//设置未选中的Item的颜色，包括图片和文字
                .setActiveColor(R.color.btn_unselect)////设置选中的Item的颜色，包括图片和文字
                .setMode(BottomNavigationBar.MODE_FIXED)//没有切换动画且都有文字（MODE_SHIFTING:换挡模式;MODE_DEFAULT）     如果设置的Mode为MODE_FIXED，将使用BACKGROUND_STYLE_STATIC 。
                .setBackgroundStyle(BottomNavigationBar.BACKGROUND_STYLE_STATIC)//（RIPPLE：波纹动画、STATIC：没有波纹动画） 如果Mode为MODE_SHIFTING将使用BACKGROUND_STYLE_RIPPLE。
                .setBarBackgroundColor(R.color.white);//设置navigationBar的背景颜色
//        BadgeItem badge = new BadgeItem()
//                .setBorderWidth(2)//Badge的Border(边界)宽度
//                .setBorderColor("#FF0000")//Badge的Border颜色
//                .setBackgroundColor(ContextCompat.getColor(context,R.color.btn_unselect))//Badge背景颜色
//                .setGravity(Gravity.RIGHT| Gravity.TOP)//位置，默认右上角
//                .setText("2")//显示的文本
//                .setTextColor(ContextCompat.getColor(context,R.color.white))//文本颜色
//                .setAnimationDuration(1000)
//                .setHideOnSelect(true);//当选中状态时消失，非选中状态显示
        navigationBar
                .addItem(new BottomNavigationItem(R.mipmap.btn_home_on,"抢购").setInactiveIconResource(R.mipmap.btn_home))
                .addItem(new BottomNavigationItem(R.mipmap.btn_9_on,"9块9").setInactiveIconResource(R.mipmap.btn_9))
                .addItem(new BottomNavigationItem(R.mipmap.btn_share_on,"邀请赚").setInactiveIconResource(R.mipmap.btn_share))
                .addItem(new BottomNavigationItem(R.mipmap.btn_me_on,"我的").setInactiveIconResource(R.mipmap.btn_me))
                .initialise();
        navigationBar.setTabSelectedListener(tabSelectedListener);
    }

    /**
     * 底部菜单按钮滑动事件
     */
    private BottomNavigationBar.OnTabSelectedListener tabSelectedListener = new BottomNavigationBar.OnTabSelectedListener() {
        @Override
        public void onTabSelected(int position) {//未选中 -> 选中
            buttonView.setCurrentItem(position,isAnimation);
        }

        @Override
        public void onTabUnselected(int position) {//选中 -> 未选中

        }

        @Override
        public void onTabReselected(int position) {//选中 -> 选中

        }
    };

    /**
     * 初始化Fragment
     */
    private void initFragments() {
        fragments = new ArrayList<>();
        fragments.add(Fragment1.newInstance());
        fragments.add(new Fragment2());
        fragments.add(new Fragment3());
        fragments.add(new Fragment4());
        buttonView.addOnPageChangeListener(pageChangeListener);
        viewPagerAdapter = new MainAdapter(getSupportFragmentManager(),fragments);
        buttonView.setAdapter(viewPagerAdapter);
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
            navigationBar.selectTab(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

    /**
     * 设置启动首次展示的页面
     * @param i
     */
    private void setCurrentPage(int i) {
        navigationBar.setFirstSelectedPosition(i);
        buttonView.setCurrentItem(i,isAnimation);
    }

    @Override
    protected void setStatusBar() {
        StatusBarUtil.setTranslucentForImageView(this, 0, mViewNeedOffset);
    }

    /**
     * 创建分享的监听
     */
    private void intiShareBReceiver() {
        IntentFilter intentFilter = new IntentFilter(Constants.SENDMSG_SHARE);
        shareOrShowBR = new ShareOrShowBReceiver();
        registerReceiver(shareOrShowBR, intentFilter);
    }

    class ShareOrShowBReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                String type = intent.getAction();
                if(TextUtils.equals(type,Constants.SENDMSG_SHARE)) {
                    String result = intent.getStringExtra("result");
                    Log.e("友盟分享",result);
                    if (null != result) {
                        String userId = PrefShared.getString(context, "userId");
                        JSONObject jsonObject = JSONObject.parseObject(result);
                        String numId = "", shareTitle = "", shareContent = "", shareImg = "", shareUrl = "";
                        numId = jsonObject.getString("numId");
                        shareTitle = jsonObject.getString("title");
                        shareContent = jsonObject.getString("shareContent");
                        shareImg = jsonObject.getString("shareImg");
                        shareUrl = jsonObject.getString("shareUrl");
//                        if (null != userId) {
//                            shareUrl = jsonObject.getString("share_url") + "?uid=" + PrefShared.getString(context, "userId");
//                        } else {
//                            shareUrl = "http://a.app.qq.com/o/simple.jsp?pkgname=com.shhb.gd.shop";
//                        }
                        new UMShare(MainActivity.this, hud, failureHud, numId).share(shareTitle, shareContent, shareImg, shareUrl);
                    }
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    /**
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);//友盟精简版的回调
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(shareOrShowBR);
        super.onDestroy();
    }

    private long mExitTime;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
            } else {
                MainApplication.exit();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
