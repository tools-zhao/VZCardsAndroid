package com.bitjini.vzcards;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentResolver;
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
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
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
import java.util.List;

/**
 * Created by bitjini on 18/12/15.
 */
public class iNeed_Activity extends Fragment {

    public static final String URL_CREATE_TICKET = "http://vzcards-api.herokuapp.com/ticket_create/?access_token=";

    private final int SELECT_PHOTO = 1;
    private Uri outputFileUri;

    Button havebtn;
    public ImageView item_image;
    Button addImage;
    TextView txtItem,txtDescription,txtDate_validity;
    ImageButton submit;
    public static String Item_picturePath;
    public String item_photo = "", item = "", description = "", date_validity="",question="";
    public ProgressDialog progress;
    public Bitmap output,bitmap;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View iNeed = inflater.inflate(R.layout.ineed, container, false);

        addImage=(Button) iNeed.findViewById(R.id.addImage);
        txtItem=(EditText) iNeed.findViewById(R.id.ask);
        txtDescription=(EditText) iNeed.findViewById(R.id.desc);
        txtDate_validity=(EditText) iNeed.findViewById(R.id.validity);
        item_image=(ImageView) iNeed.findViewById(R.id.item_img);
        submit=(ImageButton) iNeed.findViewById(R.id.imgbtn);

         havebtn = (Button) iNeed.findViewById(R.id.ihave);


        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageIntent();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new INeed_Task(getActivity()).execute(URL_CREATE_TICKET);
            }
        });

        havebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Fragment haveFragment = new AddActivity();
                // get the id of fragment
                FrameLayout contentView = (FrameLayout) getActivity().findViewById(R.id.ineed_frame);

                // Insert the fragment by replacing any existing fragment
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .add(contentView.getId(), haveFragment).addToBackStack(contentView.toString())
                        .commit();

            }
        });

        return iNeed;
    }
    private void openImageIntent() {

        // Determine Uri of camera image to save.
        final File root = new File(Environment.getExternalStorageDirectory() + File.separator + "amfb" + File.separator);
        root.mkdir();
        final String fname = "img_" + System.currentTimeMillis() + ".jpg";
        final File sdImageMainDirectory = new File(root, fname);
        outputFileUri = Uri.fromFile(sdImageMainDirectory);

        // Camera.
        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getActivity().getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            cameraIntents.add(intent);
        }

        //FileSystem
        final Intent galleryIntent = new Intent();
        galleryIntent.setType("image/");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

        // Chooser of filesystem options.
        final Intent chooserIntent = Intent.createChooser(galleryIntent, "Select Source");
        // Add the camera options.
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[]{}));
        startActivityForResult(chooserIntent, SELECT_PHOTO);
    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == getActivity().RESULT_OK) {
            if (requestCode == SELECT_PHOTO) {
                final boolean isCamera;

                if (data == null) {
                    isCamera = true;
                } else {
                    final String action = data.getAction();
                    if (action == null) {
                        isCamera = false;
                    } else {
                        isCamera = action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
                    }
                }

                Uri selectedImageUri;
                if (isCamera) {

                    selectedImageUri = outputFileUri;


                    Item_picturePath = selectedImageUri.getPath();
                        Log.e("path :", "" + Item_picturePath);

                        decodeFile(Item_picturePath);

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                        alertDialogBuilder.setMessage("Do you want to upload the image");
                        alertDialogBuilder.setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {


                                        // Upload image to server
                                        progress = new ProgressDialog(getActivity());
                                        if (progress != null) {
                                            progress.setMessage("Uploading image ..Please Wait...");
                                            progress.setCancelable(false);
                                            progress.show();
                                        }
                                        new UploadImageTask(getActivity()) {
                                            @Override
                                            public void onPostExecute(String result) {
                                                if (progress.isShowing()) {
                                                    progress.dismiss();
                                                    progress=null;
                                                }
                                                item_image.setImageBitmap(output);

                                                if (result != null) {

                                                    JSONObject json = null;
                                                    try {
                                                        json = new JSONObject(result);
                                                        item_photo = json.getString("photo");
//                                                        SavePreferences(COMPANY_IMAGE, item_photo);
                                                        String link = json.getString("link");
                                                        Log.e("item photo:", "" + item_photo);
                                                        Log.e("link :", "" + link);
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }
                                        }.execute();


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




                } else {
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

                    Item_picturePath = cursor.getString(columnIndex);
                        cursor.close();
                        Log.e("path :", "" + Item_picturePath);

                        decodeFile(Item_picturePath);

                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                        alertDialogBuilder.setMessage("Do you want to upload the image");
                        alertDialogBuilder.setPositiveButton("Ok",
                                new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface arg0, int arg1) {


                                        // Upload image to server
                                        progress = new ProgressDialog(getActivity());
                                        if (progress != null) {
                                            progress.setMessage("Uploading image ..Please Wait...");
                                            progress.setCancelable(false);
                                            progress.show();
                                        }
                                        new UploadImageTask(getActivity()) {
                                            @Override
                                            public void onPostExecute(String result) {
                                                if (progress.isShowing()) {
                                                    progress.dismiss();
                                                    progress=null;
                                                }
                                                item_image.setImageBitmap(output);

                                                if (result != null) {

                                                    JSONObject json = null;
                                                    try {
                                                        json = new JSONObject(result);
                                                        item_photo = json.getString("photo");
//                                                        SavePreferences(COMPANY_IMAGE, company_photo);
                                                        String link = json.getString("link");
                                                        Log.e("item_photo :", "" + item_photo);
                                                    } catch (JSONException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }
                                        }.execute();

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
            }else if (resultCode == getActivity().RESULT_CANCELED) {
                // user cancelled Image capture
                Toast.makeText(getActivity(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();
            } else {
                // failed to capture image
                Toast.makeText(getActivity(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }
        }

    }
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
        output=null;
        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        bitmap = BitmapFactory.decodeFile(filePath, o2);


    }

    class INeed_Task extends AsyncTask<String, Void, String> {

        Context context;

        VerifyScreen p = new VerifyScreen();
        StringBuffer response = new StringBuffer();
        public INeed_Task(Context context) {
            this.context = context;
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

            p.sharedPreferences = context.getSharedPreferences(p.VZCARD_PREFS, 0);
            p.token_sharedPreference = p.sharedPreferences.getString(p.TOKEN_KEY, null);
            p.vz_id_sharedPreference = p.sharedPreferences.getString(p.VZ_ID_KEY, null);

            URL url = new URL(postURL+  p.token_sharedPreference);
            final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            item_photo=txtItem.getText().toString();
            description=txtDescription.getText().toString();
            date_validity=txtDate_validity.getText().toString();
            question="1";

            try {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("vz_id", p.vz_id_sharedPreference));
                params.add(new BasicNameValuePair("item_photo", item_photo));
                params.add(new BasicNameValuePair("question", question));
                params.add(new BasicNameValuePair("item", item));
                params.add(new BasicNameValuePair("description", description));
                params.add(new BasicNameValuePair("date_validity", date_validity));

                OutputStream os = null;

                os = conn.getOutputStream();

                BufferedWriter writer = null;

                writer = new BufferedWriter(
                        new OutputStreamWriter(os, "UTF-8"));
                writer.write(getQuery(params));
                //Get Response
                InputStream is = conn.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                String line;

                while ((line = rd.readLine()) != null) {
                    response.append(line);
                    response.append('"');
                }
                rd.close();

                Log.e(" INeed Response", "" + response.toString());
                writer.flush();
                writer.close();
                os.close();


            } catch (IOException e) {
                e.printStackTrace();
            } finally {

                if (conn != null) {
                    conn.disconnect();
                }
            }


            conn.connect();

            return response.toString();
        }

        @Override
        public void onPostExecute(String result) {

            if (result != null) {
                Log.e(" result :", "" + result);
            }
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
    }
}
