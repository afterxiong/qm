package com.shareshow.aide.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.shareshow.aide.R;
import com.shareshow.aide.fragment.MorningFragment;
import com.shareshow.aide.fragment.PersonalFragment;
import com.shareshow.aide.fragment.VisitFragment;
import com.shareshow.aide.fragment.WorkFragment;

import java.util.ArrayList;
import java.util.List;

/*
 * Created by xiongchengguang on 2017/12/7.
 */

public class MainFragmentPagerAdapter extends FragmentPagerAdapter {

//    private String[] title;
    private List<Fragment> fragmentList = new ArrayList<>();

    public MainFragmentPagerAdapter(Context context, FragmentManager fm){
        super(fm);
//        title = context.getResources().getStringArray(R.array.tab_title);
        fragmentList.add(new WorkFragment());
        fragmentList.add(new MorningFragment());
        fragmentList.add(new VisitFragment());
        fragmentList.add(new PersonalFragment());

    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }


    @Override
    public int getCount() {
        return fragmentList.size();
    }

//    @Override
//    public CharSequence getPageTitle(int position) {
//        return title[position];
//    }

}
