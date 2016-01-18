package com.bitjini.vzcards;

import java.util.ArrayList;

/**
 * Created by bitjini on 14/1/16.
 */
public class Database_Contact {

    int _id;
    String label,values;

    String name;
    String lname,industry,company,address;

    public Database_Contact() {

    }

    public int get_id() {
        return _id;
    }

    public Database_Contact(String name, String lname, String industry, String company, String address) {
        this.name = name;
        this.lname = lname;
        this.industry = industry;
        this.company = company;
        this.address = address;
    }

    public Database_Contact(int _id, String lname, String name, String industry, String company, String address) {
        this._id = _id;
        this.lname = lname;
        this.name = name;
        this.industry = industry;
        this.company = company;
        this.address = address;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void set_id(int _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

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
