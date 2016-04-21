package com.bitjini.vzcards;

import android.animation.LayoutTransition;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
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

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by bitjini on 28/12/15.
 */
public class Referral_Fragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    String HISTORY_URL = "http://vzcards-api.herokuapp.com/history/?access_token=";
    VerifyScreen p = new VerifyScreen();
    private SwipeRefreshLayout swipeRefreshLayout;

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

        swipeRefreshLayout = (SwipeRefreshLayout) referral.findViewById(R.id.pullToRefresh);
        // the refresh listner. this would be called when the layout is pulled down
        swipeRefreshLayout.setOnRefreshListener(this);
        // sets the colors used in the refresh animation
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark,R.color.pink,
                R.color.colorPrimary
                ,R.color.red);

        referralbtn = (Button) referral.findViewById(R.id.referralbtn);
        referral.setSelected(true);
        referral.setPressed(true);
        list = (ListView) referral.findViewById(R.id.referralList);
        profilebtn = (Button) referral.findViewById(R.id.profilebtn);
        vzfrnds = (Button) referral.findViewById(R.id.vzfrnds);

        profilebtn.setOnClickListener(this);
        vzfrnds.setOnClickListener(this);

        // Populate our list with groups and it's children
        // Creating the list adapter and populating the list
        getReferalContents();
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
        // to avoid triggering of swipe to refresh on scrolling of listview
        list.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                boolean enable = false;
                if(list != null && list.getChildCount() > 0){
                    // check if the first item of the list is visible
                    boolean firstItemVisible = list.getFirstVisiblePosition() == 0;
                    // check if the top of the first item is visible
                    boolean topOfFirstItemVisible = list.getChildAt(0).getTop() == 0;
                    // enabling or disabling the refresh layout
                    enable = firstItemVisible && topOfFirstItemVisible;
                }
                swipeRefreshLayout.setEnabled(enable);
            }
        });

        return referral;

    }
    public void getReferalContents() {
        try {
            String result = new HttpAsyncTask(getActivity()).execute(HISTORY_URL + p.token_sharedPreference).get();
            Log.e("received History", "" + result);

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

                    String referedFname = "", referedLname = "", refphoto = "", phone = "", company = "", pin_code = "", industry = "",
                            address1 = "", address2 = "", city = "", company_photo = "", email = "";


                    String refPhoneDetails = c2.getString("reffered_phone_details");

                    JsonParser parser1 = new JsonParser();
                    JsonElement jsonObject1 = parser1.parse(refPhoneDetails);
//                        Log.e("json object1 1=",""+jsonObject1);
                    if (jsonObject1.isJsonObject()) {
                        JSONObject reffered_phone_details = c2.getJSONObject("reffered_phone_details");
//                        Log.w("reffered_phone_", "" + reffered_phone_details);
                        referedFname = reffered_phone_details.getString("firstname");
                        referedLname = reffered_phone_details.getString("lastname");
                        refphoto = reffered_phone_details.getString("photo");
                        phone = reffered_phone_details.getString("phone");
                        company = reffered_phone_details.getString("company");
                        pin_code = reffered_phone_details.getString("pin_code");
                        industry = reffered_phone_details.getString("industry");
                        address1 = reffered_phone_details.getString("address_line_1");
                        address2 = reffered_phone_details.getString("address_line_2");
                        city = reffered_phone_details.getString("city");
                        company_photo = reffered_phone_details.getString("company_photo");
                        email = reffered_phone_details.getString("email");

                        Log.e("json reffered_phone_ 2=", "" + jsonObject1);
                    } else {
                        phone = c2.getString("reffered_phone_details");
                        referedFname = c2.getString("reffered_ticket_details");
                        Log.e("json reffered_ticket 3=", "" + referedFname);
                        Log.e("json reffered_phone_ 3=", "" + phone);
                    }


                    JSONObject connecter_details = c2.getJSONObject("connecter_details");
//                    Log.w("connecter_details", "" + connecter_details);

                    String fname = connecter_details.getString("firstname");
                    String lastname = connecter_details.getString("lastname");
                    String photo = connecter_details.getString("photo");

                    ReferalUsers referalUsers = new ReferalUsers();


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
//                    Log.e(" description :", "" + description);
                    referalUsers.setDesc(description);
                    referalUsers.setItemName(itemName);
                    referalUsers.setItem_photo(item_photo);

                    // Connector details
                    referalUsers.setFname(fname);
                    referalUsers.setLname(lastname);
                    referalUsers.setPhoto(photo);

                    // Referral
                    referalUsers.setReferredfName(referedFname);
                    referalUsers.setReferredlName(referedLname);
                    referalUsers.setReferedPhoto(refphoto);
                    referalUsers.setPhone(phone);
                    referalUsers.setEmail(email);
                    referalUsers.setCompany(company);
                    referalUsers.setPin_code(pin_code);
                    referalUsers.setIndustry(industry);
                    referalUsers.setAddress1(address1);
                    referalUsers.setAddress2(address2);
                    referalUsers.setCity(city);
                    referalUsers.setComany_photo(company_photo);

                    Log.e("referedFname 1=", "" + referedFname);
                    groupItem.add(referalUsers);
                    String json2 = new Gson().toJson(groupItem);// updated array
                    Log.e("groupItem array", "" + json2);

                }



            }
                for(ReferalUsers u:groupItem)
                {
                    Log.w("list ",""+u.getReferredfName());
                }




        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onRefresh() {
        // TODO Auto-generated method stub

        refreshContent();

    }
    private void refreshContent(){

        new Handler().postDelayed(new Runnable() {
            @Override public void run() {

                groupItem.clear();
                getReferalContents();
                CustomListAdapter listAdapter = new CustomListAdapter(getActivity(), groupItem, R.layout.referral);
                list.setAdapter(listAdapter);

                //do processing to get new data and set your listview's adapter, maybe  reinitialise the loaders you may be using or so
                //when your data has finished loading, set the refresh state of the view to false
                swipeRefreshLayout.setRefreshing(false);

            }
        }, 5000);

    }
    /**
     * A simple implementation of list adapter.
     */

    public class CustomListAdapter extends BaseAdapter implements View.OnClickListener {
        ReferalUsers cat;
        Context _c;
        public ArrayList<ReferalUsers> itemList;
        int textViewResourceId;

        public CustomListAdapter(Context context, ArrayList<ReferalUsers> group, int textViewResourceId1) {
            itemList = group;
            textViewResourceId = textViewResourceId1;
            _c = context;
        }

        @Override
        public int getCount() {
            return itemList.size();
        }

        @Override
        public Object getItem(int i) {
            return itemList.get(i);
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

            cat = itemList.get(position);

            name.setText(cat.getFname() + " " + cat.getLname());

            referredName.setText(cat.getReferredfName() + " " + cat.getReferredlName());
            Log.e("referred fname in item=",""+cat.getReferredfName());

            itemName.setText("for " + cat.getItemName());
            try {
                if (!cat.getPhoto().isEmpty()) {
//                    photo.setTag(cat.getPhoto());
//                    new DownloadImagesTask(_c).execute(photo);// Download item_photo from AsynTask
                    Picasso.with(_c).load(cat.getPhoto()).resize(250, 250).into(photo);


                } else {
                    photo.setImageResource(R.drawable.profile_pic_placeholder);
                }
                if (!cat.getReferedPhoto().isEmpty()) {
                    Picasso.with(_c).load(cat.getReferedPhoto()).resize(250, 250).into(referredPhoto);
//                    referredPhoto.setTag(cat.getReferedPhoto());
//                    new DownloadImagesTask(_c).execute(referredPhoto);// Download item_photo from AsynTask

                } else {
                    referredPhoto.setImageResource(R.drawable.profile_pic_placeholder);
                }

            } catch (ArrayIndexOutOfBoundsException ae) {
                ae.printStackTrace();

            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }

            // Resets the toolbar to be closed
            View toolbar = (View) v.findViewById(R.id.toolbar);
            TextView desc = (TextView) v.findViewById(R.id.textView1);
            ImageView item_photo = (ImageView) v.findViewById(R.id.item_photo);
            desc.setText(cat.getDesc());
            if (!cat.getItem_photo().isEmpty()) {
                Picasso.with(_c).load(cat.getItem_photo()).resize(250, 250).into(item_photo);
//                    referredPhoto.setTag(cat.getReferedPhoto());
//                    new DownloadImagesTask(_c).execute(referredPhoto);// Download item_photo from AsynTask

            } else {
                item_photo.setImageResource(R.drawable.no_pic_placeholder_with_border_800x800);
            }
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
                        .add(contentView.getId(), profilefragment)
                        .commit();

                break;

            case R.id.vzfrnds:
                Fragment newfragment = new VZFriends_Fragment();
                // get the id of fragment
                FrameLayout contentView2 = (FrameLayout) getActivity().findViewById(R.id.referral_frame);

                // Insert the fragment by replacing any existing fragment
                FragmentManager fragmentManager2 = getFragmentManager();
                fragmentManager2.beginTransaction()
                        .replace(contentView2.getId(), newfragment)
                        .commit();

                break;


            default:
                break;
        }


    }
}
