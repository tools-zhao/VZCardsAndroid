package com.bitjini.vzcards;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by bitjini on 18/12/15.
 */
public class iNeed_Activity extends Fragment implements View.OnClickListener {

    public static final String URL_CREATE_TICKET = "http://vzcards-api.herokuapp.com/ticket_create/?access_token=";

    private static final int CAMERA_CODE = 101, GALLERY_CODE = 201, CROPING_CODE = 301;

    private Button btn_select_image;
    private Uri mImageCaptureUri;
    private File outPutFile = null;
    private final int SELECT_PHOTO = 1;
    private Uri outputFileUri;
    private static final int PERMISSIONS_REQUEST_CAMERA = 105;
    private static final int PERMISSIONS_READ_EXTERNAL_STORAGE = 106;
    private static final int PERMISSIONS_WRITE_EXTERNAL_STORAGE = 107;


    Button havebtn;
    ImageButton btnCander;
    public ImageView item_image,showImage;;
    Button addImage;
    EditText txtItem,txtDescription;
    TextView txtDate_validity;
    ImageButton submit;
    public static String Item_picturePath="";
    public String item_photo = "", item = "", description = "", date_validity="",question="";
    public ProgressDialog progress=null,progressDialog;
    public Bitmap bitmap=null;

    VerifyScreen p = new VerifyScreen();
    RelativeLayout main_layout,displayImage_layout;
    Button done1,done2, cancel,choose;
    Animation animScale;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View iNeed = inflater.inflate(R.layout.ineed, container, false);

        addImage=(Button) iNeed.findViewById(R.id.addImage);
        txtItem=(EditText) iNeed.findViewById(R.id.ask);
        txtDescription=(EditText) iNeed.findViewById(R.id.desc);
        txtDate_validity=(TextView) iNeed.findViewById(R.id.validity);
        item_image=(ImageView) iNeed.findViewById(R.id.item_img);
        submit=(ImageButton) iNeed.findViewById(R.id.imgbtn);
        txtDescription.setMovementMethod(new ScrollingMovementMethod());
        item_image.setImageResource(R.drawable.no_pic_placeholder_full);
        animScale = AnimationUtils.loadAnimation(getActivity(), R.anim.scale);
        outPutFile = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");

        main_layout=(RelativeLayout) iNeed.findViewById(R.id.main_layout);
        displayImage_layout=(RelativeLayout) iNeed.findViewById(R.id.displayLayout);

        showImage=(ImageView)iNeed.findViewById(R.id.showImage);
        choose=(Button)iNeed.findViewById(R.id.choose);
        cancel=(Button) iNeed.findViewById(R.id.cancel);

         havebtn = (Button) iNeed.findViewById(R.id.ihave);
        done1 = (Button) iNeed.findViewById(R.id.done);
        done2 = (Button) iNeed.findViewById(R.id.done2);

        p.sharedPreferences = getActivity().getSharedPreferences(p.VZCARD_PREFS, 0);
        p.token_sharedPreference = p.sharedPreferences.getString(p.TOKEN_KEY, null);
        p.vz_id_sharedPreference = p.sharedPreferences.getString(p.VZ_ID_KEY, null);

        addImage.setOnClickListener(this);
//        btnCander = (ImageButton) iNeed.findViewById(R.id.click);
//        btnCander.setOnClickListener(this);
        submit.setOnClickListener(this);

        havebtn.setOnClickListener(this);

        txtDate_validity.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                showDatePickerDialog(v);
                return false;
            }
        });
      txtItem.setOnTouchListener(new View.OnTouchListener() {
          @Override
          public boolean onTouch(View v, MotionEvent event) {
              done1.setVisibility(View.VISIBLE);
              done2.setVisibility(View.GONE);
              return false;
          }
      });
        txtDescription.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                done1.setVisibility(View.GONE);
                done2.setVisibility(View.VISIBLE);
                return false;
            }
        });
        done1.setOnClickListener(this);
        done2.setOnClickListener(this);

        return iNeed;
    }
    private void selectImageOption() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getActivity().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            getActivity().requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSIONS_REQUEST_CAMERA);
        }
        else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                getActivity().requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_WRITE_EXTERNAL_STORAGE);
            } else {
                final CharSequence[] items = { "Capture Photo", "Choose from Gallery", "Cancel" };

                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
                builder.setTitle("Add Photo!");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {

                        if (items[item].equals("Capture Photo")) {

                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp1.jpg");
                            mImageCaptureUri = Uri.fromFile(f);
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
                            startActivityForResult(intent, CAMERA_CODE);

                        } else if (items[item].equals("Choose from Gallery")) {

                            Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(i, GALLERY_CODE);

                        } else if (items[item].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            getActivity().requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_READ_EXTERNAL_STORAGE);
        } else {
            if (resultCode != Activity.RESULT_CANCELED) {
                if (requestCode == GALLERY_CODE && resultCode == Activity.RESULT_OK && data != null) {

                    mImageCaptureUri = data.getData();
                    System.out.println("Gallery Image URI : " + mImageCaptureUri);
                    CropingIMG();

                } else if (requestCode == CAMERA_CODE && resultCode == Activity.RESULT_OK) {

                    System.out.println("Camera Image URI : " + mImageCaptureUri);
                    CropingIMG();
                } else if (requestCode == CROPING_CODE) {

                    try {
                        if (outPutFile.exists()) {
                            Bitmap photo = decodeFile(outPutFile);
                            Item_picturePath = outPutFile.getPath();
                            Log.e("path :", "" + Item_picturePath);
                            item_image.setImageBitmap(photo);
                        } else {
                            Toast.makeText(getActivity(), "Error while save image", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    private void CropingIMG() {

        final ArrayList<CropOption> cropOptions = new ArrayList<CropOption>();

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");

        List<ResolveInfo> list = getActivity().getPackageManager().queryIntentActivities( intent, 0 );
        int size = list.size();
        if (size == 0) {
            Toast.makeText(getActivity(), "Cann't find image croping app", Toast.LENGTH_SHORT).show();
            return;
        } else {
            intent.setData(mImageCaptureUri);
            intent.putExtra("outputX", 512);
            intent.putExtra("outputY", 512);
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("scale", true);

            //TODO: don't use return-data tag because it's not return large image data and crash not given any message
//            intent.putExtra("return-data", true);

//            Create output file here
            if(outPutFile!=null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(outPutFile));
            }
            if (size == 1) {
                Intent i   = new Intent(intent);
                ResolveInfo res = list.get(0);

                i.setComponent( new ComponentName(res.activityInfo.packageName, res.activityInfo.name));

                startActivityForResult(i, CROPING_CODE);
            } else {
                for (ResolveInfo res : list) {
                    final CropOption co = new CropOption();

                    co.title  = getActivity().getPackageManager().getApplicationLabel(res.activityInfo.applicationInfo);
                    co.icon  =getActivity(). getPackageManager().getApplicationIcon(res.activityInfo.applicationInfo);
                    co.appIntent= new Intent(intent);
                    co.appIntent.setComponent( new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
                    cropOptions.add(co);
                }

                CropingOptionAdapter adapter = new CropingOptionAdapter(getActivity(), cropOptions);

                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
                builder.setTitle("Choose Croping App");
                builder.setCancelable(false);
                builder.setAdapter( adapter, new DialogInterface.OnClickListener() {
                    public void onClick( DialogInterface dialog, int item ) {
                        startActivityForResult( cropOptions.get(item).appIntent, CROPING_CODE);
                    }
                });

                builder.setOnCancelListener( new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel( DialogInterface dialog ) {

                        if (mImageCaptureUri != null ) {
                            getActivity().getContentResolver().delete(mImageCaptureUri, null, null );
                            mImageCaptureUri = null;
                        }
                    }
                } );

                android.app.AlertDialog alert = builder.create();
                alert.show();
            }
        }
    }

    private Bitmap decodeFile(File f) {
        try {
            // decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            // Find the correct scale value. It should be the power of 2.
            final int REQUIRED_SIZE = 512;
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE || height_tmp / 2 < REQUIRED_SIZE)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            // decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
        }
        return null;
    }
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CAMERA) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                selectImageOption();
            } else {
                Toast.makeText(getActivity(), "Until you grant the permission, we canot display the names", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == PERMISSIONS_WRITE_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                selectImageOption();
            } else {
                Toast.makeText(getActivity(), "Until you grant the permission, we canot display the names", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == PERMISSIONS_READ_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                selectImageOption();
            } else {
                Toast.makeText(getActivity(), "Until you grant the permission, we canot display the names", Toast.LENGTH_SHORT).show();
            }
        }
    }


    class INeed_Task extends AsyncTask<String, Void, String> {

        Context context;


        StringBuffer response = new StringBuffer();
        public INeed_Task(Context context) {
            this.context = context;

        }
        protected void onPreExecute() {

            progress = new ProgressDialog(this.context);
            progress.setMessage("Sending...");
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

        private String downloadUrl(String postURL) throws IOException {

            String response = null;
            Log.e(" web url",""+postURL);
            HttpClient client = new DefaultHttpClient();

            HttpPost post = new HttpPost(postURL);



            Log.e(" question test:",""+question);
            Log.e(" item_photo test:",""+item_photo);
            Log.e(" item test:",""+item);
            Log.e(" description test:",""+description);
            Log.e(" date_validity test:",""+date_validity);
            Log.e(" vz_id test:",""+p.vz_id_sharedPreference);

            try {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("vz_id",p.vz_id_sharedPreference));
                params.add(new BasicNameValuePair("item_photo", item_photo));
                params.add(new BasicNameValuePair("question", question));
                params.add(new BasicNameValuePair("item", item));
                params.add(new BasicNameValuePair("description", description));
                params.add(new BasicNameValuePair("date_validity", date_validity));

                // encode post data in url format
                UrlEncodedFormEntity ent = new UrlEncodedFormEntity(params, HTTP.UTF_8);
                post.setEntity(ent);
                HttpResponse responsePOST = client.execute(post);
                HttpEntity resEntity = responsePOST.getEntity();
                if (resEntity != null) {
                    // storing the response
                    response=EntityUtils.toString(resEntity);
                    Log.i("RESPONSE Ineed", response);

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
                return response;

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        public void onPostExecute(String result) {
            if (progress.isShowing()) {
                progress.dismiss();
                progress=null;
            }
            txtItem.setText("");
            txtDate_validity.setText("");
            txtDescription.setText("");
            item_image.setImageResource(R.drawable.no_pic_placeholder_full);
            bitmap = null;
            item_photo="";
            Item_picturePath="";
            Toast.makeText(getActivity(),"Your Data is posted ",Toast.LENGTH_LONG).show();

            if (result != null) {
                Log.e(" result :", "" + result);
            }
        }

    }


    class UploadImageTask extends AsyncTask<Void, Void, String> {
        VerifyScreen p=new VerifyScreen();
        Context context;

        MyProfile_Fragment pr=new MyProfile_Fragment();

        Activity activity;

        public UploadImageTask(Context c) {
            this.context = c;

        }


        @Override
        protected String doInBackground(Void... params) {
            try {

                Log.e(" web url :",""+pr.URL_UPLOAD_IMAGE);
                File sourceFile_profile = new File(Item_picturePath );
                Log.e("picturePath :", "" +Item_picturePath);


                Bitmap bmp = BitmapFactory.decodeFile(Item_picturePath);
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.JPEG, 70, bos);

                ContentBody foto =new ByteArrayBody(bos.toByteArray(), "filename");

                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(pr.URL_UPLOAD_IMAGE);

                String boundary = "-------------" + System.currentTimeMillis();

                httpPost.setHeader("Content-Type", "multipart/form-data; boundary="+boundary);

                HttpEntity entity = MultipartEntityBuilder.create()
                        .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                        .setBoundary(boundary)
                        .addPart("photo", foto)
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


    }

    public void onClick(View v) {
        switch (v.getId()) {

            //setting profile picture
            case R.id.addImage:
               selectImageOption();
                break;

            //setting company picture
            case R.id.imgbtn:
                v.startAnimation(animScale);
                item= txtItem.getText().toString();
                description= txtDescription.getText().toString();
                date_validity=txtDate_validity.getText().toString();
                question="1";

                if( item.length()==0 || description.length()==0 || date_validity.length()==0)
                {
                    Toast.makeText(getActivity(),"Enter details",Toast.LENGTH_SHORT).show();

                }
                else {


                    if (Item_picturePath.length() != 0) {
                        progressDialog = new ProgressDialog(getActivity());
                        if (progressDialog != null) {
                            progressDialog.setMessage("Collecting Data Please Wait...");
                            progressDialog.setCancelable(false);
                            progressDialog.show();
                        }
                        new UploadImageTask(getActivity()) {
                            @Override
                            public void onPostExecute(String result) {
                                if (progressDialog.isShowing()) {
                                    progressDialog.dismiss();
                                    progressDialog = null;
                                    Item_picturePath = "";
                                }
                                if (result != null) {
                                    JSONObject json = null;
                                    try {
                                        json = new JSONObject(result);
//
                                        String link = json.getString("link");
                                        item_photo = "http://res.cloudinary.com/harnesymz/image/upload/vzcards/" + link;
//
                                        Log.e("item_photo :", "" + item_photo);
                                        Log.e("link :", "" + link);

                                        new INeed_Task(getActivity()).execute(URL_CREATE_TICKET + VerifyScreen.token_sharedPreference);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }.execute();
                    } else {
                        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                        alertDialogBuilder.setMessage("Post data without uploading image");
                        alertDialogBuilder.setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {

                                        new INeed_Task(getActivity()).execute(URL_CREATE_TICKET + VerifyScreen.token_sharedPreference);
                                    }
                                });
                        alertDialogBuilder.setNegativeButton("cancel",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {

                                    }
                                });
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();

                    }
                }

                break;


            case R.id.done:
                txtItem.setCursorVisible(false);
                InputMethodManager inputManager2 = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (getActivity().getCurrentFocus() != null){
                    inputManager2.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
                done1.setVisibility(View.GONE);
                done2.setVisibility(View.GONE);

                break;
            case R.id.done2:
                done1.setVisibility(View.GONE);
                txtDescription.setCursorVisible(false);
                InputMethodManager inputManager1 = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (getActivity().getCurrentFocus() != null){
                    inputManager1.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
                done2.setVisibility(View.GONE);
                break;
            case R.id.ihave:

                Fragment haveFragment = new AddActivity();
                // get the id of fragment
                FrameLayout contentView = (FrameLayout) getActivity().findViewById(R.id.ineed_frame);

                // Insert the fragment by replacing any existing fragment
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(contentView.getId(), haveFragment)
                        .commit();
                break;

        }
    }


    public void showDatePickerDialog(View v) {
        DialogFragment newFragent = new DatePickerDialog1();
        newFragent.show(getActivity().getFragmentManager(), "datePicker");
    }


    public class DatePickerDialog1 extends DialogFragment implements android.app.DatePickerDialog.OnDateSetListener {


        private int myear;
        private int mmonth;
        private int mday;
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();

            myear = c.get(Calendar.YEAR);
            mmonth = c.get(Calendar.MONTH);
            mday= c.get(Calendar.DAY_OF_MONTH);


            Date newDate=c.getTime();
            Log.e("new date :",""+newDate);
            DatePickerDialog da=new DatePickerDialog(getActivity(), this, myear, mmonth, mday);
            da.getDatePicker().setMinDate(c.getTime().getTime());
            // Create a new instance of DatePickerDialog and return it
            return da;
        }

        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            if (year < myear)
                view.updateDate(myear, mmonth, mday);

            if (monthOfYear < mmonth && year == myear)
                view.updateDate(myear, mmonth, mday);

            if (dayOfMonth < mday && year == myear && monthOfYear == mmonth)
                view.updateDate(myear, mmonth, mday);

            int month = monthOfYear + 1;
            String formattedMonth = "" + month;
            String formattedDayOfMonth = "" + dayOfMonth;

            if (month < 10) {

                formattedMonth = "0" + month;
            }
            if (dayOfMonth < 10) {

                formattedDayOfMonth = "0" + dayOfMonth;
            }

            txtDate_validity.setText(year + "-" + formattedMonth + "-" + formattedDayOfMonth);
        }
    }
}
