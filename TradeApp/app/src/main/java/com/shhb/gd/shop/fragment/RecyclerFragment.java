package com.shhb.gd.shop.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.shhb.gd.shop.R;
import com.shhb.gd.shop.activity.DetailsActivity;
import com.shhb.gd.shop.activity.LoginActivity;
import com.shhb.gd.shop.adapter.RecyclerFragmentAdapter;
import com.shhb.gd.shop.module.AlibcUser;
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
 * Created by superMoon on 2017/3/15.
 */

public class RecyclerFragment extends BaseFragment implements OnRefreshListener, OnLoadMoreListener,RecyclerFragmentAdapter.OnClickListener {
    /** 1代表品牌馆，2代表领劵购*/
    private int fType;
    /** cName*/
    private String mType;
    private SwipeToLoadLayout swipeToLoadLayout;
    private RecyclerView recyclerView;
    private RecyclerFragmentAdapter mAdapter;
    /**
     * 请求页码
     */
    private int mPageIndex = 1;
    /**
     * 每页请求数量
     */
    private final static int pageNum = 10;

    public static RecyclerFragment newInstance(String type) {
        RecyclerFragment fragment = new RecyclerFragment();
        Bundle bundle = new Bundle();
        bundle.putString("LAYOUT_MANAGER_TYPE",type);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String result = getArguments().getString("LAYOUT_MANAGER_TYPE");
        String str[] = result.split(",");
        mType = str[0];
        fType = Integer.parseInt(str[1]);
        mAdapter = new RecyclerFragmentAdapter(fType);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.recycler_view, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        swipeToLoadLayout = (SwipeToLoadLayout) view.findViewById(R.id.swipeToLoadLayout);
        swipeToLoadLayout.setOnRefreshListener(this);
        swipeToLoadLayout.setOnLoadMoreListener(this);

        recyclerView = (RecyclerView) view.findViewById(R.id.swipe_target);
        GridLayoutManager layoutManager = new GridLayoutManager(context, 2, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
//        recyclerView.addItemDecoration(new RecyclerViewDivider(context, LinearLayoutManager.VERTICAL, 2, ContextCompat.getColor(context, R.color.webBg)));
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnClickListener(this);

        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE ){
                    if (!ViewCompat.canScrollVertically(recyclerView, 1)){
                        swipeToLoadLayout.setLoadingMore(true);
                    }
                }
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        swipeToLoadLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeToLoadLayout.setRefreshing(true);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        if (swipeToLoadLayout.isRefreshing()) {
            swipeToLoadLayout.setRefreshing(false);
        }
        if (swipeToLoadLayout.isLoadingMore()) {
            swipeToLoadLayout.setLoadingMore(false);
        }
    }

    /**
     * 查询首页商品
     */
    private void findByGoods() {
        OkHttpUtils okHttpUtils = new OkHttpUtils(20);
        JSONObject jsonObject = new JSONObject();
        String url = "";
        String userId = PrefShared.getString(context,"userId");
        if(null == userId){
            userId = "0";
        }
        if(mPageIndex == 1){//刷新
            if(fType == 1){
                url = Constants.FIND_BY_BRAND_REFRESH;
                jsonObject.put("cname","品牌");
            } else {
                url = Constants.FIND_BY_CATEGORY_REFRESH;
                jsonObject.put("cname","全部");
                jsonObject.put("stype","0");
            }
            jsonObject.put("size","10");
        } else {//加载
            if(fType == 1){
                url = Constants.FIND_BY_BRAND_LOAD;
            } else {
                url = Constants.FIND_BY_CATEGORY_LOAD;
                jsonObject.put("stype", "0");
            }
            jsonObject.put("cname",mType);//当前的cName
            jsonObject.put("page_no", mPageIndex+"");//第几页
            jsonObject.put("page_size", pageNum+"");//每个分类条数
            jsonObject.put("system","1");//0iOS 1android
            jsonObject.put("user_id", userId);//用户uid
        }
        String parameter = BaseTools.encodeJson(jsonObject.toString());
        okHttpUtils.postEnqueue(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                closeLoading();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                closeLoading();
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
                JSONArray jsonArray = null;
                if(mPageIndex == 1) {//刷新
                    jsonObject = jsonObject.getJSONObject("data");
                    jsonArray = jsonObject.getJSONArray(mType);
                } else {//加载
                    jsonArray = jsonObject.getJSONArray("data");
                }
                for (int i = 0; i < jsonArray.size(); i++) {
                    jsonObject = jsonArray.getJSONObject(i);
                    Map<String, Object> map = new HashMap<>();
                    map.put("imgUrl", jsonObject.getString("pict_url"));
                    map.put("type", jsonObject.getString("store_type"));
                    map.put("title", jsonObject.getString("title"));
                    map.put("cPrice", jsonObject.getString("zk_final_price"));
                    map.put("oPrice", jsonObject.getString("price"));
                    map.put("bNum", jsonObject.getString("volume"));
                    map.put("rebate", jsonObject.getString("rating"));
                    map.put("reduce", jsonObject.getString("reduce"));
                    map.put("numId", jsonObject.getString("num_iid"));
                    map.put("shareUrl", jsonObject.getString("share_url"));
                    map.put("couponUrl",jsonObject.getString("url"));
                    listMap.add(map);
                }
                Log.e("商品列表的数据", JSON.toJSONString(listMap));
                context.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
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
        findByGoods();
    }

    /**
     * 上拉更新广告列表
     */
    private void loadRequestList() {
        ++mPageIndex;
        swipeToLoadLayout.setLoadingMore(true);
        findByGoods();
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
    public void onClick(View view, int position, List<Map<String, Object>> listMap) {
        Intent intent;
        JSONObject jsonObject;
        switch (view.getId()) {
            case R.id.share_img:
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
            default:
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

}