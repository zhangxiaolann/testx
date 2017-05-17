package com.shhb.gd.shop.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shhb.gd.shop.R;

/**
 * Created by superMoon on 2017/3/15.
 */

public class Fragment2 extends BaseFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recycler_view, container, false);
        view.setBackgroundColor(ContextCompat.getColor(container.getContext(),R.color.white));
        return view;
    }

}
