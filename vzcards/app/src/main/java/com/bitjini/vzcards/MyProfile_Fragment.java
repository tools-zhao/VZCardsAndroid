package com.bitjini.vzcards;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import android.widget.RadioGroup.OnCheckedChangeListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by VEENA on 12/7/2015.
 */
public class MyProfile_Fragment extends Fragment implements View.OnClickListener {

    Context _c;
    Activity a;
    Button profilebtn,referral,vzfrnds;
    Button editbtn;

    private final int SELECT_PHOTO = 1;
    private ImageView imageProfile,imageCompany;

    private Uri outputFileUri;

    TextView mIndustry,mCompany,mEmail,mAddress;
    ImageView currentImageView = null;
    View profile;
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,
                             Bundle savedInstanceState) {

        profile = inflater.inflate(R.layout.profile_layout, container, false);

        editbtn=(Button)profile.findViewById(R.id.edit);

       mIndustry = (TextView)profile.findViewById(R.id.industry);
        mIndustry.setEnabled(false);
//        mCompany = (TextView)profile.findViewById(R.id.company);
//        mEmail = (TextView)profile.findViewById(R.id.email);
//        mAddress = (TextView)profile.findViewById(R.id.address);

        //Picking Profile picture

        imageProfile = (ImageView)profile.findViewById(R.id.profilePic);
        imageCompany = (ImageView) profile.findViewById(R.id.btn_pick);

        //image listeners
        imageCompany.setOnClickListener(this);
        imageProfile.setOnClickListener(this);


        profilebtn = (Button) profile.findViewById(R.id.profilebtn);
       referral = (Button) profile.findViewById(R.id.referralbtn);
       vzfrnds = (Button) profile.findViewById(R.id.vzfrnds);

     editbtn.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
             mIndustry.setEnabled(true);
//             mIndustry.setCursorVisible(true);
//             mIndustry.setFocusableInTouchMode(true);
//             mIndustry.setInputType(InputType.TYPE_CLASS_TEXT);
//             mIndustry.requestFocus(); //to trigger the soft input
             }

     });

        vzfrnds.setOnClickListener(this);
        referral.setOnClickListener(this);
        return profile;
    }



    private void openImageIntent(){

        // Determine Uri of camera image to save.
        final File root = new File(Environment.getExternalStorageDirectory() + File.separator + "amfb" + File.separator);
        root.mkdir();
        final String fname = "img_" + System.currentTimeMillis() + ".jpg";
        final File sdImageMainDirectory = new File(root, fname);
        outputFileUri = Uri.fromFile(sdImageMainDirectory);

        // Camera.
        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager =getActivity().getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam){
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
                    try {//Using Input Stream to get uri did the trick
                        InputStream input = getActivity().getContentResolver().openInputStream(selectedImageUri);
                        final Bitmap bitmap = BitmapFactory.decodeStream(input);
//                        imageProfile.setImageBitmap(bitmap);

                        currentImageView.setImageBitmap(bitmap);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        } else if (resultCode == getActivity().RESULT_CANCELED){
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
                currentImageView= (ImageView) v;
                openImageIntent();
                break;

            //setting company picture
            case R.id.btn_pick: currentImageView= (ImageView) v;
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
