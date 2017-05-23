package com.shhb.gd.shop.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shhb.gd.shop.R;
import com.shhb.gd.shop.tools.PrefShared;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by superMoon on 2017/3/15.
 */

public class Fragment5 extends BaseNavPagerFragment {
    private static int mType;

    public static Fragment5 newInstance(int type) {
        mType = type;
        Fragment5 fragment = new Fragment5();
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
        String json = "";
        if(1 == mType){
            json = PrefShared.getString(context,"brandTabJson");
        } else {
            json = PrefShared.getString(context,"categoryTabJson");
        }
        Log.e("这是"+mType+"的数据",json);
        List<String> titles = null;
        try {
            titles = JSON.parseArray(json,String.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return titles;
    }

    @Override
    protected List<String> getCId() {
        return null;
    }

    @Override
    protected Fragment getFragment(int position) {
        String cName = getTitles().get(position)+","+mType;
        return RecyclerFragment.newInstance(cName);
    }
}