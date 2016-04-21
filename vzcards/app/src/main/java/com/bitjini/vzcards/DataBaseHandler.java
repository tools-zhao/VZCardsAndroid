package com.bitjini.vzcards;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bitjini on 14/1/16.
 */
public class DataBaseHandler extends SQLiteOpenHelper {

    private  static final int DATABASE_VERSION=1;
    private static final String DATABASE_NAME="profileManager";
    private static final String TABLE_PROFILE=" profile ";
    private static final String KEY_ID=" id ";
    private static final String KEY_NAME=" name ";
    private static final String LName=" lname ";
    private static final String Industry=" industry";
    private static final String Company=" company ";
    private static final String Address=" address ";

    public DataBaseHandler(Context context)
    {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {

//       String CREATE_CONTACTS_TABLE = " CREATE TABLE  contacts  ( " + " id INTEGER PRIMARY KEY AUTOINCREMENT, " + " name TEXT, " + " phone_number TEXT )";
        String CREATE_CONTACTS_TABLE = " CREATE TABLE IF NOT EXISTS " + TABLE_PROFILE + " ( "
                + KEY_ID  + " INTEGER PRIMARY KEY, " + KEY_NAME  + " TEXT, " + LName  + " TEXT, "+Industry + "  Text, " + Company + " Text, "
                + Address +" TEXT ); ";
        db.execSQL(CREATE_CONTACTS_TABLE);

    }
    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Drop older table if existed
        db.execSQL(" DROP TABLE IF EXISTS " + TABLE_PROFILE );

        // Create tables again
        onCreate(db);

    }

    // code to add the new contact
    /*
    @param Database_contact is the class name with getters and setters
     */
    void addContact(Database_Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, contact.getName());   //Name
        values.put(LName, contact.getLname());  // LName
        values.put(Industry, contact.getIndustry());  // Industry
        values.put(Company, contact.getCompany());  // Company
        values.put(Address, contact.getAddress());  // Address


        // Inserting Row
        db.insert(TABLE_PROFILE, null, values);
        // 2nd argument is String containing nullColumnHack
        db.close(); //Closing database connection
    }

    // code to get the single contact
    Database_Contact getContact(int id){

        //get refernece of the ContactDB
        SQLiteDatabase db=this.getReadableDatabase();

        //get contact query
        Cursor cursor=db.query(TABLE_PROFILE,new String[]{KEY_ID,KEY_NAME,LName,Industry,Company,Address},KEY_ID +"=?",
                new String[]{ String.valueOf(id)}, null,null,null,null);

        //if results !=null , parse the 1st one
        if(cursor != null)

            cursor.moveToFirst();

            Database_Contact contact=new Database_Contact();
         contact.set_id(Integer.parseInt(cursor.getString(0)));
        contact.setName(cursor.getString(1));
        contact.setLname(cursor.getString(2));
        contact.setIndustry(cursor.getString(3));
        contact.setCompany(cursor.getString(4));
        contact.setAddress(cursor.getString(5));
            //return contact
            return contact;
        }
        // code to get all contacts in a listview
    public List<Database_Contact> getAllContacts() {
        List<Database_Contact> contactList = new ArrayList<Database_Contact>();

        // Select All Query
        String selectQuery = " SELECT * FROM " + TABLE_PROFILE;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                Database_Contact contact = new Database_Contact();
                contact.set_id(Integer.parseInt(cursor.getString(0)));
                contact.setName(cursor.getString(1));
                contact.setLname(cursor.getString(2));
                contact.setIndustry(cursor.getString(3));
                contact.setCompany(cursor.getString(4));
                contact.setAddress(cursor.getString(5));

                // Adding contact to list
                contactList.add(contact);

            } while (cursor.moveToNext());
        }

        // return contact list
        return contactList;
    }
        //code to update the single contact
        public int updateContact(Database_Contact contact)
        {
            SQLiteDatabase db=this.getWritableDatabase();

            ContentValues values=new ContentValues();
            values.put(KEY_NAME,contact.getName());
            values.put(LName,contact.getLname());
            values.put(Industry, contact.getIndustry());  // Industry
            values.put(Company, contact.getCompany());  // Company
            values.put(Address, contact.getAddress());  // Address


            // updating row

            int i= db.update(TABLE_PROFILE,values,KEY_ID + "=?", new String[]{ String.valueOf(contact.get_id())});
            db.close();
            return i;

        }

    // Deleting single contact
    public void deleteContact(Database_Contact contact)
    {
        SQLiteDatabase db= this.getWritableDatabase();
        db.delete(TABLE_PROFILE , KEY_ID + "=?", new String []{ String.valueOf(contact.get_id())});
        db.close();
    }

    //getting contacts count
    public int getContactsCount(){
        String countQuery=" SELECT * FROM " +TABLE_PROFILE;
        SQLiteDatabase db=this.getReadableDatabase();
        Cursor cursor=db.rawQuery(countQuery,null);
        cursor.close();

        //return count
        return cursor.getCount();
    }

   }
