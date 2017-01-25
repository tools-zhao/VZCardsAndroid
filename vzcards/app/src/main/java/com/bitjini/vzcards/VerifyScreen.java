package com.bitjini.vzcards;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.bitjini.vzcards.BaseURLs.URL_REGISTER;
import static com.bitjini.vzcards.BaseURLs.URL_RESEND;
import static com.bitjini.vzcards.BaseURLs.URL_VERIFY;
import static com.bitjini.vzcards.Constants.TOKEN_KEY;
import static com.bitjini.vzcards.Constants.VZCARD_PREFS;
import static com.bitjini.vzcards.Constants.is_organization_sharedPreference;
import static com.bitjini.vzcards.Constants.phone_sharedPreference;
import static com.bitjini.vzcards.Constants.sharedPreferences;
import static com.bitjini.vzcards.Constants.token_sharedPreference;
import static com.bitjini.vzcards.Constants.vz_id_sharedPreference;

/**
 * Created by VEENA on 12/8/2015.
 */
public class VerifyScreen extends Activity {




    private ProgressDialog progress;
    private EditText editTextPhoneNo, editTextOTP;
    String company_photo, photo, firstname, lastname, email;
    String industry, company, address_line_1, address_line_2, city, pin_code,title;
    public String otp, phone;
    private Button btn;

    TextView  textViewCountryCode;
    EditText textViewCountryPrefix;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.verify_activity);

       sharedPreferences = getSharedPreferences(VZCARD_PREFS, 0);
        String token_sharedPreference=sharedPreferences.getString(TOKEN_KEY,null);
        if(token_sharedPreference!=null)
        {
            Intent positveActivity = new Intent(getApplicationContext(), SplashScreen.class);
            startActivity(positveActivity);
            finish();
        }
        System.out.println(" getting token from sharedpreference "+ token_sharedPreference);

        // get the country code
        TelephonyManager tm = (TelephonyManager)getSystemService(getApplicationContext().TELEPHONY_SERVICE);
        String countryCode = tm.getNetworkCountryIso();
        String countryZipCode = GetCountryZipCode();

        editTextPhoneNo = (EditText) findViewById(R.id.phoneNo);
        textViewCountryPrefix=(EditText)findViewById(R.id.initial);
        textViewCountryCode=(TextView)findViewById(R.id.countryCode);

        btn = (Button) findViewById(R.id.verify);
        // set the country code to textview

//        textViewCountryPrefix.setText("+"+countryZipCode);
//        textViewCountryCode.setText(countryCode);
//        textViewCountryPrefix.setText("+"+countryZipCode);

        editTextPhoneNo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                String countryCode1=GetCountryIdCode();
                textViewCountryCode.setText(countryCode1);
                return false;
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editTextPhoneNo.getText().toString().length()==10){
                    InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (getCurrentFocus() != null){
                        inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                    sendPostRequest(view);


                }
                else {
                    Toast.makeText(getApplicationContext(),"Enter a valid 10 digit phone number",Toast.LENGTH_LONG).show();

                }

            }
        });
    }
    public String GetCountryZipCode(){
        String CountryID="";
        String CountryZipCode="";

        TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        //getNetworkCountryIso
        CountryID=manager.getSimCountryIso().toUpperCase();
        String[] rl=getResources().getStringArray(R.array.CountryCodes);
        for(int i=0;i<rl.length;i++){
            String[] g=rl[i].split(",");
            if(g[1].trim().equals(CountryID.trim())){
                CountryZipCode=g[0];
                break;
            }
        }
        return CountryZipCode;
    }
    public String GetCountryIdCode(){
        String CountryID="";
        String CountryZipCode="";

        TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        //getNetworkCountryIso
        CountryZipCode= textViewCountryPrefix.getText().toString().replaceAll("[\\D]", "");
        String[] rl=getResources().getStringArray(R.array.CountryCodes);
        for(int i=0;i<rl.length;i++){
            String[] g=rl[i].split(",");
            if(g[0].equals(CountryZipCode)){
                CountryID=g[1];
                break;
            }
        }
        return CountryID;
    }
    // method to call AsyncTask PostClass for registration
    public void sendPostRequest(View View) {
        new PostClass(this).execute(URL_REGISTER);
    }

//    public void sendGetRequest(View View) {
//        new GetClass(this).execute();
//    }

    /* *
      * PostClass for registration
      */
    private class PostClass extends AsyncTask<String, Void, String> {

        private final Context context;

        public PostClass(Context c) {
            this.context = c;
        }

        protected void onPreExecute() {
            progress = new ProgressDialog(this.context);
            progress.setMessage("Loading");
            progress.setCancelable(false);
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
            String response=null;
            try {
                     HttpClient client = new DefaultHttpClient();
                String postURL = URL_REGISTER;
                HttpPost post = new HttpPost(postURL);
                company_photo = "";
                photo = "";
                firstname = "";
                lastname = "";
                email = "";
                phone = textViewCountryPrefix.getText().toString().replaceAll("[\\D]", "")+editTextPhoneNo.getText().toString();
                industry = "";
                company = "";
                address_line_1 = "";
                address_line_2 = "";
                city = "";
                pin_code = "";
                title="";
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
                params1.add(new BasicNameValuePair("title", title));


                UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params1, HTTP.UTF_8);
                post.setEntity(ent);
                HttpResponse responsePOST = client.execute(post);
                HttpEntity resEntity = responsePOST.getEntity();

                if (resEntity != null) {
                    response=EntityUtils.toString(resEntity);
                    Log.i("RESPONSE", response);

                }
                StringBuilder sb = new StringBuilder();
                try {if(response!=null) {
                    BufferedReader reader =
                            new BufferedReader(new InputStreamReader(resEntity.getContent()), 65728);
                    String line = null;

                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
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


                CustomDialogClass cdd = new CustomDialogClass(VerifyScreen.this);
                cdd.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                cdd.setCanceledOnTouchOutside(false);
            cdd.requestWindowFeature(Window.FEATURE_NO_TITLE);
                cdd.show();

        }

    }

    /* *
       * class for creating  custom dialog box
       */
    class CustomDialogClass extends Dialog implements View.OnClickListener {

        public Activity c;
        public Dialog d;
        public Button ok, cancel, resend;
        private ProgressDialog progress;


        public CustomDialogClass(Activity a) {
            super(a);
            this.c = a;
        }

        public CustomDialogClass() {
            super(getApplicationContext());

        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.prompt);

            editTextOTP = (EditText) findViewById(R.id.otp);
            ok = (Button) findViewById(R.id.btnOK);
            resend = (Button) findViewById(R.id.btnResend);
            cancel = (Button) findViewById(R.id.btnCancel);

            ok.setOnClickListener(this);
            cancel.setOnClickListener(this);
            resend.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnOK:
                    // TODO Auto-generated method stub
                    if (editTextOTP.getText().toString().length() == 6) {

                        sendPostRequestOtp(v);

                        // do something, using return value from network

                        // out of range
                    } else {
                        Toast.makeText(getContext(), "please enter a valid 6 digit number", Toast.LENGTH_LONG).show();
                    }


                    break;
                case R.id.btnResend:
                    ReSendPostRequest(v);

                case R.id.btnCancel:
                    dismiss();
                default:

                    break;

            }
            dismiss();
        }

        // method to verify otp. call to verify url
        public void sendPostRequestOtp(View View) {
            new PostClassOTP(getContext()).execute();
        }

        // method to resend otp
        public void ReSendPostRequest(View View) {
            new PostClass(getContext()).execute(URL_RESEND);
        }

    }
   /* *
    * class for Verifying otp
    */
    private class PostClassOTP extends AsyncTask<String, Void, String> {
        String reply;

        private final Context context;

        public PostClassOTP(Context c) {
            this.context = c;
        }

        protected void onPreExecute() {
            progress = new ProgressDialog(this.context);
            progress.setMessage("Loading");
            progress.show();
        }

       protected String doInBackground(String... params)  {
           InputStream is = null;
           try {
               otp = editTextOTP.getText().toString().trim();
             String  urlString = URL_VERIFY;
               URL url = new URL(urlString);
               HttpURLConnection conn = (HttpURLConnection) url.openConnection();

               conn.setReadTimeout(10000 /* milliseconds */);
               conn.setConnectTimeout(15000 /* milliseconds */);
               conn.setRequestMethod("GET");
               conn.setDoInput(true);

               conn.setRequestProperty("PHONE",phone);
               conn.setRequestProperty("OTP", otp);

               // Starts the query
               conn.connect();
               int responseCode = conn.getResponseCode();
               is = conn.getInputStream();
               String contentAsString = convertStreamToString(is);
               Log.e("res=",""+contentAsString);
               return contentAsString;
           } catch (IOException e) {
               e.printStackTrace();
           }
           return null;
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

        protected void onPostExecute(String result) {
            progress.dismiss();
            if (result != null) {
                Log.e("valid =", "" + result);
                try {
                    JSONObject res=new JSONObject(result);

                    String  user_details=res.getString("user_details");
                    JSONObject userDetailsObj=new JSONObject(user_details);
                    String valid = userDetailsObj.getString("valid");
                    String token=userDetailsObj.getString("token_generated");
                     String vz_id=userDetailsObj.getString("vz_id");
                    String phone=userDetailsObj.getString("phone");

                    String  organization=res.getString("is_organization");
                    JSONObject obj2=new JSONObject(organization);
                    String is_organization=obj2.getString("is_organization");

                    Log.e("token generated =", "" + token);
                    Log.e("valid =", "" + valid);
                    Log.e("is_organisation =", "" + is_organization);

                   // saving token in shared prefernces
                    SaveResponseInSharedPreference(token,vz_id,is_organization);

                    if(Integer.parseInt(valid)==1)
                    {

                        Intent positveActivity = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(positveActivity);
                        finish();
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext()," OTP Not valid",Toast.LENGTH_LONG).show();

                    }
//
                } catch (JSONException e) {
                    e.printStackTrace();
                }






            }

        }
    }

    private void SaveResponseInSharedPreference(String token, String vz_id, String is_organization) {
        sharedPreferences = getSharedPreferences(VZCARD_PREFS, 0);
        SharedPreferences.Editor sEdit = sharedPreferences.edit();
        sEdit.putString("token", token);
        sEdit.putString("vz_id", vz_id);
        sEdit.putString("phone", phone);
        sEdit.putString("is_organization",is_organization);
        sEdit.commit();

        token_sharedPreference=sharedPreferences.getString("token",token);
        vz_id_sharedPreference=sharedPreferences.getString("vz_id",vz_id);
        phone_sharedPreference=sharedPreferences.getString("phone",phone);
        is_organization_sharedPreference=sharedPreferences.getString("is_organization",is_organization);
        System.out.println(" getting token from sharedpreference "+ token_sharedPreference);
        System.out.println(" getting vz_id from sharedpreference "+ vz_id_sharedPreference);
        System.out.println(" getting phone from sharedpreference "+ phone_sharedPreference);

    }
}
//    private class GetClass extends AsyncTask<String, Void, Void> {
//
//        private final Context context;
//
//        public GetClass(Context c) {
//            this.context = c;
//        }
//
//        protected void onPreExecute() {
//            progress = new ProgressDialog(this.context);
//            progress.setMessage("Loading");
//            progress.show();
//        }
//
//        @Override
//        protected Void doInBackground(String... params) {
//            try {
//
//                URL url = new URL("Your URL");
//                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//                String urlParameters = "fizz=buzz";
//                connection.setRequestMethod("GET");
//                connection.setRequestProperty("USER-AGENT", "Mozilla/5.0");
//                connection.setRequestProperty("ACCEPT-LANGUAGE", "en-US,en;0.5");
//
//                int responseCode = connection.getResponseCode();
//
//                final StringBuilder output = new StringBuilder("Request URL " + url);
//                output.append(System.getProperty("line.separator") + "Response Code " + responseCode);
//                output.append(System.getProperty("line.separator") + "Type " + "GET");
//                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//                String line = "";
//                StringBuilder responseOutput = new StringBuilder();
//                System.out.println("output===============" + br);
//                while ((line = br.readLine()) != null) {
//                    responseOutput.append(line);
//                }
//                br.close();
//
//                output.append(System.getProperty("line.separator") + "Response " + System.getProperty("line.separator") + System.getProperty("line.separator") + responseOutput.toString());
//
//                Post_example.this.runOnUiThread(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        outputView.setText(output);
//                        progress.dismiss();
//
//                    }
//                });
//
//            } catch (MalformedURLException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            } catch (IOException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//            return null;
//        }
//    }
