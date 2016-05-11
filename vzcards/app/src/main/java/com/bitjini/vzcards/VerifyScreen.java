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
import android.view.View;
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
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by VEENA on 12/8/2015.
 */
public class VerifyScreen extends Activity {

    String URL_REGISTER = "http://vzcards-api.herokuapp.com/user_register/?access_token=jUUMHSnuGys5nr6qr8XsNEx6rbUyNu";
    String URL_VERIFY = "http://vzcards-api.herokuapp.com/verify/?access_token=jUUMHSnuGys5nr6qr8XsNEx6rbUyNu";
    String URL_RESEND="http://vzcards-api.herokuapp.com/send_again/?access_token=jUUMHSnuGys5nr6qr8XsNEx6rbUyNu";
    public static String token_sharedPreference,phone_sharedPreference,vz_id_sharedPreference;

    public static final String VZCARD_PREFS = "MySharedPref";
    public SharedPreferences sharedPreferences;
    public String TOKEN_KEY="token";
    public String VZ_ID_KEY="vz_id";
    public String PHONE_KEY="phone";


    private ProgressDialog progress;
    private EditText editTextPhoneNo, editTextOTP;
    String company_photo, photo, firstname, lastname, email;
    String industry, company, address_line_1, address_line_2, city, pin_code;
    public String otp, phone;
    private Button btn;

    TextView  textViewCountryCode, textViewCountryPrefix;
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
        textViewCountryPrefix=(TextView)findViewById(R.id.initial);
        textViewCountryCode=(TextView)findViewById(R.id.countryCode);

        btn = (Button) findViewById(R.id.verify);
        // set the country code to textview
        textViewCountryCode.setText(countryCode);
        textViewCountryPrefix.setText(countryZipCode);



        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(editTextPhoneNo.getText().toString().length()==10){
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
        CountryID= manager.getSimCountryIso().toUpperCase();
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
                phone = textViewCountryPrefix.getText()+editTextPhoneNo.getText().toString();
                industry = "";
                company = "";
                address_line_1 = "";
                address_line_2 = "";
                city = "";
                pin_code = "";
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
    private class PostClassOTP extends AsyncTask<String, Void, JSONObject> {
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

        @Override
        protected JSONObject doInBackground(String... params) {
            String response = null;
            try {
                otp = editTextOTP.getText().toString().trim();
                HttpClient client = new DefaultHttpClient();
                String postURL = URL_VERIFY;

                // making Post request
                HttpPost post = new HttpPost(postURL);

                // Post data
                List<NameValuePair> params1 = new ArrayList<NameValuePair>();
                params1.add(new BasicNameValuePair("phone", phone));
                params1.add(new BasicNameValuePair("otp", otp));

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

        protected void onPostExecute(JSONObject result) {
            progress.dismiss();
            if (result != null) {
                Log.e("valid =", "" + result.toString());
                try {
                    JSONObject res=new JSONObject(result.toString());
                    int valid = res.getInt("valid");
                    String token=res.getString("token_generated");
                     String vz_id=res.getString("vz_id");
                    String phone=res.getString("phone");
                    Log.e("token generated =", "" + token);
                    Log.e("valid =", "" + valid);


                   // saving token in shared prefernces
                    sharedPreferences = getSharedPreferences(VZCARD_PREFS, 0);
                    SharedPreferences.Editor sEdit = sharedPreferences.edit();
                    System.out.println(" saving token generated "+ sEdit.putString("token", token));
                    System.out.println(" saving vz_id "+ sEdit.putString("vz_id", vz_id));
                    System.out.println(" saving phone "+ sEdit.putString("phone", phone));
                    sEdit.commit();

                     token_sharedPreference=sharedPreferences.getString("token",token);
                     vz_id_sharedPreference=sharedPreferences.getString("vz_id",vz_id);
                    phone_sharedPreference=sharedPreferences.getString("phone",phone);
                    System.out.println(" getting token from sharedpreference "+ token_sharedPreference);
                    System.out.println(" getting vz_id from sharedpreference "+ vz_id_sharedPreference);
                    System.out.println(" getting phone from sharedpreference "+ phone_sharedPreference);

                    if(valid==1)
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
