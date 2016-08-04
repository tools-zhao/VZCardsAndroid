package com.bitjini.vzcards;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by bitjini on 15/2/16.
 */
public class Connect_2_Tickets extends Activity {

    String URL_CONNECT = "https://vzcards-api.herokuapp.com/connect/?access_token=";
    String token_sharedPreference;
    private ProgressDialog progress;
    String connecter_vz_id, phone_1, ticket_id_1, phone_2, ticket_id_2, my_ticket, reffered_ticket, reffered_phone;

    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connect_tickets);

        Intent i=getIntent();
        //Retrieve the value
        connecter_vz_id = i.getStringExtra("connector_vz_id");
        phone_1 = i.getStringExtra("phone1");
        ticket_id_1 = i.getStringExtra("ticket_id_1");
        phone_2 = i.getStringExtra("phone2");
        ticket_id_2 = i.getStringExtra("ticket_id_2");
        my_ticket = "";
        reffered_ticket = "";
        reffered_phone = "";
        Log.e("ticket_id_1 =:",""+ticket_id_1);
        Log.e("phone1 =:",""+phone_1);
        Log.e("phone2 =:",""+phone_2);
        Log.e("connector_vz_id =:",""+connecter_vz_id);
        Log.e("ticket_id_2 =:",""+ticket_id_2);
        VerifyScreen p = new VerifyScreen();
        p.sharedPreferences = getSharedPreferences(p.VZCARD_PREFS, 0);
        token_sharedPreference = p.sharedPreferences.getString(p.TOKEN_KEY, null);
        System.out.println(" getting token from sharedpreference " + token_sharedPreference);
        // call AsynTask to perform network operation on separate thread
        new HttpPostClass().execute(URL_CONNECT + token_sharedPreference);

    }

    private class HttpPostClass extends AsyncTask<String, Void, String> {

         Context context;


        protected void onPreExecute() {
            progress = new ProgressDialog(Connect_2_Tickets.this);
            progress.setMessage("Connecting please wait....");
            progress.show();
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

        private String downloadUrl(String urlString) throws IOException {
            String response = null;
            try {
//                final TextView outputView = (TextView) findViewById(R.id.content);
                 urlString = URL_CONNECT + token_sharedPreference;
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);


                List<NameValuePair> params1 = new ArrayList<NameValuePair>();
                params1.add(new BasicNameValuePair("connecter_vz_id", connecter_vz_id));
                params1.add(new BasicNameValuePair("phone_1", phone_1));
                params1.add(new BasicNameValuePair("ticket_id_1", ticket_id_1));
                params1.add(new BasicNameValuePair("phone_2", phone_2));
                params1.add(new BasicNameValuePair("ticket_id_2", ticket_id_2));
                params1.add(new BasicNameValuePair("my_ticket", my_ticket));
                params1.add(new BasicNameValuePair("reffered_ticket", reffered_ticket));
                params1.add(new BasicNameValuePair("reffered_phone", reffered_phone));


                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getQuery(params1));
                writer.flush();
                writer.close();
                os.close();

                conn.connect();


                int responseCode = conn.getResponseCode();

                Log.e("res code", "" + responseCode);
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        response += line;
                    }
                } else {
                    response = "";

                }
                System.out.println("finalResult " + response);

                // return response
                return response;


            } catch (Exception e) {
                e.printStackTrace();
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
        protected void onPostExecute(String result) {
            progress.dismiss();
            Toast.makeText(Connect_2_Tickets.this, "Tickets Connected ", Toast.LENGTH_LONG).show();
            finish();
//            if (result != null) {
//                Log.e("valid =", "" + result.toString());
//                try {
//                    JSONObject res = new JSONObject(result);
//                    String my_ticket = res.getString("my_ticket");
//                    String reffered_ticket = res.getString("reffered_ticket");
//                    String reffered_phone = res.getString("reffered_phone");
//
//                    Log.e(" my_ticket =", "" + my_ticket);
//                    Log.e("reffered_ticket =", "" + reffered_ticket);
//                    Log.e("reffered_phone =", "" + reffered_phone);
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//            }
        }
    }
}
