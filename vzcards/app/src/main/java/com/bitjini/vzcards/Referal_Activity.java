package com.bitjini.vzcards;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.FrameLayout;
import android.view.View.OnClickListener;
import android.widget.ListView;
import android.widget.Toast;

import java.sql.Ref;
import java.util.ArrayList;

/**
 * Created by bitjini on 17/12/15.
 */
public class Referal_Activity extends Fragment implements OnClickListener,ExpandableListView.OnChildClickListener {

   Context context;
    Button vzfrnds,profilebtn;

    ArrayList<ReferalUsers> arrayReferUsers=new ArrayList<ReferalUsers>();

    // Contact List
    ListView listView;

    ReferUserAdapter adapter;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View referral = inflater.inflate(R.layout.list_referal_activity, container, false);

//        listView=(ListView)referral.findViewById(R.id.referralList);
        ExpandableListView listView=(ExpandableListView)referral.findViewById(R.id.referralList);

//        setGroupData();
        setChildGroupData();


        profilebtn = (Button) referral.findViewById(R.id.profilebtn);
       vzfrnds = (Button) referral.findViewById(R.id.vzfrnds);

        profilebtn.setOnClickListener(this);
        vzfrnds.setOnClickListener(this);

        ArrayList names=new ArrayList<String>();
        names.add("Mathew Json");
        names.add("Sheldon Cooper");
        names.add("Howard Wolowitz");

        ArrayList referedNames=new ArrayList<String>();
        referedNames.add("Walter White");
        referedNames.add("Amy Fowler");
        referedNames.add("Bernedette");


        for(int i=0;i<names.size();i++) {
            ReferalUsers referalUsers = new ReferalUsers();

            referalUsers.setName((String) names.get(i));
            referalUsers.setReferredName((String) referedNames.get(i));
//
            arrayReferUsers.add(referalUsers);
//
////            Log.e("values...", ""+referalUsers.getName());
////            Log.e("values...", ""+referalUsers.getReferredName());
//            Log.e(" array list values...", ""+arrayReferUsers.get(i).getName());
////            Log.e(" array list values...", ""+arrayReferUsers.get(i).getReferredName());
//
//
            }
//
NewAdapter mNewAdapter=new NewAdapter(arrayReferUsers,childItem,getActivity());
        listView.setAdapter(mNewAdapter);
        listView.setOnChildClickListener(this);

//        adapter = new ReferUserAdapter(arrayReferUsers,getActivity());
//        listView.setAdapter(adapter);


        return referral;
    }
//    public void setGroupData() {
//        ArrayList<String> group = new ArrayList<String>();
//        group.add("TechNology");
//        group.add("Mobile");
//        group.add("Manufacturer");
//        group.add("Extras");
//        for(int i=0;i<group.size();i++) {
//            ReferalUsers referalUsers = new ReferalUsers();
//
//            referalUsers.setName((String) group.get(i));
////            referalUsers.setReferredName((String) referedNames.get(i));
//
//            groupItem.add(referalUsers);}
//
//    }
    ArrayList<ReferalUsers> groupItem=new ArrayList<ReferalUsers>();
    ArrayList<ReferalUsers> childItem=new ArrayList<ReferalUsers>();
    public void setChildGroupData(){

        ArrayList<String> child = new ArrayList<String>();
        child.add("Contact Us");
        child.add("About Us");
        child.add("Location");
        child.add("Root Cause");
        childItem.add(child);
        for(int i=0;i<child.size();i++) {
            ReferalUsers referalUsers = new ReferalUsers();

            referalUsers.setName((String) child.get(i));
//            referalUsers.setReferredName((String) referedNames.get(i));

            childItem.add(referalUsers);}
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

    @Override
    public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long l) {
        Toast.makeText(getActivity(), "Clicked On Child",
                Toast.LENGTH_SHORT).show();
        return true;
    }


}
