package com.bitjini.vzcards;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

/**
 * Created by bitjini on 17/12/15.
 */
public class Referal_Activity extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,
                             Bundle savedInstanceState) {
        View referral = inflater.inflate(R.layout.referral, container, false);
        Button profilebtn=(Button) referral.findViewById(R.id.profilebtn);

        Button vzfrnds=(Button) referral.findViewById(R.id.vzfrnds);

        profilebtn.setOnClickListener(this);
            @Override
            public void onClick(View v)
            {

                Fragment profilefragment = new MyProfileActivity();
                // get the id of fragment
                FrameLayout contentView = (FrameLayout) getActivity().findViewById(R.id.referral_frame);

                // Insert the fragment by replacing any existing fragment
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .add(contentView.getId(), profilefragment).addToBackStack(contentView.toString())
                        .commit();

            }
        
        vzfrnds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                Fragment newfragment = new VZFriends_Activity();
                // get the id of fragment
                FrameLayout contentView = (FrameLayout) getActivity().findViewById(R.id.referral_frame);

                // Insert the fragment by replacing any existing fragment
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(contentView.getId(), newfragment).addToBackStack(contentView.toString())
                        .commit();

            }
        });

        return referral;

    }
}
