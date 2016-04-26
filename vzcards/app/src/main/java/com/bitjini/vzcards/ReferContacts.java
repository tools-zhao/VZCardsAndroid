package com.bitjini.vzcards;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
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
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by bitjini on 16/2/16.
 */
public class ReferContacts extends Fragment implements SearchView.OnQueryTextListener {

    // ArrayList
    ArrayList<SelectUser> selectUsers = null;

    // Contact List
    ListView listView;
    // Request code for READ_CONTACTS. It can be any number > 0.
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    Context context=null;
    Activity _activity;
    // Cursor to load contacts list
    Cursor phones, email;
    SearchView mSearchView;

    Filter filter;
    // Pop up
    ContentResolver resolver;
    SelectUserAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View refer_contact = inflater.inflate(R.layout.vzfriends_list, container, false);


        selectUsers = new ArrayList<SelectUser>();
        resolver = getActivity().getContentResolver();
        listView = (ListView) refer_contact.findViewById(R.id.contactList);
        mSearchView = (SearchView) refer_contact.findViewById(R.id.searchview);

        showContacts();

//        initSearchView();



return refer_contact;

    }
    private void showContacts() {
        // Check the SDK version and whether the permission is already granted or not.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getActivity().checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            getActivity().requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
            //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
        } else {
            phones = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");

            LoadContact loadContact = new LoadContact();

            loadContact.execute();
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
    // Load data on background
    class LoadContact extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... voids) {
            // Get Contact list from Phone

            if (phones != null) {
                Log.e("count", "" + phones.getCount());
                if (phones.getCount() == 0) {
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        public void run() {
                            Toast t = Toast.makeText(getActivity(), "No contact lists", Toast.LENGTH_LONG);
                            t.show();
                        }
                    });
                }

                while (phones.moveToNext()) {
                    Bitmap bit_thumb = null;
                    String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    String image_thumb = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
                    try {
                        if (image_thumb != null) {
                            bit_thumb = MediaStore.Images.Media.getBitmap(resolver, Uri.parse(image_thumb));
                        } else {
                            Log.e("No Image Thumb", "--------------");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    SelectUser selectUser = new SelectUser();
                    selectUser.setThumb(bit_thumb);
                    selectUser.setName(name);
                    selectUser.setPhone(phoneNumber);
                    selectUsers.add(selectUser);

                }
            } else {
                Log.e("Cursor close 1", "----------------");
            }
            //phones.close();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            adapter = new SelectUserAdapter(selectUsers, getActivity());
            adapter.notifyDataSetChanged();
            listView.setAdapter(adapter);
            listView.setTextFilterEnabled(true);
            // place your adapter to a separate filter to remove pop up text
            filter = adapter.getFilter();
            setupSearchView();

//                // Select item on listclick
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                    SelectUser data = (SelectUser) parent.getItemAtPosition(position);

                    // get the name and phone number from phone book on item click
                    final String name = data.getName();
                    final String phone2 = data.getPhone().replaceAll("\\D+", "");

                    // create an alert dialog box
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                    alertDialogBuilder.setMessage("Do you want to refer " + name);
                    alertDialogBuilder.setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface arg0, int arg1) {

                                    String ticket_id_1, phone1, connector_vz_id;
                                    // retrive the data sent by feed detail
                                    ticket_id_1 = getArguments().getString("ticket_id");
                                    phone1 = getArguments().getString("phone1");
                                    connector_vz_id = getArguments().getString("connector_vz_id");

                                    Fragment connect = new Connect_2_Tickets();

                                    Bundle args = new Bundle();
                                    args.putString("ticket_id_1", ticket_id_1);
                                    args.putString("phone1", phone1);
                                    args.putString("connector_vz_id", connector_vz_id);
                                    args.putString("phone2", phone2);
                                    args.putString("ticket_id_2", name);


                                    connect.setArguments(args);

                                    //Inflate the fragment
                                    getFragmentManager().beginTransaction().add(R.id.vzfrnds_list_frame, connect).addToBackStack(connect.toString())
                                            .commit();
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
        if (TextUtils.isEmpty(newText)) {
            listView.clearTextFilter();
        } else {
            listView.setFilterText(newText);
        }
        return true;
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }
}


