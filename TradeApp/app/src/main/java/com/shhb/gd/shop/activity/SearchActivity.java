package com.shhb.gd.shop.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.jaeger.library.StatusBarUtil;
import com.shhb.gd.shop.R;
import com.shhb.gd.shop.adapter.SearchActivityAdapter;
import com.shhb.gd.shop.module.AlibcUser;
import com.shhb.gd.shop.module.Constants;
import com.shhb.gd.shop.tools.BaseTools;
import com.shhb.gd.shop.tools.OkHttpUtils;
import com.shhb.gd.shop.tools.PrefShared;
import com.shhb.gd.shop.view.DividerItemDecoration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by superMoon on 2017/7/4.
 */

public class SearchActivity extends BaseActivity implements View.OnClickListener,OnRefreshListener, OnLoadMoreListener, SearchActivityAdapter.OnClickListener {
    private View mViewNeedOffset;
    private String babyName;
    private EditText search3;
    private TextView cancel;
    private SwipeToLoadLayout swipeToLoadLayout;
    private RecyclerView recyclerView;
    private SearchActivityAdapter mAdapter;
    /** 请求页码*/
    private int mPageIndex = 1;
    /** 每页请求数量*/
    private final static int mpageNum = 10;
    private String query="",startPrice="",maxPrice="",type="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);
        babyName = getIntent().getStringExtra("name");
        initView();
    }

    private void initView() {
        mViewNeedOffset = findViewById(R.id.view_need_offset);
        search3 = (EditText) findViewById(R.id.search3);
        search3.setText(babyName);
        cancel = (TextView) findViewById(R.id.cancel);
        cancel.setOnClickListener(this);
        swipeToLoadLayout = (SwipeToLoadLayout) findViewById(R.id.swipeToLoadLayout);
        swipeToLoadLayout.setOnRefreshListener(this);
        swipeToLoadLayout.setOnLoadMoreListener(this);
        recyclerView = (RecyclerView) findViewById(R.id.swipe_target);
        mAdapter = new SearchActivityAdapter();
        GridLayoutManager layoutManager = new GridLayoutManager(context, 1, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(
                new DividerItemDecoration(context, DividerItemDecoration.BOTH_SET,6, ContextCompat.getColor(context, R.color.webBg))
        );
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnClickListener(this);
        requestData();
    }

    /**
     * 请求数据的方法
     */
    private void requestData() {
        swipeToLoadLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeToLoadLayout.setRefreshing(true);
            }
        });
    }

    /**
     * 搜索你喜欢的宝贝
     */
    private void searchBaby(){
        OkHttpUtils okHttpUtils = new OkHttpUtils(20);
        JSONObject jsonObject = new JSONObject();
        String userId = PrefShared.getString(context,"userId");
        if(null == userId){
            userId = "0";
        }
        jsonObject.put("page_no", mPageIndex);//第几页
        jsonObject.put("page_size", mpageNum);//展示多少条
        jsonObject.put("title", babyName);//查询词
        jsonObject.put("user_id", userId);
        jsonObject.put("system", 1);//0 IOS 1安卓
        if(!TextUtils.equals(query,"")){
            jsonObject.put("query", query);//query=true
        }
        if(!TextUtils.equals(startPrice,"")){
            jsonObject.put("start_price", startPrice);//商品价格下限
        }
        if(!TextUtils.equals(maxPrice,"")){
            jsonObject.put("maxPrice", maxPrice);//商品价格上限
        }
        if(!TextUtils.equals(type,"")){
            jsonObject.put("type", type);//0 表示天猫 1表示淘宝
        }
        String parameter = BaseTools.encodeJson(jsonObject.toString());
        okHttpUtils.postEnqueue(Constants.FIND_BY_BABY, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                closeLoading();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                json = BaseTools.decryptJson(json);
                updateListView(json);
            }
        }, parameter);
    }

    private void updateListView(String json) {
        try {
            JSONObject jsonObject = JSONObject.parseObject(json);
            int status = jsonObject.getInteger("status");
            if (status == 1) {
                final List<Map<String,Object>> listMap = new ArrayList<>();
                jsonObject = jsonObject.getJSONObject("data");
                JSONArray selfArray = jsonObject.getJSONArray("self");
                for(int i = 0;i < selfArray.size();i++){
                    JSONObject selfObject = selfArray.getJSONObject(i);
                    Map<String,Object> map = new HashMap<>();
                    map.put("imgUrl", selfObject.getString("pict_url"));
                    map.put("type", selfObject.getString("store_type"));
                    map.put("title", selfObject.getString("title"));
                    map.put("cPrice", selfObject.getString("zk_final_price"));
                    map.put("oPrice", selfObject.getString("price"));
                    map.put("bNum", selfObject.getString("volume"));
                    map.put("rebate", selfObject.getString("rating"));
                    map.put("numId", selfObject.getString("num_iid"));
                    map.put("shareUrl", selfObject.getString("share_url"));
                    map.put("couponUrl",selfObject.getString("url"));
                    listMap.add(map);
                }
                recyclerView.post(new Runnable() {
                    @Override
                    public void run() {
                        closeLoading();
                        mAdapter.addRecyclerData(listMap,mPageIndex);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 下拉更新广告列表
     */
    private void refreshRequestList() {
        mPageIndex = 1;
        swipeToLoadLayout.setRefreshing(true);
        searchBaby();
    }

    /**
     * 上拉更新广告列表
     */
    private void loadRequestList() {
        ++mPageIndex;
        swipeToLoadLayout.setLoadingMore(true);
        searchBaby();
    }

    @Override
    public void onRefresh() {
        refreshRequestList();
    }

    @Override
    public void onLoadMore() {
        loadRequestList();
    }

    private void closeLoading() {
        if (swipeToLoadLayout.isRefreshing()) {
            swipeToLoadLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeToLoadLayout.setRefreshing(false);
                }
            });
        }
        if (swipeToLoadLayout.isLoadingMore()) {
            swipeToLoadLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeToLoadLayout.setLoadingMore(false);
                }
            });
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancel:
                finish();
                break;
        }
    }

    @Override
    public void onClick(int type, int position, List<Map<String, Object>> listMap) {
        Intent intent;
        JSONObject jsonObject;
        switch (type) {
            case 2:
                jsonObject = new JSONObject();
                jsonObject.put("numId", listMap.get(position).get("numId") + "");
                jsonObject.put("title", "惠淘分享，" + listMap.get(position).get("title") + "");
                jsonObject.put("shareContent", "超值优惠券等你来领，领卷购物更便宜，还有更多惊喜！");
                jsonObject.put("shareImg", listMap.get(position).get("imgUrl") + "");
                jsonObject.put("shareUrl", listMap.get(position).get("shareUrl") + "");
                intent = new Intent(Constants.SENDMSG_SHARE);
                intent.putExtra("result", jsonObject.toString());
                context.sendBroadcast(intent);
                break;
            case 1:
                String userId = PrefShared.getString(context, "userId");
                String nick = PrefShared.getString(context, "nick");
                try {
                    jsonObject = new JSONObject();
                    jsonObject.put("store_type", listMap.get(position).get("type") + "");
                    jsonObject.put("goods_id", listMap.get(position).get("numId") + "");
                    jsonObject.put("vocher_url", listMap.get(position).get("couponUrl") + "");
                    jsonObject.put("title", "惠淘分享，" + listMap.get(position).get("title") + "");
                    jsonObject.put("content", "超值优惠券等你来领，领卷购物更便宜，还有更多惊喜！");
                    jsonObject.put("share_img", listMap.get(position).get("imgUrl") + "");
                    jsonObject.put("share_url", listMap.get(position).get("shareUrl") + "");
                    if (null != userId && !TextUtils.equals(userId, "")) {
                        if (null != nick && !TextUtils.equals(nick, "")) {
                            intent = new Intent(context, DetailsActivity.class);
                            intent.putExtra("result", jsonObject.toString());
                            context.startActivity(intent);
                        } else {
                            new AlibcUser(context).login();
                        }
                    } else {
                        intent = new Intent(context, LoginActivity.class);
                        context.startActivity(intent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

    @Override
    protected void setStatusBar() {
        StatusBarUtil.setTranslucentForImageView(this, 0, mViewNeedOffset);
    }
}
