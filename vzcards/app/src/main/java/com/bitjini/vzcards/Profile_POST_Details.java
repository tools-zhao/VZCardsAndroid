package com.bitjini.vzcards;


import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
* Created by VEENA on 12/7/2015.
*/
public class Profile_POST_Details extends AsyncTask<String, Void, String> {

    private Context context;
    MyProfile_Fragment pr=new MyProfile_Fragment();
    VerifyScreen p = new VerifyScreen();

    public Profile_POST_Details(Context c) {
            this.context = c;
        }

        protected void onPreExecute() {

            pr.progress = new ProgressDialog(this.context);
            pr.progress.setMessage("Loading");
            pr.progress.show();
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
            String response = null;
            try {
//
                HttpClient client = new DefaultHttpClient();
                postURL = pr.URL_MY_PROFILE + pr.token_sharedPreference;
                HttpPost post = new HttpPost(postURL);

                String photo="",company_photo="",vz_id="";
                String firstname = "";
                String lastname = "";
                String industry = "";
                String company = "";
                String address_line_1 = "";
                String address_line_2 = "";
                String city = "";
                String pin_code = "";
                p.sharedPreferences = context.getSharedPreferences(p.VZCARD_PREFS, 0);
                pr.phone_sharedPreference = p.sharedPreferences.getString(p.PHONE_KEY, null);
                pr.vz_id_sharedPreference = p.sharedPreferences.getString(p.VZ_ID_KEY, null);


                String resultjson = p.sharedPreferences.getString(pr.TASKS, null);
               JSONObject json=new JSONObject(resultjson);
                Iterator<String> iter = json.keys();
                while (iter.hasNext()) {
                    String key = iter.next();
                    try {
                        Object value = json.get(key);
                        Log.e("value of json",""+value);
                        Log.e("key of json",""+key);
                    } catch (JSONException e) {
                        // Something went wrong!
                    }
                }

//                JSONArray jsonRoot = new JSONArray(resultjson);
//                for (int i=0;i<jsonRoot.length();i++) {
//                    JSONObject c = jsonRoot.getJSONObject(i);
////                    firstname = c.getString("label");
////                    lastname = c.getString("value");
//////                   industry = c.getString("industry");
//////                    company = c.getString("company");
//////                   address_line_1 = c.getString("address_line_1");
//////                     address_line_2 = c.getString("address_line_2");
//////                     city = c.getString("city");
//////                     pin_code = c.getString("pin_code");
//                }
                 String phone = pr.phone_sharedPreference;
//                 photo=pr.imageProfile.toString();
//                 company_photo=pr.imageCompany.toString();
                 vz_id=pr.vz_id_sharedPreference;

                    List<NameValuePair> params1 = new ArrayList<NameValuePair>();

                    params1.add(new BasicNameValuePair("photo", photo));
                    params1.add(new BasicNameValuePair("company_photo", company_photo));
                    params1.add(new BasicNameValuePair("vz_id", vz_id));
                    params1.add(new BasicNameValuePair("firstname", firstname));
                    params1.add(new BasicNameValuePair("lastname", lastname));
                    params1.add(new BasicNameValuePair("phone", phone));
                    params1.add(new BasicNameValuePair("industry", industry));
                    params1.add(new BasicNameValuePair("company", company));
                    params1.add(new BasicNameValuePair("address_line_1", address_line_1));
                    params1.add(new BasicNameValuePair("address_line_2", address_line_2));
                    params1.add(new BasicNameValuePair("city", city));
                    params1.add(new BasicNameValuePair("pin_code", pin_code));


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
            pr.progress.dismiss();


        }

    }



    /* *
         * HttpAsyncTask for getting data
         */
//    public class HttpAsyncTask extends AsyncTask<String, Void, String> {
//        @Override
//        protected String doInBackground(String... urls) {
//
//            // params comes from the execute() call: params[0] is the url.
//            try {
//                return downloadUrl(urls[0]);
//            } catch (IOException e) {
//                return "Unable to download the requested page.";
//            }
//        }
//
//        // onPostExecute displays the results of the AsyncTask.
//        @Override
//        protected void onPostExecute(String result) {
//            Toast.makeText(getActivity(), "Received!", Toast.LENGTH_LONG).show();
//            Log.e("response of profile...", "" + result);
//            try {
//
//                JSONObject user_detail = new JSONObject(result.toString());
//
//
//                    String phone = user_detail.getString("phone");
//                    String company_photo = user_detail.getString("company_photo");
//                    String vz_id = user_detail.getString("vz_id");
//                    String photo = user_detail.getString("photo");
//                    String firstname = user_detail.getString("firstname");
//                    String lastname = user_detail.getString("lastname");
//                    String email = user_detail.getString("email");
//                    String industry = user_detail.getString("industry");
//                    String company = user_detail.getString("company");
//                    String address_line_1 = user_detail.getString("address_line_1");
//                    String address_line_2 = user_detail.getString("address_line_2");
//                    String city = user_detail.getString("city");
//                    String pin_code = user_detail.getString("pin_code");
//
//                  ListItem profileValues = new ListItem();
//
//                    profileValues.setPhone(phone);
////                    profileValues.setCompany_photo(company_photo);
////                    profileValues.setVz_id(vz_id);
////                    profileValues.setPhoto(photo);
//                    profileValues.setFname(firstname);
//                    profileValues.setLastname(lastname);
//                    profileValues.setEmail(email);
//                    profileValues.setIndustry(industry);
//                    profileValues.setCompany(company);
//                    profileValues.setAddress_line_1(address_line_1);
//                    profileValues.setAddress_line_2(address_line_2);
//                    profileValues.setCity(city);
//                    profileValues.setPin_code(pin_code);
//                    values.add(profileValues.toString());
//
//                    label = new ArrayList<String>();
//                    label.add("phone");
//                    label.add("firstname");
//                    label.add("lastname");
//                    label.add("email");
//                    label.add("industry");
//                    label.add("company");
//                    label.add("address_line_1");
//                    label.add("address_line_2");
//                    label.add("city");
//                    label.add("pin_code");
//
//
//
//                    for (int i1 = 0; i1 < label.size(); i1++) {
//                        ListItem item = new ListItem();
//                        item.setLabel(label.get(i1));
//                        item.setValue(values.get(i1));
//                        arrayList.add(item);
//                    }
//
//
//
//
//                // send the adapterArraylist to the adapter and set it to listview
//                editTextAdapter = new EditTextAdapter(getActivity(), arrayList, R.layout.profile_layout);
//                listView.setAdapter(editTextAdapter);
//
//
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//
//        private String downloadUrl(String urlString) throws IOException {
//            InputStream is = null;
//            try {
//                URL url = new URL(urlString);
//                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//                conn.setReadTimeout(10000 /* milliseconds */);
//                conn.setConnectTimeout(15000 /* milliseconds */);
//                conn.setRequestMethod("GET");
//                conn.setDoInput(true);
//                // Starts the query
//                conn.connect();
//                int responseCode = conn.getResponseCode();
//                is = conn.getInputStream();
//                String contentAsString = convertStreamToString(is);
//                return contentAsString;
//            } finally {
//                if (is != null) {
//                    is.close();
//                }
//            }
//        }
//
//        private String convertStreamToString(InputStream is) throws UnsupportedEncodingException {
//            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);
//            StringBuilder sb = new StringBuilder();
//            String line = null;
//            try {
//                while ((line = reader.readLine()) != null) {
//                    sb.append(line + "\n");
//                }
//            } catch (IOException e) {
//                e.printStackTrace();
//            } finally {
//                try {
//                    is.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//            return sb.toString();
//        }
//    }
//}

class ListItem {
    public String value;
    public String label;
    //Required fields for profile
    String company_photo, lastname, email, industry, company, address_line_1, address_line_2, city, pin_code;
    String phone, fname, vz_id, photo;


    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getVz_id() {
        return vz_id;
    }

    public void setVz_id(String vz_id) {
        this.vz_id = vz_id;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getPhone() {
        return phone;
    }

    public String getCompany_photo() {
        return company_photo;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public void setCompany_photo(String company_photo) {
        this.company_photo = company_photo;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getAddress_line_1() {
        return address_line_1;
    }

    public void setAddress_line_1(String address_line_1) {
        this.address_line_1 = address_line_1;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress_line_2() {
        return address_line_2;
    }

    public void setAddress_line_2(String address_line_2) {
        this.address_line_2 = address_line_2;
    }

    public String getPin_code() {
        return pin_code;
    }

    public void setPin_code(String pin_code) {
        this.pin_code = pin_code;
    }


    ListItem() {
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    ListItem(String label, String value) {
        this.value = value;
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
