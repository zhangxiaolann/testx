package com.shhb.gd.shop.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shhb.gd.shop.R;
import com.shhb.gd.shop.activity.SearchActivity;
import com.shhb.gd.shop.adapter.SearchWindowAdapter;
import com.shhb.gd.shop.module.Constants;
import com.shhb.gd.shop.tools.BaseTools;
import com.shhb.gd.shop.tools.OkHttpUtils;
import com.shhb.gd.shop.tools.PrefShared;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.drakeet.materialdialog.MaterialDialog;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by superMoon on 2017/3/15.
 */
public class SearchWindow extends PopupWindow implements View.OnClickListener,SearchWindowAdapter.OnClickListener {
    private Activity context;
    private View viewmenu;
    private EditText search2;
    private TextView cancel, cleared;
    private RecyclerView hotRecyclerView,historyRecyclerView;
    private SearchWindowAdapter hotAdapter,historyAdapter;

    public SearchWindow(Activity context) {
        super(context);
        this.context = context;
        findByHotData();
        hotAdapter = new SearchWindowAdapter(1);
        historyAdapter = new SearchWindowAdapter(2);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        viewmenu = inflater.inflate(R.layout.search_window, null);
        this.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);//设置窗体的宽
        this.setHeight(LinearLayout.LayoutParams.MATCH_PARENT);//设置窗体的高
        this.setFocusable(true);
        this.setBackgroundDrawable(new ColorDrawable(0));//去掉黑色边框
        this.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);//不让输入法把窗体挤上去
        this.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);//不让输入法把窗体挤上去
//        this.setAnimationStyle(R.style.AnimTop);//设置窗体从底部进入的动画效果
        this.setContentView(viewmenu);
        initView();
    }

    private void initView() {
        search2 = (EditText) viewmenu.findViewById(R.id.search2);
        search2.setOnEditorActionListener(search2OnEditorActionListener);
        cancel = (TextView) viewmenu.findViewById(R.id.cancel);
        cleared = (TextView) viewmenu.findViewById(R.id.cleared);
        hotRecyclerView = (RecyclerView) viewmenu.findViewById(R.id.hot_search);
        historyRecyclerView = (RecyclerView) viewmenu.findViewById(R.id.history_record);
        FlowLayoutManager flowLayoutManager = new FlowLayoutManager(context);
        flowLayoutManager.setMargin(BaseTools.dp2px(context,8));
        hotRecyclerView.setLayoutManager(flowLayoutManager);
        hotRecyclerView.setAdapter(hotAdapter);

        GridLayoutManager layoutManager = new GridLayoutManager(context, 1, LinearLayoutManager.VERTICAL, false);
        historyRecyclerView.setLayoutManager(layoutManager);
        historyRecyclerView.addItemDecoration(
                new DividerItemDecoration(context, DividerItemDecoration.BOTH_SET,2, ContextCompat.getColor(context, R.color.webBg))
        );
        historyRecyclerView.setAdapter(historyAdapter);

        cancel.setOnClickListener(this);
        cleared.setOnClickListener(this);
        hotAdapter.setOnClickListener(this);
        findByHistoryData();
    }

    TextView.OnEditorActionListener search2OnEditorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
            if(actionId == EditorInfo.IME_ACTION_SEARCH){
                if(!TextUtils.equals(search2.getText().toString().trim(),"")){
                    String babyName = search2.getText().toString().trim();
                    String searchBaby = PrefShared.getString(context,"searchBaby");
                    if(null != searchBaby){
                        JSONArray jsonArray = JSON.parseArray(searchBaby);
                        if(jsonArray.size() >= 15){
                            jsonArray.remove(0);
                        }
                        Map<String,Object> map = new HashMap<>();
                        map.put("name",babyName);
                        jsonArray.add(map);
                        PrefShared.saveString(context, "searchBaby", jsonArray.toString());
                    } else {
                        List<Map<String,Object>> listMap = new ArrayList<>();
                        Map<String,Object> map = new HashMap<>();
                        map.put("name",babyName);
                        listMap.add(map);
                        PrefShared.saveString(context, "searchBaby", JSON.toJSONString(listMap));
                    }
                    Intent intent = new Intent(context, SearchActivity.class);
                    intent.putExtra("name",babyName);
                    context.startActivity(intent);
                    findByHistoryData();
                    search2.setText("");
                }
            }
            return false;
        }
    };

    /**
     * 查询热门搜索记录
     */
    private void findByHotData() {
        OkHttpUtils okHttpUtils = new OkHttpUtils(20);
        JSONObject jsonObject = new JSONObject();
        String userId = PrefShared.getString(context,"userId");
        if(null == userId){
            userId = "0";
        }
        jsonObject.put("user_id", userId);
        jsonObject.put("type", "1");
        String parameter = BaseTools.encodeJson(jsonObject.toString());
        okHttpUtils.postEnqueue(Constants.FIND_BY_HOT, new Callback() {
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
                        hotAdapter.addRecyclerData(listMap);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 搜索历史
     */
    private void findByHistoryData() {
        String searchBaby = PrefShared.getString(context,"searchBaby");
        final List<Map<String,Object>> listMap = new ArrayList<>();
        if(null != searchBaby){
            JSONArray array = JSONObject.parseArray(searchBaby);
            for(int i = array.size() - 1;i > -1;i--){
                String name = array.getJSONObject(i).getString("name");
                Map<String,Object> map = new HashMap<>();
                map.put("name",name);
                listMap.add(map);
            }
        }
        historyRecyclerView.post(new Runnable() {
            @Override
            public void run() {
                historyAdapter.addRecyclerData(listMap);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancel:
                dismiss();
                search2.setText("");
                break;
            case R.id.cleared:
                final MaterialDialog mMaterialDialog = new MaterialDialog(context);
                mMaterialDialog.setTitle("温馨提示");
                mMaterialDialog.setMessage("确认删除全部历史记录？");
                mMaterialDialog.setPositiveButton("确认", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PrefShared.removeData(context,"searchBaby");
                        historyAdapter.addRecyclerData(new ArrayList<Map<String, Object>>());
                        historyAdapter.notifyDataSetChanged();
                        mMaterialDialog.dismiss();
                    }
                }).setNegativeButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMaterialDialog.dismiss();
                    }
                });
                mMaterialDialog.show();
                break;
        }
    }

    @Override
    public void onClick(View view, int position, List<Map<String, Object>> listMap) {
        String name = listMap.get(position).get("name")+"";
        Intent intent = new Intent(context, SearchActivity.class);
        intent.putExtra("name",name);
        context.startActivity(intent);
        search2.setText("");
    }
}