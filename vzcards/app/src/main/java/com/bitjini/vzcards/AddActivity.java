package com.bitjini.vzcards;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.support.v4.app.Fragment;
import android.widget.Button;
import android.widget.FrameLayout;

/**
 * Created by VEENA on 12/7/2015.
 */
public class AddActivity extends android.support.v4.app.Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,
                             Bundle savedInstanceState)
    {
        View iHave = inflater.inflate(R.layout.add_layout, container, false);
        Button iNeed=(Button) iHave.findViewById(R.id.ineed);


        iNeed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                Fragment needFragment = new iNeed_Activity();
                // get the id of fragment
                FrameLayout contentView = (FrameLayout) getActivity().findViewById(R.id.ihave_frame);

                // Insert the fragment by replacing any existing fragment
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .add(contentView.getId(), needFragment).addToBackStack(contentView.toString())
                        .commit();

            }
        });

        return iHave;
    }
}
