package com.shhb.gd.shop.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.shhb.gd.shop.R;
import com.shhb.gd.shop.tools.PrefShared;

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
        List<String> titles = null;
        try {
            titles = JSON.parseArray(String.valueOf(JSONObject.parseObject(json).getJSONArray("titles")),String.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return titles;
    }

    @Override
    protected List<String> getCId() {
        String json = PrefShared.getString(this.getContext(), "9TabJson");
        List<String> cIds = null;
        try {
            cIds = JSON.parseArray(String.valueOf(JSONObject.parseObject(json).getJSONArray("cIds")),String.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cIds;
    }

    @Override
    protected Fragment getFragment(int position) {
        String cId = getCId().get(position) + "," + 1;
        return MainFragment.newInstance(cId);
    }
}