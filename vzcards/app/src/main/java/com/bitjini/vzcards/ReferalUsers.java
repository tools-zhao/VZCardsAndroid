package com.bitjini.vzcards;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

public class ReferalUsers {
    String fname,lname;
    String referredfName,referredlName;
    String photo,referedPhoto;
    String desc;

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getReferredfName() {
        return referredfName;
    }

    public void setReferredfName(String referredfName) {
        this.referredfName = referredfName;
    }

    public String getReferredlName() {
        return referredlName;
    }

    public void setReferredlName(String referredlName) {
        this.referredlName = referredlName;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getReferedPhoto() {
        return referedPhoto;
    }

    public void setReferedPhoto(String referedPhoto) {
        this.referedPhoto = referedPhoto;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}

