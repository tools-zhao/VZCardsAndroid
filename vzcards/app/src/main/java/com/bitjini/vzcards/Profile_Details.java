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
import java.util.List;

/**
 * Created by VEENA on 12/7/2015.
 */
public class Profile_POST_Details extends AsyncTask<String, Void, String> {

        private final Context context;

        public Profile_POST_Details(Context c) {
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

        private String downloadUrl(String postURL) throws IOException {
            String response = null;
            try {
//
                HttpClient client = new DefaultHttpClient();
                postURL = URL_MY_PROFILE+token_sharedPreference;
                HttpPost post = new HttpPost(postURL);

                List<NameValuePair> params1 = new ArrayList<NameValuePair>();

                    params1.add(new BasicNameValuePair("photo", photo));
                    params1.add(new BasicNameValuePair("company_photo", company_photo));
                    params1.add(new BasicNameValuePair("vz_id", vz_id_sharedPreference));
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
            progress.dismiss();


        }

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
                    //Bitmap factory
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    // downsizing image as it throws OutOfMemory Exception for larger
                    // images
                    options.inSampleSize = 8;
                    final Bitmap bitmap = BitmapFactory.decodeFile(selectedImageUri.getPath(), options);

//            v.imageView.setImageDrawable(roundedImage);
                    currentImageView.setImageBitmap(bitmap);
//                    imageProfile.setImageBitmap(bitmap);
//                    imageCompany.setImageBitmap(bitmap);
                } else {
                    selectedImageUri = data == null ? null : data.getData();
                    Log.d("ImageURI", selectedImageUri.getLastPathSegment());
                    // /Bitmap factory
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    // downsizing image as it throws OutOfMemory Exception for larger
                    // images
                    options.inSampleSize = 8;
                    try {//Using Input Stream to get uri
                        InputStream input = getActivity().getContentResolver().openInputStream(selectedImageUri);
                        final Bitmap bitmap = BitmapFactory.decodeStream(input);
//                        imageProfile.setImageBitmap(bitmap);

                        currentImageView.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else if (resultCode == getActivity().RESULT_CANCELED) {
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

    public void onClick(View v) {
        switch (v.getId()) {

            //setting profile picture
            case R.id.profilePic:
                currentImageView = (ImageView) v;
                openImageIntent();
                break;

            //setting company picture
            case R.id.btn_pick:
                currentImageView = (ImageView) v;
                openImageIntent();
                break;

            //redirecting to VZFriends_Fragment
            case R.id.vzfrnds:
                Fragment newfragment = new VZFriends_Fragment();
                // get the id of fragment
                FrameLayout contentView2 = (FrameLayout) getActivity().findViewById(R.id.profile_frame);

                // Insert the fragment by replacing any existing fragment
                FragmentManager fragmentManager1 = getFragmentManager();
                fragmentManager1.beginTransaction()
                        .replace(contentView2.getId(), newfragment).addToBackStack(contentView2.toString())
                        .commit();
                break;

            //redirecting to Referral_Fragmen
            case R.id.referralbtn:
                Fragment fragment = new Referral_Fragment();
                // get the id of fragment
                FrameLayout contentView3 = (FrameLayout) getActivity().findViewById(R.id.profile_frame);

                // Insert the fragment by replacing any existing fragment
                FragmentManager fragmentManager2 = getFragmentManager();
                fragmentManager2.beginTransaction()
                        .replace(contentView3.getId(), fragment).addToBackStack(contentView3.toString())
                        .commit();


                break;
            default:
                break;

        }
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
