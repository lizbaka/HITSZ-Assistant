package com.stupidtree.hita.adapter;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.SparseArray;

import com.stupidtree.hita.fragments.FragmentTimeTablePage;

import java.util.ArrayList;
import java.util.List;

public class TimeTablePagerAdapter extends FragmentPagerAdapter {
    int pageCount = 0;
    private FragmentManager mFragmentManager;
    //保存每个Fragment的Tag，刷新页面的依据
    protected SparseArray<String> tags = new SparseArray<>();
    List<FragmentTimeTablePage> framents;

    public TimeTablePagerAdapter(FragmentManager fm,int count) {
        super(fm);
        pageCount = count;
        mFragmentManager = fm;
        framents = new ArrayList<>();
        for(int i=1;i<=count;i++){
            framents.add(FragmentTimeTablePage.newInstance(i));
        }
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return "第"+(position+1)+"周";
    }

    public void setCount(int count){
        this.pageCount = count;
    }

    @Override
    public Fragment getItem(int i) {
        return framents.get(i);
    }



//    @Override
//    public int getItemPosition(Object object) {
//        Fragment fragment = (Fragment) object;
//        //如果Item对应的Tag存在，则不进行刷新
//        if (tags.indexOfValue(fragment.getTag()) > -1) {
//            return super.getItemPosition(object);
//        }
//        return POSITION_NONE;
//    }


    @Override
    public int getCount() {
        return pageCount;
    }


//    @Override
//    public Object instantiateItem(ViewGroup container, int position) {
//        //得到缓存的fragment
//        Fragment fragment = (Fragment) super.instantiateItem(container, position);
//        String tag = fragment.getTag();
//        //保存每个Fragment的Tag
//        tags.put(position, tag);
//        return fragment;
//    }


    //拿到指定位置的Fragment
    public Fragment getFragmentByPosition(int position) {
        return mFragmentManager.findFragmentByTag(tags.get(position));
    }

    public List<Fragment> getFragments(){
        return mFragmentManager.getFragments();
    }


    //刷新指定位置的Fragment
    public void notifyFragmentByPosition(int position) {
        if(position<0||position>=pageCount) return;
        //tags.removeAt(position);
        framents.get(position).NotifyRefresh();
        notifyDataSetChanged();
    }

    //刷新全部Fragment
    public void notifyAllFragments() {
        //tags.clear();
        for(Fragment d:framents){
            ((FragmentTimeTablePage)d).NotifyRefresh();
        }
        notifyDataSetChanged();
    }





}
