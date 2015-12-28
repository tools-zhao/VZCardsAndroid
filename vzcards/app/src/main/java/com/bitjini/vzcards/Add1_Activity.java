package com.bitjini.vzcards;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by bitjini on 28/12/15.
 */
public class Add1_Activity extends Fragment {

    public View onCreateView(LayoutInflater inflater,ViewGroup container,
                             Bundle savedInstanceState)
    {
        View add1=inflater.inflate(R.layout.add_1_layout,container,false);
        return add1;
    }
}
