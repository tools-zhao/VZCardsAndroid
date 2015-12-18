package com.bitjini.vzcards;

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
public class iNeed_Activity extends Fragment{
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,
                             Bundle savedInstanceState) {
        View iNeed = inflater.inflate(R.layout.ineed, container, false);
        Button havebtn = (Button) iNeed.findViewById(R.id.ihave);


        havebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Fragment haveFragment = new AddActivity();
                // get the id of fragment
                FrameLayout contentView = (FrameLayout) getActivity().findViewById(R.id.ineed_frame);

                // Insert the fragment by replacing any existing fragment
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .add(contentView.getId(), haveFragment).addToBackStack(contentView.toString())
                        .commit();

            }
        });

        return iNeed;
    }
}
