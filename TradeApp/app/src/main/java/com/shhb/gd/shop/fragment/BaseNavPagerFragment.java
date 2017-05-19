package com.shhb.gd.shop.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shhb.gd.shop.R;
import com.shhb.gd.shop.view.CustomViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public abstract class BaseNavPagerFragment extends BaseFragment {
    private CustomViewPager viewPager;
    private TabLayout tabLayout;
    private Adapter mAdapter;

    public BaseNavPagerFragment() {
        // Required empty public constructor
    }

    protected abstract List<String> getTitles();

    protected abstract List<String> getCId();

    protected abstract Fragment getFragment(int position);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new Adapter(getChildFragmentManager());
        List<String> titles = getTitles();
        for (int i = 0; i < titles.size(); i++) {
            mAdapter.addFragment(getFragment(i), titles.get(i));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_base_nav_pager, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        viewPager = (CustomViewPager) view.findViewById(R.id.viewPager);
        viewPager.setCurrentItem(0,false);
        viewPager.setScanScroll(true);
        viewPager.setAdapter(mAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    protected static class Adapter extends FragmentPagerAdapter {
        private List<Fragment> fragments = new ArrayList<>();
        private List<CharSequence> titles = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment, CharSequence title) {
            fragments.add(fragment);
            titles.add(title);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }
}
