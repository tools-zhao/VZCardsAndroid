package com.bitjini.vzcards;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;


public class ImageActivity extends Activity {


    private static String URL = "http://vzcards-api.herokuapp.com/upload_image/?access_token=CQfIdY97XGGiaayjOlYxcwguWcA5ZF";
    MyProfile_Fragment pr = new MyProfile_Fragment();
    VerifyScreen p = new VerifyScreen();
    private String webAddressToPost = URL;
    String picturePath;
    private ImageView image;
    private Button uploadButton;
    private Bitmap bitmap;
    private Button selectImageButton;
    private ProgressDialog dialog;
    // number of images to select
    private static final int PICK_IMAGE = 1;
    private Uri fileUri;
    /**
     * called when the activity is first created
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_add);

        // find the views
        image = (ImageView) findViewById(R.id.uploadImage);
        uploadButton = (Button) findViewById(R.id.upload);

        // on click select an image
        selectImageButton = (Button) findViewById(R.id.clickpic);
        selectImageButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                selectImageFromGallery();

            }
        });

        // when uploadButton is clicked
        uploadButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new ImageUploadTask().execute();
            }
        });

    }

    /**
     * Opens dialog picker, so the user can select image from the gallery. The
     * result is returned in the method <code>onActivityResult()</code>
     */
    public void selectImageFromGallery() {
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(Intent.createChooser(intent, "Select Picture"),
//                PICK_IMAGE);
        // Check Camera
        if (getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // Open default camera
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

            // start the image capture Intent
            startActivityForResult(intent, 100);

        } else {
            Toast.makeText(getApplication(), "Camera not supported", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Retrives the result returned from selecting image, by invoking the method
     * <code>selectImageFromGallery()</code>
     */
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK
//                && null != data) {
//            Uri selectedImage = data.getData();
//            String[] filePathColumn = {MediaStore.Images.Media.DATA};
//
//            Cursor cursor = getContentResolver().query(selectedImage,
//                    filePathColumn, null, null, null);
//            cursor.moveToFirst();
//
//            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//           picturePath = cursor.getString(columnIndex);
//            cursor.close();
//            Log.e("path :",""+picturePath);
//            decodeFile(picturePath);
//
//        }
//    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != RESULT_CANCELED) {
            if (requestCode == 100) {

                Uri selectedImage = data.getData();
                bitmap = (Bitmap) data.getExtras().get("data");

                // Cursor to get image uri to display

                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
                if (cursor != null) {
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    picturePath = cursor.getString(columnIndex);
                    cursor.close();
                    Log.e("path ..", "" + picturePath);
                }


                decodeFile(picturePath);
            }
        }
    }
    /**
     * The method decodes the image file to avoid out of memory issues. Sets the
     * selected image in to the ImageView.
     *
     * @param filePath
     */
    public void decodeFile(String filePath) {
        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, o);

        // The new size we want to scale to
        final int REQUIRED_SIZE = 1024;

        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE)
                break;
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        bitmap = BitmapFactory.decodeFile(filePath, o2);

        image.setImageBitmap(bitmap);
    }


    /**
     * The class connects with server and uploads the photo
     */
    class ImageUploadTask extends AsyncTask<Void, Void, String> {
//        private String webAddressToPost = "http://your-website-here.com";

        // private ProgressDialog dialog;


        @Override
        protected void onPreExecute() {

            if(dialog !=null)
            {
                dialog = null;
            }
            dialog = new ProgressDialog(ImageActivity.this);
            if(dialog!=null){
            dialog.setMessage("Uploading...");
            dialog.show();}
        }

        @Override
        protected String doInBackground(Void... params) {
            try {


                File sourceFile = new File(picturePath );



                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(webAddressToPost);

                String boundary = "-------------" + System.currentTimeMillis();

                httpPost.setHeader("Content-Type", "multipart/form-data; boundary="+boundary);

//                ByteArrayBody bab = new ByteArrayBody(data, "pic.png");

                HttpEntity entity = MultipartEntityBuilder.create()
                        .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                        .setBoundary(boundary)
                        .addPart("photo", new FileBody(sourceFile))
                        .build();

                httpPost.setEntity(entity);


                    HttpResponse response = httpclient.execute(httpPost);

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

        @Override
        protected void onPostExecute(String result) {

            if(dialog.isShowing()){
            dialog.dismiss();}


            if(result!=null)
            {Toast.makeText(getApplicationContext(), "file uploaded",
                    Toast.LENGTH_LONG).show();
                try {
                    JSONArray jsonarr = new JSONArray(result);
                    String photo=jsonarr.getJSONObject(0).getString("photo");
                    String link=jsonarr.getJSONObject(0).getString("link");
                    Log.e("photo :",""+photo);
                    Log.e("link :",""+link);

                } catch (JSONException e) {
                    e.printStackTrace();
                }



            }
        }

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }
}
