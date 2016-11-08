package com.bitjini.vzcards;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Layout;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by bitjini on 28/12/15.
 */
public class FeedActivity extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    String SYNC_CONTACT_URL="https://vzcards-api.herokuapp.com/sync/?access_token=jUUMHSnuGys5nr6qr8XsNEx6rbUyNu";
    String URL_CONNECT = "https://vzcards-api.herokuapp.com/connect/?access_token=";

    VerifyScreen p=new VerifyScreen();
    String URL_GETLIST="https://vzcards-api.herokuapp.com/get_list/?access_token=";
    ProgressBar progressBar,progressBar2;
    private SwipeRefreshLayout swipeRefreshLayout;
    public Cursor phones;
    ArrayList<DataFeeds> feedsArrayList = new ArrayList<DataFeeds>();
    ArrayList<DataFeeds> feeds = new ArrayList<DataFeeds>();
    String token_sharedPreference,vz_id;
    // Request code for READ_CONTACTS. It can be any number > 0.
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    String connecter_vz_id, phone_1, ticket_id_1, phone_2, ticket_id_2, my_ticket, reffered_ticket, reffered_phone;
    private ProgressDialog progress;
    ListView listView;
    public FeedsAdapter adapter;
    ViewHolder holder;
FrameLayout layout_MainMenu;

    TextView emptyMsg;
    int countOfFeeds=0;
    int currentPage=1;
    int totalPage=0;
    int progressCount=0;
    boolean isLoading=false;
    int itemCount=0;
    int next;
    View footer;
    DataFeeds dataFeeds2 = new DataFeeds();
    DataFeeds dataFeeds1 = new DataFeeds();
    ImageView progressContainer;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View feed = inflater.inflate(R.layout.feed_listview, container, false);


        progressBar = (ProgressBar)feed.findViewById(R.id.progress1);
        progressContainer = (ImageView) feed.findViewById(R.id.progress);

        emptyMsg=(TextView) feed.findViewById(R.id.emptyFeeds);
        swipeRefreshLayout = (SwipeRefreshLayout) feed.findViewById(R.id.pullToRefresh);
        swipeRefreshLayout.setOnRefreshListener(this);

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark,R.color.pink,
                R.color.colorPrimary
                ,R.color.red);

        layout_MainMenu = (FrameLayout) feed.findViewById(R.id.feed_detail);
        layout_MainMenu.getForeground().setAlpha( 0);

//        // To avoid NetworkOnMainThreadException
//        if (android.os.Build.VERSION.SDK_INT > 9) {
//            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//            StrictMode.setThreadPolicy(policy);
//        }

        listView = (ListView) feed.findViewById(R.id.feedList);

        LayoutInflater inflater2 = (LayoutInflater) super.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        footer = (View) inflater2.inflate(R.layout.loading_layout, null);


        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        // get the access token from shared prefernces
        VerifyScreen p = new VerifyScreen();
        p.sharedPreferences = getActivity().getSharedPreferences(p.VZCARD_PREFS, 0);
        token_sharedPreference = p.sharedPreferences.getString(p.TOKEN_KEY, null);
        vz_id=p.sharedPreferences.getString(p.VZ_ID_KEY,null);
//        System.out.println(" getting token from sharedpreference " + token_sharedPreference);


        // check if you are connected or not
        if (isConnected()) {
            Log.e("", "You are conncted");
        } else {
            Log.e("", "You are NOT conncted");
            Toast.makeText(getActivity(),"Check your Network Connectivity",Toast.LENGTH_LONG).show();
        }
        showContacts();


            // refresh contents
            getFeedsContents(URL_GETLIST + token_sharedPreference);

        if(getActivity()!=null) {
            adapter = new FeedsAdapter(getActivity(), R.layout.feed_layout, feedsArrayList);
            listView.setAdapter(adapter);
        }



        // set on onList item click
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                DataFeeds dataFeeds = feeds.get(i);
                String title = dataFeeds.getItem();
                String desc = dataFeeds.getDescription();
                String name = dataFeeds.getFname();
                String photo = dataFeeds.getPhoto();
                String item_photo = dataFeeds.getItem_photo();
                String ticket_id= dataFeeds.getTicket_id();
                String phone1=dataFeeds.getPhone();
                String connector_vz_id=vz_id;
                String question=dataFeeds.getQuestion();


                if (Integer.parseInt(dataFeeds.getQuestion()) == 0) {
                    //Put the value for has to feed detail has
                    Intent intent=new Intent(getActivity(),Feed_detail_has.class);


                    // sending values of has
                    intent.putExtra("titleHas", title);
                    intent.putExtra("descHas", desc);
                    intent.putExtra("nameHas", name);
                    intent.putExtra("photoHas", photo);
                    intent.putExtra("ticket_idHas", ticket_id);
                    intent.putExtra("item_photoHas", item_photo);
                    intent.putExtra("phone1Has", phone1);
                    intent.putExtra("connector_vz_idHas", vz_id);
                    intent.putExtra("questionHas",question);

                    // sending values of needs
                    intent.putExtra("titleNeeds", title);
                    intent.putExtra("descNeeds", desc);
                    intent.putExtra("nameNeeds", name);
                    intent.putExtra("photoNeeds", photo);
                    intent.putExtra("ticket_idNeeds", ticket_id);
                    intent.putExtra("item_photoNeeds", item_photo);
                    intent.putExtra("phone1Needs", phone1);
                    intent.putExtra("connector_vz_idNeeds", vz_id);
                    intent.putExtra("questionNeeds",question);
                    startActivity(intent);

//                    //Inflate the fragment
//                    getFragmentManager().beginTransaction().add(R.id.feed_detail, ldf).addToBackStack(ldf.toString())
//                            .commit();
                }
                if (Integer.parseInt(dataFeeds.getQuestion()) == 1) {
                    //Put the value needs to feed_details
                    Intent intent=new Intent(getActivity(),Feed_detail_needs.class);



                    intent.putExtra("titleNeeds", title);
                    intent.putExtra("descNeeds", desc);
                    intent.putExtra("nameNeeds", name);
                    intent.putExtra("photoNeeds", photo);
                    intent.putExtra("ticket_idNeeds", ticket_id);
                    intent.putExtra("item_photoNeeds", item_photo);
                    intent.putExtra("phone1Needs", phone1);
                    intent.putExtra("connector_vz_idNeeds", vz_id);
                    intent.putExtra("questionNeeds",question);

//                    Bundle args = new Bundle();
                    intent.putExtra("titleHas", title);
                    intent.putExtra("descHas", desc);
                    intent.putExtra("nameHas", name);
                    intent.putExtra("photoHas", photo);
                    intent.putExtra("ticket_idHas", ticket_id);
                    intent.putExtra("item_photoHas", item_photo);
                    intent.putExtra("phone1Has", phone1);
                    intent.putExtra("connector_vz_idHas", vz_id);
                    intent.putExtra("questionHas",question);

                    startActivity(intent);

//                    //Inflate the fragment
//                    getFragmentManager().beginTransaction().add(R.id.feed_detail, ldf).addToBackStack(ldf.toString())
//                            .commit();
                }
            }
        });
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                boolean enable = false;
                if(listView != null && listView.getChildCount() > 0){
                    // check if the first item of the list is visible
                    boolean firstItemVisible = listView.getFirstVisiblePosition() == 0;
                    // check if the top of the first item is visible
                    boolean topOfFirstItemVisible = listView.getChildAt(0).getTop() == 0;
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

        progressContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                listView.setVisibility(View.VISIBLE);

//                swipeRefreshLayout.setRefreshing(true);
                showContacts();

                refreshContent();
            }
        });
        // on configuration changes (screen rotation) we want fragment member variables to preserved
        setRetainInstance(true);

        return feed;
    }

    public void getFeedsContents(String url) {

    try {


                progressCount = 1;

                String received =  new HttpAsyncTask(getActivity()).execute(url).get();

                int status=0;
                JSONObject jsonObj = new JSONObject(received);
                if (jsonObj.has("status")) {
                    status= jsonObj.getInt("status");
                }
                if(status==401)
                {
                                android.support.v7.app.AlertDialog.Builder alertDialogBuilder = new android.support.v7.app.AlertDialog.Builder(getActivity());
                                alertDialogBuilder.setTitle("Authentication Failed");
                                alertDialogBuilder.setMessage("Invalid Access Token Please Login Again");
                                alertDialogBuilder.setPositiveButton("Ok",
                                        new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface arg0, int arg1) {
                                                p.sharedPreferences = getActivity().getSharedPreferences(p.VZCARD_PREFS, 0);
                                                SharedPreferences.Editor sEdit = p.sharedPreferences.edit();
                                                sEdit.clear();
                                                sEdit.commit();
                                                Intent intent1 = new Intent(getActivity(), VerifyScreen.class);
                                                startActivity(intent1);
                                            }

                                        });

                                alertDialogBuilder.setNegativeButton("cancel",
                                        new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface arg0, int arg1) {

                                            }
                                        });
                                android.support.v7.app.AlertDialog alertDialog = alertDialogBuilder.create();
                                alertDialog.show();

                            }


                            if (jsonObj.has("count")) {

                                // Getting JSON Array node
                                countOfFeeds = jsonObj.getInt("count");
                                if (countOfFeeds == 0) {

                                    emptyMsg.setVisibility(View.VISIBLE);
                                    emptyMsg.setText("Hey, there are no feeds for you.\nPlease invite friends & \"Add\" ticket");
                                    listView.setVisibility(View.GONE);

                                } else {
                                    emptyMsg.setVisibility(View.GONE);
                                    listView.setVisibility(View.VISIBLE);
                                    Log.e("countOfFeeds", "" + countOfFeeds);
                                    JSONArray arr = jsonObj.getJSONArray("response");

                                    // looping through All Contacts
                                    for (int i = 0; i < arr.length(); i++) {
                                        JSONObject c = arr.getJSONObject(i);
                                        // Feed node is JSON Object
                                        JSONObject feed = c.getJSONObject("feed");

                                        String item = feed.getString("item");
                                        String question = feed.getString("question");
                                        String item_photo = feed.getString("item_photo");
                                        String description = feed.getString("description");
                                        String ticket_id = feed.getString("ticket_id");
                                        String isNeeds = "1", isHas = "0";
//                    String vz_id = feed.getString("vz_id");


                                        if (question == isNeeds) {
                                            isNeeds = question;
                                        }
                                        if (question == isHas) {
                                            isHas = question;
                                        }
                                        // user_details node is JSON Object
                                        JSONObject user_detail = c.getJSONObject("user_details");

                                        String firstname = user_detail.getString("firstname");
                                        String photo = user_detail.getString("photo");

                                        String phone = user_detail.getString("phone");

                                        DataFeeds dataFeeds = new DataFeeds();

                                        dataFeeds.setFname(firstname);
                                        dataFeeds.setItem(item);
                                        dataFeeds.setQuestion(question);
                                        dataFeeds.setPhoto(photo);
                                        dataFeeds.setItem_photo(item_photo);
                                        dataFeeds.setDescription(description);
                                        dataFeeds.setIsHas(isHas);
                                        dataFeeds.setIsNeeds(isNeeds);
                                        dataFeeds.setTicket_id(ticket_id);
                                        dataFeeds.setVz_id(vz_id);
                                        dataFeeds.setPhone(phone);

                                        feedsArrayList.add(dataFeeds);
                                        swipeRefreshLayout.setRefreshing(false);
                                    }
                                }
                            }


                            } catch (JSONException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }

            }








            public boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    @Override
    public void onRefresh() {
        // TODO Auto-generated method stub

        refreshContent();

    }
    private void refreshContent(){

        new Handler().postDelayed(new Runnable() {
            @Override public void run() {

                feedsArrayList.clear();

                currentPage=1;
                totalPage=0;
                countOfFeeds=0;
                isLoading = false;
                getFeedsContents(URL_GETLIST + token_sharedPreference );
                listView.setVisibility(View.VISIBLE);
                if(getActivity()!=null) {
                    adapter = new FeedsAdapter(getActivity(), R.layout.feed_layout, feedsArrayList);
                    listView.setAdapter(adapter);
                }

                Log.e("feed size aft refresh",""+feedsArrayList.size());
                swipeRefreshLayout.setRefreshing(false);
            }
        }, 5000);


    }
    public void loadMore(){
       listView.addFooterView(footer);

        new Handler().postDelayed(new Runnable() {

            @Override public void run() {
//
                currentPage++;
                if (currentPage <= totalPage) {

                    Log.e("currentpage=", "" + currentPage);


                    getFeedsContents("https://vzcards-api.herokuapp.com/get_list/?access_token=" + token_sharedPreference + "&page=" + currentPage);

                  // Notify the ListView of data changed
                    adapter.notifyDataSetChanged();
                    Log.e("feed size load more", "" + feedsArrayList.size());

                    isLoading = false;
                    listView.removeFooterView(footer);

                    swipeRefreshLayout.setRefreshing(false);
                }
                else {
                    listView.removeFooterView(footer);
                }
            }
            }, 2000);

        }

    private void showContacts() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getActivity().checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            getActivity().requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            listView.setVisibility(View.GONE);
//
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(0);

                new Handler().postDelayed(new Runnable() {
                    @Override public void run() {

                        new SyncContacts(getActivity()).LoadContacts();
//                        new SyncContacts(getActivity()).execute(SYNC_CONTACT_URL);

                        progressBar.setVisibility(View.GONE);
                        listView.setVisibility(View.VISIBLE);
                    }
                }, 5000);
//

        }
    }
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                showContacts();
            } else {
                Toast.makeText(getActivity(), "Until you grant the permission, we canot display the names", Toast.LENGTH_SHORT).show();
            }
        }
    }


    static class ViewHolder {
        public TextView name, question, item;
        public View viewLine;
        public RoundedImageView photo;
        public ImageView item_photo;
        public RadioButton referButtonRed, referButtonGreen;
        public RadioGroup radioGroup;
    }

    public class FeedsAdapter extends ArrayAdapter<DataFeeds> {

        Context context;
        private RadioButton mSelectedRB=null, mSelectedRB2=null;
        private int mSelectedPosition = -1, mSelectedPosition2 = -1;
        boolean red = false, green = false;

        public FeedsAdapter(Context context, int textViewResourceId, ArrayList<DataFeeds> items) {
              super(context, textViewResourceId, items);
            this.context = context;
            FeedActivity.this.feeds = items;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View v = null;
            convertView = null;
            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.feed_layout, null);


            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final DataFeeds data = feeds.get(position);

            holder = new ViewHolder();
            holder.name = (TextView) v.findViewById(R.id.feedName);
            holder.question = (TextView) v.findViewById(R.id.selectionText);
            holder.item = (TextView) v.findViewById(R.id.feedProfile);
            holder.item_photo = (ImageView) v.findViewById(R.id.itemPhoto);
            holder.photo = (RoundedImageView) v.findViewById(R.id.profilePic);
            holder.viewLine = (View) v.findViewById(R.id.viewLine);

            holder.referButtonRed = (RadioButton) v.findViewById(R.id.referButton);
            holder.referButtonGreen = (RadioButton) v.findViewById(R.id.referButton);
            holder.radioGroup = (RadioGroup) v.findViewById(R.id.radioGroup1);



            holder.name.setText(String.valueOf(data.getFname()));
            holder.item.setText(String.valueOf(data.getItem()));

//            holder.item_photo.setTag(String.valueOf(data.getItem_photo()));
          if(!data.getItem_photo().isEmpty())
            {
                Picasso.with(context).load(data.getItem_photo()).centerCrop().resize(200,200).placeholder(R.drawable.no_pic_placeholder_with_border_800x800).into(holder.item_photo);
                //            new DownloadImagesTask(getActivity()).execute(holder.item_photo);
            } else
          {
              holder.item_photo.setImageResource(R.drawable.no_pic_placeholder_with_border_800x800);
            }

            if(!data.getPhoto().isEmpty())
            {

            Picasso.with(context).load(data.getPhoto()).centerCrop().resize(200,200).placeholder(R.drawable.profile_pic_placeholder).into(holder.photo);}
            else  {
                holder.photo.setImageResource(R.drawable.profile_pic_placeholder);
                //            new DownloadImagesTask(getActivity()).execute(holder.photo);

            }

            if (Integer.parseInt(data.getQuestion()) == 1) {
                holder.question.setBackgroundResource(R.drawable.addimage_red);
                holder.question.setText("Need");
                holder.viewLine.setBackgroundColor(Color.parseColor("#f27166"));
                holder.referButtonRed.setTag(position);
                holder.referButtonRed.setId(position);

                // button listener
                holder.referButtonRed.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        int position = (Integer) v.getTag();
                        // check the postion of red button on click and make the previous red button unchecked
                        if (position != mSelectedPosition && mSelectedRB != null) {
                            mSelectedRB.setChecked(false);

                        }

                        mSelectedPosition = position;
                        Log.e("mselected pos",""+mSelectedPosition);
                        mSelectedRB = (RadioButton) v;
                        notifyDataSetChanged();

                    }
                });


                if (mSelectedPosition != position) {
                    holder.referButtonRed.setChecked(false);

                } else {

                    holder.referButtonRed.setChecked(true);
                    holder.referButtonRed.setText("Selected");
                    // send data to popup

                    dataFeeds1.setFname(data.getFname());
                    dataFeeds1.setItem(data.getItem());
                    dataFeeds1.setItem_photo(data.getItem_photo());
                    dataFeeds1.setPhoto(data.getPhoto());
                    dataFeeds1.setIsNeeds(data.getIsNeeds());
                    dataFeeds1.setTicket_id(data.getTicket_id());
                    dataFeeds1.setVz_id(data.getVz_id());
                    dataFeeds1.setPhone(data.getPhone());
                    red = true;
                    if (mSelectedRB != null && holder.referButtonRed != mSelectedRB) {
                        mSelectedRB = holder.referButtonRed;
                    }
                }
            }

            if (Integer.parseInt(data.getQuestion()) == 0) {
                holder.question.setBackgroundResource(R.drawable.addimage);
                holder.question.setText("Has");
                holder.question.setTextSize(10);
                holder.viewLine.setBackgroundColor(Color.parseColor("#add58a"));
                holder.referButtonGreen.setTag(position);
                holder.referButtonGreen.setId(position);

                holder.referButtonGreen.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        int position = (Integer) v.getTag();

                        // check the postion of green button on click and make the previous green button unchecked
                        if (position != mSelectedPosition2 && mSelectedRB2 != null) {
                            mSelectedRB2.setChecked(false);
                        }

                        mSelectedPosition2 = position;
                        mSelectedRB2 = (RadioButton) v;
                        notifyDataSetChanged();

                    }
                });


                if (mSelectedPosition2 != position) {
                    holder.referButtonGreen.setChecked(false);

                } else {

                    holder.referButtonGreen.setChecked(true);
                    holder.referButtonGreen.setText("Selected");
                    // send data to popup after the button is checked

                    dataFeeds2.setFname(data.getFname());
                    dataFeeds2.setItem(data.getItem());
                    dataFeeds2.setItem_photo(data.getItem_photo());
                    dataFeeds2.setPhoto(data.getPhoto());
                    dataFeeds2.setIsHas(data.getIsHas());
                     dataFeeds2.setTicket_id(data.getTicket_id());
                    dataFeeds2.setVz_id(data.getVz_id());
                    dataFeeds2.setPhone(data.getPhone());

                    green = true;
                    if (mSelectedRB2 != null && holder.referButtonGreen != mSelectedRB2) {
                        mSelectedRB2 = holder.referButtonGreen;
                    }
                }
            }

            if (red == true && green == true) {
                initiatePopupWindow();

                red = false;
                green = false;


            }
            return v;
        }

    }



    private void initiatePopupWindow() {
        View v = null;
        Button btnClosePopup,btnOkPopup;
        final PopupWindow pwindo;
        layout_MainMenu.getForeground().setAlpha( 150); // dim


        try {
// We need to get the instance of the LayoutInflater
            LayoutInflater inflater = (LayoutInflater) getActivity()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View layout = inflater.inflate(R.layout.screen_popup, null);

            CheckDensity checkdensity=new CheckDensity(getActivity());
           int density= checkdensity.getDensity();
//            Toast.makeText(getActivity(),"density = "+density,Toast.LENGTH_SHORT).show();
            if(density==480)
            {
                pwindo = new PopupWindow(layout, 1080, 730, true);
            }else if(density==240)
            {
                pwindo = new PopupWindow(layout, 540, 400, true);

            }else {
                pwindo = new PopupWindow(layout, 700, 500, true);
            }
            pwindo.showAtLocation(layout, Gravity.CENTER, 0, 0);
            pwindo.getAnimationStyle();
            pwindo.setBackgroundDrawable(new ColorDrawable(Color.LTGRAY));

            // Object 1
            TextView name = (TextView) layout.findViewById(R.id.feedName);
            TextView question = (TextView) layout.findViewById(R.id.selectionText);
            TextView item = (TextView) layout.findViewById(R.id.feedProfile);
            ImageView item_photo = (ImageView) layout.findViewById(R.id.itemPhoto);
            ImageView photo = (ImageView) layout.findViewById(R.id.profilePic);
            View viewLine = (View) layout.findViewById(R.id.viewLine);


            name.setText(dataFeeds1.getFname());
            item.setText(dataFeeds1.getItem());

            if(!dataFeeds1.getItem_photo().isEmpty()) {
//            item_photo.setTag(dataFeeds1.getItem_photo());
                Picasso.with(getActivity()).load(dataFeeds1.getItem_photo()).centerCrop().resize(200,200).into(item_photo);
//            new DownloadImagesTask(getActivity()).execute(item_photo); // Download item_photo from AsynTask
            }else
            {
                item_photo.setImageResource(R.drawable.no_pic_placeholder_with_border_800x800);
            }
            if(!dataFeeds1.getPhoto().isEmpty()) {
//            photo.setTag(dataFeeds1.getPhoto());
                Picasso.with(getActivity()).load(dataFeeds1.getPhoto()).centerCrop().resize(150,150).into(photo);
//            new DownloadImagesTask(getActivity()).execute(photo);// Download photo from AsynTask
            }  else
                {
                    photo.setImageResource(R.drawable.profile_pic_placeholder);
                }

            // check if it is needs change the color to red
            if (Integer.parseInt(dataFeeds1.getIsNeeds()) == 1) {
                viewLine.setBackgroundColor(Color.parseColor("#f27166"));
                question.setText("Need");
                holder.question.setTextSize(12);
                question.setBackgroundResource(R.drawable.addimage_red);
            }

            // Object 2
            TextView name2 = (TextView) layout.findViewById(R.id.feedName2);
            TextView question2 = (TextView) layout.findViewById(R.id.selectionText2);
            TextView item2 = (TextView) layout.findViewById(R.id.feedProfile2);
            ImageView item_photo2 = (ImageView) layout.findViewById(R.id.itemPhoto2);
            ImageView photo2 = (ImageView) layout.findViewById(R.id.profilePic2);
            View viewLine2 = (View) layout.findViewById(R.id.viewLine2);

            name2.setText(dataFeeds2.getFname());
            item2.setText(dataFeeds2.getItem());

            if(!dataFeeds2.getItem_photo().isEmpty()){
//            item_photo2.setTag(dataFeeds2.getItem_photo());
            Picasso.with(getActivity()).load(dataFeeds2.getItem_photo()).centerCrop().resize(200,200).into(item_photo2);

//            new DownloadImagesTask(getActivity()).execute(item_photo2);// Download item_photo from AsynTask
            }else
            {
                item_photo2.setImageResource(R.drawable.no_pic_placeholder_with_border_800x800);
            }

                if(!dataFeeds2.getPhoto().isEmpty()) {
//                    photo2.setTag(dataFeeds2.getPhoto());
                    Picasso.with(getActivity()).load(dataFeeds2.getPhoto()).centerCrop().resize(150,150).into(photo2);

//            new DownloadImagesTask(getActivity()).execute(photo2);// Download photo from AsynTask
                }
               else
                {
                    photo2.setImageResource(R.drawable.profile_pic_placeholder);
                }
            // check if it is has change the color to green
            if (Integer.parseInt(dataFeeds2.getIsHas()) == 0) {
                viewLine2.setBackgroundColor(Color.parseColor("#add58a"));
                question2.setText("Has");
                holder.question.setTextSize(12);
                question2.setBackgroundResource(R.drawable.addimage);
            }


            btnOkPopup=(Button) layout.findViewById(R.id.btn_Ok_popup);
            btnClosePopup = (Button) layout.findViewById(R.id.btn_close_popup);
            // set button close listener
            btnClosePopup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pwindo.dismiss();

                    adapter = new FeedsAdapter(getActivity(), R.layout.feed_layout, feedsArrayList);
                    listView.setAdapter(adapter);

                    layout_MainMenu.getForeground().setAlpha( 0); // restore

                }
            });
            // set button connect listener
            btnOkPopup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                      // get the data from objects
                    ticket_id_1= dataFeeds1.getTicket_id();
                    ticket_id_2=dataFeeds2.getTicket_id();
                        phone_1=dataFeeds1.getPhone();
                     phone_2=dataFeeds2.getPhone();
                     connecter_vz_id=dataFeeds1.getVz_id();

                    // send data to Connect_2_Tickets
                    HttpPostClass connect = new HttpPostClass();
                    connect.execute(URL_CONNECT + token_sharedPreference);


//                    Intent intent=new Intent(getActivity(),Connect_2_Tickets.class);
//                    intent.putExtra("ticket_id_1", ticket_id_1);
//                    intent.putExtra("phone1", phone1);
//                    intent.putExtra("connector_vz_id", connector_vz_id);
//                    intent.putExtra("phone2", phone2);
//                    intent.putExtra("ticket_id_2", ticket_id_2);
//                    startActivity(intent);

                    pwindo.dismiss();

                    adapter = new FeedsAdapter(getActivity(), R.layout.feed_layout, feedsArrayList);
                    listView.setAdapter(adapter);

                    layout_MainMenu.getForeground().setAlpha( 0); // restore

                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    private class HttpPostClass extends AsyncTask<String, Void, String> {

        Context context;


        protected void onPreExecute() {
            progress = new ProgressDialog(getActivity());
            progress.setMessage("Connecting please wait....");
            progress.show();
        }

        @Override
        protected String doInBackground(String... urls) {
            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to download the requested page.";
            }
        }

        private String downloadUrl(String urlString) throws IOException {
            String response = null;
            try {
//                final TextView outputView = (TextView) findViewById(R.id.content);
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();


                HttpClient client = new DefaultHttpClient();

                HttpPost post = new HttpPost(urlString);

                List<NameValuePair> params1 = new ArrayList<NameValuePair>();
                params1.add(new BasicNameValuePair("connecter_vz_id", connecter_vz_id));
                params1.add(new BasicNameValuePair("phone_1", phone_1));
                params1.add(new BasicNameValuePair("ticket_id_1", ticket_id_1));
                params1.add(new BasicNameValuePair("phone_2", phone_2));
                params1.add(new BasicNameValuePair("ticket_id_2", ticket_id_2));
                params1.add(new BasicNameValuePair("my_ticket", my_ticket));
                params1.add(new BasicNameValuePair("reffered_ticket", reffered_ticket));
                params1.add(new BasicNameValuePair("reffered_phone", reffered_phone));


                UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params1, HTTP.UTF_8);
                post.setEntity(ent);
                HttpResponse responsePOST = client.execute(post);
                HttpEntity resEntity = responsePOST.getEntity();

                if (resEntity != null) {
                    response = EntityUtils.toString(resEntity);
                    Log.i("RESPONSE", response);

                }
                StringBuilder sb = new StringBuilder();
                try {
                    BufferedReader reader =
                            new BufferedReader(new InputStreamReader(resEntity.getContent()), 65728);
                    String line = null;

                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }


                System.out.println("finalResult " + sb.toString());
                return sb.toString();


            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String result) {
            progress.dismiss();
            Toast.makeText(getActivity(), "Connected tickets", Toast.LENGTH_LONG).show();

        }
    }


}


