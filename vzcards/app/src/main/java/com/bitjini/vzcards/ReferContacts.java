package com.bitjini.vzcards;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
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

import java.io.IOException;
import java.util.ArrayList;

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

    Filter filter;
    // Pop up
    ContentResolver resolver;
    SelectUserAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View refer_contact = inflater.inflate(R.layout.vzfriends_list, container, false);


        phoneList = new ArrayList<SelectUser>();
        resolver = getActivity().getContentResolver();
        listView = (ListView) refer_contact.findViewById(R.id.contactList);
        mSearchView = (SearchView) refer_contact.findViewById(R.id.searchview);

//        showContacts();
        SyncContacts sync=new SyncContacts(getActivity());

        adapter = new SelectUserAdapter(sync.phoneList12, getActivity());
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

                                Intent intent=new Intent(getActivity(),Connect_2_Tickets.class);
                                intent.putExtra("ticket_id_1", ticket_id_1);
                                intent.putExtra("phone1", phone1);
                                intent.putExtra("connector_vz_id", connector_vz_id);
                                intent.putExtra("phone2", phone2);
                                intent.putExtra("ticket_id_2", name);
                                startActivity(intent);


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
}


