package com.bitjini.vzcards;

import android.graphics.Bitmap;

public class SelectUser {

    //for contact details
    String name;
    String phone;
    Bitmap thumb;

    //for profile details
    String industry,company,email,address;
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
    public String getIndustry()

    {
        return industry;
    }
    public void setIndustry(String industry)
    {
        this.industry=industry;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}

