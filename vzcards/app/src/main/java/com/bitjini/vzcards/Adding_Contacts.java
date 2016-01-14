package com.bitjini.vzcards;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bitjini on 14/1/16.
 */
public class Adding_Contacts  extends Activity implements AdapterView.OnItemClickListener{

    DataBaseHandler db;
    List<Database_Contact> list;
    ArrayAdapter myAdapter;
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.db_contacts);
        listView=(ListView) findViewById(R.id.list);
       db=new DataBaseHandler(this);
//        db.onUpgrade(db.getWritableDatabase(), 1, 2);
        // Inserting Contacts
        Log.d(" Insert :","Inserting..");
         db.addContact(new Database_Contact("Jack","910000000"));
        db.addContact(new Database_Contact("Mack","9267889392"));
        db.addContact(new Database_Contact("Tommy","952828393"));
        db.addContact(new Database_Contact("Jill","910000000"));
        db.addContact(new Database_Contact("Sean","9267889392"));
        db.addContact(new Database_Contact("Ron","952828393"));


        //Reading all contacts
        Log.d("Reading :", "Reading all contacts..");
       list=db.getAllContacts();
        List arrayList=new ArrayList();

        for(int i=0;i<list.size();i++)
        {


            arrayList.add(list.get(i).getName());

        }
        myAdapter=new ArrayAdapter(this,R.layout.db_items,R.id.label,arrayList);
        listView.setAdapter(myAdapter);
       listView.setOnItemClickListener(this);

    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View view, int i, long l) {

        // start Database Activity with extras the contact id
        Intent intent=new Intent(this,Database_Activity.class);
        intent.putExtra("contact", list.get(i).get_id());
        startActivityForResult(intent,1);

    }
//    @Override
    protected  void onActivityResult(int requestCode,int resultCode,Intent data){
        // get All contacts again, because something changed

        list=db.getAllContacts();
        List listName=new ArrayList();

        for(int i=0;i<list.size();i++)
        {
            listName.add(i,list.get(i).getName());
        }
        myAdapter=new ArrayAdapter(this,R.layout.db_items,R.id.label,listName);
      listView.setOnItemClickListener(this);
        listView.setAdapter(myAdapter);
    }
}
