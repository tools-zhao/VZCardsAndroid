package com.bitjini.vzcards;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
* Created by bitjini on 14/1/16.
*/


public class Database_Activity extends Activity {
    TextView bookTitle;
    TextView authorName;
    Database_Contact selectedContact;
    DataBaseHandler db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_row);
        bookTitle = (TextView) findViewById(R.id.name);
        authorName = (TextView) findViewById(R.id.phoneNo);
        // get the intent that we have passed from Adding_Contacts
        Intent intent = getIntent();
        int id = intent.getIntExtra("contact", -1);
        // open the database of the application context
        db = new DataBaseHandler(getApplicationContext());
        // read the contact with "id" from the database
        selectedContact = db.getContact(id);
        initializeViews();
    }

    public void initializeViews() {
        bookTitle.setText(selectedContact.getName());
        authorName.setText(selectedContact.getLname());

    }

    public void update(View v) {


        selectedContact.setName(((EditText) findViewById(R.id.nameEdit)).getText().toString());

        selectedContact.setLname(((EditText) findViewById(R.id.phoneEdit)).getText().toString());

        // update contact  with changes
        db.updateContact(selectedContact);

        finish();

    }


    public void delete(View v) {


        // delete selected contact
     db.deleteContact(selectedContact);
        Toast.makeText(getApplicationContext(), "This contact"
                + selectedContact.getName()+" is deleted.", Toast.LENGTH_SHORT).show();
        finish();
    }
}