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
import com.shhb.gd.shop.activity.RecyclerActivity;
import com.shhb.gd.shop.adapter.MainFragmentAdapter;
import com.shhb.gd.shop.module.AlibcUser;
import com.shhb.gd.shop.module.BannerInfo;
import com.shhb.gd.shop.module.Constants;
import com.shhb.gd.shop.tools.BaseTools;
import com.shhb.gd.shop.tools.OkHttpUtils;
import com.shhb.gd.shop.tools.PrefShared;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

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

public class MainFragment extends BaseFragment implements OnRefreshListener, OnLoadMoreListener, MainFragmentAdapter.OnClickListener{
    /** 1代表首页，2代表9块9；cId */
    private int fType,mType;
    private SwipeToLoadLayout swipeToLoadLayout;
    private RecyclerView recyclerView;
    private MainFragmentAdapter mAdapter;
    /**
     * 请求页码
     */
    private int mPageIndex = 1;
    /**
     * 每页请求数量
     */
    private final static int pageNum = 10;

    public static MainFragment newInstance(String type) {
        MainFragment fragment = new MainFragment();
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
        mType = Integer.parseInt(str[0]);
        fType = Integer.parseInt(str[1]);
        mAdapter = new MainFragmentAdapter(mType);
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
        GridLayoutManager layoutManager = new GridLayoutManager(context, 2,LinearLayoutManager.VERTICAL, false);
        if(mType == 0){
            layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup(){
                @Override
                public int getSpanSize(int position) {
                    if(position == 0){
                        return 2;
                    } else if(position == 1){
                        return 2;
                    } else {
                        return 1;
                    }
                }
            });
        }
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(String type){
        swipeToLoadLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeToLoadLayout.setRefreshing(true);
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        EventBus.getDefault().register(this);//注册EventBus
//        swipeToLoadLayout.post(new Runnable() {
//            @Override
//            public void run() {
//                swipeToLoadLayout.setRefreshing(true);
//            }
//        });
    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);//取消注册
        closeLoading();
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
        if(mType == 0){//首页
            url = Constants.FIND_BY_GOODS1;
            jsonObject.put("page_no", mPageIndex+"");//第几页
            jsonObject.put("page_size", pageNum+"");//每个分类条数
            jsonObject.put("system","1");//0iOS 1android
            jsonObject.put("user_id", userId);//用户uid
        } else {//非首页
            if(mPageIndex == 1){//刷新
                url = Constants.FIND_BY_TABS_REFRESH;
                jsonObject.put("cid","1");//暂时是固定的传值
                jsonObject.put("size", "10");//每个分类条数
            } else {//加载
                url = Constants.FIND_BY_TABS_LOAD;
                jsonObject.put("cid",mType+"");//当前的cId
                jsonObject.put("page_no", mPageIndex+"");//第几页
                jsonObject.put("page_size", pageNum+"");//每个分类条数
                jsonObject.put("system","1");//0iOS 1android
                jsonObject.put("user_id", userId);//用户uid
            }
            jsonObject.put("stype", fType+"");//0 普通，1 9块9
        }
        Log.e("商品列表传入的参数",jsonObject.toString());
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
                if(mType == 0){//首页
                    jsonArray = jsonObject.getJSONArray("data");
                } else {//非首页
                    if(mPageIndex == 1) {//刷新
                        jsonObject = jsonObject.getJSONObject("data");
                        Log.e("选项卡对象",jsonObject.toString());
                        Log.e("选项卡类型",mType+"");
                        try {
                            jsonArray = jsonObject.getJSONArray(mType+"");
                            Log.e("选项卡数组",jsonArray.toString());
                        } catch (Exception e){
                            Log.e("选项卡数组","解析出错");
                            e.printStackTrace();
                        }
                    } else {//加载
                        jsonArray = jsonObject.getJSONArray("data");
                    }
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
     * 查询banner信息
     */
    private void findByBanner(){
        OkHttpUtils okHttpUtils = new OkHttpUtils(20);
        okHttpUtils.postEnqueue(Constants.FIND_BY_BANNER, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                try {
                    JSONObject jsonObject = JSONObject.parseObject(json);
                    int status = jsonObject.getInteger("status");
                    if(1 == status){
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        final List<BannerInfo> bannerList = new ArrayList<>();
                        for(int i = 0;i < jsonArray.size();i++){
                            jsonObject = jsonArray.getJSONObject(i);
                            BannerInfo banner = new BannerInfo();
                            banner.setAvatar(jsonObject.getString("icon_url"));
                            banner.setName("第"+(i+1)+"张图");
                            bannerList.add(banner);
                        }
                        mAdapter.addBannerData(bannerList);
                    }
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }, "");
    }

    /**
     * 下拉更新广告列表
     */
    private void refreshRequestList() {
        mPageIndex = 1;
        swipeToLoadLayout.setRefreshing(true);
        findByGoods();
        if(mType == 0){
            findByBanner();
        }
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
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()){
            case R.id.group_1_1:
                break;
            case R.id.group_1_2:
                intent = new Intent(context,RecyclerActivity.class);
                intent.putExtra("type","1");
                startActivity(intent);
                break;
            case R.id.group_1_3:
                break;
            case R.id.group_1_4:
                break;
            case R.id.group_2_1:
                intent = new Intent(context,RecyclerActivity.class);
                intent.putExtra("type","3");
                startActivity(intent);
                break;
            case R.id.group_2_2:
                intent = new Intent(context,RecyclerActivity.class);
                intent.putExtra("type","2");
                startActivity(intent);
                break;
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