package com.bitjini.vzcards;

import java.io.IOException;
import java.util.ArrayList;


import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Filter;
import android.widget.SearchView;
import android.widget.Toast;
// Load data on background
public class ContactsMainActivity extends AsyncTask<Object, Object, ArrayList<SelectUser>> {
    // ArrayList
    public ArrayList<SelectUser> selectUsers = new ArrayList<>();


    // Cursor to load contacts list
    Cursor phones, email;
    SearchView mSearchView;

    Filter filter;
    // Pop up
    ContentResolver resolver;
    SelectUserAdapter adapter;

    Context context;

    public ContactsMainActivity(Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();

    }

    @Override
    protected ArrayList<SelectUser> doInBackground(Object... voids) {
        // Get Contact list from Phone
        ContentResolver resolver=context.getContentResolver();

        phones =context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");

        if (phones != null) {
//                Log.e("count", "" + phones.getCount());
            if (phones.getCount() == 0) {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(new Runnable() {
                    public void run() {
                        Toast t = Toast.makeText(context, "No contact lists", Toast.LENGTH_LONG);
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
//                            Log.e("No Image Thumb", "--------------");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                phoneNumber=phoneNumber.replaceAll("[\\D]", "");
                phoneNumber=phoneNumber.replaceFirst("^0+(?!$)", "");
                // get the country code
                String countryCode = GetCountryZipCode();

                if(phoneNumber.length()== 10)
                {
                    phoneNumber=countryCode+phoneNumber;

                }


                SelectUser selectUser = new SelectUser();
                selectUser.setThumb(bit_thumb);
                selectUser.setName(name);
                selectUser.setPhone(phoneNumber);
                int flag = 0;
                if(selectUsers.size() == 0){
                    selectUsers.add(selectUser);
                }
                for(int i=0;i<selectUsers.size();i++){


                    if(!selectUsers.get(i).getPhone().trim().equals(phoneNumber)){
                        flag = 1;


                    }else{
                        flag =0;
                        break;
                    }

                }
                if(flag == 1){
                    selectUsers.add(selectUser);
                }


            }
            phones.close();
        } else {
//                Log.e("Cursor close 1", "----------------");
        }
        phones.close();
        return selectUsers;
    }
    public  String GetCountryZipCode(){
        String CountryID="";
        String CountryZipCode="";

        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        //getNetworkCountryIso
        CountryID= manager.getSimCountryIso().toUpperCase();
        String[] rl=context.getResources().getStringArray(R.array.CountryCodes);
        for(int i=0;i<rl.length;i++){
            String[] g=rl[i].split(",");
            if(g[1].trim().equals(CountryID.trim())){
                CountryZipCode=g[0];
                break;
            }
        }
        return CountryZipCode;
    }

}