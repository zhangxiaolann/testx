package com.shhb.gd.shop.activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shhb.gd.shop.R;
import com.shhb.gd.shop.fragment.Fragment3;
import com.shhb.gd.shop.module.Constants;
import com.shhb.gd.shop.tools.BaseTools;
import com.shhb.gd.shop.tools.OkHttpUtils;
import com.shhb.gd.shop.tools.PrefShared;
import com.shhb.gd.shop.view.CustomViewPager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by superMoon on 2017/5/22.
 */

public class RecyclerActivity extends BaseActivity implements View.OnClickListener{
    private int type;
    private TextView title;
    private LinearLayout onBack;
    private CustomViewPager viewPager;
    private TabLayout tabLayout;
    private Adapter mAdapter;
    /**
     * 请求页码
     */
    private int mPageIndex = 1;
    /**
     * 每页请求数量
     */
    private final static int pageNum = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_activity);
        type = Integer.parseInt(getIntent().getStringExtra("type"));
        initView();
        findByData();
    }

    private void initView() {
        onBack = (LinearLayout) findViewById(R.id.onBack);
        onBack.setOnClickListener(this);
        title = (TextView) findViewById(R.id.webView_title);
        if(0 == type){
            title.setText("品牌馆");
        } else if(1 == type){
            title.setText("新品特惠");
        } else {
            title.setText("超值优惠卷");
        }
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        viewPager = (CustomViewPager) findViewById(R.id.viewPager);
        viewPager.setCurrentItem(0,false);
        viewPager.setScanScroll(true);
        mAdapter = new Adapter(getSupportFragmentManager());
        viewPager.setAdapter(mAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void findByData() {
        OkHttpUtils okHttpUtils = new OkHttpUtils(20);
        JSONObject jsonObject = new JSONObject();
        String url = "";
        if(0 == type){//品牌馆
            url = Constants.FIND_BY_BRAND_REFRESH;
            jsonObject.put("cname","品牌");
            jsonObject.put("size","1");
        } else if(1 == type){//新品特惠

        } else {//领劵购

        }
        Log.e("品牌馆列表传入的参数",jsonObject.toString());
        String parameter = BaseTools.encodeJson(jsonObject.toString());
        okHttpUtils.postEnqueue(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                json = BaseTools.decryptJson(json);
                updateListView(json);
            }
        }, parameter);
    }

    /**
     * 将数据填充到adapter中
     *
     * @param json
     */
    private void updateListView(String json) {
        try {
            JSONObject jsonObject = JSONObject.parseObject(json);
            int status = jsonObject.getInteger("status");
            if (status == 1) {
                final List<Map<String, Object>> listMap = new ArrayList<>();
                JSONArray cateArray = jsonObject.getJSONArray("cate");
                jsonObject = jsonObject.getJSONObject("data");
                List<String> titles = new ArrayList<>();
                for(int i = 0;i < cateArray.size();i++){
                    titles.add(cateArray.getString(i));
                    mAdapter.addFragment(Fragment3.newInstance(),cateArray.getString(i));
//                    JSONArray dataArray = jsonObject.getJSONArray(cateArray.getString(i));
//                    for (int j = 0; j < titles.size(); j++) {
//                        jsonObject = dataArray.getJSONObject(j);
//                        Map<String, Object> map = new HashMap<>();
//                        map.put("imgUrl", jsonObject.getString("pict_url"));
//                        map.put("type", jsonObject.getString("store_type"));
//                        map.put("title", jsonObject.getString("title"));
//                        map.put("cPrice", jsonObject.getString("zk_final_price"));
//                        map.put("oPrice", jsonObject.getString("price"));
//                        map.put("bNum", jsonObject.getString("volume"));
//                        map.put("rebate", jsonObject.getString("rating"));
//                        map.put("numId", jsonObject.getString("num_iid"));
//                        map.put("shareUrl", jsonObject.getString("share_url"));
//                        map.put("couponUrl",jsonObject.getString("url"));
//                        listMap.add(map);
//                    }
                }
                Log.e("品牌馆列表的数据", JSON.toJSONString(titles));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected static class Adapter extends FragmentPagerAdapter {
        private List<Fragment> fragments = new ArrayList<>();
        private List<String> titles = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, String title) {
            fragments.add(fragment);
            titles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public String getPageTitle(int position) {
            return titles.get(position);
        }
    }

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
}
