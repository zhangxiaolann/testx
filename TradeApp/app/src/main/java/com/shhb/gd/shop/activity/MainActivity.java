package com.shhb.gd.shop.activity;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.ali.auth.third.ui.context.CallbackContext;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
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
import com.shhb.gd.shop.listener.ShareOrShowBReceiver;
import com.shhb.gd.shop.module.Constants;
import com.shhb.gd.shop.tools.BaseTools;
import com.shhb.gd.shop.tools.OkHttpUtils;
import com.shhb.gd.shop.tools.PrefShared;
import com.shhb.gd.shop.view.CustomViewPager;
import com.umeng.socialize.UMShareAPI;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.shhb.gd.shop.module.Constants.FIND_BY_BRAND_REFRESH;
import static com.shhb.gd.shop.module.Constants.FIND_BY_CATEGORY_REFRESH;

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
    private boolean isAnimation = false;
    private ArrayList<Fragment> fragments;
    private MainAdapter viewPagerAdapter;
    /** Fragment中单击分享或WebView加载完毕的广播接收器*/
    private ShareOrShowBReceiver shareOrShowBR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        findByData();
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
                .addItem(new BottomNavigationItem(R.mipmap.btn_home_on,"首页").setInactiveIconResource(R.mipmap.btn_home))
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
        fragments.add(Fragment2.newInstance());
        fragments.add(Fragment3.newInstance());
        fragments.add(Fragment4.newInstance());
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

    /**
     * 查询品牌馆和领劵购的tab信息
     */
    private void findByData() {
        int type[] = {1,2};
        OkHttpUtils okHttpUtils = new OkHttpUtils(20);
        JSONObject jsonObject = new JSONObject();
        for(int i = 0;i < type.length;i++){
            if(type[i] == 1){//品牌馆
                jsonObject.put("cname","品牌");
                jsonObject.put("size","10");
                String parameter = BaseTools.encodeJson(jsonObject.toString());
                okHttpUtils.postEnqueue(FIND_BY_BRAND_REFRESH, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        try {
                            String json = response.body().string();
                            json = BaseTools.decryptJson(json);
                            List<String> titles = new ArrayList<>();
                            try {
                                JSONObject jsonObject = JSONObject.parseObject(json);
                                int status = jsonObject.getInteger("status");
                                if (status == 1) {
                                    JSONArray tabArray = jsonObject.getJSONArray("cate");
                                    for (int i = 0; i < tabArray.size(); i++) {
                                        titles.add(tabArray.getString(i));
                                    }
                                }
                                json = JSON.toJSONString(titles);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            PrefShared.saveString(context,"brandTabJson",json);
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }, parameter);
            } else {//领劵购
                jsonObject.put("cname","全部");
                jsonObject.put("size","10");
                jsonObject.put("stype","0");
                String parameter = BaseTools.encodeJson(jsonObject.toString());
                okHttpUtils.postEnqueue(FIND_BY_CATEGORY_REFRESH, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        try {
                            String json = response.body().string();
                            json = BaseTools.decryptJson(json);
                            List<String> titles = new ArrayList<>();
                            try {
                                JSONObject jsonObject = JSONObject.parseObject(json);
                                int status = jsonObject.getInteger("status");
                                if (status == 1) {
                                    JSONArray tabArray = jsonObject.getJSONArray("cate");
                                    for (int i = 0; i < tabArray.size(); i++) {
                                        titles.add(tabArray.getString(i));
                                    }
                                }
                                json = JSON.toJSONString(titles);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            PrefShared.saveString(context,"categoryTabJson",json);
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }, parameter);

            }
        }
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
    protected void onStart() {
        intiShareBReceiver();
        super.onStart();
    }

    @Override
    protected void onStop() {
        unregisterReceiver(shareOrShowBR);
        super.onStop();
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
