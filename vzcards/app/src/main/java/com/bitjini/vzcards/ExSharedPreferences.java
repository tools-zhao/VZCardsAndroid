package com.bitjini.vzcards;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by bitjini on 18/1/16.
 */
public class ExSharedPreferences extends Activity {


    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.db_items);
        SharedPrefManager.Init(this);
        onRestart();

    }
    public void onClickStore(View v) {

        //get user input first, then store. we will use our SharedPrefManager Class functions
        EditText editTextName,editTextEmpID,editTextEmail,editTextIndustry,editTextCompany,editTextAddress;
        editTextName=(EditText)findViewById(R.id.name);

        editTextEmpID=(EditText)findViewById(R.id.phone);

        editTextEmail=(EditText)findViewById(R.id.email);

        editTextIndustry=(EditText)findViewById(R.id.industry);
        editTextIndustry.setEnabled(true);

        editTextCompany=(EditText)findViewById(R.id.company);

        editTextAddress=(EditText)findViewById(R.id.address);

        editTextName.setEnabled(true);
        editTextName.requestFocus();

        editTextEmpID.setEnabled(true);
        editTextEmpID.requestFocus();
        editTextEmail.setEnabled(true);
        editTextEmail.requestFocus();
        editTextCompany.setEnabled(true);
        editTextCompany.requestFocus();
        editTextAddress.setEnabled(true);
        editTextAddress.requestFocus();

        //convert EditText to string
        String srtTextName = editTextName.getText().toString();
        String	srtTextEmpID = editTextEmpID.getText().toString();
        String	strTextEmail = editTextEmail.getText().toString();
        String	strTextIndustry = editTextIndustry.getText().toString();
        String	strTextCompany = editTextCompany.getText().toString();
        String	strTextaddress = editTextAddress.getText().toString();

        if(0!= srtTextName.length())
            SharedPrefManager.SetName(srtTextName); // need string value so convert it
        if(0 !=srtTextEmpID.length())
            SharedPrefManager.SetEmployeeID(srtTextEmpID); // need string value so convert it
        if(0 != strTextEmail.length())
            SharedPrefManager.setEmail(strTextEmail);
        if(0 != strTextIndustry.length())
            SharedPrefManager.setIndustry(strTextIndustry);
        if(0 != strTextCompany.length())
            SharedPrefManager.setCompany(strTextCompany);
        if(0 != strTextaddress.length())
            SharedPrefManager.setAddress(strTextaddress);

        //now save all to shared pref, all updated values are now available in SharedPrefManager class, as we set above
        SharedPrefManager.StoreToPref();

        //reset all fields to blank before load and update from sharedpref
//        EditText tv = null;
//        tv = (EditText)findViewById(R.id.name);
//        tv.setText("");
//        tv = (EditText)findViewById(R.id.phone);
//        tv.setText("");
//        tv = (EditText)findViewById(R.id.email);
//        tv.setText("");
//        tv = (EditText)findViewById(R.id.industry);
//        tv.setText("");
//        tv = (EditText)findViewById(R.id.company);
//        tv.setText("");
//        tv = (EditText)findViewById(R.id.address);
//        tv.setText("");

        Toast.makeText(this, "Data Successfully Stored to SharedPreference", Toast.LENGTH_LONG).show();

    }
    public void onClickLoad(View v) {

        //Get all values from SharedPrefference file
        SharedPrefManager.LoadFromPref(); // all values are loaded into corresponding variables of SharedPrefManager class

        //Now get the values form SharedPrefManager class using it's static functions.
        String strTextName,strTextEmpID,strTextEmail,strTextAddress,strTextCompany,strTextIndustry;

        strTextName = SharedPrefManager.GetName();
        strTextEmpID = SharedPrefManager.GetEmployeeID();
        strTextEmail =SharedPrefManager.GetEmail();
        strTextIndustry =SharedPrefManager.getIndustry();
        strTextCompany =SharedPrefManager.getCompany();
        strTextAddress =SharedPrefManager.getAddress();

        //Now we can show these persistent values on our activity (GUI)
        EditText tv = null;
        tv = (EditText)findViewById(R.id.name);
        tv.setText(strTextName);
        tv.setEnabled(false);
        tv = (EditText)findViewById(R.id.phone);
        tv.setText(strTextEmpID);
        tv.setEnabled(false);
        tv = (EditText)findViewById(R.id.email);
        tv.setText(strTextEmail);
        tv.setEnabled(false);
        tv = (EditText)findViewById(R.id.industry);
        tv.setText(strTextIndustry);
        tv.setEnabled(false);
        tv = (EditText)findViewById(R.id.company);
        tv.setText(strTextCompany);
        tv.setEnabled(false);
        tv = (EditText)findViewById(R.id.address);
        tv.setText(strTextAddress);
        tv.setEnabled(false);

        Toast.makeText(this, "Data Successfully Loaded from SharedPreference", Toast.LENGTH_LONG).show();
    }
    public void onRestart()
    {
        SharedPrefManager.LoadFromPref();
        String strTextName,strTextEmpID,strTextEmail,strTextAddress,strTextCompany,strTextIndustry;

        strTextName = SharedPrefManager.GetName();
        strTextEmpID = SharedPrefManager.GetEmployeeID();
        strTextEmail =SharedPrefManager.GetEmail();
        strTextIndustry =SharedPrefManager.getIndustry();
        strTextCompany =SharedPrefManager.getCompany();
        strTextAddress =SharedPrefManager.getAddress();

        //Now we can show these persistent values on our activity (GUI)
        EditText tv = null;
        tv = (EditText)findViewById(R.id.name);
        tv.setText(strTextName);
        tv.setEnabled(false);
        tv = (EditText)findViewById(R.id.phone);
        tv.setText(strTextEmpID);
        tv.setEnabled(false);
        tv = (EditText)findViewById(R.id.email);
        tv.setText(strTextEmail);
        tv.setEnabled(false);
        tv = (EditText)findViewById(R.id.industry);
        tv.setText(strTextIndustry);
        tv.setEnabled(false);
        tv = (EditText)findViewById(R.id.company);
        tv.setText(strTextCompany);
        tv.setEnabled(false);
        tv = (EditText)findViewById(R.id.address);
        tv.setText(strTextAddress);
        tv.setEnabled(false);


    }


}
