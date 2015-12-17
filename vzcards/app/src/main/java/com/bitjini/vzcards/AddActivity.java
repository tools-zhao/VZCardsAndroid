package com.bitjini.vzcards;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.support.v4.app.Fragment;
/**
 * Created by VEENA on 12/7/2015.
 */
public class AddActivity extends android.support.v4.app.Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,
                             Bundle savedInstanceState)
    {
        View addView=inflater.inflate(R.layout.add_layout,container,false);
        return addView;
    }
}
