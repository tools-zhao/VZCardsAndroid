package com.bitjini.vzcards;

import java.util.ArrayList;

public class DataFeeds extends ArrayList<DataFeeds> {

    // Required fields for Feeds
    String item,question,item_photo,description;
    String ticket_id;
    String isHas,isNeeds;

    //Required fields for profile and Feeds
    String phone,fname,vz_id,photo;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getVz_id() {
        return vz_id;
    }

    public void setVz_id(String vz_id) {
        this.vz_id = vz_id;
    }

    public String getIsHas() {
        return isHas;
    }

    public void setIsHas(String isHas) {
        this.isHas = isHas;
    }

    public String getIsNeeds() {
        return isNeeds;
    }

    public void setIsNeeds(String isNeeds) {
        this.isNeeds = isNeeds;
    }

    public String getItem_photo() {
        return item_photo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setItem_photo(String item_photo) {
        this.item_photo = item_photo;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getTicket_id() {
        return ticket_id;
    }

    public void setTicket_id(String ticket_id) {
        this.ticket_id = ticket_id;
    }
}
