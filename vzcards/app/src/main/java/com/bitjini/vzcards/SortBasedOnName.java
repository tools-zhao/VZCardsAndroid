package com.bitjini.vzcards;

/**
 * Created by bitjini on 18/10/16.
 */

import android.content.Context;
import android.support.v4.app.FragmentActivity;

import java.util.Comparator;

public class SortBasedOnName implements Comparator<SelectUser>
{
Context context;

    public SortBasedOnName(Context context) {
        this.context=context;

    }

    @Override
    public int compare(SelectUser selectUser, SelectUser t1) {
        SelectUser dd1 = (SelectUser)selectUser;// where SelectUser is your object class
        SelectUser dd2 = (SelectUser)t1;
        if(dd1.getfName()!=null && dd2.getfName()!=null) {
            return dd1.getfName().compareToIgnoreCase(dd2.getfName());//where getfName is field name
        }else {
            return 0;
        }
    }
}