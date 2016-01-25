package com.bitjini.vzcards;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.AndroidCharacter;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ListViewActivity extends Activity {
    public static final String MY_EMP_PREFS = "MySharedPref";
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_layout);

        ArrayList<String> myArrayList = new ArrayList<String>();

        myArrayList.add("value1");
        myArrayList.add("value2");
        myArrayList.add("value3");
        myArrayList.add("value4");
        myArrayList.add("value5");
        myArrayList.add("value6");

//        Store arraylist in sharedpreference

        sharedPreferences = getSharedPreferences(MY_EMP_PREFS, 0);
        SharedPreferences.Editor sEdit = sharedPreferences.edit();
        for (int i = 0; i < myArrayList.size(); i++) {
            sEdit.putString("val" + i, myArrayList.get(i));
            System.out.println(" values "+ sEdit.putString("val" + i, myArrayList.get(i)));
        }
        sEdit.putInt("size", myArrayList.size());
        sEdit.commit();

//        Retrive arraylist from sharedpreference

//        I am retriving values in another arraylist

        ArrayList<String> myAList = new ArrayList<String>();
        int size = sharedPreferences.getInt("size", 0);

        for (int j = 0; j < size; j++) {
            myAList.add(sharedPreferences.getString("val" + j,""));
            System.out.println(" values "+ myAList);
        }

    }
}