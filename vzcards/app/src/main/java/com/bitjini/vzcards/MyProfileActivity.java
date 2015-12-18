package com.bitjini.vzcards;


import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by VEENA on 12/7/2015.
 */
public class MyProfileActivity extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,
                             Bundle savedInstanceState)
    {
        View profile=inflater.inflate(R.layout.profile_layout,container,false);
        Button referral=(Button) profile.findViewById(R.id.referral);

        referral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

            Fragment newfragment = new Referal_Activity();

//
//// Insert the fragment by replacing any existing fragment
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.profile_frame,newfragment).addToBackStack(null)
                        .commit();

            }
        });
        return profile;
    }
}
