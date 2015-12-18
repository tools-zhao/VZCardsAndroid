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
 * Created by bitjini on 18/12/15.
 */
public class VZFriends_Activity extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,
                             Bundle savedInstanceState) {
        View vzfrnds = inflater.inflate(R.layout.contact_listview, container, false);
        Button profilebtn=(Button) vzfrnds.findViewById(R.id.profilebtn);
        Button referral=(Button) vzfrnds.findViewById(R.id.referral);

        profilebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                Fragment profilefragment = new MyProfileActivity();
                // get the id of fragment
                FrameLayout contentView = (FrameLayout) getActivity().findViewById(R.id.vzfrnds_frame);

                // Insert the fragment by replacing any existing fragment
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .add(contentView.getId(), profilefragment).addToBackStack(contentView.toString())
                        .commit();

            }
        });

        referral.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                Fragment fragment = new Referal_Activity();
                // get the id of fragment
                FrameLayout contentView = (FrameLayout) getActivity().findViewById(R.id.vzfrnds_frame);

                // Insert the fragment by replacing any existing fragment
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(contentView.getId(),fragment).addToBackStack(contentView.toString())
                        .commit();

            }
        });
        return vzfrnds;

    }
}
