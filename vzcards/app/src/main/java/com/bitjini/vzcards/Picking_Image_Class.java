package com.bitjini.vzcards;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by bitjini on 25/3/16.
 */
public class Picking_Image_Class {
    private final int SELECT_PHOTO = 1;
    Context c;
    Uri outputFileUri;
    public Bitmap bitmap=null;
    public static String picturePath;
    MyProfile_Fragment m=new MyProfile_Fragment();
    public Picking_Image_Class() {
    }

    public void openImageIntent() {

        // Determine Uri of camera image to save.
        final File root = new File(Environment.getExternalStorageDirectory() + File.separator + "amfb" + File.separator);
        root.mkdir();
        final String fname = "img_" + System.currentTimeMillis() + ".jpg";
        final File sdImageMainDirectory = new File(root, fname);
        outputFileUri = Uri.fromFile(sdImageMainDirectory);

        // Camera.
        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager;
        if ((packageManager = c.getPackageManager())== null) {
            //Do Something
        }else{
            if (captureIntent.resolveActivity(c.getPackageManager()) != null) {

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
                ((Activity) c).startActivityForResult(chooserIntent, SELECT_PHOTO);
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == 100) {
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
                    picturePath=selectedImageUri.getPath();
                    Log.e("path :", "" + picturePath);
                    decodeFile(picturePath);
////            v.imageView.setImageDrawable(roundedImage);
//                        currentImageView.setImageBitmap(bitmap);
                    m.currentImageView.setImageBitmap(bitmap);

                } else {
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = c.getContentResolver().query(selectedImage,
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    picturePath = cursor.getString(columnIndex);
                    cursor.close();
                    Log.e("path :", "" + picturePath);
//                        BitmapFactory.Options options = new BitmapFactory.Options();
//                        // downsizing image as it throws OutOfMemory Exception for larger
//                        // images
//                        options.inSampleSize = 8;
//                        bitmap = BitmapFactory.decodeFile(picturePath, options);
//
//
//                        currentImageView.setImageBitmap(bitmap);

                    decodeFile(picturePath);
                    m.currentImageView.setImageBitmap(bitmap);

                }

            }
//            else if (resultCode ==c.RESULT_CANCELED) {
//                // user cancelled Image capture
//                Toast.makeText(c,
//                        "User cancelled image capture", Toast.LENGTH_SHORT)
//                        .show();
//            }
            else {
                // failed to capture image
                Toast.makeText(c,
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
}
