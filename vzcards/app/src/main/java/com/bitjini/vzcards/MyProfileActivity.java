package com.bitjini.vzcards;


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
public class MyProfileActivity extends Fragment implements View.OnClickListener {


    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,
                             Bundle savedInstanceState) {
        View profile = inflater.inflate(R.layout.profile_layout, container, false);
        Button profilebtn = (Button) profile.findViewById(R.id.profilebtn);
        Button referral = (Button) profile.findViewById(R.id.referralbtn);
        Button vzfrnds = (Button) profile.findViewById(R.id.vzfrnds);

        profilebtn.setOnClickListener(this);
        vzfrnds.setOnClickListener(this);
        referral.setOnClickListener(this);

        return profile;
    }

    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.profilebtn:
                Fragment profilefragment = new MyProfileActivity();
                // get the id of fragment
                FrameLayout contentView = (FrameLayout) getActivity().findViewById(R.id.profile_frame);

                // Insert the fragment by replacing any existing fragment
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .add(contentView.getId(), profilefragment).addToBackStack(contentView.toString())
                        .commit();


                break;

            case R.id.vzfrnds:
                Fragment newfragment = new VZFriends_Fragment();
                // get the id of fragment
                FrameLayout contentView2 = (FrameLayout) getActivity().findViewById(R.id.profile_frame);

                // Insert the fragment by replacing any existing fragment
                FragmentManager fragmentManager1 = getFragmentManager();
                fragmentManager1.beginTransaction()
                        .replace(contentView2.getId(), newfragment).addToBackStack(contentView2.toString())
                        .commit();



                break;

            case R.id.referralbtn:
                Fragment fragment = new Referral_Fragment();
                // get the id of fragment
                FrameLayout contentView3 = (FrameLayout) getActivity().findViewById(R.id.profile_frame);

                // Insert the fragment by replacing any existing fragment
                FragmentManager fragmentManager2 = getFragmentManager();
                fragmentManager2.beginTransaction()
                        .replace(contentView3.getId(), fragment).addToBackStack(contentView3.toString())
                        .commit();


                break;
            default:
                break;

        }
    }
}
