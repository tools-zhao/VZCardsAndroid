package com.bitjini.vzcards;

import android.app.ProgressDialog;
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
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import static com.bitjini.vzcards.BaseURLs.SYNC_CONTACT_URL;

/**
 * Created by bitjini on 27/1/17.
 */

// Load data on background
public class LoadContacts extends AsyncTask<Void, Void, ArrayList<SelectUser >> {
    ProgressDialog progressDialog;
    Context context;
    static ArrayList<String > phoneArray=new ArrayList<>();
    public static  ArrayList<SelectUser>  phoneList12=new ArrayList<>();

    public ProgressDialog progress;
    public Cursor phones;

    public LoadContacts(Context c) {
        this.context = c;
    }


    @Override
    protected ArrayList<SelectUser> doInBackground(Void... voids) {

        ContentResolver resolver = context.getContentResolver();

        phones = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");

//            Log.e("show contact:",""+phones);
//            // Get Contact list from Phone
//            Log.e("phones", "" + phones);

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
                phoneNumber = phoneNumber.replaceAll("[\\D]", "");
                phoneNumber = phoneNumber.replaceFirst("^0+(?!$)", "");
                // get the country code
                String countryCode = GetCountryZipCode();

                if (phoneNumber.length() == 10) {
                    phoneNumber = countryCode + phoneNumber;

                }

                phoneArray.add(phoneNumber);


                SelectUser selectUser = new SelectUser();
                selectUser.setThumb(bit_thumb);
                selectUser.setName(name);
                selectUser.setPhone(phoneNumber);
                int flag = 0;
                if (phoneList12.size() == 0) {
                    phoneList12.add(selectUser);
                }
                for (int i = 0; i < phoneList12.size(); i++) {

                    if (!phoneList12.get(i).getPhone().trim().equals(phoneNumber)) {
                        flag = 1;

                    } else {
                        flag = 0;
                        break;
                    }

                }
                if (flag == 1) {
                    phoneList12.add(selectUser);
                }


            }
            phones.close();
        } else {
//                Log.e("Cursor close 1", "----------------");
        }
        return phoneList12;
    }

    public String GetCountryZipCode() {
        String CountryID = "";
        String CountryZipCode = "";

        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        //getNetworkCountryIso
        CountryID = manager.getSimCountryIso().toUpperCase();
        String[] rl = context.getResources().getStringArray(R.array.CountryCodes);
        for (int i = 0; i < rl.length; i++) {
            String[] g = rl[i].split(",");
            if (g[1].trim().equals(CountryID.trim())) {
                CountryZipCode = g[0];
                break;
            }
        }
        return CountryZipCode;
    }
}
