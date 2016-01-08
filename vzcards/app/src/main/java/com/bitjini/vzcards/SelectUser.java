package com.bitjini.vzcards;

import android.graphics.Bitmap;

public class SelectUser {

    //for contact details
    String name;
    String phone;
    Bitmap thumb;

    //for profile details
    String label,values;


    SelectUser(String name, String phone,Bitmap thumb) {
        this.name = name;
        this.phone = phone;
        this.thumb = thumb;
    }

    public SelectUser() {

    }

    public String getName()

    {
        return name;
    }
    public void setName(String name)
    {
        this.name=name;
    }

    public Bitmap getThumb()
    {
        return thumb;

    }
    public void setThumb(Bitmap thumb)
    {
        this.thumb=thumb;
    }


    public String getPhone()
    {
        return phone;
    }
    public void setPhone(String phone)
    {
        this.phone=phone;
    }



    //for profile details
       public String getLabel() {
        return label;

    }

    public void setLabel(String label) {
        this.label = label;
    }
    public String getValues() {
        return values;
    }

    public void setValues(String values) {
        this.values = values;
    }

}

