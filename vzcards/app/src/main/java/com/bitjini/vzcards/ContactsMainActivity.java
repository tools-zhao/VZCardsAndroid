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
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
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

public class ContactsMainActivity extends Activity {

    // ArrayList
    ArrayList<SelectUser> selectUsers;
    List<SelectUser> temp;
    // Contact List
    ListView listView;
    // Cursor to load contacts list
    Cursor phones, email;

    private TextView displayText;

    // Pop up
    ContentResolver resolver;
   SelectUserAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contact_listview);


        selectUsers = new ArrayList<SelectUser>();
        resolver = this.getContentResolver();
        listView = (ListView) findViewById(R.id.contactList);

        displayText = (TextView)findViewById(R.id.resultText);

        initSearchView();

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
            listView.setAdapter(adapter);

            // Select item on listclick
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Bitmap image = null;
                    SelectUser data = selectUsers.get(position);

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

    private void initSearchView() {
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView = (SearchView) findViewById(R.id.searchview);
        SearchableInfo searchableInfo = searchManager.getSearchableInfo(getComponentName());
        searchView.setSearchableInfo(searchableInfo);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (ContactsContract.Intents.SEARCH_SUGGESTION_CLICKED.equals(intent.getAction())) {
            String displayName = getContactName(intent);
            displayText.setText(displayName);
        } else if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            displayText.setText("search for: '" + query + "'...");
        }
    }

    private String getContactName(Intent intent) {
        Cursor phoneCursor = getContentResolver().query(intent.getData(), null, null, null, null);
        phoneCursor.moveToFirst();
        int colNameIndex = phoneCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
        String contactName = phoneCursor.getString(colNameIndex);
        phoneCursor.close();
        return contactName;
    }
    @Override
    protected void onStop() {
        super.onStop();
        phones.close();
    }
}
