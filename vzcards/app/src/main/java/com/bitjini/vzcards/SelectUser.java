package com.bitjini.vzcards;

import android.graphics.Bitmap;

public class SelectUser {
    String name;
    String phone;
    Bitmap thumb;

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


}

