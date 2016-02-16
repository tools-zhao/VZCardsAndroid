package com.bitjini.vzcards;

import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.View;

/**
 * Created by VEENA on 12/7/2015.
 */
public class MainActivity extends AppCompatActivity implements ActionBar.TabListener {


    @Override
    protected void onCreate(Bundle savedInsatnceState)
    {
        super.onCreate(savedInsatnceState);
        setContentView(R.layout.viewpager_activty);

//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);

//        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.my_vz_profile));
//
//        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.feeds_drawable));
//        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.add_drawable));
//        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.history_drawable));

        View view1 = getLayoutInflater().inflate(R.layout.custom_tab, null);
        view1.findViewById(R.id.icon).setBackgroundResource(R.drawable.my_vz_profile);
        tabLayout.addTab(tabLayout.newTab().setCustomView(view1));


        View view2 = getLayoutInflater().inflate(R.layout.custom_tab, null);
        view2.findViewById(R.id.icon).setBackgroundResource(R.drawable.feeds_drawable);
        tabLayout.addTab(tabLayout.newTab().setCustomView(view2));


        View view3 = getLayoutInflater().inflate(R.layout.custom_tab, null);
        view3.findViewById(R.id.icon).setBackgroundResource(R.drawable.add_drawable);
        tabLayout.addTab(tabLayout.newTab().setCustomView(view3));

        View view4 = getLayoutInflater().inflate(R.layout.custom_tab, null);
        view4.findViewById(R.id.icon).setBackgroundResource(R.drawable.history_drawable);
        tabLayout.addTab(tabLayout.newTab().setCustomView(view4));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        final TabPagerAdapter adapter = new TabPagerAdapter
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
        viewPager.setCurrentItem(1);}

//    @Override
//    public CharSequence getPageTitle(int position) {
//        // Generate title based on item position
//        // return tabTitles[position];
//
//        // getDrawable(int i) is deprecated, use getDrawable(int i, Theme theme) for min SDK >=21
//        // or ContextCompat.getDrawable(Context context, int id) if you want support for older versions.
//        // Drawable image = context.getResources().getDrawable(iconIds[position], context.getTheme());
//        // Drawable image = context.getResources().getDrawable(imageResId[position]);
//
//        Drawable image = ContextCompat.getDrawable(context, imageResId[position]);
//        image.setBounds(0, 0, image.getIntrinsicWidth(), image.getIntrinsicHeight());
//        SpannableString sb = new SpannableString(" ");
//        ImageSpan imageSpan = new ImageSpan(image, ImageSpan.ALIGN_BOTTOM);
//        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
//        return sb;
//    }


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
}
