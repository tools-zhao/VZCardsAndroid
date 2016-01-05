package com.bitjini.vzcards;

import android.app.Activity;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.bitjini.vzcards.SelectUser;
import com.bitjini.vzcards.SelectUserAdapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ContactsMainActivity extends Activity implements SearchView.OnQueryTextListener {

    // ArrayList
    ArrayList<SelectUser> selectUsers=null;

    // Contact List
    ListView listView;

    // Cursor to load contacts list
    Cursor phones, email;
    SearchView mSearchView;

    Filter filter;
    // Pop up
    ContentResolver resolver;
   SelectUserAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vzfriends_list);


        selectUsers = new ArrayList<SelectUser>();
        resolver = this.getContentResolver();
        listView = (ListView) findViewById(R.id.contactList);
        mSearchView = (SearchView) findViewById(R.id.searchview);


//        initSearchView();


        phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        LoadContact loadContact = new LoadContact();

        loadContact.execute();

        //getting screen size--(1280 X 720)
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;
        Log.e("Screen"," size...");
        System.out.println("Height..:"+height+"...Width...:"+width);



    }

    // Load data on background
    class LoadContact extends AsyncTask<Void, Void, Void>  {
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
                            Toast t = Toast.makeText(getApplicationContext(), "No contact lists", Toast.LENGTH_LONG);
                            t.show();
                        }
                    });}

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
            adapter = new SelectUserAdapter(selectUsers, ContactsMainActivity.this);
            adapter.notifyDataSetChanged();
            listView.setAdapter(adapter);
            listView.setTextFilterEnabled(true);
           // place your adapter to a separate filter to remove pop up text
           filter = adapter.getFilter();
            setupSearchView();

            // Select item on listclick
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Bitmap image = null;
                    SelectUser data = (SelectUser) parent.getItemAtPosition(position);

                    String name = data.getName();
                    String phoneNo = data.getPhone();
                    image = data.getThumb();


                    if (image== null) {

                        Drawable d = getResources().getDrawable(R.drawable.simple_profile_placeholder1);
                       ImageView contactimage = (ImageView) findViewById(R.id.contactImage);
                        contactimage.setImageDrawable(d);
                        contactimage.buildDrawingCache();
                        image = contactimage.getDrawingCache();
                    }

                    //dynamically increase the size of the imageview
                    int width = image.getWidth();
                    int height = image.getHeight();
                    int newWidth = 300;
                    int newHeight = 240;
                    float scaleWidth = ((float) newWidth) / width;
                    float scaleHeight = ((float) newHeight) / height;
                    Matrix matrix = new Matrix();
                    matrix.postScale(scaleWidth, scaleHeight);
                    Bitmap newbm = Bitmap.createBitmap(image, 0, 0, width, height, matrix,true);

                    //Passing data to nextscreen
                    Intent nextScreenIntent = new Intent(getApplicationContext(), DisplayContact.class);
                    nextScreenIntent.putExtra("name", name);
                    nextScreenIntent.putExtra("phoneNo", phoneNo);

                    Bundle extras = new Bundle();
                    extras.putParcelable("photo", newbm);

                    nextScreenIntent.putExtras(extras);


                    Log.e("n", name + "." + phoneNo);
                    startActivity(nextScreenIntent);
                }
            });

            listView.setFastScrollEnabled(true);


        }
    }
    private void setupSearchView()
    {
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.setSubmitButtonEnabled(true);
        mSearchView.setQueryHint("Search");
    }

    @Override
    public boolean onQueryTextChange(String newText)
    {
        if (TextUtils.isEmpty(newText)) {
            listView.clearTextFilter();
        } else {
            listView.setFilterText(newText);
        }
        return true;
    }


    @Override
    public boolean onQueryTextSubmit(String query)
    {
        return false;
    }




    @Override
    protected void onStop() {
        super.onStop();
        phones.close();
    }

    @Override
    public void onBackPressed() {
        int count = getFragmentManager().getBackStackEntryCount();
        if (count == 0) {
            super.onBackPressed();
        } else {
            getFragmentManager().popBackStack();
        }


    }}

//
///**
// * Shows a list that can be filtered in-place with a SearchView in non-iconified mode.
// */
//        public class SearchViewFilterMode extends Activity implements SearchView.OnQueryTextListener {
//
//            private SearchView mSearchView;
//            private ListView mListView;
//
//            private final String[] mStrings = Cheeses.sCheeseStrings;
//
//            @Override
//            protected void onCreate(Bundle savedInstanceState) {
//                super.onCreate(savedInstanceState);
//                getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
//
//                setContentView(R.layout.contact_listview);
//
//                mSearchView = (SearchView) findViewById(R.id.searchview);
//                mListView = (ListView) findViewById(R.id.contactList);
//                mListView.setAdapter(new ArrayAdapter<String>(this,
//                        android.R.layout.simple_list_item_1,
//                        mStrings));
//                mListView.setTextFilterEnabled(true);
//                setupSearchView();
//            }
//
//            private void setupSearchView() {
//                mSearchView.setIconifiedByDefault(false);
//                mSearchView.setOnQueryTextListener(this);
//                mSearchView.setSubmitButtonEnabled(true);
//                mSearchView.setQueryHint("Search Here");
//            }
//
//            public boolean onQueryTextChange(String newText) {
//                if (TextUtils.isEmpty(newText)) {
//                    mListView.clearTextFilter();
//                } else {
//                    mListView.setFilterText(newText.toString());
//                }
//                return true;
//            }
//
//            public boolean onQueryTextSubmit(String query) {
//                return false;
//            }
//        }
