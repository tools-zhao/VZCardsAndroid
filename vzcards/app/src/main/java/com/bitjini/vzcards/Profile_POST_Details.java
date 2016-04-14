package com.bitjini.vzcards;


import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
* Created by VEENA on 12/7/2015.
*/
public class Profile_POST_Details extends AsyncTask<String, Void, String> {

    private Context context;
    MyProfile_Fragment pr = new MyProfile_Fragment();
    VerifyScreen p = new VerifyScreen();

    public Profile_POST_Details(Context c) {
        this.context = c;
    }

    protected void onPreExecute() {

        pr.progress = new ProgressDialog(this.context);
        pr.progress.setMessage("Loading");
        pr.progress.setCancelable(false);
        pr.progress.show();
    }

    @Override
    protected String doInBackground(String... urls) {
        // params comes from the execute() call: params[0] is the url.
        try {
            return String.valueOf(downloadUrl(urls[0]));
        } catch (IOException e) {
            return "Unable to download the requested page.";
        }
    }

    private JSONObject downloadUrl(String postURL) throws IOException {

//              private String downloadUrl(String urlString) throws IOException {
        String response = null;
        try {
//                final TextView outputView = (TextView) findViewById(R.id.content);

            p.sharedPreferences = context.getSharedPreferences(p.VZCARD_PREFS, 0);
            p.token_sharedPreference = p.sharedPreferences.getString(p.TOKEN_KEY, null);
            p.phone_sharedPreference = p.sharedPreferences.getString(p.PHONE_KEY, null);
            p.vz_id_sharedPreference = p.sharedPreferences.getString(p.VZ_ID_KEY, null);

            HttpClient client = new DefaultHttpClient();
            postURL = pr.URL_PROFILE_UPDATE + p.token_sharedPreference;
            HttpPost post = new HttpPost(postURL);

            pr.data = context.getSharedPreferences(pr.MY_PROFILE_PREFERENCES, 0);

            Log.e("photo in profile",""+pr.photo);
            String company_photo = pr.data.getString(pr.COMPANY_IMAGE, null);
            String photo =pr.data.getString(pr.PROFILE_IMAGE, null);;
            String firstname = "";
            String lastname = "";
            String email = "";
            String phone = p.phone_sharedPreference;
            String industry = "";
            String company = "";
            String address_line_1 = "";
            String address_line_2 = "";
            String city = "";
            String pin_code = "";
            String vz_id = p.vz_id_sharedPreference;




            String json = pr.data.getString(pr.TASKS, null);

            JSONArray jsonArray = new JSONArray(json);

            firstname = jsonArray.getJSONObject(0).getString("value");
            lastname = jsonArray.getJSONObject(1).getString("value");
            email = jsonArray.getJSONObject(2).getString("value");
            industry = jsonArray.getJSONObject(4).getString("value");
            company = jsonArray.getJSONObject(5).getString("value");
            address_line_1 = jsonArray.getJSONObject(6).getString("value");
            address_line_2 = jsonArray.getJSONObject(7).getString("value");
            city = jsonArray.getJSONObject(8).getString("value");
            pin_code = jsonArray.getJSONObject(9).getString("value");

            List<NameValuePair> params1 = new ArrayList<NameValuePair>();


            params1.add(new BasicNameValuePair("company_photo", company_photo));
            params1.add(new BasicNameValuePair("photo", photo));
            params1.add(new BasicNameValuePair("firstname", firstname));
            params1.add(new BasicNameValuePair("lastname", lastname));
            params1.add(new BasicNameValuePair("email", email));
            params1.add(new BasicNameValuePair("phone", phone));
            params1.add(new BasicNameValuePair("industry", industry));
            params1.add(new BasicNameValuePair("company", company));
            params1.add(new BasicNameValuePair("address_line_1", address_line_1));
            params1.add(new BasicNameValuePair("address_line_2", address_line_2));
            params1.add(new BasicNameValuePair("city", city));
            params1.add(new BasicNameValuePair("pin_code", pin_code));
            params1.add(new BasicNameValuePair("vz_id", vz_id));


            // encode post data in url format
            UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params1, HTTP.UTF_8);
            post.setEntity(ent);
            HttpResponse responsePOST = client.execute(post);
            HttpEntity resEntity = responsePOST.getEntity();
            if (resEntity != null) {
                // storing the response
                response=EntityUtils.toString(resEntity);
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
            // return response
            return new JSONObject(response);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    protected void onPostExecute(String result) {
        pr.progress.dismiss();
        Toast.makeText(context, "Profile is updated ", Toast.LENGTH_LONG).show();
        if (result != null) {
            Log.e("Post response =", "" + result.toString());
            try {
                JSONObject res = new JSONObject(result.toString());

                String firstname = res.getString("firstname");
                String photo = res.getString("photo");
                String companyphoto = res.getString("company_photo");
                Log.e("firstname generated =", "" + firstname);
                Log.e("photo generated =", "" + photo);
                Log.e("company pic generated =", "" + companyphoto);


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }
}

