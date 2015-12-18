package com.bitjini.vzcards;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by bitjini on 17/12/15.
 */
public class Referal_Activity extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,
                             Bundle savedInstanceState) {
        View referral = inflater.inflate(R.layout.referral, container, false);
        return referral;

    }
}
