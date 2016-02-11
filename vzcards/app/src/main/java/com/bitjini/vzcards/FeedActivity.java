package com.bitjini.vzcards;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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

    ArrayList<DataFeeds> feedsArrayList=new ArrayList<DataFeeds>();
    ArrayList<DataFeeds> feeds=new ArrayList<DataFeeds>();
    ListView listView;
    public FeedsAdapter adapter;
    ViewHolder holder;

    ArrayList<String > queArray0=new ArrayList<String>();
    ArrayList<String > queArray1=new ArrayList<String>();
    public View onCreateView(LayoutInflater inflater,ViewGroup container,
                             Bundle savedInstanceState)
    {
        View feed=inflater.inflate(R.layout.feed_listview,container,false);

        // To avoid NetworkOnMainThreadException
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        listView = (ListView) feed.findViewById(R.id.feedList);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

//      // check if you are connected or not
        if(isConnected()){
           Log.e("","You are conncted");
        }
        else{
           Log.e("","You are NOT conncted");
        }

        // get the access token from shared prefernces
        VerifyScreen p=new VerifyScreen();
        p.sharedPreferences = getActivity().getSharedPreferences(p.VZCARD_PREFS, 0);
        String token_sharedPreference=p.sharedPreferences.getString(p.TOKEN_KEY,null);
        System.out.println(" getting token from sharedpreference "+ token_sharedPreference);
        // call AsynTask to perform network operation on separate thread
        new HttpAsyncTask().execute("http://vzcards-api.herokuapp.com/get_list/?access_token="+token_sharedPreference);





        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                DataFeeds dataFeeds=feeds.get(i);
                String title=dataFeeds.getItem();
                String desc=dataFeeds.getDescription();
                String name=dataFeeds.getFname();
                  String photo=dataFeeds.getPhoto();
                String item_photo=dataFeeds.getItem_photo();


                if(Integer.parseInt(dataFeeds.getQuestion())==0){
                    //Put the value
                    Feed_detail_has ldf = new Feed_detail_has();
                    Bundle args = new Bundle();
                    args.putString("title", title);
                    args.putString("desc", desc);
                    args.putString("name", name);
                    args.putString("photo",photo);
                    args.putString("item_photo",item_photo);

                    ldf.setArguments(args);

//Inflate the fragment
                    getFragmentManager().beginTransaction().add(R.id.feed_detail_has, ldf).addToBackStack(ldf.toString())
                            .commit();
                }
                if(Integer.parseInt(dataFeeds.getQuestion())==1){
                    //Put the value
                    Feed_detail_has.Feed_detail_needs ldf = new Feed_detail_has.Feed_detail_needs();
                    Bundle args = new Bundle();
                    args.putString("title", title);
                    args.putString("desc", desc);
                    args.putString("name", name);
                    args.putString("photo",photo);
                    args.putString("item_photo",item_photo);
                    ldf.setArguments(args);

//Inflate the fragment
                    getFragmentManager().beginTransaction().add(R.id.feed_detail_has, ldf).addToBackStack(ldf.toString())
                            .commit();
                }
            }
        });




        return feed;
    }
    public boolean isConnected(){
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
            Log.e("response...", "" + result);
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
                    Log.d("item :", "" + item);
//                    Log.d("question :", "" + question);

                    if(Integer.parseInt(question)==1)
                    {
//                        Log.d("question with value 1:", "" + question);
                        queArray1.add(question);

                    }
                    if(Integer.parseInt(question)==0)
                    {
//                        Log.d("question with value 0 :", "" + question);
                        queArray0.add(question);
                    }
                    // user_details node is JSON Object
                    JSONObject user_detail = c.getJSONObject("user_details");

                    String firstname = user_detail.getString("firstname");
                    String photo = user_detail.getString("photo");

                    Log.d("firstname :", "" + firstname);
                    Log.d("photo :", "" + photo);

                    DataFeeds dataFeeds=new DataFeeds();
                    dataFeeds.setFname(firstname);
                    dataFeeds.setItem(item);
                    dataFeeds.setQuestion(question);
                     dataFeeds.setPhoto(photo);
                    dataFeeds.setItem_photo(item_photo);
                    dataFeeds.setDescription(description);
                    feedsArrayList.add(dataFeeds);

                }
                adapter=new FeedsAdapter(getActivity(),R.layout.feed_layout,feedsArrayList);
                listView.setAdapter(adapter);

                for (int i=0;i<queArray1.size();i++)
                {
                    Log.e("queArray1 ",""+queArray1.get(i));
                }
                for (int i=0;i<queArray0.size();i++)
                {
                    Log.e("queArray0 ",""+queArray0.get(i));
                }
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
        public TextView name,question,item;
        public  View viewLine;
        public ImageView item_photo,photo;
        public Button referButtonRed,referButtonGreen;
    }

 public class FeedsAdapter extends ArrayAdapter<DataFeeds> {

     Context context;
     private int EnabledButton;

     public FeedsAdapter(Context context, int textViewResourceId, ArrayList<DataFeeds> items) {
         super(context, textViewResourceId, items);
         this.context = context;
         FeedActivity.this.feeds = items;
     }

     @Override
     public View getView(int position, View convertView, ViewGroup parent) {
         View v = null;
         convertView = null;
         if (convertView == null) {
             LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
             v = vi.inflate(R.layout.feed_layout, null);


         }
      else {
         holder = (ViewHolder) convertView.getTag();
     }
         DataFeeds data = feeds.get(position);

             holder = new ViewHolder();
             holder.name = (TextView) v.findViewById(R.id.feedName);
             holder.question = (TextView) v.findViewById(R.id.selectionText);
             holder.item = (TextView) v.findViewById(R.id.feedProfile);
             holder.item_photo = (ImageView) v.findViewById(R.id.itemPhoto);
             holder.photo = (ImageView) v.findViewById(R.id.profilePic);
             holder.viewLine = (View) v.findViewById(R.id.viewLine);
             holder.referButtonRed = (Button) v.findViewById(R.id.referButton);
             holder.referButtonGreen = (Button) v.findViewById(R.id.referButton);

//         holder.referButtonRed.setSelected(!holder.referButtonRed.isSelected());


             holder.name.setText(String.valueOf(data.getFname()));
             holder.item.setText(String.valueOf(data.getItem()));
             holder.item_photo.setImageBitmap(getBitmapFromURL(String.valueOf(data.getItem_photo())));
             holder.photo.setImageBitmap(getBitmapFromURL(String.valueOf(data.getPhoto())));

             if (Integer.parseInt(data.getQuestion()) == 1) {
                 holder.question.setBackgroundColor(Color.RED);
                 holder.question.setText("needs");
                 holder.viewLine.setBackgroundColor(Color.RED);
//                 holder.referButtonRed.setId(position);
                 holder.referButtonRed.setTag(position);
                 holder.referButtonRed.setOnClickListener(myButtonRedListener);
              }
             if (Integer.parseInt(data.getQuestion()) == 0) {
                 holder.question.setBackgroundColor(Color.GREEN);
                 holder.question.setText("has");
                 holder.viewLine.setBackgroundColor(Color.GREEN);
                 holder.referButtonGreen.setTag(position);

                 holder.referButtonGreen.setOnClickListener(myButtonGreenListener);
              }
         if(holder.referButtonRed.isEnabled()==true && holder.referButtonGreen.isEnabled()==true);
         {
//             initiatePopupWindow();
         }
         return v;
     }
//     public void DeselectButtons() {
//         for(int i=0; i<feeds.size();i++){
//             if (EnabledButton!= i)
//                 getActivity().findViewById(i).setSelected(false);
//         }
//
//     }
private View.OnClickListener myButtonRedListener = new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        int position = (Integer) v.getTag();
        Toast.makeText(getActivity(), "you selected red at position" + position, Toast.LENGTH_LONG).show();
//                     EnabledButton=holder.referButton.getId();
//                     DeselectButtons();
//        holder.referButtonRed.setSelected(false);
//        initiatePopupWindow();
        isEnabled(position);
    }
};
     private View.OnClickListener myButtonGreenListener = new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             int position = (Integer) v.getTag();
             Toast.makeText(getActivity(), "you selected green at position" + position, Toast.LENGTH_LONG).show();
//                     EnabledButton=holder.referButton.getId();
//                     DeselectButtons();
//             holder.referButtonGreen.setSelected(false);
//             initiatePopupWindow();
             isEnabled(position);
         }
     };
     public boolean isEnabled(int position)
     {
         return true;
     }


 }
    public static Bitmap getBitmapFromURL(String src) {
        try {
            Log.e("src",src);
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            Log.e("Bitmap","returned");
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Exception",e.getMessage());
            return null;
        }
    }
    private PopupWindow pwindo;

    private void initiatePopupWindow() {
        View v = null;
        Button btnClosePopup;
        try {
// We need to get the instance of the LayoutInflater
            LayoutInflater inflater = (LayoutInflater) getActivity()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View layout = inflater.inflate(R.layout.screen_popup,null);

            pwindo = new PopupWindow(layout, 700, 370, true);

            pwindo.showAtLocation(layout, Gravity.CENTER, 0, 0);

            btnClosePopup = (Button) layout.findViewById(R.id.btn_close_popup);
            btnClosePopup.setOnClickListener(cancel_button_click_listener);

        } catch (Exception e) {
            e.printStackTrace();
        }
//        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
//        LayoutInflater inflater = (LayoutInflater) getActivity()
//                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        View convertView = (View) inflater.inflate(R.layout.feed_listview, null);
//        alertDialog.setView(convertView);
//        alertDialog.setTitle("List");
//        ListView lv = (ListView) convertView.findViewById(R.id.feedList);
//        adapter=new FeedsAdapter(getActivity(),R.layout.feed_layout,feedsArrayList);
//
//        lv.setAdapter(adapter);
//        alertDialog.show();
    }

    private View.OnClickListener cancel_button_click_listener = new View.OnClickListener() {
        public void onClick(View v) {
            pwindo.dismiss();

        }
    };





public class DataFeeds extends ArrayList<DataFeeds> {
    String fname,item,question,ticket_id,item_photo,photo,description;

    private DataFeeds() {
    }

    public String getItem_photo() {
        return item_photo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setItem_photo(String item_photo) {
        this.item_photo = item_photo;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getTicket_id() {
        return ticket_id;
    }

    public void setTicket_id(String ticket_id) {
        this.ticket_id = ticket_id;
    }
}
//    class ResultActivity extends Activity {
//        @Override
//        protected void onCreate(Bundle savedInstanceState) {
//            super.onCreate(savedInstanceState);
//            setContentView(R.layout.result);
//            ArrayList<DataFeeds> feedsArrayList2;
//            Bundle b = getIntent().getExtras();
//            String[] resultArr = b.getStringArray("selectedItems");
//            feedsArrayList2=b.getStringArrayList("selectedItems");
//            ListView lv = (ListView) findViewById(R.id.outputList);
//
//
//            adapter=new FeedsAdapter(getActivity(),R.layout.feed_layout,feedsArrayList2);
//            lv.setAdapter(adapter);
//        }
//    }
}
