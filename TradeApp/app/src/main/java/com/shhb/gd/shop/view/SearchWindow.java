package com.shhb.gd.shop.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shhb.gd.shop.R;
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
    private FlowLayout flowLayout ;
    private SearchWindowAdapter mAdapter;
    private List<Map<String,Object>> listMap = new ArrayList<>();

    public SearchWindow(Activity context) {
        super(context);
        this.context = context;
        findByData();
        mAdapter = new SearchWindowAdapter(1);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        viewmenu = inflater.inflate(R.layout.search_activity, null);
        this.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);//设置窗体的宽
        this.setHeight(LinearLayout.LayoutParams.MATCH_PARENT);//设置窗体的高
        this.setFocusable(true);
        this.setBackgroundDrawable(new ColorDrawable(0));//去掉黑色边框
        this.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);//不让输入法把窗体挤上去
        this.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);//不让输入法把窗体挤上去
        this.setAnimationStyle(R.style.AnimTop);//设置窗体从底部进入的动画效果
        this.setContentView(viewmenu);
        initView();
    }

    private void initView() {
        search2 = (EditText) viewmenu.findViewById(R.id.search2);
        search2.setOnEditorActionListener(search2OnEditorActionListener);
        cancel = (TextView) viewmenu.findViewById(R.id.cancel);
        cleared = (TextView) viewmenu.findViewById(R.id.cleared);
        flowLayout = (FlowLayout) viewmenu.findViewById(R.id.search_flow);
        cancel.setOnClickListener(this);
        cleared.setOnClickListener(this);
        mAdapter.setOnClickListener(this);
    }

    TextView.OnEditorActionListener search2OnEditorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
            if(actionId == EditorInfo.IME_ACTION_SEARCH){

            }
            return false;
        }
    };

    private void findByData() {
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
                JSONArray array = jsonObject.getJSONArray("data");
                for(int i = 0;i < array.size();i++){
                    String name = array.getString(i);
                    TextView textView = new TextView(context);
                    textView.setTextSize(16);
                    textView.setTextColor(ContextCompat.getColor(context, R.color.black));
                    textView.setBackgroundResource(R.drawable.text_btn_bg);
                    textView.setText(name);
                    flowLayout.addView(textView);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cancel:
                dismiss();
                break;
        }
    }

    @Override
    public void onClick(View view, int position, List<Map<String, Object>> listMap) {
        Toast.makeText(context,listMap.get(position).get("name")+"",Toast.LENGTH_SHORT).show();
    }
}