package com.bitjini.vzcards;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by VEENA on 12/7/2015.
 */
public class TabPagerAdapter extends FragmentPagerAdapter {
    int mNumOfTabs;
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
        return POSITION_NONE;
    }
    @Override
    public int getCount() {
        return 4;
    }
}
