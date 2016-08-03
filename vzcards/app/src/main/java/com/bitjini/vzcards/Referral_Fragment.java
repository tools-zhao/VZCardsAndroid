package com.bitjini.vzcards;

import android.Manifest;
import android.animation.LayoutTransition;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.LauncherActivity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.*;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by bitjini on 28/12/15.
 */
public class Referral_Fragment extends Fragment implements View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    String HISTORY_URL = "https://vzcards-api.herokuapp.com/history/?access_token=";
    VerifyScreen p = new VerifyScreen();
    private SwipeRefreshLayout swipeRefreshLayout;
    // Request code for READ_CONTACTS. It can be any number > 0.
    private static final int PERMISSIONS_REQUEST_CALL_CONTACTS = 100;

    MyProfile_Fragment pr = new MyProfile_Fragment();
   ArrayList<ReferalUsers> groupItem = new ArrayList<ReferalUsers>();
    Button vzfrnds, profilebtn, referralbtn;
    ListView list;
    ProgressBar progressBar;
    CustomListAdapter listAdapter;

    int countOfFeeds=0;
    int currentPage=1;
    int totalPage=0;
    int progressCount=0;
    boolean isLoading=false;
    View footer;
    View referral;
    int count=0;
    TextView emptyMsg;
//


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         referral = inflater.inflate(R.layout.list_referal_activity, container, false);
        emptyMsg=(TextView) referral.findViewById(R.id.emptyFeeds);

        setRetainInstance(true);
        RelativeLayout linearLayout = (RelativeLayout) referral.findViewById(R.id.parent);
        linearLayout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);

        progressBar = (ProgressBar) referral.findViewById(R.id.progress1);
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

        LayoutInflater inflater2 = (LayoutInflater) super.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        footer = (View) inflater2.inflate(R.layout.loading_layout, null);

        profilebtn = (Button) referral.findViewById(R.id.profilebtn);
        vzfrnds = (Button) referral.findViewById(R.id.vzfrnds);

        profilebtn.setOnClickListener(this);
        vzfrnds.setOnClickListener(this);

        // Populate our list with groups and it's children
        // Creating the list adapter and populating the list
 if(savedInstanceState==null) {
     list.setVisibility(View.GONE);
     progressBar.setVisibility(View.VISIBLE);
     progressBar.setProgress(0);
        ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", 0, 500); // see this max value coming back here, we animale towards that value
        animation.setDuration(2000); //in milliseconds
        animation.setRepeatCount(5);
        animation.setInterpolator(new DecelerateInterpolator());
        animation.start();

     getReferalContents(HISTORY_URL + p.token_sharedPreference);
                progressBar.clearAnimation();

                progressBar.setVisibility(View.GONE);
                list.setVisibility(View.VISIBLE);

     if (getActivity() != null) {
//            list.setVisibility(View.VISIBLE);
//            getReferalContents(HISTORY_URL + p.token_sharedPreference);
         listAdapter = new CustomListAdapter(getActivity(), groupItem, R.layout.referral);
         list.setAdapter(listAdapter);
     }
 }
        // on configuration changes (screen rotation) we want fragment member variables to preserved
        setRetainInstance(true);
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
                Log.i("Main",totalItemCount+"");


                int lastIndexInScreen = visibleItemCount + firstVisibleItem;

                if (lastIndexInScreen>= totalItemCount && 	!isLoading) {


                    // It is time to load more items
                    isLoading = true;
                    totalPage=(int) Math.ceil((double)countOfFeeds / 10.0);
                    Log.e("totalPage ",""+totalPage);

                    loadMore();

                }
            }
        });

        return referral;

    }
    public void loadMore(){

            list.addFooterView(footer);

            new Handler().postDelayed(new Runnable() {

                @Override public void run() {
//
        currentPage++;
        if(currentPage<=totalPage) {

            Log.e("currentpage=",""+currentPage);

            getReferalContents("https://vzcards-api.herokuapp.com/history/?access_token=" + p.token_sharedPreference +"&page="+currentPage);

//            // Notify the ListView of data changed
//
            listAdapter.notifyDataSetChanged();

            isLoading = false;
            list.removeFooterView(footer);


        }
        else {
            list.removeFooterView(footer);
        }
                }
            }, 2000);

        }
    public void getReferalContents(String url) {
        try{
            count=1;
        String received=new HttpAsyncTask(getActivity()).execute(url).get();

//                    Log.e("received History", "" + received);

                    JSONObject jsonObject = new JSONObject(received);
            countOfFeeds=jsonObject.getInt("count");

            if(countOfFeeds==0)
            {
                emptyMsg.setVisibility(View.VISIBLE);
                emptyMsg.setText("Hey, you do not have any Tickets referred yet.\nPlease \"Add\" tickets.");
                list.setVisibility(View.GONE);

            }else {
                emptyMsg.setVisibility(View.GONE);
                list.setVisibility(View.VISIBLE);

                String response = jsonObject.getString("response");
                // Getting JSON Array node
                JSONArray arr = jsonObject.getJSONArray("response");

                Log.e("referrals", "" + response);
                // looping through All Contacts
                for (int i = 0; i < arr.length(); i++) {
                    JSONObject c = arr.getJSONObject(i);
                    // Connection Node in an array
                    JSONArray arr2 = c.getJSONArray("connections");
//                        Log.e(" connections :", "" + arr2);

                    for (int i2 = 0; i2 < arr2.length(); i2++) {
                        JSONObject c2 = arr2.getJSONObject(i2);

                        String referedFname = "", referedLname = "", refphoto = "", phone = "", company = "", pin_code = "", industry = "",
                                address1 = "", address2 = "", city = "", company_photo = "", email = "", title = "";
                        String refQuestion = "", refDescription = "", refTicket_id = "", refItemName = "",
                                refDate_validity = "", refVz_id = "", refItem_photo = "", refDate_created = "";

                        String refPhoneDetails = c2.getString("reffered_phone_details");

                        // check if refPhone Details is a string or valid json object
                        JsonParser parser1 = new JsonParser();
                        JsonElement jsonObject1 = parser1.parse(refPhoneDetails);

                        if (jsonObject1.isJsonObject()) {
                            JSONObject reffered_phone_details = c2.getJSONObject("reffered_phone_details");

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
                            title = reffered_phone_details.getString("title");
                            company_photo = reffered_phone_details.getString("company_photo");
                            email = reffered_phone_details.getString("email");

//                                Log.e("json reffered_phone_ 2=", "" + jsonObject1);
                        } else {
                            phone = c2.getString("reffered_phone_details");
                            company = "";
                            pin_code = "";
                            industry = "";
                            address1 = "";
                            address2 = "";
                            city = "";
                            company_photo = "";
                            email = "";
                            title = "";

//                                Log.e("json reffered_ticket 3=", "" + referedFname);
//                                Log.e("json reffered_phone_ 3=", "" + phone);
                        }
                        String refTicketDetails = c2.getString("reffered_ticket_details");

                        // check if refPhone Details is a string or valid json object
                        JsonParser parser2 = new JsonParser();
                        JsonElement jsonObject2 = parser2.parse(refPhoneDetails);

                        if (jsonObject2.isJsonObject()) {
                            JSONObject reffered_TicketDetails = c2.getJSONObject("reffered_ticket_details");
                            refQuestion = reffered_TicketDetails.getString("question");
                            refDescription = reffered_TicketDetails.getString("description");
                            refTicket_id = reffered_TicketDetails.getString("ticket_id");
                            refItemName = reffered_TicketDetails.getString("item");
                            refDate_validity = reffered_TicketDetails.getString("date_validity");
                            refVz_id = reffered_TicketDetails.getString("vz_id");
                            refItem_photo = reffered_TicketDetails.getString("item_photo");
                            refDate_created = reffered_TicketDetails.getString("date_created");
                        } else {
                            referedFname = c2.getString("reffered_ticket_details");
                        }


                        JSONObject connecter_details = c2.getJSONObject("connecter_details");
//                    Log.w("connecter_details", "" + connecter_details);

                        String fname = connecter_details.getString("firstname");
                        String lastname = connecter_details.getString("lastname");
                        String photo = connecter_details.getString("photo");

                        ReferalUsers referalUsers = new ReferalUsers();


                        JSONObject ticket_details = c.getJSONObject("ticket_details");
//                            Log.e(" ticket_details :", "" + ticket_details);
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
                        referalUsers.setRefQuestion(question);
                        Log.e("ref question ", "" + question);
                        // Connector details
                        referalUsers.setFname(fname);
                        referalUsers.setLname(lastname);
                        referalUsers.setPhoto(photo);

                        // Referral details
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
                        referalUsers.setTitle(title);
                        referalUsers.setComany_photo(company_photo);
                        // Referred ticket details

                        referalUsers.setRefDesc(refDescription);
                        referalUsers.setRefItemName(refItemName);
                        referalUsers.setRefItem_photo(refItem_photo);

//                            Log.e("referedFname 1=", "" + referedFname);
                        groupItem.add(referalUsers);
                        String json2 = new Gson().toJson(groupItem);// updated array
//                            Log.e("groupItem array", "" + json2);

                    }


                }
            }
//                    for (ReferalUsers u : groupItem) {
//                        Log.w("list ", "" + u.getReferredfName());
//                    }


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
                currentPage=1;
                totalPage=0;
                countOfFeeds=0;
                isLoading = false;
                if(getActivity()!=null) {
                getReferalContents(HISTORY_URL + p.token_sharedPreference);

                    CustomListAdapter listAdapter = new CustomListAdapter(getActivity(), groupItem, R.layout.referral);
                    list.setAdapter(listAdapter);
                }

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
            TextView ref = (TextView) v.findViewById(R.id.ref);
            TextView referredName = (TextView) v.findViewById(R.id.referred);
            TextView itemName = (TextView) v.findViewById(R.id.itemName);
            ImageView referredPhoto = (ImageView) v.findViewById(R.id.referdPhoto);
            ImageView photo = (ImageView) v.findViewById(R.id.photo);
            View viewLine = (View) v.findViewById(R.id.viewLine);
            ReferalUsers cat = itemList.get(position);

            name.setText(cat.getFname() + " " + cat.getLname());

            referredName.setText(cat.getReferredfName() + " " + cat.getReferredlName());
//            Log.e("referred fname in item=",""+cat.getReferredfName());



            itemName.setText("For "+"\"" + cat.getItemName()+ "\"");
            try {
                if (!cat.getPhoto().isEmpty()) {
//                    photo.setTag(cat.getPhoto());
//                    new DownloadImagesTask(_c).execute(photo);// Download item_photo from AsynTask
                    Picasso.with(_c).load(cat.getPhoto()).resize(150, 150).into(photo);


                } else {
                    photo.setImageResource(R.drawable.profile_pic_placeholder);
                }
                if (!cat.getReferedPhoto().isEmpty()) {
                    Picasso.with(_c).load(cat.getReferedPhoto()).resize(150, 150).into(referredPhoto);
//                    referredPhoto.setTag(cat.getReferedPhoto());
//                    new DownloadImagesTask(_c).execute(referredPhoto);// Download item_photo from AsynTask

                } else {
                    referredPhoto.setImageResource(R.drawable.profile_pic_placeholder);
                }
                if (Integer.parseInt(cat.getRefQuestion()) == 1) {

                    viewLine.setBackgroundColor(Color.parseColor("#f27166"));
                    ref.setBackgroundResource(R.drawable.addimage_red);
                }
                if (Integer.parseInt(cat.getRefQuestion()) == 0) {

                    viewLine.setBackgroundColor(Color.parseColor("#add58a"));
                    ref.setBackgroundResource(R.drawable.addimage);
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
            desc.setText(cat.getRefItemName()+"\n"+cat.getRefDesc());
            if (!cat.getRefItem_photo().isEmpty()) {
                Picasso.with(_c).load(cat.getRefItem_photo()).resize(250, 250).placeholder(R.drawable.no_pic_placeholder_with_border_800x800).into(item_photo);
//                    referredPhoto.setTag(cat.getReferedPhoto());
//                    new DownloadImagesTask(_c).execute(referredPhoto);// Download item_photo from AsynTask

            } else {
                item_photo.setImageResource(R.drawable.no_pic_placeholder_with_border_800x800);
            }
            Button callBtn = (Button) v.findViewById(R.id.btnCall);
            Button vzCardBtn = (Button) v.findViewById(R.id.btnVzCard);
             vzCardBtn.setTag(position);
            callBtn.setOnClickListener(this);
            vzCardBtn.setOnClickListener(this);

            toolbar.setVisibility(View.GONE);
            return v;
        }

        public void onClick(View view) {
//            ReferalUsers cat=itemList.get(position);
            int position = 0;
            if(view.getTag()!=null)
            {
                position=(Integer)view.getTag();
            }
            ReferalUsers data=itemList.get(position);
            switch (view.getId()) {
                case R.id.btnCall:
                    try {
                       CallPhone(data.getPhone());
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(_c, "your Activity is not found", Toast.LENGTH_LONG).show();
                    }
                    break;

                case R.id.btnVzCard:

                    Intent nextScreenIntent = new Intent(_c, Friends_Profile.class);


                    nextScreenIntent.putExtra("fname", data.getReferredfName());
                    nextScreenIntent.putExtra("lname", data.getReferredlName());
                    nextScreenIntent.putExtra("photo", data.getReferedPhoto());
                    nextScreenIntent.putExtra("phone", data.getPhone());
                    nextScreenIntent.putExtra("company", data.getCompany());
                    nextScreenIntent.putExtra("pin_code", data.getPin_code());
                    nextScreenIntent.putExtra("industry", data.getIndustry());
                    nextScreenIntent.putExtra("address1", data.getAddress1());
                    nextScreenIntent.putExtra("address2", data.getAddress2());
                    nextScreenIntent.putExtra("city", data.getCity());
                    nextScreenIntent.putExtra("title", data.getTitle());
                    nextScreenIntent.putExtra("company_photo", data.getComany_photo());
                    startActivity(nextScreenIntent);

                    break;
                default:
                    break;
            }


        }

        @Override
        public boolean isEnabled(int position) {
            return true;
        }
        private void CallPhone(String phone) {
            // Check the SDK version and whether the permission is already granted or not.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getActivity().checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, PERMISSIONS_REQUEST_CALL_CONTACTS);
                //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
            } else {
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                Log.e("phone ",""+phone);
                callIntent.setData(Uri.parse("tel:" + "+"+phone));
                startActivity(callIntent);
            }
        }


    }




    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.profilebtn:
                Fragment profilefragment = new MyProfile_Fragment();
                // get the id of fragment
                FrameLayout contentView = (FrameLayout) referral.findViewById(R.id.referral_frame);

                // Insert the fragment by replacing any existing fragment
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(contentView.getId(), profilefragment)
                        .commit();

                break;

            case R.id.vzfrnds:
                Fragment newfragment = new VZFriends_Fragment();
                // get the id of fragment
                FrameLayout contentView2 = (FrameLayout) referral.findViewById(R.id.referral_frame);

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
//    public void onSaveInstanceState(Bundle outState)
//    {
//        super.onSaveInstanceState(outState);
//       String json2 = new Gson().toJson(groupItem);// updated array
//
//       outState.putString("MyString", json2);
//        Log.e(" saving",""+json2);
//        // etc.
//
//        // Save countries into bundle
//    }
////    @Override
////    public void onActivityCreated(Bundle savedInstanceState) {
////        super.onActivityCreated(savedInstanceState);
////
////        if (savedInstanceState != null) {
////            //Restore the fragment's state here
////            Gson gson = new Gson();
////            String json = savedInstanceState.getString("MyString");
//////        Log.e("Load json shared prefs ", "" + json);
////
////            Type type = new TypeToken<ArrayList<LauncherActivity.ListItem>>() {
////            }.getType();
////            groupItem = gson.fromJson(json, type);
////            listAdapter = new CustomListAdapter(getActivity(), groupItem, R.layout.referral);
////            list.setAdapter(listAdapter);
////        }
////    }

}
