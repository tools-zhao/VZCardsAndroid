package com.bitjini.vzcards;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by VEENA on 12/7/2015.
 */
public class MyProfile_Fragment extends Fragment implements View.OnClickListener {

    public static final String URL_PROFILE_UPDATE = "http://staging-vzcards-api.herokuapp.com/my_profile/update/?access_token=";
    public static final String URL_GET_PROFILE = "http://staging-vzcards-api.herokuapp.com/my_profile/?access_token=";
    public static final String URL_UPLOAD_IMAGE = "http://staging-vzcards-api.herokuapp.com/upload_image/?access_token=gWgLsmgEafve3TEUewVf26rh9tuq69";
    public static final String MY_PROFILE_PREFERENCES = "mypref.txt";
    public static final String PROFILE_IMAGE="profile";
    public static final String COMPANY_IMAGE="company";

    private static final int PERMISSIONS_REQUEST_CAMERA =197;
    private static final int PERMISSIONS_WRITE_EXTERNAL_STORAGE = 198;
    private static final int PERMISSIONS_READ_EXTERNAL_STORAGE = 199;

    private static final int CAMERA_CODE = 101, GALLERY_CODE = 201, CROPING_CODE = 301;

    private Button btn_select_image;
    private Uri mImageCaptureUri;
    private File outPutFile = null;

    public static final String TASKS = "key";

    private final int SELECT_PHOTO = 1;

    public ImageView imageProfile, imageCompany;
    private Uri outputFileUri;
    public ImageView currentImageView = null;
    View profile;
    SharedPreferences data;

    ArrayList<String> label;
    ArrayList<String> values;
    public Bitmap output;
    int clickCount = 0;
    //Declaring widgets
    Button editbtn,cancelBtn;
    RadioButton profilebtn, vzfrndsbtn, referralbtn;
    TextView textViewName;

    Context c;
    public ProgressDialog progress,progress1,progressDialog1;

    ListView listView;
    EditTextAdapter editTextAdapter;

    ArrayList<ListItem> arrayList = new ArrayList<ListItem>();
    ArrayList<ListItem> adapterArrayList = new ArrayList<ListItem>();
    public ArrayList<ListItem> groupItem = new ArrayList<ListItem>();

    LinearLayout linearLayout;
    VerifyScreen p = new VerifyScreen();
    Bitmap bm = null;
    String json, json2,json3;
    public   String  firstname = "", lastname = "", email = "", industry = "", company = "", address_line_1 = "", address_line_2 = "",
            city, pin_code = "",title="";
    public static String photo="",company_photo="";
    public Bitmap bitmap;
    public static String picturePath;
    public String profilePicturePath="",companyPicturePath="";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
// on configuration changes (screen rotation) we want fragment member variables to preserved
        setRetainInstance(true);
        profile = inflater.inflate(R.layout.profile_layout, container, false);
        TextView textView=(TextView)profile.findViewById(R.id.emptytext);
        textView.setText("");
        textView.setVisibility(View.GONE);
        linearLayout=(LinearLayout)profile. findViewById(R.id.l2);

        editbtn = (Button) profile.findViewById(R.id.edit);
        cancelBtn=(Button) profile.findViewById(R.id.cancel);
        cancelBtn.setVisibility(View.GONE);
        listView = (ListView) profile.findViewById(R.id.profileList);
//        listView.setOnScrollListener(new MyScrollListener());
        p.sharedPreferences = getActivity().getSharedPreferences(p.VZCARD_PREFS, 0);
        p.token_sharedPreference = p.sharedPreferences.getString(p.TOKEN_KEY, null);
        p.phone_sharedPreference = p.sharedPreferences.getString(p.PHONE_KEY, null);
        p.vz_id_sharedPreference = p.sharedPreferences.getString(p.VZ_ID_KEY, null);


        textViewName = (TextView) profile.findViewById(R.id.name);
        //Picking Profile picture
        imageProfile = (ImageView) profile.findViewById(R.id.profilePic);
        imageCompany = (ImageView) profile.findViewById(R.id.btn_pick);

        //image listeners
        imageCompany.setOnClickListener(this);
        imageProfile.setOnClickListener(this);

        cancelBtn.setOnClickListener(this);

        imageCompany.setClickable(false);
        imageProfile.setClickable(false);

        profilebtn = (RadioButton) profile.findViewById(R.id.profilebtn);
        referralbtn = (RadioButton) profile.findViewById(R.id.referralbtn);
        vzfrndsbtn = (RadioButton) profile.findViewById(R.id.vzfrnds);

        profilebtn.setChecked(true);
        vzfrndsbtn.setChecked(false);
        referralbtn.setChecked(false);


        data = getActivity().getSharedPreferences(MY_PROFILE_PREFERENCES, 0);
        String details = data.getString(TASKS, null);
        if (details != null) {

            LoadPreferences();
        }
        label = new ArrayList<String>();
        label.add("Firstname");
        label.add("Lastname");
        label.add("title");
        label.add("Email");
        label.add("Phone");
        label.add("What do you do?");
        label.add("Company");
        label.add("Address");
        label.add("City");
        label.add("Pin_code");

        // Making http get request to load profile details

        getProfileDetails();


        editbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (clickCount == 0) {

                    editbtn.setText("Save");
                    editbtn.setBackgroundResource(R.drawable.addimage);
                    cancelBtn.setVisibility(View.VISIBLE);
                    imageCompany.setClickable(true);
                    imageProfile.setClickable(true);

                    editTextAdapter.actv(true);
                    editTextAdapter.notifyDataSetChanged();


                    clickCount = 1;

                } else if (clickCount == 1) {
                    // to hide keypad
                    InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (getActivity().getCurrentFocus() != null){
                        inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    }
                    editbtn.setText("Edit");
                    editbtn.setBackgroundResource(R.drawable.drawable_edit);
                    cancelBtn.setVisibility(View.GONE);
                    imageCompany.setClickable(false);
                    imageProfile.setClickable(false);

                    editTextAdapter.actv(false);
                    json2 = new Gson().toJson(groupItem);// updated array

                    data = getActivity().getSharedPreferences(MY_PROFILE_PREFERENCES, 0);
                    json3=data.getString(TASKS, null);

                    SavePreferences(TASKS, json2);
                    // check if any changes done if yes make an api call

                    assert json3 != null;
                    if(json3.equals(json2) && profilePicturePath.length() == 0 && companyPicturePath.length() == 0 ) {
                    }else {
                        if (profilePicturePath.length() != 0 || companyPicturePath.length() != 0) {
                            if (profilePicturePath.length() != 0) {

                                    picturePath = profilePicturePath;
                                   progress = new ProgressDialog(getActivity());
                                    if (progress != null) {
                                        progress.setMessage("Saving user details...");
                                        progress.setCancelable(false);
                                        progress.show();

                                    }
//                                            String result = new UploadImageTask(getActivity()).execute().get();
                                new UploadImageTask(getActivity()) {

                                    @Override
                                    public void onPostExecute(String result) {
                                        if (progress.isShowing() && progress!=null) {
                                            progress.dismiss();
                                            progress = null;
//                                            profilePicturePath="";
                                            File f = new File(outPutFile.getPath());

                                            if (f.exists()) f.delete();

                                        }

                                        try
                                        { if(result!=null) {
                                            JSONObject json = new JSONObject(result);
                                            photo = "http://res.cloudinary.com/harnesymz/image/upload/vzcards/";

                                            String link = json.getString("link");
                                            SavePreferences(PROFILE_IMAGE, photo + link);
                                            Log.e("photo :", "" + photo + link);
                                            if(companyPicturePath.length()==0) {
                                                new Profile_POST_Details(getActivity()).execute(URL_PROFILE_UPDATE);
                                                if (!json2.equals(json3)) {
                                                    getProfileDetails();
                                                }
                                                else {
                                                    uploadCompanyImage();
                                                }
                                            }
                                        }

                                        } catch (JSONException e)
                                        {
                                            e.printStackTrace();
                                        }
                                    }

                                }.execute(profilePicturePath);


                            }
                            if (companyPicturePath.length() != 0) {

//                                    picturePath = companyPicturePath;
                                uploadCompanyImage();


                            }


//
                        } else {

                            progress1 = new ProgressDialog(getActivity());
                            if (progress1 != null) {
                                progress1.setMessage("Saving user details...");
                                progress1.setCancelable(false);
                                progress1.show();

                            }
                            new Profile_POST_Details(getActivity()) {
                                @Override
                                public void onPostExecute(String result) {
                                    if (progress1.isShowing() && progress1!=null) {
                                        progress1.dismiss();
                                        progress1 = null;}
                                    Toast.makeText(getActivity(), "Profile is updated ", Toast.LENGTH_LONG).show();
                                    getProfileDetails();
                                }
                            }.execute(URL_PROFILE_UPDATE);


                        }
                    }
                    editTextAdapter.notifyDataSetChanged();


                    clickCount = 0;
                    companyPicturePath="";profilePicturePath="";


                }

            }
        });

        if (!groupItem.isEmpty())
            json2 = new Gson().toJson(groupItem);// updated array
//        Log.e("updated array", "" + json2);
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

        // on configuration changes (screen rotation) we want fragment member variables to preserved
        setRetainInstance(true);
        return profile;
    }

    public void uploadCompanyImage()
    {

//                                                String res = new UploadImageTask(getActivity()).execute().get();
           new UploadImageTask(getActivity()) {

            @Override
            public void onPostExecute(String result) {

                File f = new File(companyPicturePath);

                if (f.exists()) f.delete();
                try {
                    if(result!=null) {
                        JSONObject json = new JSONObject(result);
//                                                            company_photo = json.getString("photo");
                        company_photo = "http://res.cloudinary.com/harnesymz/image/upload/vzcards/";

                        String link = json.getString("link");
                        SavePreferences(COMPANY_IMAGE, company_photo + link);

                        Log.e("link :", "" + link);

                            new Profile_POST_Details(getActivity()).execute(URL_PROFILE_UPDATE);
                            if (!json2.equals(json3)) {
                                getProfileDetails();

                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }.execute(companyPicturePath);
    }
    public void getProfileDetails()
    {
        try {
            String receivedData = null;


                receivedData = new Get_Profile_AsyncTask().execute(URL_GET_PROFILE + p.token_sharedPreference).get();//cal to get profile data

            //Profile details
            if(receivedData!=null) {
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
                title = jsonObj.getString("title");
                photo= jsonObj.getString("photo");
                company_photo=jsonObj.getString("company_photo");

            }


        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        Log.e(" Photo Received ",""+photo);
        Log.e(" company_photoReceived",""+company_photo);

//


        if(!photo.isEmpty()) {

            Picasso.with(getActivity()).load(photo).centerCrop().resize(200,200).into(target);
            Picasso.with(getActivity()).load(photo).centerCrop().resize(400,400).placeholder(R.drawable.profile_pic_placeholder).into(imageProfile);

            Log.e(" Photo on Received ",""+photo);

//            if(PROFILE_IMAGE.length()==0) {
            SavePreferences(PROFILE_IMAGE, photo);
//            }
//            imageProfile.setTag(photo);
//                    new DownloadImagesTask(getActivity()).execute(imageProfile);// Download item_photo from AsynTask

        } else  {
            imageProfile.setImageResource(R.drawable.profile_pic_placeholder);
            //            new DownloadImagesTask(getActivity()).execute(holder.photo);

        }



        if(!company_photo.isEmpty()) {
            Picasso.with(getActivity()).load(company_photo).centerCrop().resize(200,200).into(imageCompany);
            Log.e(" company_photoReceived",""+company_photo);

            SavePreferences(COMPANY_IMAGE, company_photo);

//            imageCompany.setTag(company_photo);
//            new DownloadImagesTask(getActivity()).execute(imageCompany);// Download item_photo from AsynTask
        }else  {
            imageCompany.setImageResource(R.drawable.com_logo);

        }


        textViewName.setText(firstname+ " "+lastname);
        values = new ArrayList<String>();
        values.add(firstname);
        values.add(lastname);
        values.add(title);
        values.add(email);
        values.add(p.phone_sharedPreference);
        values.add(industry);
        values.add(company);
        values.add(address_line_1);
        values.add(city);
        values.add(pin_code);


        for (int i = 0; i < label.size(); i++) {
            ListItem item = new ListItem();
            item.setLabel(label.get(i));
            item.setValue(values.get(i));
            arrayList.add(item);
        }

    }
    protected void SavePreferences(String key, String value) {
// TODO Auto-generated method stub
        data = getActivity().getSharedPreferences(MY_PROFILE_PREFERENCES, 0);
        SharedPreferences.Editor editor = data.edit();
        editor.putString(key, value);

        editor.commit();


    }

    private Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

//             imageProfile.setImageBitmap(bitmap);


            Bitmap blurredBitmap = BlurBuilder.blur(getActivity(), bitmap);

            linearLayout.setBackground(new BitmapDrawable(getResources(), blurredBitmap));

        }

        @Override
        public void onBitmapFailed(Drawable drawable) {

        }

        @Override
        public void onPrepareLoad(Drawable drawable) {

        }
    };

        // To retrive saved values in shared preference Now convert the JSON string back to your java object

    protected void LoadPreferences() {

        data = getActivity().getSharedPreferences(MY_PROFILE_PREFERENCES, 0);
        Gson gson = new Gson();
        String json = data.getString(TASKS, null);
//        Log.e("Load json shared prefs ", "" + json);

        Type type = new TypeToken<ArrayList<ListItem>>() {
        }.getType();
        adapterArrayList = gson.fromJson(json, type);

        // send the adapterArraylist to the adapter and set it to listview
        editTextAdapter = new EditTextAdapter(getActivity(), adapterArrayList, R.layout.profile_layout);
        listView.setAdapter(editTextAdapter);


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
//                            File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp1.jpg");
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
                    String fname = "img_" + System.currentTimeMillis() + ".jpg";
                    outPutFile = new File(android.os.Environment.getExternalStorageDirectory(), fname);

                    mImageCaptureUri = data.getData();
                    System.out.println("Gallery Image URI : " + mImageCaptureUri);
                    CropingIMG();

                } else if (requestCode == CAMERA_CODE && resultCode == Activity.RESULT_OK) {
                    String fname = "img_" + System.currentTimeMillis() + ".jpg";
                    outPutFile = new File(android.os.Environment.getExternalStorageDirectory(), fname);

                    System.out.println("Camera Image URI : " + mImageCaptureUri);
                    CropingIMG();
                } else if (requestCode == CROPING_CODE) {

                    try {
                        if (currentImageView == imageProfile) {


                            if (outPutFile.exists()) {
                                Bitmap photo = decodeFile(outPutFile);
                                profilePicturePath = outPutFile.getPath();
                                Log.e("profilePicturePath :", "" + profilePicturePath);
                                imageProfile.setImageBitmap(photo);
                                Bitmap blurredBitmap = BlurBuilder.blur(getActivity(), photo);

                                linearLayout.setBackgroundDrawable(new BitmapDrawable(getResources(), blurredBitmap));
                            } else {
                                Toast.makeText(getActivity(), "Error while save image", Toast.LENGTH_SHORT).show();
                            }
                        }
                        if (currentImageView == imageCompany) {

                            if (outPutFile.exists()) {
                                Bitmap photo = decodeFile(outPutFile);
                                companyPicturePath = outPutFile.getPath();
                                Log.e("companyPicturePath :", "" + companyPicturePath);
                                imageCompany.setImageBitmap(photo);
                            } else {
                                Toast.makeText(getActivity(), "Error while save image", Toast.LENGTH_SHORT).show();
                            }
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
                Toast.makeText(getActivity(), "Until you grant the permission,names cannot be displayed", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == PERMISSIONS_WRITE_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                selectImageOption();
            } else {
                Toast.makeText(getActivity(), "Until you grant the permission, names cannot be displayed", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == PERMISSIONS_READ_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                selectImageOption();
            } else {
                Toast.makeText(getActivity(), "Until you grant the permission,names cannot be displayed", Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void decodeFile(String filePath) {
        // First decode with inJustDecodeBounds=true to check dimensions
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, o);

        // The new size we want to scale to
        final int REQUIRED_SIZE = 1024;
        // Calculate inSampleSize
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

        // Decode bitmap with inSampleSize set
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        bitmap = BitmapFactory.decodeFile(filePath, o2);
//        getRoundedCornerBitmap(bitmap, 100);

    }

    public void onClick(View v) {
        switch (v.getId()) {

            //setting profile picture
            case R.id.profilePic:
                currentImageView = (ImageView) v;
                selectImageOption();

                break;

            //setting company picture
            case R.id.btn_pick:
                currentImageView = (ImageView) v;
                selectImageOption();

                break;

            case R.id.cancel:
                // to hide keypad

//                setRetainInstance(true);
                Fragment newfragment1 = new MyProfile_Fragment();
                // get the id of fragment
//                FrameLayout contentView1 = (FrameLayout) getActivity().findViewById(R.id.profile_frame);

                // Insert the fragment by replacing any existing fragment
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.profile_frame, newfragment1)
                        .commit();

//               gment image is blank
                break;
                //redirecting to VZFriends_Fragment
            case R.id.vzfrnds:
                Fragment newfragment = new VZFriends_Fragment();
                // get the id of fragment
//                FrameLayout contentView2 = (FrameLayout) getActivity().findViewById(R.id.profile_frame);

                // Insert the fragment by replacing any existing fragment
                FragmentManager fragmentManager1 = getFragmentManager();
                fragmentManager1.beginTransaction()
                        .replace(R.id.profile_frame, newfragment)
                        .commit();
                break;

            //redirecting to Referral_Fragmen
            case R.id.referralbtn:
                Fragment fragment = new Referral_Fragment();
                // get the id of fragment
//                FrameLayout contentView3 = (FrameLayout) getActivity().findViewById(R.id.profile_frame);

                // Insert the fragment by replacing any existing fragment
                FragmentManager fragmentManager2 = getFragmentManager();
                fragmentManager2.beginTransaction()
                        .replace(R.id.profile_frame, fragment)
                        .commit();


                break;
            default:
                break;

        }
    }



    /**
     * The object we have a list of
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

    // custom adapter class
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
                rowView = li.inflate(R.layout.profile_listitems, parent,false);

                rowView.setTag(holder);
            }
            holder.textView = (TextView) rowView.findViewById(R.id.labels);
            holder.editText = (EditText) rowView.findViewById(R.id.values1);
            ViewParent parent2 =  holder.editText .getParent();
            parent.clearChildFocus(  holder.editText );
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