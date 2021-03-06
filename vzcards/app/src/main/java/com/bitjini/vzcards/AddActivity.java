package com.bitjini.vzcards;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
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
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import android.support.v4.app.Fragment;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.net.ssl.HttpsURLConnection;

import static com.bitjini.vzcards.BaseURLs.URL_CREATE_TICKET;
import static com.bitjini.vzcards.BaseURLs.URL_Cloudynary_Image_Path;
import static com.bitjini.vzcards.Constants.IS_ORGANIZATION_KEY;
import static com.bitjini.vzcards.Constants.TOKEN_KEY;
import static com.bitjini.vzcards.Constants.VZCARD_PREFS;
import static com.bitjini.vzcards.Constants.VZ_ID_KEY;
import static com.bitjini.vzcards.Constants.is_organization_sharedPreference;
import static com.bitjini.vzcards.Constants.sharedPreferences;
import static com.bitjini.vzcards.Constants.token_sharedPreference;
import static com.bitjini.vzcards.Constants.vz_id_sharedPreference;

/**
 * Created by VEENA on 12/7/2015.
 */
public class AddActivity extends Fragment implements View.OnClickListener {

    private static final int CAMERA_CODE = 101, GALLERY_CODE = 201, CROPING_CODE = 301;

    private Uri mImageCaptureUri;
    private File outPutFile = null;


    private static final int PERMISSIONS_REQUEST_CAMERA = 197;
    private static final int PERMISSIONS_WRITE_EXTERNAL_STORAGE = 198;
    private static final int PERMISSIONS_READ_EXTERNAL_STORAGE = 199;

    RelativeLayout main_layout;
    public ImageView item_image;
    Button addImage;
    EditText txtItem, txtDescription;
    public static TextView txtDate_validity;
    ImageButton submit;
    public static String Item_picturePath = "";
    public String item_photo = "", item = "", description = "", date_validity = "", question = "";
    public ProgressDialog progress = null, progressDialog;
    public Bitmap bitmap;

    VerifyScreen p = new VerifyScreen();

    Button done1, done2, cancel;
    View view;

    Animation animScale;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View iHave = inflater.inflate(R.layout.add_layout, container, false);
        addImage = (Button) iHave.findViewById(R.id.addImage);
        txtItem = (EditText) iHave.findViewById(R.id.ask);
        txtDescription = (EditText) iHave.findViewById(R.id.desc);
        txtDate_validity = (TextView) iHave.findViewById(R.id.validity);
        item_image = (ImageView) iHave.findViewById(R.id.item_img);
        animScale = AnimationUtils.loadAnimation(getActivity(), R.anim.scale);
        txtDescription.setMovementMethod(new ScrollingMovementMethod());

        item_image.setImageResource(R.drawable.no_pic_placeholder_full);
        outPutFile = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");

        main_layout = (RelativeLayout) iHave.findViewById(R.id.main_layout);

        cancel = (Button) iHave.findViewById(R.id.cancel);

//        btnCander = (ImageButton) iHave.findViewById(R.id.click);

        submit = (ImageButton) iHave.findViewById(R.id.imgbtn);

        done1 = (Button) iHave.findViewById(R.id.done);
        done2 = (Button) iHave.findViewById(R.id.done2);

//        btnCander.setOnClickListener(this);
        addImage.setOnClickListener(this);

        submit.setOnClickListener(this);

        Button iNeed = (Button) iHave.findViewById(R.id.ineed);

        txtDate_validity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v);
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


        iNeed.setOnClickListener(this);

        return iHave;
    }

    private void selectImageOption() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getActivity().checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            getActivity().requestPermissions(new String[]{Manifest.permission.CAMERA}, PERMISSIONS_REQUEST_CAMERA);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getActivity().checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                getActivity().requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_WRITE_EXTERNAL_STORAGE);
            } else {
                final CharSequence[] items = {"Capture Photo", "Choose from Gallery", "Cancel"};

                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
                builder.setTitle("Add Photo!");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {

                        if (items[item].equals("Capture Photo")) {

                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp1.jpg");
                            final File root = new File(Environment.getExternalStorageDirectory() + File.separator + "amfb" + File.separator);
                            root.mkdir();
                            final String fname = "img_" + System.currentTimeMillis() + ".jpg";
                            final File sdImageMainDirectory = new File(root, fname);
                            mImageCaptureUri = Uri.fromFile(sdImageMainDirectory);
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
        if (resultCode == getActivity().RESULT_OK) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                getActivity().requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_READ_EXTERNAL_STORAGE);
            } else {

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

        List<ResolveInfo> list = getActivity().getPackageManager().queryIntentActivities(intent, 0);
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
            if (outPutFile != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(outPutFile));
            }
            if (size == 1) {
                Intent i = new Intent(intent);
                ResolveInfo res = list.get(0);

                i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));

                startActivityForResult(i, CROPING_CODE);
            } else {
                for (ResolveInfo res : list) {
                    final CropOption co = new CropOption();

                    co.title = getActivity().getPackageManager().getApplicationLabel(res.activityInfo.applicationInfo);
                    co.icon = getActivity().getPackageManager().getApplicationIcon(res.activityInfo.applicationInfo);
                    co.appIntent = new Intent(intent);
                    co.appIntent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
                    cropOptions.add(co);
                }

                CropingOptionAdapter adapter = new CropingOptionAdapter(getActivity(), cropOptions);

                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getActivity());
                builder.setTitle("Choose Croping App");
                builder.setCancelable(false);
                builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int item) {
                        startActivityForResult(cropOptions.get(item).appIntent, CROPING_CODE);
                    }
                });

                builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {

                        if (mImageCaptureUri != null) {
                            getActivity().getContentResolver().delete(mImageCaptureUri, null, null);
                            mImageCaptureUri = null;
                        }
                    }
                });

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


    public void onClick(final View v) {
        view = v;
        switch (view.getId()) {

            //setting profile picture
            case R.id.addImage:
                selectImageOption();
                break;

            //setting company picture
            case R.id.imgbtn:
                v.startAnimation(animScale);

                item = txtItem.getText().toString();
                description = txtDescription.getText().toString();
                date_validity = txtDate_validity.getText().toString();
                question = "0";


                Log.e("item=", "" + item + "" + item.length());
                if (item.length() == 0 || description.length() == 0 || date_validity.length() == 0) {
                    Log.e("item=", "" + item + "" + item.length());

                    Toast toast = Toast.makeText(getActivity(), "Enter details", Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                    view.clearAnimation();


                } else {
                    Log.e("item_photo :", "" + Item_picturePath);
                    Log.e("task item=", "" + Item_picturePath + "" + Item_picturePath.length());

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
                                    File f = new File(outPutFile.getPath());

                                    if (f.exists()) f.delete();
                                }
                                if (result != null) {
                                    JSONObject json = null;
                                    try {
                                        json = new JSONObject(result);
//
                                        String link = json.getString("link");
                                        item_photo = URL_Cloudynary_Image_Path + link;
//
                                        Log.e("item_photo :", "" + item_photo);
                                        Log.e("link :", "" + link);

                                        new INeed_Task(getActivity()).execute(URL_CREATE_TICKET + token_sharedPreference);

//                                        view.clearAnimation();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }.execute(Item_picturePath);
                    } else {
                        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                        alertDialogBuilder.setMessage("Post data without uploading image");
                        alertDialogBuilder.setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {

                                        new INeed_Task(getActivity()).execute(URL_CREATE_TICKET + token_sharedPreference);

//                                        v.clearAnimation();
                                    }
                                });
                        alertDialogBuilder.setNegativeButton("No",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {
                                        view.clearAnimation();
                                    }
                                });
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        alertDialog.show();

                    }


                }

                break;
//            case R.id.click:
//                showDatePickerDialog(v);
//
//                break;
            case R.id.done:
                txtItem.setCursorVisible(false);
                InputMethodManager inputManager2 = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (getActivity().getCurrentFocus() != null) {
                    inputManager2.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
                done1.setVisibility(View.GONE);
                done2.setVisibility(View.GONE);
                break;
            case R.id.done2:
                done1.setVisibility(View.GONE);
                txtDescription.setCursorVisible(false);
                InputMethodManager inputManager1 = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (getActivity().getCurrentFocus() != null) {
                    inputManager1.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                }
                done2.setVisibility(View.GONE);
                break;
            case R.id.ineed:
                Fragment needFragment = new iNeed_Activity();
                // get the id of fragment
//                FrameLayout contentView = (FrameLayout) getActivity().findViewById(R.id.ihave_frame);

                // Insert the fragment by replacing any existing fragment
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.ihave_frame, needFragment)
                        .commit();
                break;

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
            if (progress != null) {
                progress.setMessage("Sending...");
                progress.setCancelable(false);
                progress.show();
            }
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
            Log.e(" web url", "" + postURL);
//            HttpClient client = new DefaultHttpClient();
//
//            HttpPost post = new HttpPost(postURL);

            URL url = new URL(postURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);


            Log.e(" question test:", "" + question);
            Log.e(" item_photo test:", "" + item_photo);
            Log.e(" item test:", "" + item);
            Log.e(" description test:", "" + description);
            Log.e(" date_validity test:", "" + date_validity);
            Log.e(" vz_id test:", "" + vz_id_sharedPreference);

            try {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("vz_id", vz_id_sharedPreference));
                params.add(new BasicNameValuePair("item_photo", item_photo));
                params.add(new BasicNameValuePair("question", question));
                params.add(new BasicNameValuePair("item", item));
                params.add(new BasicNameValuePair("description", description));
                params.add(new BasicNameValuePair("date_validity", date_validity));

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getQuery(params));
                writer.flush();
                writer.close();
                os.close();

                conn.connect();


                int responseCode = conn.getResponseCode();

                Log.e("res code", "" + responseCode);
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        response += line;
                    }
                } else {
                    response = "";

                }
                System.out.println("finalResult " + response);

                // return response
                return response;

            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        private String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException {
            StringBuilder result = new StringBuilder();
            boolean first = true;

            for (NameValuePair pair : params) {
                if (first)
                    first = false;
                else
                    result.append("&");

                result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
                result.append("=");
                result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
            }

            return result.toString();
        }


        @Override
        public void onPostExecute(String result) {
            if (progress.isShowing() && progress != null) {
                progress.dismiss();
                progress = null;
            }
            txtItem.setText("");
            txtDate_validity.setText("");
            txtDescription.setText("");
            item_image.setImageResource(R.drawable.no_pic_placeholder_full);
            bitmap = null;
            item_photo = "";
            Item_picturePath = "";
            view.clearAnimation();
            Toast.makeText(getActivity(), "Your Data is posted ", Toast.LENGTH_LONG).show();

            if (result != null) {
                Log.e(" result :", "" + result);
            }
        }

    }


    //
    public void showDatePickerDialog(View v) {
        DialogFragment newFragent = new DatePickerDialog1();
        newFragent.show(getActivity().getFragmentManager(), "datePicker");
    }

    private void scaleImage(ImageView view) {
        Drawable drawing = view.getDrawable();
        if (drawing == null) {
            return;
        }
        Bitmap bitmap = ((BitmapDrawable) drawing).getBitmap();

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int bounding_x = ((View) view.getParent()).getWidth();//EXPECTED WIDTH
        int bounding_y = ((View) view.getParent()).getHeight();//EXPECTED HEIGHT

        float xScale = ((float) bounding_x) / width;
        float yScale = ((float) bounding_y) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(xScale, yScale);

        Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        width = scaledBitmap.getWidth();
        height = scaledBitmap.getHeight();
        BitmapDrawable result = new BitmapDrawable(getActivity().getResources(), scaledBitmap);

        view.setImageDrawable(result);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
        params.width = width;
        params.height = height;
        view.setLayoutParams(params);
    }


  public static   class DatePickerDialog1 extends DialogFragment implements DatePickerDialog.OnDateSetListener {


        private int myear;
        private int mmonth;
        private int mday;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();

            myear = c.get(Calendar.YEAR);
            mmonth = c.get(Calendar.MONTH);
            mday = c.get(Calendar.DAY_OF_MONTH);


            Date newDate = c.getTime();
            Log.e("new date :", "" + newDate);
            DatePickerDialog da = new DatePickerDialog(getActivity(), this, myear, mmonth, mday);
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




