package com.bitjini.vzcards;

import android.animation.LayoutTransition;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.*;
import android.view.animation.Transformation;
import android.widget.LinearLayout.LayoutParams;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import java.util.ArrayList;

/**
 * Created by bitjini on 28/12/15.
 */
public class Referral_Fragment extends Fragment implements View.OnClickListener{
    ArrayList<ReferalUsers> groupItem=new ArrayList<ReferalUsers>();
    Button vzfrnds,profilebtn,referralbtn;
    ListView list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View referral = inflater.inflate(R.layout.list_referal_activity, container, false);
        RelativeLayout linearLayout = (RelativeLayout) referral.findViewById(R.id.parent);
        linearLayout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);

        referralbtn=(Button)referral.findViewById(R.id.referralbtn);
        referral.setSelected(true);
        referral.setPressed(true);
        list = (ListView) referral.findViewById(R.id.referralList);
        profilebtn = (Button) referral.findViewById(R.id.profilebtn);
        vzfrnds = (Button) referral.findViewById(R.id.vzfrnds);

        profilebtn.setOnClickListener(this);
        vzfrnds.setOnClickListener(this);


        ArrayList names = new ArrayList<String>();
        names.add("Mathew Json");
        names.add("Sheldon Cooper");
        names.add("Howard Wolowitz");

        ArrayList referedNames = new ArrayList<String>();
        referedNames.add("Walter White");
        referedNames.add("Amy Fowler");
        referedNames.add("Bernedette");


        ArrayList<String> child = new ArrayList<String>();
        child.add("Contact Us");
        child.add("About Us");
        child.add("Location");


        // Populate our list with groups and it's children
        for (int i = 0; i < names.size(); i++) {
            ReferalUsers referalUsers = new ReferalUsers();

            referalUsers.setName((String) names.get(i));
            referalUsers.setReferredName((String) referedNames.get(i));
            referalUsers.setDesc(child.get(i));

            groupItem.add(referalUsers);
        }
        // Creating the list adapter and populating the list
        CustomListAdapter listAdapter = new CustomListAdapter(getActivity(), groupItem, R.layout.referral);
        list.setAdapter(listAdapter);

        // Creating an item click listener, to open/close our toolbar for each item

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int height = 0;
                View toolbar=(View) view.findViewById(R.id.toolbar);
                if (toolbar.getVisibility() == View.VISIBLE) {
                    MyCustomAnimation a = new MyCustomAnimation(toolbar, 500, MyCustomAnimation.COLLAPSE);

                    toolbar.startAnimation(a);
                    toolbar.setClickable(true);
                } else {
                    MyCustomAnimation a = new MyCustomAnimation(toolbar, 500, MyCustomAnimation.EXPAND);
                    a.setHeight(height);
                    toolbar.startAnimation(a);
                    toolbar.setClickable(true);
                }
            }
        });
        return referral;

    }
    /**
     * A simple implementation of list adapter.
     */

   public class CustomListAdapter extends BaseAdapter implements View.OnClickListener {

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
                LayoutInflater inflater = (LayoutInflater) _c.getSystemService
                        (Context.LAYOUT_INFLATER_SERVICE);
                v = inflater.inflate(R.layout.referral, null);
            }
            TextView name = (TextView) v.findViewById(R.id.referralName);
            TextView referredName = (TextView) v.findViewById(R.id.referred);

            ReferalUsers cat = groupItem.get(position);

            name.setText(cat.getName());
            referredName.setText(cat.getReferredName());

            // Resets the toolbar to be closed
            View toolbar = (View) v.findViewById(R.id.toolbar);
            TextView desc = (TextView) v.findViewById(R.id.textView1);
            desc.setText(cat.getDesc());
            Button callBtn = (Button) v.findViewById(R.id.btnCall);
            Button vzCardBtn = (Button) v.findViewById(R.id.btnVzCard);

            callBtn.setOnClickListener(this);
            vzCardBtn.setOnClickListener(this);

            toolbar.setVisibility(View.GONE);
            return v;
        }

        public void onClick(View view) {
          switch (view.getId()) {
              case R.id.btnCall:
                  try{
                      Intent callIntent = new Intent(Intent.ACTION_CALL);
                      callIntent.setData(Uri.parse("tel:"+"8904826233"));
                      startActivity(callIntent);
                  }
                  catch (android.content.ActivityNotFoundException ex)
              {
                  Toast.makeText(_c,"your Activity is not found",Toast.LENGTH_LONG).show();
              }
                  break;

              case R.id.btnVzCard:
                      Fragment add1 = new Add1_Activity();
                        // get the id of fragment
                       FrameLayout contentView = (FrameLayout) getActivity().findViewById(R.id.referral_frame);

                      // Insert the fragment by replacing any existing fragment
                      FragmentManager fragmentManager = getFragmentManager();
                      fragmentManager.beginTransaction()
                        .add(contentView.getId(), add1).addToBackStack(contentView.toString())
                        .commit();

                       break;
              default:
                  break;
           }


        }
        @Override
        public boolean isEnabled(int position)
        {
            return true;
        }


    }


    public class MyCustomAnimation extends Animation {

        public final static int COLLAPSE = 1;
        public final static int EXPAND = 0;

        private View mView;
        private int mEndHeight;
        private int mType;
        private LinearLayout.LayoutParams mLayoutParams;

        public MyCustomAnimation(View view, int duration, int type) {

            setDuration(duration);
            mView = view;
            mEndHeight = mView.getHeight();
            mLayoutParams = ((LinearLayout.LayoutParams) view.getLayoutParams());
            mType = type;
            if(mType == EXPAND) {
                mLayoutParams.height = 0;
            } else {
                mLayoutParams.height = LayoutParams.WRAP_CONTENT;
            }
            view.setVisibility(View.VISIBLE);
        }

        public int getHeight(){
            return mView.getHeight();
        }

        public void setHeight(int height){
            mEndHeight = height;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {

            super.applyTransformation(interpolatedTime, t);
            if (interpolatedTime < 1.0f) {
                if(mType == EXPAND) {
                    mLayoutParams.height =  (int)(mEndHeight * interpolatedTime);
                } else {
                    mLayoutParams.height = (int) (mEndHeight * (1 - interpolatedTime));
                }
                mView.requestLayout();
            } else {
                if(mType == EXPAND) {
                    mLayoutParams.height = LayoutParams.WRAP_CONTENT;
                    mView.requestLayout();
                }else{
                    mView.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.profilebtn:
                Fragment profilefragment = new MyProfile_Fragment();
                // get the id of fragment
                FrameLayout contentView = (FrameLayout) getActivity().findViewById(R.id.referral_frame);

                // Insert the fragment by replacing any existing fragment
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .add(contentView.getId(), profilefragment).addToBackStack(contentView.toString())
                        .commit();

                break;

            case R.id.vzfrnds:
                Fragment newfragment = new VZFriends_Fragment();
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
