package com.bitjini.vzcards;

import android.content.SharedPreferences;

/**
 * Created by bitjini on 25/1/17.
 */

public class Constants {
    public static String token_sharedPreference;
    public static String phone_sharedPreference;
    public static String vz_id_sharedPreference;
    public static String is_organization_sharedPreference;
    public static boolean isFetched_sharedPreference;

    public static String VZCARD_PREFS = "MySharedPref";
    public static SharedPreferences sharedPreferences;

    public static String TOKEN_KEY="token";
    public static String VZ_ID_KEY="vz_id";
    public static String IS_ORGANIZATION_KEY="is_organization";
    public static String PHONE_KEY="phone";


    public static int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    public static int PERMISSIONS_REQUEST_CALL_CONTACTS = 100;

    public static int PERMISSIONS_REQUEST_CAMERA = 105;
    public static int PERMISSIONS_READ_EXTERNAL_STORAGE = 106;
    public static int PERMISSIONS_WRITE_EXTERNAL_STORAGE = 107;

    public static int CAMERA_CODE = 101, GALLERY_CODE = 201, CROPING_CODE = 301;


    public static String PROFILE_IMAGE="profile";
    public static String COMPANY_IMAGE="company";

    public static  String TASKS = "key";
    public static int SELECT_PHOTO = 1;

}
