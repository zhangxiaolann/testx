package com.shhb.gd.shop.activity;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.shhb.gd.shop.R;
import com.shhb.gd.shop.module.Constants;
import com.shhb.gd.shop.tools.BaseTools;
import com.shhb.gd.shop.tools.OkHttpUtils;
import com.shhb.gd.shop.tools.PrefShared;

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

public class SearchActivity extends BaseActivity implements View.OnClickListener,OnRefreshListener, OnLoadMoreListener {
    private String babyName;
    private EditText search3;
    private TextView cancel;
    private SwipeToLoadLayout swipeToLoadLayout;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_activity);
        babyName = getIntent().getStringExtra("name");
        initView();
    }

    private void initView() {
        search3 = (EditText) findViewById(R.id.search3);
        search3.setText(babyName);
        cancel = (TextView) findViewById(R.id.cancel);
        cancel.setOnClickListener(this);
        swipeToLoadLayout = (SwipeToLoadLayout) findViewById(R.id.swipeToLoadLayout);
        swipeToLoadLayout.setOnRefreshListener(this);
        swipeToLoadLayout.setOnLoadMoreListener(this);
        recyclerView = (RecyclerView) findViewById(R.id.swipe_target);

        searchBaby();
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
        jsonObject.put("user_id", userId);
        jsonObject.put("page_no", 1);//第几页
        jsonObject.put("page_size", 20);//展示多少条
        jsonObject.put("title", babyName);//查询词
        jsonObject.put("system", 1);//0 IOS 1安卓
        String parameter = BaseTools.encodeJson(jsonObject.toString());
        okHttpUtils.postEnqueue(Constants.FIND_BY_BABY, new Callback() {
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

    private void updateListView(String json) {
        try {
            JSONObject jsonObject = JSONObject.parseObject(json);
            int status = jsonObject.getInteger("status");
            if (status == 1) {
                final List<Map<String,Object>> listMap = new ArrayList<>();
                JSONArray array = jsonObject.getJSONArray("data");
                for(int i = 0;i < array.size();i++){
                    String name = array.getString(i);
                    Map<String,Object> map = new HashMap<>();
                    map.put("name",name);
                    listMap.add(map);
                }
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.addRecyclerData(listMap);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLoadMore() {

    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancel:
                finish();
                break;
        }
    }
}
