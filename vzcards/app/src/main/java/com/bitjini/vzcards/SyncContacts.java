package com.bitjini.vzcards;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import java.nio.channels.SelectableChannel;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

import static com.bitjini.vzcards.BaseURLs.SYNC_CONTACT_URL;

/**
 * Created by bitjini on 5/4/16.
 */
public class SyncContacts extends AsyncTask<String, Void, String> {
    static ArrayList<String > phoneArray=new ArrayList<>();
    public static  ArrayList<SelectUser>  phoneList12=new ArrayList<>();

    public ProgressDialog progress;
    public Cursor phones;
    // Request code for READ_CONTACTS. It can be any number > 0.
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    // Cursor to load contacts list

   Activity _activity;
    // Pop up

   public Context context;

    VerifyScreen p = new VerifyScreen();

    public SyncContacts(Context context) {
        this.context = context;
    }

    public void LoadContacts()
    {
        new LoadContact().execute();
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

    private String downloadUrl(String postURL) throws IOException {
        {
            p.sharedPreferences = context.getSharedPreferences(p.VZCARD_PREFS, 0);
           p.vz_id_sharedPreference = p.sharedPreferences.getString(p.VZ_ID_KEY, null);

            //Create connection
            URL url = new URL(postURL);
            final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");

            conn.setRequestProperty("USER-AGENT", "Mozilla/5.0");
            conn.setRequestProperty("ACCEPT-LANGUAGE", "en-US,en;0.5");
            conn.setDoOutput(true);

//            new LoadContact().execute();
                    try {
//                        for (String s:phoneArray) {
//                            Log.e(" phone arrays:", "" + s);
//                        }
                        List<NameValuePair> params = new ArrayList<NameValuePair>();
                        params.add(new BasicNameValuePair("vz_id", p.vz_id_sharedPreference));
                        Log.e(" p.vz_id_", "" + p.vz_id_sharedPreference);

                        for(String s: phoneArray) {
                            params.add(new BasicNameValuePair("contact_list", s));
//                            Log.e("s", "" + s);
                        }
                        //Send request
                        DataOutputStream wr = new DataOutputStream (
                                conn.getOutputStream ());
                        wr.writeBytes (getQuery(params));
                        wr.flush ();
                        wr.close ();

                        //Get Response
                        InputStream is = conn.getInputStream();
                        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                        String line;
                        StringBuilder response = new StringBuilder();
                        while ((line = rd.readLine()) != null) {
                            response.append(line);
                            response.append('"');
                        }
                        rd.close();


//                        Log.e(" contact list Response", "" + response.toString());



                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {

                        if (conn != null) {
                            conn.disconnect();
                        }
                    }






            conn.connect();
        }
        return null;
    }



    private String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (NameValuePair pair : params) {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
            result.append("=");

            result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    // Load data on background
    class LoadContact extends AsyncTask<Void, Void, ArrayList<String >> {
        ProgressDialog progressDialog;
        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Syncing contacts.Please Wait..");
            progressDialog.setCancelable(false);
            progressDialog.show();

        }

        @Override
        protected ArrayList<String > doInBackground(Void... voids) {

            ContentResolver resolver=context.getContentResolver();

            phones =context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");

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
                    phoneNumber=phoneNumber.replaceAll("[\\D]", "");
                    phoneNumber=phoneNumber.replaceFirst("^0+(?!$)", "");
                    // get the country code
                    String countryCode = GetCountryZipCode();

                    if(phoneNumber.length()== 10)
                    {
                        phoneNumber=countryCode+phoneNumber;

                    }

                    phoneArray.add(phoneNumber);


                    SelectUser selectUser = new SelectUser();
                    selectUser.setThumb(bit_thumb);
                    selectUser.setName(name);
                    selectUser.setPhone(phoneNumber);
                    int flag = 0;
                    if(phoneList12.size() == 0){
                        phoneList12.add(selectUser);
                    }
                    for(int i=0;i<phoneList12.size();i++){

                        if(!phoneList12.get(i).getPhone().trim().equals(phoneNumber)){
                            flag = 1;

                        }else{
                            flag =0;
                            break;
                        }

                    }
                    if(flag == 1){
                        phoneList12.add(selectUser);
                    }


                }
                phones.close();
            } else {
//                Log.e("Cursor close 1", "----------------");
            }
            return phoneArray;
        }
        protected void onPostExecute(ArrayList<String> result) {
            if(!result.isEmpty()) {
                if (progressDialog.isShowing() && progressDialog != null) {
                    progressDialog.dismiss();
                    progressDialog = null;
                }
                new SyncContacts(context).execute(SYNC_CONTACT_URL);
            }
        }

    }
    public String GetCountryZipCode(){
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

