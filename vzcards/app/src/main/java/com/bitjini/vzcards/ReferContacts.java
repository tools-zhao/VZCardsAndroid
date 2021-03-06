package com.bitjini.vzcards;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

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

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
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

import static com.bitjini.vzcards.BaseURLs.SYNC_CONTACT_URL;
import static com.bitjini.vzcards.BaseURLs.URL_CONNECT;
import static com.bitjini.vzcards.Constants.PERMISSIONS_REQUEST_READ_CONTACTS;
import static com.bitjini.vzcards.Constants.token_sharedPreference;

/**
 * Created by bitjini on 16/2/16.
 */
public class ReferContacts extends Fragment implements SearchView.OnQueryTextListener {

    // ArrayList
    ArrayList<SelectUser> phoneList = new ArrayList<>();

    // Contact List
    ListView listView;

    Context context;
    // Cursor to load contacts list
    Cursor email;
    SearchView mSearchView;
    String connecter_vz_id="", phone_1="", ticket_id_1="", phone_2="", ticket_id_2="", my_ticket="", reffered_ticket="", reffered_phone="";
    private ProgressDialog progress;
    Filter filter;
    // Pop up
    ContentResolver resolver;
    SelectUserAdapter adapter;
    public ProgressDialog progress1;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View refer_contact = inflater.inflate(R.layout.vzfriends_list, container, false);


        phoneList = new ArrayList<SelectUser>();
        resolver = getActivity().getContentResolver();
        listView = (ListView) refer_contact.findViewById(R.id.contactList);
        mSearchView = (SearchView) refer_contact.findViewById(R.id.searchview);

        showContacts();

        setupSearchView();

//                // Select item on listclick
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                SelectUser data = (SelectUser) parent.getItemAtPosition(position);

                // get the name and phone number from phone book on item click
                 ticket_id_2 = data.getName();
                 phone_2 = data.getPhone().replaceAll("\\D+", "");

                 createDialog();

            }
        });

        listView.setFastScrollEnabled(true);
        // on configuration changes (screen rotation) we want fragment member variables to preserved
        setRetainInstance(true);

return refer_contact;

    }

    private void createDialog() {
        // create an alert dialog box
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setMessage("Do you want to refer " + ticket_id_2);
        alertDialogBuilder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {

//                                String ticket_id_1, phone1, connector_vz_id;
                        // retrive the data sent by feed detail
                        ticket_id_1 = getArguments().getString("ticket_id");
                        phone_1 = getArguments().getString("phone1");
                        connecter_vz_id = getArguments().getString("connector_vz_id");
                        Log.e("ticket_id_2",""+ticket_id_2);
                        Log.e("phone_2",""+phone_2);
                        Log.e("phone_1",""+phone_1);
//                                Intent intent=new Intent(getActivity(),Connect_2_Tickets.class);
//                                intent.putExtra("ticket_id_1", ticket_id_1);
//                                intent.putExtra("phone1", phone1);
//                                intent.putExtra("connector_vz_id", connector_vz_id);
//                                intent.putExtra("phone2", phone2);
//                                intent.putExtra("ticket_id_2", name);
//                                startActivity(intent);
                        VerifyScreen p=new VerifyScreen();
                        HttpPostClass connect = new HttpPostClass();
                        connect.execute(URL_CONNECT + token_sharedPreference);


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

    private void showContacts() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getActivity().checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            getActivity().requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            if (getActivity() != null)
            {
                SyncContacts loadContacts = new SyncContacts(getActivity());
                if(!GetSharedPreference.isOrganisation()) {

                    adapter = new SelectUserAdapter(loadContacts.phoneList12, getActivity());
                    listView.setAdapter(adapter);
                    listView.setTextFilterEnabled(true);
                    // place your adapter to a separate filter to remove pop up text
                    filter = adapter.getFilter();

                }else {
                    new SyncContacts.LoadContact(){
                        @Override
                        protected void onPostExecute(ArrayList<SelectUser> result) {
                            super.onPostExecute(result);

                            adapter = new SelectUserAdapter(result, getActivity());
                            listView.setAdapter(adapter);
                            listView.setTextFilterEnabled(true);
                            // place your adapter to a separate filter to remove pop up text
                            filter = adapter.getFilter();

                        }
                    }.execute();

                }



            }
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


