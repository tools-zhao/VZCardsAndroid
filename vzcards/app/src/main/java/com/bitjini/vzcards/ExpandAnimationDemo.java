package com.bitjini.vzcards;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.*;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout.LayoutParams;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import com.bitjini.vzcards.R;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: udic
 * Date: 30/08/11
 * Time: 00:05
 * To change this template use File | Settings | File Templates.
 */
public class ExpandAnimationDemo extends Fragment implements View.OnClickListener,AdapterView.OnItemClickListener {
    ArrayList<ReferalUsers> groupItem=new ArrayList<ReferalUsers>();
    Button vzfrnds,profilebtn;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View referral = inflater.inflate(R.layout.list_referal_activity, container, false);

        ListView list = (ListView)referral.findViewById(R.id.referralList);
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


        ArrayList<String> child = new ArrayList<String>();
        child.add("Contact Us");
        child.add("About Us");
        child.add("Location");



        // Populate our list with groups and it's children
        for(int i = 0; i < names.size(); i++) {
            ReferalUsers referalUsers = new ReferalUsers();

            referalUsers.setName((String) names.get(i));
            referalUsers.setReferredName((String) referedNames.get(i));
            referalUsers.setDesc(child.get(i));

            groupItem.add(referalUsers);
        }
        // Creating the list adapter and populating the list
        CustomListAdapter listAdapter = new CustomListAdapter(getActivity(), groupItem,R.layout.referral);
        list.setAdapter(listAdapter);

        // Creating an item click listener, to open/close our toolbar for each item
        list.setOnItemClickListener(this) ;

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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int i, long l) {
//        LayoutInflater li = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        view= li.inflate(R.layout.referral, parent,false);
        View toolbar = view.findViewById(R.id.toolbar);

        // Creating the expand animation for the item
        ExpandAnimation expandAni = new ExpandAnimation(toolbar, 500);

        // Start the animation on the toolbar
        toolbar.startAnimation(expandAni);

        toolbar.setFocusable(false);
    }

    /**
     * A simple implementation of list adapter.
     */
    class CustomListAdapter extends BaseAdapter {

        Context _c;
        public ArrayList<ReferalUsers> groupItem;
        int textViewResourceId;
        public CustomListAdapter(Context context, ArrayList<ReferalUsers> group,int textViewResourceId1) {
            groupItem=group;
               textViewResourceId=textViewResourceId1;
            _c=context;
        }

        @Override
        public int getCount()
        {
            return groupItem.size();
        }
        @Override
        public Object getItem(int i)
        {
            return groupItem.get(i);
        }
        @Override
        public long getItemId(int i)
        {
            return i;

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View v = convertView;

            if (v == null) {
                LayoutInflater inflater = (LayoutInflater)_c.getSystemService
                        (Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(R.layout.referral, null);
            }
            TextView name = (TextView) v.findViewById(R.id.referralName);
            TextView referredName=(TextView)v.findViewById(R.id.referred);

            ReferalUsers cat = groupItem.get(position);

            name.setText(cat.getName());
            referredName.setText(cat.getReferredName());
//            ((TextView)convertView.findViewById(R.id.title)).setText(getItem(position));

            // Resets the toolbar to be closed
            View toolbar = (View)v.findViewById(R.id.toolbar);
            TextView desc=(TextView)v.findViewById(R.id.textView1);
            desc.setText(cat.getDesc());

            ((LinearLayout.LayoutParams) toolbar.getLayoutParams()).bottomMargin = -80;
            toolbar.setVisibility(View.GONE);

            return v;
        }
    }

    /**
     * This animation class is animating the expanding and reducing the size of a view.
     * The animation toggles between the Expand and Reduce, depending on the current state of the view
     * @author Udinic
     *
     */
    private class ExpandAnimation extends Animation {
        private View mAnimatedView;
        private LayoutParams mViewLayoutParams;
        private int mMarginStart, mMarginEnd;
        private boolean mIsVisibleAfter = false;
        private boolean mWasEndedAlready = false;

        /**
         * Initialize the animation
         *
         * @param view     The layout we want to animate
         * @param duration The duration of the animation, in ms
         */
        public ExpandAnimation(View view, int duration) {

            setDuration(duration);
            mAnimatedView = view;
            mViewLayoutParams = (LayoutParams) view.getLayoutParams();

            // decide to show or hide the view
            mIsVisibleAfter = (view.getVisibility() == View.VISIBLE);

            mMarginStart = mViewLayoutParams.bottomMargin;
            mMarginEnd = (mMarginStart == 0 ? (0 - view.getHeight()) : 0);

            view.setVisibility(View.VISIBLE);
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);

            if (interpolatedTime < 1.0f) {

                // Calculating the new bottom margin, and setting it
                mViewLayoutParams.bottomMargin = mMarginStart
                        + (int) ((mMarginEnd - mMarginStart) * interpolatedTime);

                // Invalidating the layout, making us seeing the changes we made
                mAnimatedView.requestLayout();

                // Making sure we didn't run the ending before (it happens!)
            } else if (!mWasEndedAlready) {
                mViewLayoutParams.bottomMargin = mMarginEnd;
                mAnimatedView.requestLayout();

                if (mIsVisibleAfter) {
                    mAnimatedView.setVisibility(View.GONE);
                }
                mWasEndedAlready = true;
            }
        }
    }
}