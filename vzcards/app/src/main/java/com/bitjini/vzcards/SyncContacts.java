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
import static com.bitjini.vzcards.Constants.vz_id_sharedPreference;

/**
 * Created by bitjini on 5/4/16.
 */
public class SyncContacts extends AsyncTask<String, Void, String> {
    static ArrayList<String > phoneArray=new ArrayList<>();
    public static  ArrayList<SelectUser>  phoneList12=new ArrayList<>();

    public ProgressDialog progress;
    public Cursor phones;
    // Cursor to load contacts list

   public Context context;

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
                        params.add(new BasicNameValuePair("vz_id", vz_id_sharedPreference));
                        Log.e(" p.vz_id_", "" + vz_id_sharedPreference);

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


}

