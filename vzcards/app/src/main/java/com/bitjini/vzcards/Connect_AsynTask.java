package com.bitjini.vzcards;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bitjini on 26/4/16.
 */
public class Connect_AsynTask extends AsyncTask<String, Void, String> {
         Context context;


    @Override
    protected String doInBackground(String... urls1) {
        // params comes from the execute() call: params[0] is the url.
        try {
            return downloadUrl(urls1[0]);
        } catch (IOException e) {
            return "Unable to download the requested page.";
        }
    }

    private String downloadUrl(String urlString) throws IOException {
        String response = null;
        try {
//                final TextView outputView = (TextView) findViewById(R.id.content);

            HttpClient client = new DefaultHttpClient();

            HttpPost post = new HttpPost(urlString);

            Refer_VZfriends r=new Refer_VZfriends();
            List<NameValuePair> params1 = new ArrayList<NameValuePair>();
            params1.add(new BasicNameValuePair("connecter_vz_id", r.connecter_vz_id));
            params1.add(new BasicNameValuePair("phone_1", r.phone_1));
            params1.add(new BasicNameValuePair("ticket_id_1", r.ticket_id_1));
            params1.add(new BasicNameValuePair("phone_2", r.phone_2));
            params1.add(new BasicNameValuePair("ticket_id_2", r.ticket_id_2));
            params1.add(new BasicNameValuePair("my_ticket", r.my_ticket));
            params1.add(new BasicNameValuePair("reffered_ticket", r.reffered_ticket));
            params1.add(new BasicNameValuePair("reffered_phone", r.reffered_phone));


            UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params1, HTTP.UTF_8);
            post.setEntity(ent);
            HttpResponse responsePOST = client.execute(post);
            HttpEntity resEntity = responsePOST.getEntity();

            if (resEntity != null) {
                response = EntityUtils.toString(resEntity);
                Log.i("RESPONSE", response);


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
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


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
