package com.bitjini.vzcards;

import android.graphics.Bitmap;

import java.util.ArrayList;
import java.util.List;

public class ReferalUsers {
    String name;
    String referredName;
//    Bitmap thumb;
private List<ItemDetail> itemList = new ArrayList<ItemDetail>();
    public String getName()

    {
        return name;
    }
    public void setName(String name)
    {
        this.name=name;
    }
    public String toString() { return name; }

//    public Bitmap getThumb()
//    {
//        return thumb;
//
//    }
//    public void setThumb(Bitmap thumb)
//    {
//        this.thumb=thumb;
//    }


    public String getReferredName()
    {
        return referredName;
    }

    public void setReferredName(String referredName)
    {
        this.referredName=referredName;
    }



}
class ItemDetail extends ReferalUsers {
    private String desc;
    public String getDesc()

    {
        return desc;
    }
    public void setDesc(String desc)
    {
        this.desc=desc;
    }
}
