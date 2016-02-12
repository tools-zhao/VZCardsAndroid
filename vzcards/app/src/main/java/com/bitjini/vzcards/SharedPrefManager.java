package com.bitjini.vzcards;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by bitjini on 18/1/16.
 */
// all methods are static , so we can call from any where in the code
//all member variables are private, so that we can save load with our own fun only
public class SharedPrefManager {
    //this is your shared preference file name, in which we will save data
    public static final String MY_EMP_PREFS = "MySharedPref";

    //saving the context, so that we can call all
    //shared pref methods from non activity classes.
    //because getSharedPreferences required the context.
    //but in activity class we can call without this context
    private static Context mContext;

    // will get user input in below variables, then will store in to shared pref
    public static String 	mName 			= "";
    public static String 	mEid 			= "";
    public static String  memail 			= "";
    public static String  mindustry 		= "";
    public static String  mcompany 			= "";
    public static String  maddress 			= "";

    public static void Init(Context context)
    {
        mContext 		= context;
    }
    public static void LoadFromPref()
    {
        SharedPreferences settings 	= mContext.getSharedPreferences(MY_EMP_PREFS, 0);
        // Note here the 2nd parameter 0 is the default parameter for private access,
        //Operating mode. Use 0 or MODE_PRIVATE for the default operation,
        mName 			= settings.getString("Name",""); // 1st parameter Name is the key and 2nd parameter is the default if data not found
        mEid 			= settings.getString("EmpID","");
        memail 			= settings.getString("Email", "");
        mindustry 			= settings.getString("Industry", "");
        mcompany 			= settings.getString("Company", "");
        maddress 			= settings.getString("Address", "");
    }
    public static void StoreToPref()
    {
        // get the existing preference file
        SharedPreferences settings = mContext.getSharedPreferences(MY_EMP_PREFS, 0);
        //need an editor to edit and save values
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("Name",mName); // Name is the key and mName is holding the value
        editor.putString("EmpID",mEid);// EmpID is the key and mEid is holding the value
        editor.putString("Email", memail); // Age is the key and mAge is holding the value
        editor.putString("Industry", mindustry);
        editor.putString("Company", mcompany);
        editor.putString("Address", maddress);

        //final step to commit (save)the changes in to the shared pref
        editor.commit();
    }

//    public static void DeleteSingleEntryFromPref(String keyName)
//    {
//        SharedPreferences settings = mContext.getSharedPreferences(MY_EMP_PREFS, 0);
//        //need an editor to edit and save values
//        SharedPreferences.Editor editor = settings.edit();
//        editor.remove(keyName);
//        editor.commit();
//    }
//
//    public static void DeleteAllEntriesFromPref()
//    {
//        SharedPreferences settings = mContext.getSharedPreferences(MY_EMP_PREFS, 0);
//        //need an editor to edit and save values
//        SharedPreferences.Editor editor = settings.edit();
//        editor.clear();
//        editor.commit();
//    }

    public static void SetName(String name)
    {
        mName =name;
    }
    public static void SetEmployeeID(String empID)
    {
        mEid = empID ;
    }
    public static void setEmail(String email)
    {
        memail = email;
    }

    public static String GetName()
    {
        return mName ;
    }
    public static String GetEmployeeID()
    {
        return mEid ;
    }
    public static String  GetEmail()
    {
        return memail ;
    }

    public static String getCompany() {
        return mcompany;
    }

    public static void setCompany(String company) {
       mcompany = company;
    }

    public static String getAddress() {
        return maddress;
    }

    public static void setAddress(String address) {
      maddress = address;
    }

    public static String getIndustry() {
        return mindustry;
    }

    public static void setIndustry(String industry) {
        mindustry = industry;
    }
}