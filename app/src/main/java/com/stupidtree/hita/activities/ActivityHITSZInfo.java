package com.stupidtree.hita.activities;

import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.stupidtree.hita.BaseActivity;
import com.stupidtree.hita.R;
import com.stupidtree.hita.adapter.HITSZInfoPagerAdapter;
import com.stupidtree.hita.fragments.FragmentNews;
import com.stupidtree.hita.fragments.FragmentNewsBulletin;
import com.stupidtree.hita.fragments.FragmentNewsIPNews;
import com.stupidtree.hita.fragments.FragmentNewsLecture;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ActivityHITSZInfo extends BaseActivity {

    ViewPager pager;
    HITSZInfoPagerAdapter pagerAdapter;
    List<Fragment> fragments;
    TabLayout tab;
    Toolbar toolbar;

    ActivityLostAndFound fragmentSociety;

    @Override
    protected void stopTasks() {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setWindowParams(true,true,false);
        setContentView(R.layout.activity_hitszinfo);
        initPager();

    }

    void initPager(){
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        tab = findViewById(R.id.hitszinfo_tab);
        tab.setBackgroundColor(ContextCompat.getColor(this,R.color.material_backgroung_grey_50));
        tab.setTabTextColors(Color.parseColor("#55000000"),ContextCompat.getColor(this,R.color.material_primary_text));
        tab.setSelectedTabIndicatorColor(getColorPrimary());
        tab.setSelectedTabIndicatorColor(Color.parseColor("#00000000"));
        pager = findViewById(R.id.hitszinfo_pager);
        fragments = new ArrayList<>();
        fragments.add(new FragmentNewsLecture());
        fragments.add(new FragmentNewsBulletin());
        fragments.add(new FragmentNewsIPNews());
        pagerAdapter = new HITSZInfoPagerAdapter(getSupportFragmentManager(),fragments);
        pager.setAdapter(pagerAdapter);
        tab.setTabIndicatorFullWidth(false);
        tab.setupWithViewPager(pager);

    }


}