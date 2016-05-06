package com.bitjini.vzcards;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * Created by VEENA on 12/7/2015.
 */
public class TabPagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    private long baseId = 0;

    public TabPagerAdapter(FragmentManager fm,int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:

                return new MyProfile_Fragment();
            case 1:
                return new FeedActivity();
            case 2:
                return new AddActivity();
            case 3:
                    return new HistoryActivity();
              default:
                  break;
        }
        return null;
    }
    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }
    @Override
    public int getCount() {
        return 4;
    }
// public void notifyChangeInPosition(int n) {
////     shift the ID returned by getItemId outside the range of all previous fragments
//    baseId += getCount() + n;
//}
}
