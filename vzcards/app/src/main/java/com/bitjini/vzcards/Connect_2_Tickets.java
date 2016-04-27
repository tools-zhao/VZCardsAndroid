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
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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
        Log.e("connector_vz_id =:",""+connecter_vz_id);

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
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();


                HttpClient client = new DefaultHttpClient();
                String postURL = URL_CONNECT + token_sharedPreference;
                HttpPost post = new HttpPost(postURL);

                List<NameValuePair> params1 = new ArrayList<NameValuePair>();
                params1.add(new BasicNameValuePair("connecter_vz_id", connecter_vz_id));
                params1.add(new BasicNameValuePair("phone_1", phone_1));
                params1.add(new BasicNameValuePair("ticket_id_1", ticket_id_1));
                params1.add(new BasicNameValuePair("phone_2", phone_2));
                params1.add(new BasicNameValuePair("ticket_id_2", ticket_id_2));
                params1.add(new BasicNameValuePair("my_ticket", my_ticket));
                params1.add(new BasicNameValuePair("reffered_ticket", reffered_ticket));
                params1.add(new BasicNameValuePair("reffered_phone", reffered_phone));


                UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params1, HTTP.UTF_8);
                post.setEntity(ent);
                HttpResponse responsePOST = client.execute(post);
                HttpEntity resEntity = responsePOST.getEntity();

                if (resEntity != null) {
                    response = EntityUtils.toString(resEntity);
                    Log.i("RESPONSE", response);

                }
                StringBuilder sb = new StringBuilder();
                try {
                    BufferedReader reader =
                            new BufferedReader(new InputStreamReader(resEntity.getContent()), 65728);
                    String line = null;

                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }


                System.out.println("finalResult " + sb.toString());
                return sb.toString();


            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String result) {
            progress.dismiss();
            Toast.makeText(Connect_2_Tickets.this, "Connected tickets", Toast.LENGTH_LONG).show();
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
