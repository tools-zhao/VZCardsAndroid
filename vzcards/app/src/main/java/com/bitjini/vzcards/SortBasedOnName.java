package com.bitjini.vzcards;

/**
 * Created by bitjini on 18/10/16.
 */

import java.util.Comparator;

public class SortBasedOnName implements Comparator
{
    public int compare(Object o1, Object o2)
    {

        SelectUser dd1 = (SelectUser)o1;// where SelectUser is your object class
        SelectUser dd2 = (SelectUser)o2;
        return dd1.getfName().compareToIgnoreCase(dd2.getfName());//where getfName is field name
    }

}