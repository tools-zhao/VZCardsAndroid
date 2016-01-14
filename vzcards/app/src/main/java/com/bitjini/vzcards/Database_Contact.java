package com.bitjini.vzcards;

/**
 * Created by bitjini on 14/1/16.
 */
public class Database_Contact {

    int _id;
    String name;
    String phone_number;

    public Database_Contact() {

    }

    public Database_Contact(int _id, String name, String phone_number) {
        this._id = _id;
        this.name = name;
        this.phone_number = phone_number;
    }

    public Database_Contact(String name, String phone_number) {
        this.name = name;
        this.phone_number = phone_number;
    }

    public int get_id() {
        return _id;
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

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }
}
