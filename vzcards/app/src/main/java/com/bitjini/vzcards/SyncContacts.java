package com.bitjini.vzcards;

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

/**
 * Created by bitjini on 5/4/16.
 */
public class SyncContacts extends AsyncTask<String, Void, String> {
    ArrayList<String > phoneArray=new ArrayList<>();


    // Cursor to load contacts list
    Cursor phones;

    // Pop up
    ContentResolver resolver;

    Context context;

    VerifyScreen p = new VerifyScreen();

    public SyncContacts(Context context) {
        this.context = context;
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

            URL url = new URL(postURL);
            final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            new LoadContact() {
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    try {

                        List<NameValuePair> params = new ArrayList<NameValuePair>();
                        params.add(new BasicNameValuePair("vz_id", p.vz_id_sharedPreference));

                        for(String s: phoneArray) {
                            params.add(new BasicNameValuePair("contact_list", s));
                            Log.e("s", "" + s);
                        }
                        OutputStream os = null;

                        os = conn.getOutputStream();

                        BufferedWriter writer = null;

                        writer = new BufferedWriter(
                                new OutputStreamWriter(os, "UTF-8"));
                        writer.write(getQuery(params));
                        //Get Response
                        InputStream is = conn.getInputStream();
                        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                        String line;
                        StringBuffer response = new StringBuffer();
                        while ((line = rd.readLine()) != null) {
                            response.append(line);
                            response.append('"');
                        }
                        rd.close();

                        Log.e(" contact list Response", "" + response.toString());
                        writer.flush();
                        writer.close();
                        os.close();


                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {

                        if (conn != null) {
                            conn.disconnect();
                        }
                    }

                }}.execute();




            conn.connect();
        }
        return null;
    }
    @Override
    public void onPostExecute(String result) {

        if(result!=null)
        {
            Log.e(" result :",""+result);
        }
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
    class LoadContact extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... voids) {
            // Get Contact list from Phone
            phones = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");

            if (phones != null) {
                Log.e("count", "" + phones.getCount());
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
                   String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                    phoneNumber=phoneNumber.replaceAll("[\\D]", "");
                    // get the country code
                    String countryCode = GetCountryZipCode();

//                    Log.e(" phone number :",""+phoneNumber);
                    SelectUser selectUser = new SelectUser();

                    if(phoneNumber.length()== 10)
                    {
                        phoneNumber=countryCode+phoneNumber;
                        Log.e(" phone No.countrycode:",""+phoneNumber);

                    }

                    phoneArray.add(phoneNumber);

//                    for (String s:phoneArray) {
//                        Log.e(" phone arrays:", "" + s);
//                    }

                }
            } else {
                Log.e("Cursor close 1", "----------------");
            }
            //phones.close();
            return null;
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
}

