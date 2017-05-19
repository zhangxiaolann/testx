package com.shhb.gd.shop.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
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

public class Fragment2 extends BaseNavPagerFragment {

    public static Fragment2 newInstance() {
        Fragment2 fragment = new Fragment2();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment2, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    protected List<String> getTitles() {
        String json = PrefShared.getString(this.getContext(), "9TabJson");
        List<String> titles = new ArrayList<>();
        try {
            JSONObject jsonObject = JSONObject.parseObject(json);
            int status = jsonObject.getInteger("status");
            if (status == 1) {
                JSONArray tabArray = jsonObject.getJSONArray("cate");
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
        String json = PrefShared.getString(this.getContext(), "9TabJson");
        List<String> cId = new ArrayList<>();
        try {
            JSONObject jsonObject = JSONObject.parseObject(json);
            int status = jsonObject.getInteger("status");
            if (status == 1) {
                JSONArray tabArray = jsonObject.getJSONArray("cate");
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
        String cId = getCId().get(position)+","+1;
        Log.e("cId", cId);
        return Fragment1_1.newInstance(cId);
    }
}