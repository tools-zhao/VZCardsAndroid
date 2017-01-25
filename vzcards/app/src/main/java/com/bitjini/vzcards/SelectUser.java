package com.bitjini.vzcards;

import android.graphics.Bitmap;

import org.json.JSONArray;

import java.util.ArrayList;

public class SelectUser {

    //for contact details
    String name;
    String phone;
    Bitmap thumb;

    String syncPhone;
    JSONArray connections;

    public JSONArray getConnections() {
        return connections;
    }

    public void setConnections(JSONArray connections) {
        this.connections = connections;
    }

    String itemName;
    String date_validity;
    String item_description;
    String date_created;
    String ticket_id;
    String question;
    String company,industry,city,address1,address2,pin_code,email;
    String referedPhoto="",comany_photo="",item_photo="";
    String fName,lname,photo="",referredFname="",referredLname="",referredPhoto="";
    String firstName="",lastName="",title="";

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getSyncPhone() {
        return syncPhone;
    }

    public void setSyncPhone(String syncPhone) {
        this.syncPhone = syncPhone;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getPin_code() {
        return pin_code;
    }

    public void setPin_code(String pin_code) {
        this.pin_code = pin_code;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getReferedPhoto() {
        return referedPhoto;
    }

    public void setReferedPhoto(String referedPhoto) {
        this.referedPhoto = referedPhoto;
    }

    public String getComany_photo() {
        return comany_photo;
    }

    public void setComany_photo(String comany_photo) {
        this.comany_photo = comany_photo;
    }

    public String getQuestion() {

        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getDate_created() {
        return date_created;
    }

    public String getTicket_id() {
        return ticket_id;
    }

    public void setTicket_id(String ticket_id) {
        this.ticket_id = ticket_id;
    }

    public void setDate_created(String date_created) {
        this.date_created = date_created;
    }

    public String getReferredLname() {
        return referredLname;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public void setReferredLname(String referredLname) {
        this.referredLname = referredLname;
    }

    public String getReferredPhoto() {
        return referredPhoto;
    }

    public void setReferredPhoto(String referredPhoto) {
        this.referredPhoto = referredPhoto;
    }

    public String getReferredFname() {
        return referredFname;
    }

    public void setReferredFname(String referredFname) {
        this.referredFname = referredFname;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getPhoto() {
        return photo;
    }

    public String getItem_description() {
        return item_description;
    }

    public void setItem_description(String item_description) {
        this.item_description = item_description;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItem_photo() {
        return item_photo;
    }

    public void setItem_photo(String item_photo) {
        this.item_photo = item_photo;
    }

    public String getDate_validity() {
        return date_validity;
    }

    public void setDate_validity(String date_validity) {
        this.date_validity = date_validity;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

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

