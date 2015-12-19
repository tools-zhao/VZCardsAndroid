package com.bitjini.vzcards;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.view.View.OnClickListener;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by bitjini on 17/12/15.
 */
public class Referal_Activity extends Fragment implements OnClickListener {

   Context context;
    Button vzfrnds,profilebtn;

    ArrayList<ReferalUsers> ReferUsers=new ArrayList<ReferalUsers>();
    List<ReferalUsers> temp;
    // Contact List
    ListView listView;

    ReferUserAdapter adapter;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View referral = inflater.inflate(R.layout.list_referal_activity, container, false);

        listView=(ListView)referral.findViewById(R.id.referralList);

        profilebtn = (Button) referral.findViewById(R.id.profilebtn);
       vzfrnds = (Button) referral.findViewById(R.id.vzfrnds);

//        ArrayList names=new ArrayList<String>();
//        names.add("Mathew Json");
//        names.add("Sheldon Cooper");
//        names.add("Howard Wolowitz");
//
//        ArrayList referedNames=new ArrayList<String>();
//        referedNames.add("Walter White");
//        referedNames.add("Amy Fowler");
//        referedNames.add("Bernedette");
//
//        ArrayList n=new ArrayList<String>();
//        n.addAll(names);
//
//        ArrayList r=new ArrayList<String>();
//        r.addAll(referedNames);



        profilebtn.setOnClickListener(this);
        vzfrnds.setOnClickListener(this);

        String [] names={"Mathew Json","Sheldon Cooper","Howard Wolowitz"};
        String [] referedNames={"Walter White","Amy Fowler","Bernedette"};

        ReferalUsers referalUsers = new ReferalUsers();


            referalUsers.setName(names.toString());

            referalUsers.setReferredName(referedNames.toString());

        ReferUsers.add(referalUsers);
        adapter = new ReferUserAdapter(ReferUsers,getActivity());
        listView.setAdapter(adapter);


        return referral;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.profilebtn:
                Fragment profilefragment = new MyProfileActivity();
                // get the id of fragment
                FrameLayout contentView = (FrameLayout) getActivity().findViewById(R.id.referral_frame);

                // Insert the fragment by replacing any existing fragment
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .add(contentView.getId(), profilefragment).addToBackStack(contentView.toString())
                        .commit();

                break;

            case R.id.vzfrnds:
                Fragment newfragment = new VZFriends_Activity();
                // get the id of fragment
                FrameLayout contentView2 = (FrameLayout) getActivity().findViewById(R.id.referral_frame);

                // Insert the fragment by replacing any existing fragment
                FragmentManager fragmentManager2 = getFragmentManager();
                fragmentManager2.beginTransaction()
                        .replace(contentView2.getId(), newfragment).addToBackStack(contentView2.toString())
                        .commit();

                break;


            default:
                break;
        }


    }
}
