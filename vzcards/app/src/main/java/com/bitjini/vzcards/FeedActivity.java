package com.bitjini.vzcards;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Layout;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.ArrayList;

/**
 * Created by bitjini on 28/12/15.
 */
public class FeedActivity extends Fragment {

    String SYNC_CONTACT_URL="http://vzcards-api.herokuapp.com/sync/?access_token=";

    ArrayList<DataFeeds> feedsArrayList = new ArrayList<DataFeeds>();
    ArrayList<DataFeeds> feeds = new ArrayList<DataFeeds>();
    ListView listView;
    public FeedsAdapter adapter;
    ViewHolder holder;
FrameLayout layout_MainMenu;

    DataFeeds dataFeeds2 = new DataFeeds();
    DataFeeds dataFeeds1 = new DataFeeds();

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View feed = inflater.inflate(R.layout.feed_listview, container, false);
        layout_MainMenu = (FrameLayout) feed.findViewById(R.id.feed_detail);
        layout_MainMenu.getForeground().setAlpha( 0);

        // To avoid NetworkOnMainThreadException
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        listView = (ListView) feed.findViewById(R.id.feedList);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        // check if you are connected or not
        if (isConnected()) {
            Log.e("", "You are conncted");
        } else {
            Log.e("", "You are NOT conncted");
        }

        // get the access token from shared prefernces
        VerifyScreen p = new VerifyScreen();
        p.sharedPreferences = getActivity().getSharedPreferences(p.VZCARD_PREFS, 0);
        String token_sharedPreference = p.sharedPreferences.getString(p.TOKEN_KEY, null);
        System.out.println(" getting token from sharedpreference " + token_sharedPreference);

        new SyncContacts(getActivity()).execute(SYNC_CONTACT_URL+token_sharedPreference);
        // call AsynTask to perform network operation on separate thread
        new HttpAsyncTask().execute("http://vzcards-api.herokuapp.com/get_list/?access_token=" + token_sharedPreference);

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
                String connector_vz_id=dataFeeds.getVz_id();
                String question=dataFeeds.getQuestion();


                if (Integer.parseInt(dataFeeds.getQuestion()) == 0) {
                    //Put the value for has to feed detail has
                    Feed_detail_has ldf = new Feed_detail_has();

                    Bundle args = new Bundle();
                    args.putString("title", title);
                    args.putString("desc", desc);
                    args.putString("name", name);
                    args.putString("photo", photo);
                    args.putString("ticket_id", ticket_id);
                    args.putString("item_photo", item_photo);
                    args.putString("phone1", phone1);
                    args.putString("connector_vz_id", connector_vz_id);
                    args.putString("question",question);

                    ldf.setArguments(args);

                    //Inflate the fragment
                    getFragmentManager().beginTransaction().add(R.id.feed_detail, ldf).addToBackStack(ldf.toString())
                            .commit();
                }
                if (Integer.parseInt(dataFeeds.getQuestion()) == 1) {
                    //Put the value needs to feed_details
                   Feed_detail_needs ldf = new Feed_detail_needs();

                    Bundle args = new Bundle();

                    args.putString("title", title);
                    args.putString("desc", desc);
                    args.putString("name", name);
                    args.putString("photo", photo);
                    args.putString("ticket_id", ticket_id);
                    args.putString("item_photo", item_photo);
                    args.putString("phone1", phone1);
                    args.putString("connector_vz_id", connector_vz_id);
                    args.putString("question",question);
                    ldf.setArguments(args);

                    //Inflate the fragment
                    getFragmentManager().beginTransaction().add(R.id.feed_detail, ldf).addToBackStack(ldf.toString())
                            .commit();
                }
            }
        });


        return feed;
    }

    public boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to download the requested page.";
            }
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getActivity(), "Received!", Toast.LENGTH_LONG).show();
            Log.e("response of feeds...", "" + result);
            try {

                JSONObject jsonObj = new JSONObject(result.toString());

                // Getting JSON Array node
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
                    String vz_id=feed.getString("vz_id");

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

                }
                adapter = new FeedsAdapter(getActivity(), R.layout.feed_layout, feedsArrayList);
                listView.setAdapter(adapter);


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private String downloadUrl(String urlString) throws IOException {
            InputStream is = null;
            try {
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                // Starts the query
                conn.connect();
                int responseCode = conn.getResponseCode();
                is = conn.getInputStream();
                String contentAsString = convertStreamToString(is);
                return contentAsString;
            } finally {
                if (is != null) {
                    is.close();
                }
            }
        }

        private String convertStreamToString(InputStream is) throws UnsupportedEncodingException {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            try {
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return sb.toString();
        }
    }

    static class ViewHolder {
        public TextView name, question, item;
        public View viewLine;
        public ImageView item_photo, photo;
        public RadioButton referButtonRed, referButtonGreen;
        public RadioGroup radioGroup;
    }

    public class FeedsAdapter extends ArrayAdapter<DataFeeds> {

        Context context;
        private RadioButton mSelectedRB, mSelectedRB2;
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
            holder.photo = (ImageView) v.findViewById(R.id.profilePic);
            holder.viewLine = (View) v.findViewById(R.id.viewLine);

            holder.referButtonRed = (RadioButton) v.findViewById(R.id.referButton);
            holder.referButtonGreen = (RadioButton) v.findViewById(R.id.referButton);
            holder.radioGroup = (RadioGroup) v.findViewById(R.id.radioGroup1);


            holder.name.setText(String.valueOf(data.getFname()));
            holder.item.setText(String.valueOf(data.getItem()));

            holder.item_photo.setTag(String.valueOf(data.getItem_photo()));
            new DownloadImagesTask(getActivity()).execute(holder.item_photo);

            holder.photo.setTag(String.valueOf(data.getPhoto()));
            new DownloadImagesTask(getActivity()).execute(holder.photo);



            if (Integer.parseInt(data.getQuestion()) == 1) {
                holder.question.setBackgroundColor(Color.parseColor("#f27166"));
                holder.question.setText("needs");
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
                        mSelectedRB = (RadioButton) v;
                        notifyDataSetChanged();
                    }
                });


                if (mSelectedPosition != position) {
                    holder.referButtonRed.setChecked(false);

                } else {

                    holder.referButtonRed.setChecked(true);
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
                holder.question.setBackgroundColor(Color.parseColor("#add58a"));
                holder.question.setText("has");
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


            name.setText(dataFeeds1.getFname());
            item.setText(dataFeeds1.getItem());

            item_photo.setTag(dataFeeds1.getItem_photo());
            new DownloadImagesTask(getActivity()).execute(item_photo); // Download item_photo from AsynTask

            photo.setTag(dataFeeds1.getPhoto());
            new DownloadImagesTask(getActivity()).execute(photo);// Download photo from AsynTask

            // check if it is needs change the color to red
            if (Integer.parseInt(dataFeeds1.getIsNeeds()) == 1) {
                viewLine.setBackgroundColor(Color.parseColor("#f27166"));
                question.setText("needs");
                question.setBackgroundColor(Color.parseColor("#f27166"));
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

            item_photo2.setTag(dataFeeds2.getItem_photo());
            new DownloadImagesTask(getActivity()).execute(item_photo2);// Download item_photo from AsynTask

            photo2.setTag(dataFeeds2.getPhoto());
            new DownloadImagesTask(getActivity()).execute(photo2);// Download photo from AsynTask

            // check if it is has change the color to green
            if (Integer.parseInt(dataFeeds2.getIsHas()) == 0) {
                viewLine2.setBackgroundColor(Color.parseColor("#add58a"));
                question2.setText("has");
                question2.setBackgroundColor(Color.parseColor("#add58a"));
            }


            btnOkPopup=(Button) layout.findViewById(R.id.btn_Ok_popup);
            btnClosePopup = (Button) layout.findViewById(R.id.btn_close_popup);
            // set button close listener
            btnClosePopup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pwindo.dismiss();
                    layout_MainMenu.getForeground().setAlpha( 0); // restore

                }
            });
            // set button connect listener
            btnOkPopup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                      // get the data from objects
                    String ticket_id_1= dataFeeds1.getTicket_id();
                    String ticket_id_2=dataFeeds2.getTicket_id();
                    String phone1=dataFeeds1.getPhone();
                    String phone2=dataFeeds2.getPhone();
                    String connector_vz_id=dataFeeds1.getVz_id();

                    // send data to Connect_2_Tickets
                    Connect_2_Tickets connect = new Connect_2_Tickets();

                    Bundle args = new Bundle();
                    args.putString("ticket_id_1", ticket_id_1);
                    args.putString("ticket_id_2", ticket_id_2);
                    args.putString("phone1", phone1);
                    args.putString("phone2", phone2);
                    args.putString("connector_vz_id", connector_vz_id);

                    connect.setArguments(args);

                    //Inflate the fragment
                    getFragmentManager().beginTransaction().add(R.id.feed_detail, connect).addToBackStack(connect.toString())
                            .commit();
                    pwindo.dismiss();
                    layout_MainMenu.getForeground().setAlpha( 0); // restore

                }
            });


        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}


