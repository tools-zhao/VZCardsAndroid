package com.bitjini.vzcards;

import android.app.SearchManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bitjini on 18/12/15.
 */
public class VZFriends_Fragment extends Fragment implements View.OnClickListener {

    Context c;
    View v;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vzfrnds = inflater.inflate(R.layout.contact_listview, container, false);

            c=vzfrnds.getContext();
        selectUsers = new ArrayList<SelectUser>();
//            resolver = c.getContentResolver();
        listView = (ListView) vzfrnds.findViewById(R.id.contactList);

        displayText = (TextView) vzfrnds.findViewById(R.id.resultText);

//            initSearchView();

//            phones = c.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        LoadContact loadContact = new LoadContact();
        loadContact.execute();


        Button profilebtn = (Button) vzfrnds.findViewById(R.id.profilebtn);
        Button referral = (Button) vzfrnds.findViewById(R.id.referralbtn);

        profilebtn.setOnClickListener(this);

        referral.setOnClickListener(this);
        return vzfrnds;

    }


    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.profilebtn:
                Fragment profilefragment = new MyProfileActivity();
                // get the id of fragment
                FrameLayout contentView = (FrameLayout) getActivity().findViewById(R.id.vzfrnds_frame);

                // Insert the fragment by replacing any existing fragment
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .add(contentView.getId(), profilefragment).addToBackStack(contentView.toString())
                        .commit();


                break;
            case R.id.referralbtn:

                Fragment fragment = new Referral_Fragment();
                // get the id of fragment
                FrameLayout contentView2 = (FrameLayout) getActivity().findViewById(R.id.vzfrnds_frame);

                // Insert the fragment by replacing any existing fragment
                FragmentManager fragmentManager1 = getFragmentManager();
                fragmentManager1.beginTransaction()
                        .replace(contentView2.getId(), fragment).addToBackStack(contentView2.toString())
                        .commit();


                break;
            default:
                break;
        }


    }


    // Load data on background
    class LoadContact extends AsyncTask<Void, Void, Void> {

        View v;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... voids) {
            // Get Contact list from Phone

//        if (phones != null) {
//            Log.e("count", "" + phones.getCount());
//            if (phones.getCount() == 0) {
//                Handler handler = new Handler(Looper.getMainLooper());
//                handler.post(new Runnable() {
//                    public void run() {
//                        Toast t = Toast.makeText(getActivity(), "No contact lists", Toast.LENGTH_LONG);
//                        t.show();
//                    }
//                });}
//
//            while (phones.moveToNext()) {
            Bitmap bit_thumb = null;
//                String name = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
//                String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//                String image_thumb = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
//                try {
//                    if (image_thumb != null) {
//                        bit_thumb = MediaStore.Images.Media.getBitmap(resolver, Uri.parse(image_thumb));
//                    } else {
//                        Log.e("No Image Thumb", "--------------");
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }

            ArrayList names = new ArrayList<String>();
            names.add("Girish");
            names.add("Supreet");
            names.add("Rohit");
            names.add("Pooja");
            names.add("Muktadhir");
            names.add("Bharat");
            names.add("Mathew Json");
            names.add("Sheldon Cooper");
            names.add("Howard Wolowitz");

            ArrayList phoneNumbers = new ArrayList<String>();
            phoneNumbers.add("+918904826345");
            phoneNumbers.add("+919060482834");
            phoneNumbers.add("+917456575762");
            phoneNumbers.add("+918904826345");
            phoneNumbers.add("+919060482834");
            phoneNumbers.add("+917456575762");
            phoneNumbers.add("+918904826345");
            phoneNumbers.add("+919060482834");
            phoneNumbers.add("+917456575762");




            // Populate our list with groups and it's children
            for (int i = 0; i < names.size(); i++) {


                SelectUser selectUser = new SelectUser();
                selectUser.setName((String) names.get(i));
                selectUser.setPhone((String)phoneNumbers.get(i));
                selectUsers.add(selectUser);
            }


//        } else {
//            Log.e("Cursor close 1", "----------------");
//    }
    //phones.close();
    return null;

}
    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        adapter = new SelectUserAdapter(selectUsers, getActivity());
        listView.setAdapter(adapter);

        // Select item on listclick
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                LayoutInflater li = (LayoutInflater)c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = li.inflate(R.layout.vz_frnds, null);

                Bitmap image = null;
                SelectUser data = selectUsers.get(position);

                String name = data.getName();
                String phoneNo = data.getPhone();
//                image = data.getThumb();
//
//
//                if (image== null) {

                    Drawable d = getResources().getDrawable(R.drawable.simple_profile_placeholder1);
                    ImageView contactimage = (ImageView)view.findViewById(R.id.contactImage);
                    contactimage.setImageDrawable(d);
                    contactimage.buildDrawingCache();
                    image = contactimage.getDrawingCache();
//                }

                //dynamically increase the size of the imageview
//                int width = image.getWidth();
//                int height = image.getHeight();
//                int newWidth = 300;
//                int newHeight = 240;
//                float scaleWidth = ((float) newWidth) / width;
//                float scaleHeight = ((float) newHeight) / height;
//                Matrix matrix = new Matrix();
//                matrix.postScale(scaleWidth, scaleHeight);
//                Bitmap newbm = Bitmap.createBitmap(image, 0, 0, width, height, matrix,true);

                //Passing data to nextscreen
                Intent nextScreenIntent = new Intent(c, DisplayContact.class);
                nextScreenIntent.putExtra("name", name);
                nextScreenIntent.putExtra("phoneNo", phoneNo);

                Bundle extras = new Bundle();
                extras.putParcelable("photo", image);

                nextScreenIntent.putExtras(extras);


                Log.e("n", name + "." + phoneNo);
                startActivity(nextScreenIntent);
            }
        });

        listView.setFastScrollEnabled(true);
    }
}


//    private void initSearchView() {
//        SearchManager searchManager = (SearchManager)c.getSystemService(Context.SEARCH_SERVICE);
//        final SearchView searchView = (SearchView) v.findViewById(R.id.searchview);
//        SearchableInfo searchableInfo = searchManager.getSearchableInfo(getComponentName());
//        searchView.setSearchableInfo(searchableInfo);
//    }


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
        Cursor phoneCursor = c.getContentResolver().query(intent.getData(), null, null, null, null);
        phoneCursor.moveToFirst();
        int colNameIndex = phoneCursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
        String contactName = phoneCursor.getString(colNameIndex);
        phoneCursor.close();
        return contactName;
    }
    @Override
    public void onStop() {
        super.onStop();
//        phones.close();
    }
}


