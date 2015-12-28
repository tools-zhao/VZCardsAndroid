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
import android.widget.FrameLayout;

/**
 * Created by VEENA on 12/7/2015.
 */
public class MyProfileActivity extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,
                             Bundle savedInstanceState)
    {
        View profile=inflater.inflate(R.layout.profile_layout,container,false);
        Button profilebtn=(Button) profile.findViewById(R.id.profilebtn);
        Button referral=(Button) profile.findViewById(R.id.referral);
        Button vzfrnds=(Button) profile.findViewById(R.id.vzfrnds);

        profilebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                Fragment profilefragment = new MyProfileActivity();
                // get the id of fragment
                FrameLayout contentView = (FrameLayout) getActivity().findViewById(R.id.profile_frame);

                // Insert the fragment by replacing any existing fragment
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .add(contentView.getId(), profilefragment).addToBackStack(contentView.toString())
                        .commit();

            }
        });
        vzfrnds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

            Fragment newfragment = new VZFriends_Activity();
                // get the id of fragment
                FrameLayout contentView = (FrameLayout) getActivity().findViewById(R.id.profile_frame);

               // Insert the fragment by replacing any existing fragment
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(contentView.getId(), newfragment).addToBackStack(contentView.toString())
                        .commit();

            }
        });
        referral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                Fragment fragment = new ExpandAnimationDemo();
                // get the id of fragment
                FrameLayout contentView = (FrameLayout) getActivity().findViewById(R.id.profile_frame);

                // Insert the fragment by replacing any existing fragment
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(contentView.getId(),fragment).addToBackStack(contentView.toString())
                        .commit();

            }
        });
        return profile;
    }
}
