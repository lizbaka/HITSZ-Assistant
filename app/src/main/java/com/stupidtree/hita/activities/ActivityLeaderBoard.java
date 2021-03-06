package com.stupidtree.hita.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.stupidtree.hita.R;
import com.stupidtree.hita.fragments.campus.FragmentCanteenList;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ActivityLeaderBoard extends BaseActivity {


    RankBoardPagerAdapter pagerAdapter;
    ViewPager viewPager;
    TabLayout tabLayout;
    List<Fragment> pagerRes;
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        setWindowParams(true,true,false);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);//左侧添加一个默认的返回图标
        getSupportActionBar().setHomeButtonEnabled(true); //设置返回键可用
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        initPager();
    }

    void initPager() {
        tabLayout = findViewById(R.id.tablayout);
        viewPager = findViewById(R.id.viewpager);
        tabLayout.setTabIndicatorFullWidth(false);
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#00000000"));
        pagerRes = new ArrayList<>();
        pagerRes.add(new FragmentCanteenList());
        //pagerRes.add(new FragmentSceneryList());
        String[] titles = getResources().getStringArray(R.array.leaderboard_tabs);
        pagerAdapter = new RankBoardPagerAdapter(this.getSupportFragmentManager(), pagerRes, titles);
        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
    }
    

    static class RankBoardPagerAdapter extends FragmentPagerAdapter {
        List<Fragment> mBeans;
        String[] titles;

        RankBoardPagerAdapter(FragmentManager fm, List<Fragment> fx, String[] titles) {
            super(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
            this.titles = titles;
            mBeans = fx;
        }

        @NonNull
        @Override
        public Fragment getItem(int i) {
            return mBeans.get(i);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public int getCount() {
            return mBeans.size();
        }
    }



}
