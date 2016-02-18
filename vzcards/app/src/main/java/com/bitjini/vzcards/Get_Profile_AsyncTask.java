package com.bitjini.vzcards;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by bitjini on 18/2/16.
 */
public class Get_Profile_AsyncTask extends AsyncTask<String, Void, String> {

    private Context context;
    MyProfile_Fragment pr = new MyProfile_Fragment();
    VerifyScreen p = new VerifyScreen();
    public ArrayList<String> values;

    @Override
        protected String doInBackground(String... urls) {

            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadUrl(urls[0]);
            } catch (IOException e) {
                return "Unable to download the requested page.";
            }
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
//            Toast.makeText(this, "Received!", Toast.LENGTH_LONG).show();
            Log.e("response of profile details...", "" + result);
            try {

                JSONObject jsonObj = new JSONObject(result);


                    String firstname = jsonObj.getString("firstname");
                String lastname = jsonObj.getString("lastname");
                    String email = jsonObj.getString("email");
                String industry = jsonObj.getString("industry");
                String company = jsonObj.getString("company");
                String address_line_1 = jsonObj.getString("address_line_1");
                String address_line_2 = jsonObj.getString("address_line_2");
                String city = jsonObj.getString("city");
                String pin_code = jsonObj.getString("pin_code");

                Log.e("firstname  =", "" + firstname);
                values = new ArrayList<String>();
                values.add(firstname);
                values.add(lastname);
                values.add(email);
                values.add(p.phone_sharedPreference);
                values.add(industry);
                values.add(company);
                values.add(address_line_1);
                values.add(address_line_2);
               values.add(city);
                values.add(pin_code);



            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private String downloadUrl(String urlString) throws IOException {
            InputStream is = null;
            try {
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                // Starts the query
                conn.connect();
                int responseCode = conn.getResponseCode();
                is = conn.getInputStream();
                String contentAsString = convertStreamToString(is);
                return contentAsString;
            } finally {
                if (is != null) {
                    is.close();
                }
            }
        }

        private String convertStreamToString(InputStream is) throws UnsupportedEncodingException {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            try {
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return sb.toString();
        }
    }