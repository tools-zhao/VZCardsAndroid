package com.bitjini.vzcards;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by bitjini on 16/2/16.
 */
public class InviteContacts extends Fragment implements SearchView.OnQueryTextListener {
    String URL_INVITE_FRNDS = "https://vzcards-api.herokuapp.com/invite_friends/?access_token=";

    // ArrayList
    ArrayList<SelectUser> phoneList = new ArrayList<>();

    // Contact List
    ListView listView;

    Toolbar toolbar;
    Context context;
    // Cursor to load contacts list
    Cursor email;
    SearchView mSearchView;
    String myName="", myPhoneNumer, frndsPhoneNumber, frndsName="";
    private ProgressDialog progress;
    Filter filter;
    // Pop up
    ContentResolver resolver;
    SelectUserAdapter adapter;
    MyProfile_Fragment pr = new MyProfile_Fragment();
    VerifyScreen p = new VerifyScreen();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View refer_contact = inflater.inflate(R.layout.vzfriends_list, container, false);


        phoneList = new ArrayList<SelectUser>();
        resolver = getActivity().getContentResolver();
        listView = (ListView) refer_contact.findViewById(R.id.contactList);
        mSearchView = (SearchView) refer_contact.findViewById(R.id.searchview);
        toolbar=(Toolbar)refer_contact.findViewById(R.id.toolbar);

        toolbar.setVisibility(View.GONE);
//        showContacts();
        if (getActivity() != null) {
            SyncContacts sync = new SyncContacts(getActivity());

            adapter = new SelectUserAdapter(sync.phoneList12, getActivity());

            listView.setAdapter(adapter);
            listView.setTextFilterEnabled(true);
            // place your adapter to a separate filter to remove pop up text
            filter = adapter.getFilter();
        }
        setupSearchView();

//                // Select item on listclick
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                SelectUser data = (SelectUser) parent.getItemAtPosition(position);

                // get the name and phone number from phone book on item click
//                final String name = data.getName();
                frndsName = data.getName();
                frndsPhoneNumber = data.getPhone().replaceAll("\\D+", "");

                // create an alert dialog box
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setMessage("Do you want to invite " + frndsName);
                alertDialogBuilder.setPositiveButton("Ok",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {


//                                phoneNumber = getArguments().getString("phone1");

                                VerifyScreen p = new VerifyScreen();
                                GetHttpRequest connect = new GetHttpRequest(getActivity());
                                connect.execute(URL_INVITE_FRNDS + p.token_sharedPreference);


                            }
                        });

                alertDialogBuilder.setNegativeButton("cancel",
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {

                            }
                        });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();


            }
        });

        listView.setFastScrollEnabled(true);
        // on configuration changes (screen rotation) we want fragment member variables to preserved
        setRetainInstance(true);

        return refer_contact;

    }

    private void setupSearchView() {
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setSubmitButtonEnabled(true);
        mSearchView.setQueryHint("Search");
    }

    @Override
    public boolean onQueryTextChange(String newText) {
//        if (TextUtils.isEmpty(newText)) {
//            listView.clearTextFilter();
//        } else {
//            listView.setFilterText(newText);
//        }
        filter.filter(newText);
        return true;
//        return true;
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }


    private class GetHttpRequest extends AsyncTask<String, Void, String> {

        private final Context context;

        public GetHttpRequest(Context c) {
            this.context = c;
        }

        protected void onPreExecute() {
            progress = new ProgressDialog(this.context);
            if (progress != null) {
                progress.setMessage("Loading....");
                progress.setCancelable(false);
                progress.show();
            }
        }

        @Override
        protected String doInBackground(String... urls) {
            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadUrl(urls[0]);
            } catch (Exception e) {
                return "Unable to download the requested page."+e.getMessage();
            }
        }


        private String downloadUrl(String urlString) throws IOException {
            InputStream is = null;
            try {
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//
//                    Headers:
//                    PHONE : <friends no>
//                    SENDER : <vzcard users name or phones>
//                            RECEIVER : <frinds name or phone>
                pr.data = getActivity().getSharedPreferences(pr.MY_PROFILE_PREFERENCES, 0); // getting data from sharedprefernces
                p.sharedPreferences = context.getSharedPreferences(p.VZCARD_PREFS, 0);
                myPhoneNumer= p.sharedPreferences.getString(p.PHONE_KEY, null);  // getting phone of user

                String json = pr.data.getString(pr.TASKS, null);



                String firstname = null,lastname = null;  // getting name of user
                try {
                    JSONArray jsonArray = new JSONArray(json);
                    firstname = jsonArray.getJSONObject(0).getString("value");
                    lastname = jsonArray.getJSONObject(1).getString("value");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                myName=firstname+" "+lastname;

                Log.e("fph=",""+frndsPhoneNumber);
                Log.e("f name=",""+frndsName);

                if(frndsName.isEmpty())
                {
                    frndsName=frndsPhoneNumber;  // if frnds name is empty send phone no.
                }
                if(myName.isEmpty())
                {
                    myName=myPhoneNumer;     // if myname is empty send my phone no.
                }

                conn.setRequestProperty("PHONE", frndsPhoneNumber);
                conn.setRequestProperty("SENDER", myName);
                conn.setRequestProperty("RECEIVER", frndsName);
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                // Starts the query
                conn.connect();
                int responseCode = conn.getResponseCode();
                is = conn.getInputStream();
                String contentAsString = convertStreamToString(is);

                Log.e("res=",""+contentAsString);
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
            Log.e("get res=",""+sb.toString());
            return sb.toString();
        }


        protected void onPostExecute(String result) {
            if (progress.isShowing()) {
                progress.dismiss();
                progress = null;
            }
            Toast.makeText(getActivity(), "Invitation sent", Toast.LENGTH_LONG).show();
            Log.e("result=",""+result);
        }
    }
}


