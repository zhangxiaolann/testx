package com.shhb.gd.shop.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shhb.gd.shop.R;
import com.shhb.gd.shop.tools.PrefShared;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by superMoon on 2017/3/15.
 */

public class Fragment1 extends BaseNavPagerFragment {

    public static Fragment1 newInstance() {
        Fragment1 fragment = new Fragment1();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_base_nav_pager, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    protected List<String> getTitles() {
        String json = PrefShared.getString(this.getContext(),"tabJson");
        List<String> titles = new ArrayList<>();
        try {
            JSONObject jsonObject = JSONObject.parseObject(json);
            int status = jsonObject.getInteger("status");
            if (status == 1) {
                JSONArray tabArray = jsonObject.getJSONArray("cate");
                JSONObject addJson = new JSONObject();
                addJson.put("name","首页");
                tabArray.add(0,addJson);
                for (int i = 0; i < tabArray.size(); i++) {
                    jsonObject = tabArray.getJSONObject(i);
                    titles.add(jsonObject.getString("name"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return titles;
    }

    @Override
    protected List<String> getCId() {
        String json = PrefShared.getString(this.getContext(),"tabJson");
        List<String> cId = new ArrayList<>();
        try {
            JSONObject jsonObject = JSONObject.parseObject(json);
            int status = jsonObject.getInteger("status");
            if (status == 1) {
                JSONArray tabArray = jsonObject.getJSONArray("cate");
                JSONObject addJson = new JSONObject();
                addJson.put("cid","0");
                tabArray.add(0,addJson);
                for (int i = 0; i < tabArray.size(); i++) {
                    jsonObject = tabArray.getJSONObject(i);
                    cId.add(jsonObject.getString("cid"));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cId;
    }

    @Override
    protected Fragment getFragment(int position) {
        String cId = getCId().get(position);
        Log.e("cId",cId);
        return Fragment1_1.newInstance(Integer.parseInt(cId));
    }
}