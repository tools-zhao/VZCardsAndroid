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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.bitjini.vzcards.BaseURLs.URL_Cloudynary_Image_Path;
import static com.bitjini.vzcards.BaseURLs.URL_GET_PROFILE;
import static com.bitjini.vzcards.BaseURLs.URL_PROFILE_UPDATE;
import static com.bitjini.vzcards.Constants.CAMERA_CODE;
import static com.bitjini.vzcards.Constants.COMPANY_IMAGE;
import static com.bitjini.vzcards.Constants.CROPING_CODE;
import static com.bitjini.vzcards.Constants.GALLERY_CODE;
import static com.bitjini.vzcards.Constants.MY_PROFILE_PREFERENCES;
import static com.bitjini.vzcards.Constants.PERMISSIONS_READ_EXTERNAL_STORAGE;
import static com.bitjini.vzcards.Constants.PERMISSIONS_REQUEST_CAMERA;
import static com.bitjini.vzcards.Constants.PERMISSIONS_WRITE_EXTERNAL_STORAGE;
import static com.bitjini.vzcards.Constants.PROFILE_IMAGE;
import static com.bitjini.vzcards.Constants.TASKS;
import static com.bitjini.vzcards.Constants.is_organization_sharedPreference;
import static com.bitjini.vzcards.Constants.profileSharedPreference;
import static com.bitjini.vzcards.Constants.token_sharedPreference;

/**
 * Created by VEENA on 12/7/2015.
 */
public class MyProfile_Fragment extends Fragment implements View.OnClickListener {
 
    private Uri mImageCaptureUri;
    private File outPutFile = null;
    public ImageView imageProfile, imageCompany;
    public ImageView currentImageView = null;
    View profile;
    
    ArrayList<String> label=new ArrayList<>();
    ArrayList<String> values=new ArrayList<>();
    int clickCount = 0;
    //Declaring widgets
    Button editbtn,cancelBtn;
    RadioButton profilebtn, vzfrndsbtn, referralbtn;
    TextView textViewName;
    ArrayList<ListItem> arrayList = new ArrayList<ListItem>();
    Context c;
    public ProgressDialog progress,progress1,progressDialog1;
    ListView listView;
    EditTextAdapter editTextAdapter;
    EditTextAdapter frndEditTextAdapter;
    int width,height;
    ArrayList<ListItem> adapterArrayList = new ArrayList<ListItem>();
    public ArrayList<ListItem> groupItem = new ArrayList<ListItem>();
    RelativeLayout relativeLayout;
    VerifyScreen p = new VerifyScreen();
    Bitmap bm = null;
    String json, json2,json3;
    public   String  firstname = "", lastname = "", email = "", industry = "", company = "", address_line_1 = "", address_line_2 = "",
            city="", pin_code = "",title="";
    public static String photo="",company_photo="";
    public Bitmap bitmap;
    public static String picturePath;
    public String profilePicturePath="",companyPicturePath="";
    ListView listView2;
    MyAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
// on configuration changes (screen rotation) we want fragment member variables to preserved
        setRetainInstance(true);
        profile = inflater.inflate(R.layout.profile_layout, container, false);

        initViews();
        initListener();

        GetSharedPreference.getSharePreferenceValue(getActivity());// get data from sharedpreference

        String details = profileSharedPreference.getString(TASKS, null);
        if (details != null) {
            LoadPreferences();
        }
        getDensity();
        addArrayOfLabels();

        // Making http get request to load profile details
        getProfileDetails();


        editbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!GetSharedPreference.isOrganisation()) {
                    switch (clickCount) {
                        case 0:
                            EditUserDetails();
                            break;
                        case 1:
                            saveUserDetails();
                            break;
                    }

                } else {
                    switch (clickCount) {
                        case 0:
                            ChangeLayoutParametersOnEdit();
                            currentImageView = imageProfile;
                            selectImageOption();
                            break;
                        case 1:
                            ChangeLayoutParametersOnSave();

                            if(profilePicturePath!=null)
                            {
                                Log.e("profilePicturePath= ",""+ profilePicturePath);
                                UploadProfileImage();
                            }

                            break;
                    }

                }
            }
        });
        if (!groupItem.isEmpty())
            json2 = new Gson().toJson(groupItem);// updated array
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

    private void ChangeLayoutParametersOnSave() {
        editbtn.setText("Edit");
        RelativeLayout.LayoutParams paramImage5 = new RelativeLayout.LayoutParams(width/2, width/6);
        paramImage5.leftMargin=width/2;
        paramImage5.topMargin=((width/2)-(width/6));
        editbtn.setLayoutParams(paramImage5);
        editbtn.setBackgroundResource(R.color.primary);
        cancelBtn.setVisibility(View.GONE);
        clickCount=0;


    }

    private void ChangeLayoutParametersOnEdit() {
        editbtn.setText("Save");
        editbtn.setBackgroundResource(R.color.primaryGreen);
        cancelBtn.setVisibility(View.VISIBLE);
        RelativeLayout.LayoutParams paramImage3 = new RelativeLayout.LayoutParams(width/4, width/6);
        paramImage3.leftMargin=(3*(width/4));
        paramImage3.topMargin=((width/2)-(width/6));
        editbtn.setLayoutParams(paramImage3);
        RelativeLayout.LayoutParams paramImage4 = new RelativeLayout.LayoutParams(width/4, width/6);
        paramImage4.leftMargin=width/2;
        paramImage4.topMargin=((width/2)-(width/6));
        cancelBtn.setLayoutParams(paramImage4);

        clickCount=1;
    }


    private void saveUserDetails() {
        relativeLayout.setBackgroundResource(R.color.white);
        // to hide keypad
        InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (getActivity().getCurrentFocus() != null){
            inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        ChangeLayoutParametersOnSave();
        imageCompany.setClickable(false);
        imageProfile.setClickable(false);

//                    editTextAdapter.actv(false);
        json2 = new Gson().toJson(groupItem);// updated array


        Log.e("arr",""+json2);

        json3=profileSharedPreference.getString(TASKS, null);

        SavePreferences(TASKS, json2);
        // check if any changes done if yes make an api call
        assert json3 != null;
        if(json3.equals(json2) && profilePicturePath.length() == 0 && companyPicturePath.length() == 0 ) {
        }else {
            if (profilePicturePath.length() != 0 || companyPicturePath.length() != 0) {
                if (profilePicturePath.length() != 0) {
                    UploadProfileImage();
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
        adapter.notifyDataSetChanged();
        LoadPreferences();

        companyPicturePath="";profilePicturePath="";

        listView2.setVisibility(View.VISIBLE);
        listView.setVisibility(View.GONE);
    }

    private void UploadProfileImage() {
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
                    profilePicturePath="";
                }
                try
                { if(result!=null) {
                    JSONObject json = new JSONObject(result);
                    photo = URL_Cloudynary_Image_Path;
                    String link = json.getString("link");
                    SavePreferences(PROFILE_IMAGE, photo + link);
                    Log.e("photo :", "" + photo + link);
                    if(companyPicturePath.length()==0) {
                        // calling profile post details
                        new Profile_POST_Details(getActivity()).execute(URL_PROFILE_UPDATE);
                        Toast.makeText(getActivity(), "Profile is updated ", Toast.LENGTH_LONG).show();
                        if (!json2.equals(json3)) {
                            getProfileDetails();
                        }
                    }else {
                        uploadCompanyImage();
                    }
                }
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        }.execute(profilePicturePath);
    }

    private void EditUserDetails() {

        ChangeLayoutParametersOnEdit();

        listView.setVisibility(View.VISIBLE);
        listView2.setVisibility(View.GONE);



        imageCompany.setClickable(true);
        imageProfile.setClickable(true);
        relativeLayout.setBackgroundResource(R.color.disabled_blue);
//                    editTextAdapter.actv(true);
        editTextAdapter.notifyDataSetChanged();
        adapter.notifyDataSetChanged();

    }

    private void addArrayOfLabels() {
        label = new ArrayList<String>();
        label.add("Firstname");
        label.add("Lastname");
        label.add("What do you do?");
        label.add("Email");
        label.add("Address");
        label.add("City");
        label.add("Pin code");
    }

    private void getDensity() {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        width = metrics.widthPixels;
        int density = metrics.densityDpi;
        Log.e("width=",""+width);
        height = metrics.heightPixels;
        RelativeLayout.LayoutParams paramImage = new RelativeLayout.LayoutParams(width/2,width/2);
        imageProfile.setLayoutParams(paramImage);
        RelativeLayout.LayoutParams textParams = new RelativeLayout.LayoutParams(width/2,43);
        if(density==480) {
            textParams = new RelativeLayout.LayoutParams(width / 2, 64);
            textParams.topMargin = ((width / 2) - 64);
        }else if(density==240) {
            textParams = new RelativeLayout.LayoutParams(width / 2, 34);
            textParams.topMargin = ((width / 2) - 34);
        }else
        {
            textParams = new RelativeLayout.LayoutParams(width / 2, 45);
            textParams.topMargin = ((width / 2) - 45);
        }

        Log.e("width=",""+width/2);
        textViewName.setTextColor(Color.WHITE);
        textViewName.setLayoutParams(textParams);

        RelativeLayout.LayoutParams paramImage2 = new RelativeLayout.LayoutParams(width/2, width/3);
        paramImage2.leftMargin=width/2;
        imageCompany.setLayoutParams(paramImage2);
        imageCompany.setScaleType(ImageView.ScaleType.CENTER_CROP);

        RelativeLayout.LayoutParams paramImage3 = new RelativeLayout.LayoutParams(width/2, width/6);
        paramImage3.leftMargin=width/2;
        paramImage3.topMargin=((width/2)-(width/6));
        editbtn.setLayoutParams(paramImage3);
        editbtn.setBackgroundResource(R.color.primary);
    }

    private void initListener() {
        //image listeners
        imageCompany.setOnClickListener(this);
        imageProfile.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
        imageCompany.setClickable(false);
        imageProfile.setClickable(false);

    }

    private void initViews() {
        relativeLayout=(RelativeLayout) profile. findViewById(R.id.rl);
        editbtn = (Button) profile.findViewById(R.id.edit);
        cancelBtn=(Button) profile.findViewById(R.id.cancel);
        cancelBtn.setVisibility(View.GONE);
        listView = (ListView) profile.findViewById(R.id.profileList);
        listView2=(ListView)profile.findViewById(R.id.profileList2);

        relativeLayout.setBackgroundResource(R.color.white);
//        listView.setOnScrollListener(new MyScrollListener());

        textViewName = (TextView) profile.findViewById(R.id.name);
        //Picking Profile picture
        imageProfile = (ImageView) profile.findViewById(R.id.profilePic);
        imageCompany = (ImageView) profile.findViewById(R.id.btn_pick);

        profilebtn = (RadioButton) profile.findViewById(R.id.profilebtn);
        referralbtn = (RadioButton) profile.findViewById(R.id.referralbtn);
        vzfrndsbtn = (RadioButton) profile.findViewById(R.id.vzfrnds);

        profilebtn.setChecked(true);
        vzfrndsbtn.setChecked(false);
        referralbtn.setChecked(false);
        listView2.setVisibility(View.VISIBLE);
    }

    public void uploadCompanyImage()
    {
//                                                String res = new UploadImageTask(getActivity()).execute().get();
        progressDialog1 = new ProgressDialog(getActivity());
        if (progressDialog1 != null) {
            progressDialog1.setMessage("Saving user details...");
            progressDialog1.setCancelable(false);
            progressDialog1.show();
        }
        new UploadImageTask(getActivity()) {
            @Override
            public void onPostExecute(String result) {
                if (progressDialog1.isShowing() && progressDialog1!=null) {
                    progressDialog1.dismiss();
                    progressDialog1 = null;
                    companyPicturePath="";
//                                            File f = new File(outPutFile.getPath());
//
//                                            if (f.exists()) f.delete();
                }
//                File f = new File(companyPicturePath);
//
//                if (f.exists()) f.delete();
                try {
                    if(result!=null) {
                        JSONObject json = new JSONObject(result);
//                                                            company_photo = json.getString("photo");
                        company_photo =URL_Cloudynary_Image_Path;
                        String link = json.getString("link");
                        SavePreferences(COMPANY_IMAGE, company_photo + link);
                        Log.e("link :", "" + link);
                        new Profile_POST_Details(getActivity()).execute(URL_PROFILE_UPDATE);
                        Toast.makeText(getActivity(), "Profile is updated ", Toast.LENGTH_LONG).show();
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


            receivedData = new Get_Profile_AsyncTask().execute(URL_GET_PROFILE + token_sharedPreference).get();//cal to get profile data

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


            Picasso.with(getActivity()).load(photo).centerCrop().resize(400,400).placeholder(R.drawable.profile_pic_placeholder).into(imageProfile);

            Log.e(" Photo on Received ",""+photo);

//            if(PROFILE_IMAGE.length()==0) {
            SavePreferences(PROFILE_IMAGE, photo);
//            }
//            imageProfile.setTag(photo);
//                    new DownloadImagesTask(getActivity()).execute(imageProfile);// Download item_photo from AsynTask

        } else  {
            imageProfile.setImageResource(R.drawable.profile_pic_placeholder);
            imageProfile.setCropToPadding(true);
            imageProfile.setPadding(50,50,50,50);
            //            new DownloadImagesTask(getActivity()).execute(holder.photo);

        }



        if(!company_photo.isEmpty()) {
            Picasso.with(getActivity()).load(company_photo).centerCrop().resize(200,200).placeholder(R.drawable.no_pic_placeholder_2).into(imageCompany);
            Log.e(" company_photoReceived",""+company_photo);

            SavePreferences(COMPANY_IMAGE, company_photo);

//            imageCompany.setTag(company_photo);
//            new DownloadImagesTask(getActivity()).execute(imageCompany);// Download item_photo from AsynTask
        }else  {
            imageCompany.setImageResource(R.drawable.no_pic_placeholder_2);
            imageCompany.setCropToPadding(true);
            imageProfile.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            imageCompany.setPadding(50,50,50,50);
        }


        textViewName.setText(firstname+ " "+lastname);
        values = new ArrayList<String>();
        values.add(firstname );
        values.add(lastname);
        values.add(title);  // contains value for what do you do?
        values.add(email);
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
         SharedPreferences.Editor editor = profileSharedPreference.edit();
        editor.putString(key, value);
        editor.commit();
    }

    // To retrive saved values in shared preference Now convert the JSON string back to your java object
    protected void LoadPreferences() {
        Gson gson = new Gson();
        String json = profileSharedPreference.getString(TASKS, null);

        Log.e("Load json shared prefs ", "" + json);
        Type type = new TypeToken<ArrayList<ListItem>>() {
        }.getType();
        adapterArrayList = gson.fromJson(json, type);
        // send the adapterArraylist to the adapter and set it to listview
        if(clickCount==0) {
            editTextAdapter = new EditTextAdapter(getActivity(), adapterArrayList, R.layout.profile_layout);
            listView.setAdapter(editTextAdapter);
        }

        try { JSONArray jsonArray = new JSONArray(json);

            firstname = jsonArray.getJSONObject(0).getString("value");
            lastname = jsonArray.getJSONObject(1).getString("value");
            title = jsonArray.getJSONObject(2).getString("value");
            email = jsonArray.getJSONObject(3).getString("value");
            address_line_1 = jsonArray.getJSONObject(4).getString("value");
            city = jsonArray.getJSONObject(5).getString("value");
            pin_code = jsonArray.getJSONObject(6).getString("value");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        values = new ArrayList<String>();
        values.add(firstname + " " + lastname);
//        values.add(lastname);
        if (!title.isEmpty())
            values.add(title);  // contains value for what do you do?

        if (!email.isEmpty())
            values.add(email);

        if (!address_line_1.isEmpty())
            values.add(address_line_1);

        if (!city.isEmpty())
            values.add(city);

        if (!pin_code.isEmpty())
            values.add(pin_code);

        addValues(values);


    }
    public void addValues(ArrayList<String> values) {
        ArrayList<ListItem> arr=new ArrayList<>();
        for (int i = 0; i < values.size(); i++) {
       ListItem item = new ListItem();
//            item.setLabel(label.get(i));
            item.setValue(values.get(i));
            arr.add(item);
        }


        // send the adapterArraylist to the adapter and set it to listview
         adapter=new MyAdapter(arr,getActivity(),R.layout.profile_layout);
        listView2.setAdapter(adapter);


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
    public void onActivityResult(int requestCode, int resultCode, Intent profileSharedPreference) {
        super.onActivityResult(requestCode, resultCode, profileSharedPreference);
        if (resultCode == getActivity().RESULT_OK) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                getActivity().requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_READ_EXTERNAL_STORAGE);
            } else {
                if (requestCode == GALLERY_CODE && resultCode == Activity.RESULT_OK && profileSharedPreference != null) {
                    String fname = "img_" + System.currentTimeMillis() + ".jpg";
                    outPutFile = new File(android.os.Environment.getExternalStorageDirectory(), fname);
                    mImageCaptureUri = profileSharedPreference.getData();
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
            //TODO: don't use return-profileSharedPreference tag because it's not return large image profileSharedPreference and crash not given any message
//            intent.putExtra("return-profileSharedPreference", true);
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
                Fragment newfragment1 = new MyProfile_Fragment();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction()
                        .replace(R.id.profile_frame, newfragment1)
                        .commit();
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
                FragmentManager fragmentManager2 = getFragmentManager();
                fragmentManager2.beginTransaction()
                        .replace(R.id.profile_frame, fragment)
                        .commit();
                break;
            default:
                break;
        }
    }

    //    /**
//     * ViewHolder which also tracks the TextWatcher for an EditText
//     */
    private static class ViewHolder {
        TextView textView;
        EditText editText;
        TextWatcher textWatcher;
    }
    // custom adapter class
    class EditTextAdapter extends BaseAdapter {
        ArrayList<String> arr=new ArrayList<>();
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
                    rowView = li.inflate(R.layout.profile_listitems, parent, false);
                    rowView.setTag(holder);
                }
                holder.textView = (TextView) rowView.findViewById(R.id.labels);
                holder.editText = (EditText) rowView.findViewById(R.id.values1);
                ViewParent parent2 = holder.editText.getParent();
                parent.clearChildFocus(holder.editText);
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
                Log.e("val=", "" + listItem.value);
                holder.textView.setText(listItem.getLabel());

                return rowView;
            }

    }
}
