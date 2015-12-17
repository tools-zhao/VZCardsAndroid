package com.bitjini.vzcards;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by VEENA on 12/7/2015.
 */
public class HistoryActivity extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,
                             Bundle savedInstanceState)
    {
        View history=inflater.inflate(R.layout.history_layout,container,false);
        return history;
    }
}
