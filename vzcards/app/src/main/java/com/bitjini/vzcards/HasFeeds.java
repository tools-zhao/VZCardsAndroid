package com.bitjini.vzcards;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.DecelerateInterpolator;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
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
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by bitjini on 9/5/16.
 */
public class HasFeeds extends Fragment implements SwipeRefreshLayout.OnRefreshListener{
    String URL_CONNECT = "http://staging-vzcards-api.herokuapp.com/connect/?access_token=";

    String URL_GETLIST="http://staging-vzcards-api.herokuapp.com/get_list/?access_token=";
    String token_sharedPreference;
    String vz_id;
    private SwipeRefreshLayout swipeRefreshLayout;
    ListView listView;
    View footer;
    FrameLayout layout_MainMenu;
    String connecter_vz_id, phone_1, ticket_id_1, phone_2, ticket_id_2, my_ticket, reffered_ticket, reffered_phone;
    private ProgressDialog progress;
    ArrayList<DataFeeds> feedsArrayList = new ArrayList<DataFeeds>();

    ProgressBar progressBar;
    DataFeeds dataFeeds_Has = new DataFeeds();
    DataFeeds dataFeeds_Needs = new DataFeeds();

    public HasFeedsAdapter adapter;
    ViewHolder holder;
    int countOfFeeds=0;
    int currentPage=1;
    int totalPage=0;
    int progressCount=0;
    boolean isLoading=false;
    int itemCount=0;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View needFeeds = inflater.inflate(R.layout.question_feed_listview, container, false);
        TextView textView=(TextView)needFeeds.findViewById(R.id.emptytext);
        textView.setText("");
        textView.setVisibility(View.GONE);
        progressBar = (ProgressBar) needFeeds.findViewById(R.id.progress1);
        swipeRefreshLayout = (SwipeRefreshLayout) needFeeds.findViewById(R.id.pullToRefresh);
        swipeRefreshLayout.setOnRefreshListener(this);

        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark,R.color.pink,
                R.color.colorPrimary
                ,R.color.red);

        listView = (ListView) needFeeds.findViewById(R.id.feedList);
        layout_MainMenu = (FrameLayout) needFeeds.findViewById(R.id.feed_detail);
        layout_MainMenu.getForeground().setAlpha( 0);

        LayoutInflater inflater2 = (LayoutInflater) super.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        footer = (View) inflater2.inflate(R.layout.loading_layout, null);

        getActivity();
        VerifyScreen p = new VerifyScreen();
        p.sharedPreferences = getActivity().getSharedPreferences(p.VZCARD_PREFS, 0);
        token_sharedPreference = p.sharedPreferences.getString(p.TOKEN_KEY, null);
        vz_id=p.sharedPreferences.getString(p.VZ_ID_KEY,null);

//            listView.setVisibility(View.GONE);
//            progressBar.setVisibility(View.VISIBLE);
//            progressBar.setProgress(0);
//            ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", 0, 500); // see this max value coming back here, we animale towards that value
//            animation.setDuration(1000); //in milliseconds
//            animation.setRepeatCount(3);
//            animation.setInterpolator(new DecelerateInterpolator());
//            animation.start();
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {

                getFeedsContents(URL_GETLIST + token_sharedPreference);
//                progressBar.setVisibility(View.GONE);
//                listView.setVisibility(View.VISIBLE);
//
//            }
//        }, 5000);
        if(getActivity()!=null) {
            adapter = new HasFeedsAdapter(getActivity(), R.layout.question_feeds, feedsArrayList);
            listView.setAdapter(adapter);
        }
        // on configuration changes (screen rotation) we want fragment member variables to preserved
        setRetainInstance(true);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                DataFeeds dataFeeds = feedsArrayList.get(i);
                String title = dataFeeds.getItem();
                String desc = dataFeeds.getDescription();
                String name = dataFeeds.getFname();
                String photo = dataFeeds.getPhoto();
                String item_photo = dataFeeds.getItem_photo();
                String ticket_id = dataFeeds.getTicket_id();
                String phone1 = dataFeeds.getPhone();
                String question = dataFeeds.getQuestion();

                dataFeeds_Has.setIsHas(question);
                dataFeeds_Has.setFname(name);
                dataFeeds_Has.setItem(title);
                dataFeeds_Has.setQuestion(question);
                dataFeeds_Has.setPhoto(photo);
                dataFeeds_Has.setItem_photo(item_photo);
                dataFeeds_Has.setDescription(desc);
                dataFeeds_Has.setTicket_id(ticket_id);
                dataFeeds_Has.setPhone(phone1);
                dataFeeds_Has.setVz_id(vz_id);

                String itemNeeds = getArguments().getString("titleNeeds");
                String descNeeds = getArguments().getString("descNeeds");
                String profileNameNeeds = getArguments().getString("nameNeeds");
                String photoNeeds = getArguments().getString("photoNeeds");
                String itemPicNeeds=getArguments().getString("item_photoNeeds");
                String ticket_id_1Needs = getArguments().getString("ticket_idNeeds");
                String phone1Needs = getArguments().getString("phone1Needs");
                String connector_vz_idNeeds = getArguments().getString("connector_vz_idNeeds");
                String questionNeeds = getArguments().getString("questionNeeds");


                dataFeeds_Needs.setIsNeeds(questionNeeds);
                dataFeeds_Needs.setFname(profileNameNeeds);
                dataFeeds_Needs.setItem(itemNeeds);
                dataFeeds_Needs.setQuestion(questionNeeds);
                dataFeeds_Needs.setPhoto(photoNeeds);
                dataFeeds_Needs.setItem_photo(itemPicNeeds);
                dataFeeds_Needs.setDescription(descNeeds);
                dataFeeds_Needs.setTicket_id(ticket_id_1Needs);

                dataFeeds_Needs.setPhone(phone1Needs);

                initiatePopupWindow();



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

                Log.e("visibleItemCount",""+visibleItemCount);
                Log.e("lastIndexInScreen",""+firstVisibleItem);
                Log.e("totalItemCount",""+totalItemCount);

                if (lastIndexInScreen>= totalItemCount && 	!isLoading) {


                    // It is time to load more items
                    isLoading = true;
                    totalPage=(int) Math.ceil((double)countOfFeeds / 10.0);
                    Log.e("totalPage ",""+totalPage);
                    loadMore();
                }

            }
        });

        return needFeeds;

    }
    public void getFeedsContents(String url) {
        try {


            String received =  new HttpAsyncTask(getActivity()).execute(url).get();


            JSONObject jsonObj = new JSONObject(received);


            // Getting JSON Array node
            int countOfFeeds=jsonObj.getInt("count");
            Log.e("countOfFeeds",""+countOfFeeds);
            JSONArray arr = jsonObj.getJSONArray("response");

            // looping through All Contacts
            for (int i = 0; i < arr.length(); i++) {
                JSONObject c = arr.getJSONObject(i);
                // Feed node is JSON Object
                JSONObject feed = c.getJSONObject("feed");


                String question = feed.getString("question");
                String  isHas = "0";

                if (question .equals(isHas)) {
                    isHas = question;
                    String item = feed.getString("item");
                    String item_photo = feed.getString("item_photo");
                    String description = feed.getString("description");
                    String ticket_id = feed.getString("ticket_id");

                    String vz_id=feed.getString("vz_id");

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
                    dataFeeds.setTicket_id(ticket_id);
                    dataFeeds.setVz_id(vz_id);
                    dataFeeds.setPhone(phone);

                    feedsArrayList.add(dataFeeds);

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
                if(getActivity()!=null) {
                getFeedsContents(URL_GETLIST + token_sharedPreference );

                    adapter = new HasFeedsAdapter(getActivity(), R.layout.question_feeds, feedsArrayList);
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


                    getFeedsContents("http://staging-vzcards-api.herokuapp.com/get_list/?access_token=" + token_sharedPreference + "&page=" + currentPage);

                    // Notify the ListView of data changed
                    adapter.notifyDataSetChanged();
                    Log.e("feed size load more", "" + feedsArrayList.size());

                    isLoading = false;
                    listView.removeFooterView(footer);


                }
                else {
                    listView.removeFooterView(footer);
                }
            }
        }, 2000);

    }


    static class ViewHolder {
        public TextView name, question, item;
        public View viewLine;
        public RoundedImageView photo;
        public ImageView item_photo;
    }

    public class HasFeedsAdapter extends ArrayAdapter<DataFeeds> {

        Context context;
        int textViewResourceId;
        public HasFeedsAdapter(Context context, int textViewResourceId, ArrayList<DataFeeds> items) {
            super(context, textViewResourceId, items);
            this.context = context;
            feedsArrayList = items;
            this.textViewResourceId=textViewResourceId;

        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View v = null;
            convertView = null;
            if (convertView == null) {
                LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.question_feeds, null);


            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final DataFeeds data = feedsArrayList.get(position);

            holder = new ViewHolder();
            holder.name = (TextView) v.findViewById(R.id.feedName);
            holder.question = (TextView) v.findViewById(R.id.selectionText);
            holder.item = (TextView) v.findViewById(R.id.feedProfile);
            holder.item_photo = (ImageView) v.findViewById(R.id.itemPhoto);
            holder.photo = (RoundedImageView) v.findViewById(R.id.profilePic);
            holder.viewLine = (View) v.findViewById(R.id.viewLine);


            if (Integer.parseInt(data.getIsHas()) == 0) {

                holder.name.setText(String.valueOf(data.getFname()));
                holder.item.setText(String.valueOf(data.getItem()));

//            holder.item_photo.setTag(String.valueOf(data.getItem_photo()));
                if (!data.getItem_photo().isEmpty()) {
                    Picasso.with(context).load(data.getItem_photo()).placeholder(R.drawable.no_pic_placeholder_with_border_800x800).into(holder.item_photo);
                    //            new DownloadImagesTask(getActivity()).execute(holder.item_photo);
                } else {
                    holder.item_photo.setImageResource(R.drawable.no_pic_placeholder_with_border_800x800);
                }

                if (!data.getPhoto().isEmpty()) {

                    Picasso.with(context).load(data.getPhoto()).placeholder(R.drawable.profile_pic_placeholder).into(holder.photo);
                } else {
                    holder.photo.setImageResource(R.drawable.profile_pic_placeholder);
                    //            new DownloadImagesTask(getActivity()).execute(holder.photo);
                }


                    holder.question.setBackgroundResource(R.drawable.addimage);
                    holder.question.setText("Has");
                holder.question.setTextSize(10);
                    holder.viewLine.setBackgroundColor(Color.parseColor("#add58a"));

                }


            return v;
        }
    }
    private void initiatePopupWindow() {
        final String ticket_id_1Has,phone1Has,  questionHas;
        View v = null;
        Button btnClosePopup,btnOkPopup;
        final PopupWindow pwindo;
        layout_MainMenu.getForeground().setAlpha( 150); // dim


        try {
// We need to get the instance of the LayoutInflater
            LayoutInflater inflater = (LayoutInflater) getActivity()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View layout = inflater.inflate(R.layout.screen_popup, null);

            pwindo = new PopupWindow(layout, 700, 500, true);

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


            name.setText(dataFeeds_Has.getFname());
            item.setText(dataFeeds_Has.getItem());

            if(!dataFeeds_Has.getItem_photo().isEmpty()) {
//            item_photo.setTag(dataFeeds1.getItem_photo());
                Picasso.with(getActivity()).load(dataFeeds_Has.getItem_photo()).into(item_photo);
//            new DownloadImagesTask(getActivity()).execute(item_photo); // Download item_photo from AsynTask
            }else
            {
                item_photo.setImageResource(R.drawable.no_pic_placeholder_with_border_800x800);
            }
            if(!dataFeeds_Has.getPhoto().isEmpty()) {
//            photo.setTag(dataFeeds1.getPhoto());
                Picasso.with(getActivity()).load(dataFeeds_Has.getPhoto()).into(photo);
//            new DownloadImagesTask(getActivity()).execute(photo);// Download photo from AsynTask
            }  else
            {
                photo.setImageResource(R.drawable.profile_pic_placeholder);
            }

            // check if it is needs change the color to red
            if (Integer.parseInt(dataFeeds_Has.getIsHas()) ==0) {
                viewLine.setBackgroundColor(Color.parseColor("#add58a"));
                question.setText("Has");
                question.setBackgroundResource(R.drawable.addimage);
            }

            // Object 2
            TextView name2 = (TextView) layout.findViewById(R.id.feedName2);
            TextView question2 = (TextView) layout.findViewById(R.id.selectionText2);
            TextView item2 = (TextView) layout.findViewById(R.id.feedProfile2);
            ImageView item_photo2 = (ImageView) layout.findViewById(R.id.itemPhoto2);
            ImageView photo2 = (ImageView) layout.findViewById(R.id.profilePic2);
            View viewLine2 = (View) layout.findViewById(R.id.viewLine2);


            Log.e("questionHas",""+dataFeeds_Needs.getIsNeeds());
            if (Integer.parseInt(dataFeeds_Needs.getIsNeeds()) == 1) {
                viewLine2.setBackgroundColor(Color.parseColor("#f27166"));
                question2.setText("Need");
                question2.setBackgroundResource(R.drawable.addimage_red);
            }
            name2.setText(dataFeeds_Needs.getFname());
            item2.setText(dataFeeds_Needs.getItem());

            if(!dataFeeds_Needs.getItem_photo().isEmpty()){
//            item_photo2.setTag(dataFeeds2.getItem_photo());
                Picasso.with(getActivity()).load(dataFeeds_Needs.getItem_photo()).into(item_photo2);

//            new DownloadImagesTask(getActivity()).execute(item_photo2);// Download item_photo from AsynTask
            }else
            {
                item_photo2.setImageResource(R.drawable.no_pic_placeholder_with_border_800x800);
            }

            if(!dataFeeds_Needs.getPhoto().isEmpty()) {
//                    photo2.setTag(dataFeeds2.getPhoto());
                Picasso.with(getActivity()).load(dataFeeds_Needs.getPhoto()).into(photo2);

//            new DownloadImagesTask(getActivity()).execute(photo2);// Download photo from AsynTask
            }
            else
            {
                photo2.setImageResource(R.drawable.profile_pic_placeholder);
            }
            // check if it is has change the color to green



            btnOkPopup=(Button) layout.findViewById(R.id.btn_Ok_popup);
            btnClosePopup = (Button) layout.findViewById(R.id.btn_close_popup);
            // set button close listener
            btnClosePopup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pwindo.dismiss();

//                    adapter = new NeedFeedsAdapter(getActivity(), R.layout.feed_layout, feedsArrayList);
//                    listView.setAdapter(adapter);

                    layout_MainMenu.getForeground().setAlpha( 0); // restore

                }
            });
            // set button connect listener
            btnOkPopup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // get the data from objects
                     ticket_id_1= dataFeeds_Has.getTicket_id();
                     ticket_id_2=dataFeeds_Needs.getTicket_id();
                     phone_1=dataFeeds_Has.getPhone();
                     phone_2=dataFeeds_Needs.getPhone();
                     connecter_vz_id=dataFeeds_Has.getVz_id();

                    Log.e("token=",""+token_sharedPreference);
                    HttpPostClass connect = new HttpPostClass();
                    connect.execute(URL_CONNECT + token_sharedPreference);


                    pwindo.dismiss();

//                    adapter = new NeedFeedsAdapter(getActivity(), R.layout.feed_layout, feedsArrayList);
//                    listView.setAdapter(adapter);

                    layout_MainMenu.getForeground().setAlpha( 0); // restore

                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public class HttpPostClass extends AsyncTask<String, Void, String> {

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


