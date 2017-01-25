package com.bitjini.vzcards;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;


public class UploadImageTask extends AsyncTask<String, Void, String> {
    MyProfile_Fragment pr = new MyProfile_Fragment();
    VerifyScreen p = new VerifyScreen();
    Context context;
    private ProgressDialog dialog;
    Activity activity;
    private String webAddressToPost = pr.URL_UPLOAD_IMAGE;

    public UploadImageTask(Context c) {
        this.context = c;
        dialog = new ProgressDialog(c);
    }


        @Override
    protected String doInBackground(String... params) {
        try {

            String picturePath = params[0];
            Log.e(" web url :", "" + pr.URL_UPLOAD_IMAGE);
            File sourceFile_profile = new File(picturePath);
            Log.e("picturePath :", "" + picturePath);

            Bitmap bmp = BitmapFactory.decodeFile(picturePath);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 70, bos);

            ContentBody foto = new ByteArrayBody(bos.toByteArray(), "filename");

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(pr.URL_UPLOAD_IMAGE);

            String boundary = "-------------" + System.currentTimeMillis();

            httpPost.setHeader("Content-Type", "multipart/form-data; boundary=" + boundary);

            Log.e("content foto=",""+foto);
            HttpEntity entity = MultipartEntityBuilder.create()
                    .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                    .setBoundary(boundary)
                    .addPart("photo", foto)
                    .build();

            httpPost.setEntity(entity);


            HttpResponse response = httpclient.execute(httpPost);
            int responseCode = response.getStatusLine().getStatusCode();
            Log.e("responseCode=", "" + responseCode);
            String responseBody = EntityUtils.toString(response.getEntity());
            Log.v(" HTTP Response", responseBody);
            return responseBody;

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            // something went wrong. connection with the server error
            e.printStackTrace();
        } catch (Throwable t) {
            t.printStackTrace();
        }

        return null;
    }
}
//    @Override
//    protected String doInBackground(String... params) {
//
//
//            String picturePath = params[0];
//
//            String attachmentName = "photo";
//            String attachmentFileName = "bitmap.bmp";
//            String crlf = "\r\n";
//            String twoHyphens = "--";
//            String boundary = "*****";
//
//        HttpURLConnection conn = null;
//
//            try {
//
//                HttpURLConnection httpUrlConnection = null;
//                URL url = new URL(webAddressToPost);
//                String BOUNDRY = "==================================";
//
//                File sourceFile_profile = new File(picturePath);
//                // These strings are sent in the request body. They provide information about the file being uploaded
//                String contentDisposition = "Content-Disposition: form-data; name=\"photo\"; filename=\"" + sourceFile_profile.getName() + "\"";
//                String contentType = "Content-Type: application/octet-stream";
//                Bitmap bmp = BitmapFactory.decodeFile(picturePath);
//            ByteArrayOutputStream bos = new ByteArrayOutputStream();
//            bmp.compress(Bitmap.CompressFormat.JPEG, 70, bos);
//
//            ContentBody foto = new ByteArrayBody(bos.toByteArray(), "filename");
//                // This is the standard format for a multipart request
//                StringBuffer requestBody = new StringBuffer();
//                requestBody.append("--");
//                requestBody.append(BOUNDRY);
//                requestBody.append('\n');
//                requestBody.append(contentDisposition);
//                requestBody.append('\n');
//                requestBody.append(contentType);
//                requestBody.append('\n');
//                requestBody.append('\n');
//                requestBody.append(foto);
//                requestBody.append("--");
//                requestBody.append(BOUNDRY);
//                requestBody.append("--");
//
//                // Make a connect to the server
//                conn = (HttpURLConnection) url.openConnection();
//
//
//
//                conn.setDoOutput(true);
//                conn.setDoInput(true);
//                conn.setUseCaches(false);
//                conn.setRequestMethod("POST");
//                conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDRY);
//
//                // Send the body
//                DataOutputStream dataOS = new DataOutputStream(conn.getOutputStream());
//                dataOS.writeBytes(requestBody.toString());
//                dataOS.flush();
//                dataOS.close();
//// Get the response code
//                int statusCode = conn.getResponseCode();
//
//                InputStream is = null;
//
//                if (statusCode >= 200 && statusCode < 400) {
//                    // Create an InputStream in order to extract the response object
//                    is = conn.getInputStream();
//                    Log.e("is 1",""+conn.getInputStream());
//                }
//                else {
//                    is = conn.getErrorStream();
//                    Log.e("is 2",""+conn.getErrorStream());
//                }
//
//                // Ensure we got the HTTP 200 response code
//                int responseCode = conn.getResponseCode();
//                if (responseCode != 200) {
//                    throw new Exception(String.format("Received the response code %d from the URL %s", responseCode, url));
//                }
//
//                // Read the response
//                is = conn.getInputStream();
//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                byte[] bytes = new byte[1024];
//                int bytesRead;
//                while((bytesRead = is.read(bytes)) != -1) {
//                    baos.write(bytes, 0, bytesRead);
//                }
//                byte[] bytesReceived = baos.toByteArray();
//                baos.close();
//
//                is.close();
//                String response = new String(bytesReceived);
//
//                Log.e("res",""+response);
//                return response;
//                // TODO: Do something here to handle the 'response' string
//
//            } catch (ProtocolException e) {
//                e.printStackTrace();
//            } catch (MalformedURLException e) {
//                e.printStackTrace();
//            } catch (IOException e) {
//                e.printStackTrace();
//            } catch (Exception e) {
//                e.printStackTrace();
//            } finally {
//                if (conn != null) {
//                    conn.disconnect();
//                }
//            }
//        return null;
//
//        }
//}







