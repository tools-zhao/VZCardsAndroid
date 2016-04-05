//package com.bitjini.vzcards;
//
//import android.content.Context;
//import android.os.AsyncTask;
//
//import org.apache.http.NameValuePair;
//import org.apache.http.message.BasicNameValuePair;
//import org.json.JSONObject;
//
//import java.io.BufferedReader;
//import java.io.BufferedWriter;
//import java.io.DataOutputStream;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.OutputStream;
//import java.io.OutputStreamWriter;
//import java.io.UnsupportedEncodingException;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.net.URLEncoder;
//import java.nio.charset.StandardCharsets;
//import java.util.ArrayList;
//import java.util.List;
//
//import javax.net.ssl.HttpsURLConnection;
//
///**
// * Created by bitjini on 5/4/16.
// */
//public class SyncContacts extends AsyncTask<String, Void, String> {
//
//    Context context;
//    String SYNC_CONTACT_URL="http://vzcards-api.herokuapp.com/sync_contacts/?access_token=";
//
//    VerifyScreen p = new VerifyScreen();
//
//
//    @Override
//    protected String doInBackground(String... urls) {
//        // params comes from the execute() call: params[0] is the url.
//        try {
//            return downloadUrl(urls[0]);
//        } catch (IOException e) {
//            return "Unable to download the requested page.";
//        }
//    }
//
//    private String downloadUrl(String postURL) throws IOException {
//        {
//            p.sharedPreferences = context.getSharedPreferences(p.VZCARD_PREFS, 0);
//            p.token_sharedPreference = p.sharedPreferences.getString(p.TOKEN_KEY, null);
//            p.phone_sharedPreference = p.sharedPreferences.getString(p.PHONE_KEY, null);
//            p.vz_id_sharedPreference = p.sharedPreferences.getString(p.VZ_ID_KEY, null);
//
//            URL url = new URL("http://yoururl.com");
//            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
//            conn.setReadTimeout(10000);
//            conn.setConnectTimeout(15000);
//            conn.setRequestMethod("POST");
//            conn.setDoInput(true);
//            conn.setDoOutput(true);
//
//            ContactsMainActivity c=new ContactsMainActivity();
//
//            List<NameValuePair> params = new ArrayList<NameValuePair>();
//            params.add(new BasicNameValuePair("vz_id", p.vz_id_sharedPreference));
//            params.add(new BasicNameValuePair("contact_list", c.selectUsers));
//
//            OutputStream os = conn.getOutputStream();
//            BufferedWriter writer = new BufferedWriter(
//                    new OutputStreamWriter(os, "UTF-8"));
//            writer.write(getQuery(params));
//            writer.flush();
//            writer.close();
//            os.close();
//
//            conn.connect();
//
//        }
//        private String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException
//        {
//            StringBuilder result = new StringBuilder();
//            boolean first = true;
//
//            for (NameValuePair pair : params)
//            {
//                if (first)
//                    first = false;
//                else
//                    result.append("&");
//
//                result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
//                result.append("=");
//                result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
//            }
//
//            return result.toString();
//        }
//}
