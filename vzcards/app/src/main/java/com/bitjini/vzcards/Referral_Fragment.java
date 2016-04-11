package com.bitjini.vzcards;

import android.animation.LayoutTransition;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.*;
import android.view.animation.Transformation;
import android.widget.LinearLayout.LayoutParams;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by bitjini on 28/12/15.
 */
public class Referral_Fragment extends Fragment implements View.OnClickListener {

    String HISTORY_URL = "http://vzcards-api.herokuapp.com/history/?access_token=";
    VerifyScreen p = new VerifyScreen();

    MyProfile_Fragment pr = new MyProfile_Fragment();
    ArrayList<ReferalUsers> groupItem = new ArrayList<ReferalUsers>();
    Button vzfrnds, profilebtn, referralbtn;
    ListView list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View referral = inflater.inflate(R.layout.list_referal_activity, container, false);
        RelativeLayout linearLayout = (RelativeLayout) referral.findViewById(R.id.parent);
        linearLayout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);

        referralbtn = (Button) referral.findViewById(R.id.referralbtn);
        referral.setSelected(true);
        referral.setPressed(true);
        list = (ListView) referral.findViewById(R.id.referralList);
        profilebtn = (Button) referral.findViewById(R.id.profilebtn);
        vzfrnds = (Button) referral.findViewById(R.id.vzfrnds);

        profilebtn.setOnClickListener(this);
        vzfrnds.setOnClickListener(this);

        try {
            String result = new HttpAsyncTask(getActivity()).execute(HISTORY_URL + p.token_sharedPreference).get();
            Log.e("received History", "" + result);
            ReferalUsers referalUsers = new ReferalUsers();

            JSONObject jsonObject = new JSONObject(result);

            String response = jsonObject.getString("response");
            // Getting JSON Array node
            JSONArray arr = jsonObject.getJSONArray("response");

            // looping through All Contacts
            for (int i = 0; i < arr.length(); i++) {
                JSONObject c = arr.getJSONObject(i);
                // Connection Node in an array
                JSONArray arr2 = c.getJSONArray("connections");
                Log.e(" connections :", "" + arr2);

                for (int i2 = 0; i2 < arr2.length(); i2++) {
                    JSONObject c2 = arr2.getJSONObject(i2);
                    JSONObject reffered_phone_details = c2.getJSONObject("reffered_phone_details");
                    Log.w("reffered_phone_", "" + reffered_phone_details);
                    String referedFname = reffered_phone_details.getString("firstname");
                    String referedLname = reffered_phone_details.getString("lastname");
                    String referedphoto = reffered_phone_details.getString("photo");
                    String phone = reffered_phone_details.getString("phone");
                    String company = reffered_phone_details.getString("company");
                    String pin_code = reffered_phone_details.getString("pin_code");
                    String industry = reffered_phone_details.getString("industry");
                    String address1 = reffered_phone_details.getString("address_line_1");
                    String address2 = reffered_phone_details.getString("address_line_2");
                    String city = reffered_phone_details.getString("city");
                    String company_photo = reffered_phone_details.getString("company_photo");
                    String email = reffered_phone_details.getString("email");

                    referalUsers.setReferredfName(referedFname);
                    referalUsers.setReferredlName(referedLname);
                    referalUsers.setReferedPhoto(referedphoto);
                    referalUsers.setPhone(phone);
                    referalUsers.setEmail(email);
                    referalUsers.setCompany(company);
                    referalUsers.setPin_code(pin_code);
                    referalUsers.setIndustry(industry);
                    referalUsers.setAddress1(address1);
                    referalUsers.setAddress2(address2);
                    referalUsers.setCity(city);
                    referalUsers.setComany_photo(company_photo);

                    JSONObject reffered_ticket_details = c2.getJSONObject("reffered_ticket_details");
                    Log.w("reffered_ticket_details", "" + reffered_ticket_details);
                    JSONObject connecter_details = c2.getJSONObject("connecter_details");
                    Log.w("connecter_details", "" + connecter_details);

                    String fname = connecter_details.getString("firstname");
                    String lastname = connecter_details.getString("lastname");
                    String photo = connecter_details.getString("photo");
                    referalUsers.setFname(fname);
                    referalUsers.setLname(lastname);
                    referalUsers.setPhoto(photo);

                }
                // ticket_details Node in an json object
                JSONObject ticket_details = c.getJSONObject("ticket_details");

                Log.e(" ticket_details :", "" + ticket_details);
                String question = ticket_details.getString("question");
                String description = ticket_details.getString("description");
                String ticket_id = ticket_details.getString("ticket_id");
                String itemName = ticket_details.getString("item");
                String date_validity = ticket_details.getString("date_validity");
                String vz_id = ticket_details.getString("vz_id");
                String item_photo = ticket_details.getString("item_photo");
                String date_created = ticket_details.getString("date_created");
                Log.e(" description :", "" + description);

                referalUsers.setDesc(description);
                referalUsers.setItemName(itemName);
                referalUsers.setItem_photo(item_photo);
                groupItem.add(referalUsers);


            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        // Populate our list with groups and it's children
        // Creating the list adapter and populating the list
        CustomListAdapter listAdapter = new CustomListAdapter(getActivity(), groupItem, R.layout.referral);
        list.setAdapter(listAdapter);

        // Creating an item click listener, to open/close our toolbar for each item

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                int height = 0;
                View toolbar = (View) view.findViewById(R.id.toolbar);
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
        ReferalUsers cat;
        Context _c;
        public ArrayList<ReferalUsers> groupItem;
        int textViewResourceId;

        public CustomListAdapter(Context context, ArrayList<ReferalUsers> group, int textViewResourceId1) {
            groupItem = group;
            textViewResourceId = textViewResourceId1;
            _c = context;
        }

        @Override
        public int getCount() {
            return groupItem.size();
        }

        @Override
        public Object getItem(int i) {
            return groupItem.get(i);
        }

        @Override
        public long getItemId(int i) {
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
            TextView itemName = (TextView) v.findViewById(R.id.itemName);
            ImageView referredPhoto = (ImageView) v.findViewById(R.id.referdPhoto);
            ImageView photo = (ImageView) v.findViewById(R.id.photo);

            cat = groupItem.get(position);

            name.setText(cat.getFname() + " " + cat.getLname());
            referredName.setText(cat.getReferredfName() + " " + cat.getReferredlName());
            itemName.setText("for " + cat.getItemName());
            try {
                if (cat.getPhoto() != null) {
                    photo.setTag(cat.getPhoto());
                    new DownloadImagesTask(_c).execute(photo);// Download item_photo from AsynTask
//                    pr.bm=null; pr.output=null;
//                    pr.DownloadFullFromUrl(cat.getPhoto());
//                    pr.getRoundedCornerBitmap(pr.bm, 100);
//                    photo.setImageBitmap(pr.output);

                } else {
                    photo.setImageResource(R.drawable.profile_pic_placeholder);
                }
                if (cat.getReferedPhoto() != null) {
//                    pr.bm=null; pr.output=null;
//                    pr.DownloadFullFromUrl(cat.getReferedPhoto());
//                    pr.getRoundedCornerBitmap(pr.bm, 100);
//                    referredPhoto.setImageBitmap(pr.output);
                    referredPhoto.setTag(cat.getReferedPhoto());
                    new DownloadImagesTask(_c).execute(referredPhoto);// Download item_photo from AsynTask

                } else {
                    photo.setImageResource(R.drawable.profile_pic_placeholder);
                }

            } catch (ArrayIndexOutOfBoundsException ae) {
                ae.printStackTrace();

            } catch (OutOfMemoryError e) {
                //  v.imageView.setImageDrawable(this._c.getDrawable(R.drawable.contact));
                e.printStackTrace();
            }

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
                    try {
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:" + cat.getPhone()));
                        startActivity(callIntent);
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(_c, "your Activity is not found", Toast.LENGTH_LONG).show();
                    }
                    break;

                case R.id.btnVzCard:

                    Friends_Profile ldf = new Friends_Profile();

                    Bundle args = new Bundle();

                    args.putString("fname", cat.getReferredfName());
                    args.putString("lname", cat.getReferredlName());
                    args.putString("lname", cat.getReferredlName());
                    args.putString("photo", cat.getReferedPhoto());
                    args.putString("phone", cat.getPhone());
                    args.putString("company", cat.getCompany());
                    args.putString("pin_code", cat.getPin_code());
                    args.putString("industry", cat.getIndustry());
                    args.putString("address1", cat.getAddress1());
                    args.putString("address2", cat.getAddress2());
                    args.putString("city", cat.getCity());
                    args.putString("company_photo", cat.getComany_photo());
                    ldf.setArguments(args);
                    //Inflate the fragment
                    getFragmentManager().beginTransaction().add(R.id.referral_frame, ldf).addToBackStack(ldf.toString())
                            .commit();


//                      Fragment add1 = new Add1_Activity();
//                        // get the id of fragment
//                       FrameLayout contentView = (FrameLayout) getActivity().findViewById(R.id.referral_frame);
//
//                      // Insert the fragment by replacing any existing fragment
//                      FragmentManager fragmentManager = getFragmentManager();
//                      fragmentManager.beginTransaction()
//                        .add(contentView.getId(), add1).addToBackStack(contentView.toString())
//                        .commit();

                    break;
                default:
                    break;
            }


        }

        @Override
        public boolean isEnabled(int position) {
            return true;
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
