package com.bitjini.vzcards;


        import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.text.Editable;
import android.text.TextWatcher;
        import android.util.Base64;
        import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
        import java.io.ByteArrayOutputStream;
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
        import java.util.concurrent.ExecutionException;

/**
 * Created by VEENA on 12/7/2015.
 */
public class MyProfile_Fragment extends Fragment implements View.OnClickListener {

    public static final String URL_PROFILE_UPDATE = "http://vzcards-api.herokuapp.com/my_profile/update/?access_token=";
    public static final String URL_GET_PROFILE = "http://vzcards-api.herokuapp.com/my_profile/?access_token=";
    public static final String URL_UPLOAD_IMAGE = "http://vzcards-api.herokuapp.com/upload_image/?access_token=";
    public static final String MY_PROFILE_PREFERENCES = "mypref.txt";

    public static final String TASKS = "key";

    private final int SELECT_PHOTO = 1;

    public ImageView imageProfile, imageCompany;
    private Uri outputFileUri;
    public ImageView currentImageView = null;
    View profile;
    SharedPreferences data;

    ArrayList<String> label;
    ArrayList<String> values;

    int clickCount = 0;
    //Declaring widgets
    Button editbtn, profilebtn, vzfrndsbtn, referralbtn;
    TextView textViewName;
    public ProgressDialog progress;
    ListView listView;
    EditTextAdapter editTextAdapter;

    ArrayList<ListItem> arrayList = new ArrayList<ListItem>();
    ArrayList<ListItem> adapterArrayList = new ArrayList<ListItem>();
    public ArrayList<ListItem> groupItem = new ArrayList<ListItem>();

    VerifyScreen p = new VerifyScreen();

    String json, json2;
    public String firstname = "", lastname = "", email = "", industry = "", company = "", address_line_1 = "", address_line_2 = "", city, pin_code = "",photo="";
    public Bitmap bitmap=null;
    public static String profile_picturePath,company_picturePath;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        profile = inflater.inflate(R.layout.profile_layout, container, false);

        editbtn = (Button) profile.findViewById(R.id.edit);
        listView = (ListView) profile.findViewById(R.id.profileList);

        p.sharedPreferences = getActivity().getSharedPreferences(p.VZCARD_PREFS, 0);
        p.token_sharedPreference = p.sharedPreferences.getString(p.TOKEN_KEY, null);
        p.phone_sharedPreference = p.sharedPreferences.getString(p.PHONE_KEY, null);
        p.vz_id_sharedPreference = p.sharedPreferences.getString(p.VZ_ID_KEY, null);
        System.out.println(" getting token from sharedpreference " + p.token_sharedPreference);


        textViewName = (TextView) profile.findViewById(R.id.name);
        //Picking Profile picture
        imageProfile = (ImageView) profile.findViewById(R.id.profilePic);
        imageCompany = (ImageView) profile.findViewById(R.id.btn_pick);

        //image listeners
        imageCompany.setOnClickListener(this);
        imageProfile.setOnClickListener(this);

           imageCompany.setClickable(false);
        imageProfile.setClickable(false);

        profilebtn = (Button) profile.findViewById(R.id.profilebtn);
        referralbtn = (Button) profile.findViewById(R.id.referralbtn);
        vzfrndsbtn = (Button) profile.findViewById(R.id.vzfrnds);

//        new Get_Profile_AsyncTask().execute(URL_GET_PROFILE + p.token_sharedPreference);


        data = getActivity().getSharedPreferences(MY_PROFILE_PREFERENCES, 0);
        String details = data.getString(TASKS, null);
        if (details != null) {

            LoadPreferences();
        }
        label = new ArrayList<String>();
        label.add("Firstname");
        label.add("Lastname");
        label.add("Email");
        label.add("Phone");
        label.add("Industry");
        label.add("Company");
        label.add("Address_line_1");
        label.add("Address_line_2");
        label.add("City");
        label.add("Pin_code");
        // Makinh http get request to load profile details
        try {

            String receivedData = new Get_Profile_AsyncTask().execute(URL_GET_PROFILE + p.token_sharedPreference).get();

            JSONObject jsonObj = new JSONObject(receivedData);

            firstname = jsonObj.getString("firstname");
            lastname = jsonObj.getString("lastname");
            email = jsonObj.getString("email");
            industry = jsonObj.getString("industry");
            company = jsonObj.getString("company");
            address_line_1 = jsonObj.getString("address_line_1");
            address_line_2 = jsonObj.getString("address_line_2");
            city = jsonObj.getString("city");
            pin_code = jsonObj.getString("pin_code");
            photo = jsonObj.getString("photo");

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        textViewName.setText(firstname+ " "+lastname);
        values = new ArrayList<String>();
        values.add(firstname);
        values.add(lastname);
        values.add(email);
        values.add(p.phone_sharedPreference);
        values.add(industry);
        values.add(company);
        values.add(address_line_1);
        values.add(address_line_2);
        values.add(city);
        values.add(pin_code);

        for (int i = 0; i < label.size(); i++) {
            ListItem item = new ListItem();
            item.setLabel(label.get(i));
            item.setValue(values.get(i));
            arrayList.add(item);
        }

        editbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (clickCount == 0) {

                    editbtn.setText("Save");

                    imageCompany.setClickable(true);
                    imageProfile.setClickable(true);

                    editTextAdapter.actv(true);
                    editTextAdapter.notifyDataSetChanged();

                    Toast.makeText(getActivity(), "click 0", Toast.LENGTH_LONG).show();
                    clickCount = 1;

                } else if (clickCount == 1) {

                    editbtn.setText("Edit");

                    imageCompany.setClickable(false);
                    imageProfile.setClickable(false);

                    editTextAdapter.actv(false);
                    json2 = new Gson().toJson(groupItem);// updated array

                    SavePreferences(TASKS, json2);
                    new Profile_POST_Details(getActivity()).execute(URL_PROFILE_UPDATE + p.token_sharedPreference);
                    // Upload image to server
                    new UploadImageTask().execute();

                    editTextAdapter.notifyDataSetChanged();

                    Toast.makeText(getActivity(), "click 1", Toast.LENGTH_LONG).show();
                    clickCount = 0;

                }

            }
        });

        if (!groupItem.isEmpty())
            json2 = new Gson().toJson(groupItem);// updated array
        Log.e("updated array", "" + json2);
        json = new Gson().toJson(arrayList); //default array


        //converting arrayList to json to Save the values in sharedpreference by calling SavePrefernces
        // Check if the updated array is equal to default array if false load default array else load updated array
        if (json.equals(json2) || json2 == null) {

            SavePreferences(TASKS, json);
        } else {


            SavePreferences(TASKS, json2);
        }
        LoadPreferences();


        vzfrndsbtn.setOnClickListener(this);
        referralbtn.setOnClickListener(this);
        return profile;
    }

    protected void SavePreferences(String key, String value) {
// TODO Auto-generated method stub
        data = getActivity().getSharedPreferences(MY_PROFILE_PREFERENCES, 0);
        SharedPreferences.Editor editor = data.edit();
        editor.putString(key, value);
//        SavePreferences(IMAGE, currentImageView.toString());

        editor.commit();
//        System.out.println(value);

    }


    // To retrive saved values in shared preference Now convert the JSON string back to your java object

    protected void LoadPreferences() {

        data = getActivity().getSharedPreferences(MY_PROFILE_PREFERENCES, 0);
        Gson gson = new Gson();
        String json = data.getString(TASKS, null);
        Log.e("Load json shared prefs ", "" + json);

        Type type = new TypeToken<ArrayList<ListItem>>() {
        }.getType();
        adapterArrayList = gson.fromJson(json, type);

        // send the adapterArraylist to the adapter and set it to listview
        editTextAdapter = new EditTextAdapter(getActivity(), adapterArrayList, R.layout.profile_layout);
        listView.setAdapter(editTextAdapter);


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
//                        BitmapFactory.Options options = new BitmapFactory.Options();
//                        // downsizing image as it throws OutOfMemory Exception for larger
//                        // images
//                        options.inSampleSize = 8;
//                        final Bitmap bitmap = BitmapFactory.decodeFile(selectedImageUri.getPath(), options);
                        if(currentImageView==imageProfile)
                        {
                        profile_picturePath = selectedImageUri.getPath();
                        Log.e("path :", "" + profile_picturePath);
                        decodeFile(profile_picturePath);
////            v.imageView.setImageDrawable(roundedImage);
//                        currentImageView.setImageBitmap(bitmap);

                        imageProfile.setImageBitmap(bitmap);}
                        if(currentImageView==imageCompany)
                        {
                            profile_picturePath = selectedImageUri.getPath();
                            Log.e("path :", "" + profile_picturePath);
                            decodeFile(profile_picturePath);
////            v.imageView.setImageDrawable(roundedImage);
//                        currentImageView.setImageBitmap(bitmap);

                            imageProfile.setImageBitmap(bitmap);}


                    } else {
                        Uri selectedImage = data.getData();
                        String[] filePathColumn = {MediaStore.Images.Media.DATA};

                        Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                                filePathColumn, null, null, null);
                        cursor.moveToFirst();

                        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

//                        BitmapFactory.Options options = new BitmapFactory.Options();
//                        // downsizing image as it throws OutOfMemory Exception for larger
//                        // images
//                        options.inSampleSize = 8;
//                        bitmap = BitmapFactory.decodeFile(picturePath, options);
//
//
                        if(currentImageView==imageProfile)
                        {  profile_picturePath = cursor.getString(columnIndex);
                            cursor.close();
                            Log.e("path :", "" + profile_picturePath);
                        decodeFile(profile_picturePath);
                        imageProfile.setImageBitmap(bitmap);}
                        if(currentImageView==imageCompany)
                        {  company_picturePath = cursor.getString(columnIndex);
                            cursor.close();
                            Log.e("path :", "" + company_picturePath);
                            decodeFile(company_picturePath);
                            imageCompany.setImageBitmap(bitmap);}

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

        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        bitmap = BitmapFactory.decodeFile(filePath, o2);


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



    /**
     * The object we have a list of, probably more complex in your app
     */
    static class ListItem {
        public String value;
        public String label;


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

    //    /**
//     * ViewHolder which also tracks the TextWatcher for an EditText
//     */
    static class ViewHolder {
        public TextView textView;
        public EditText editText;
        public TextWatcher textWatcher;
    }

    class EditTextAdapter extends BaseAdapter {
        ViewHolder holder = new ViewHolder();
        Context _c;

        EditTextAdapter(Context context, ArrayList<ListItem> groupItem, int resource) {

            this._c = context;
            MyProfile_Fragment.this.groupItem = groupItem;
        }

        @Override
        public int getCount() {
            return groupItem.size();
        }

        @Override
        public ListItem getItem(int i) {
            return groupItem.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View rowView = null;
            convertView = null;
            if (convertView == null) {
                // Not recycled, inflate a new view
                LayoutInflater li = (LayoutInflater) _c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                rowView = li.inflate(R.layout.profile_listitems, null);
//                rowView = getLayoutInflater().inflate(R.layout.profile_listitems, null);


                rowView.setTag(holder);
            }
            holder.textView = (TextView) rowView.findViewById(R.id.labels);
            holder.editText = (EditText) rowView.findViewById(R.id.values1);
            ViewHolder holder = (ViewHolder) rowView.getTag();
            // Remove any existing TextWatcher that will be keyed to the wrong ListItem
            if (holder.textWatcher != null)
                holder.editText.removeTextChangedListener(holder.textWatcher);

            final ListItem listItem = groupItem.get(position);

            // Keep a reference to the TextWatcher so that we can remove it later
            holder.textWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    listItem.value = s.toString();
                    System.out.println(listItem.value + "" + groupItem.get(position));

                }

                @Override
                public void afterTextChanged(Editable s) {
                }
            };
            holder.editText.addTextChangedListener(holder.textWatcher);

            holder.editText.setText(listItem.value);

            holder.textView.setText(listItem.getLabel().toString());
//              holder.editText.setEnabled(false);

            if (clickCount == 0) {
                actv(false);
            }
            return rowView;
        }

        protected void actv(final boolean active) {
            holder.editText.setEnabled(active);
            if (active) {
                holder.editText.requestFocus();

            }
        }
    }

}