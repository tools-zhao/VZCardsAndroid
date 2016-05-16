package com.bitjini.vzcards;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;


import java.util.HashMap;

public class UploadImageTask extends AsyncTask<Void, Void, String> {
    MyProfile_Fragment pr=new MyProfile_Fragment();
    VerifyScreen p=new VerifyScreen();
    Context context;
    private ProgressDialog dialog;
    Activity activity;
    private String webAddressToPost = pr.URL_UPLOAD_IMAGE;

    public UploadImageTask(Context c) {
        this.context = c;
       dialog=new ProgressDialog(c);
    }


    @Override
    protected String doInBackground(Void... params) {
        try {

           Log.e(" web url :",""+webAddressToPost);
            File sourceFile_profile = new File(pr.picturePath );
            Log.e("picturePath :", "" +pr.picturePath);


            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(webAddressToPost);

            String boundary = "-------------" + System.currentTimeMillis();

            httpPost.setHeader("Content-Type", "multipart/form-data; boundary="+boundary);

            HttpEntity entity = MultipartEntityBuilder.create()
                    .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                    .setBoundary(boundary)
                    .addPart("photo", new FileBody(sourceFile_profile))
                    .build();

            httpPost.setEntity(entity);


            HttpResponse response = httpclient.execute(httpPost);
            int responseCode=response.getStatusLine().getStatusCode();
            Log.e("responseCode=",""+responseCode);
            String responseBody = EntityUtils.toString(response.getEntity());
            Log.v(" HTTP Response", responseBody);
            return responseBody;

        }
        catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            // something went wrong. connection with the server error
            e.printStackTrace();
        }catch (Throwable t) {
            t.printStackTrace(); }

        return null;
    }


}


