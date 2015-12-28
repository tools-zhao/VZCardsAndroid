package com.bitjini.vzcards;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by bitjini on 28/12/15.
 */
public class FeedActivity extends Fragment {

    public View onCreateView(LayoutInflater inflater,ViewGroup container,
                             Bundle savedInstanceState)
    {
        View feed=inflater.inflate(R.layout.feed_layout,container,false);
        return feed;
    }
}
