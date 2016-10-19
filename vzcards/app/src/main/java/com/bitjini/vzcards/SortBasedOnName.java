package com.bitjini.vzcards;

/**
 * Created by bitjini on 18/10/16.
 */

import java.util.Comparator;

public class SortBasedOnName implements Comparator<SelectUser>
{


    @Override
    public int compare(SelectUser selectUser, SelectUser t1) {
        SelectUser dd1 = (SelectUser)selectUser;// where SelectUser is your object class
        SelectUser dd2 = (SelectUser)t1;
        return dd1.getfName().compareToIgnoreCase(dd2.getfName());//where getfName is field name
    }
}