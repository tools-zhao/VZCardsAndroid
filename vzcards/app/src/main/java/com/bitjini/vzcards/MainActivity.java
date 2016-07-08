package com.bitjini.vzcards;

import android.animation.ObjectAnimator;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ProgressBar;

/**
 * Created by VEENA on 12/7/2015.
 */
public class MainActivity extends AppCompatActivity implements ActionBar.TabListener {

   public TabLayout tabLayout;
     ViewPager viewPager;
     TabPagerAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInsatnceState)
    {
        super.onCreate(savedInsatnceState);
        setContentView(R.layout.viewpager_activty);

         tabLayout = (TabLayout) findViewById(R.id.tab_layout);



        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.my_vz_profile));

        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.feeds_drawable));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.add_drawable));
        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.history_drawable));
        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) tab.setCustomView(R.layout.view_home_tab);
        }

        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        viewPager = (ViewPager) findViewById(R.id.viewPager);
        adapter = new TabPagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setOffscreenPageLimit(3); // the number of "off screen" pages to keep loaded each side of the current page

        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

//                ... anything you may need to do to handle pager state ...
//                adapter.notifyDataSetChanged(); //this line will force all pages to be loaded fresh when changing between fragments

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {



            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        int page=1;
        viewPager.setCurrentItem(page);}




    @Override
    public void onTabSelected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {

    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, android.support.v4.app.FragmentTransaction ft) {

    }
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }

    public static String POSITION = "POSITION";

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(POSITION, tabLayout.getSelectedTabPosition());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        viewPager.setCurrentItem(savedInstanceState.getInt(POSITION));
    }
}
