package com.bitjini.vzcards;

/**
 * Created by bitjini on 12/1/16.
 */
import java.text.DecimalFormat;
import java.util.ArrayList;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class ProductList extends Activity {

    private MyCustomAdapter dataAdapter = null;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_layout);

        //Generate list View from ArrayList
        displayListView();

    }

    private void displayListView() {

        //Array list of products
        ArrayList<String> label;
        ArrayList<String> values;
        ArrayList<SelectUser> productList = new ArrayList<SelectUser>();
        label=new ArrayList<String>();
        label.add("Fname");
        label.add("Lname");
        label.add("Industry");
        label.add("Company");
        label.add("Address");

        values=new ArrayList<String>();
        values.add("Veena");
        values.add("Mawarkar");
        values.add("IT");
        values.add("Bitjini");
        values.add("GIT");
        for(int i=0;i<label.size();i++) {
            SelectUser selectUser = new SelectUser();
            selectUser.setLabel(label.get(i));
            selectUser.setValues(values.get(i));
            productList.add(selectUser);
        }



        //create an ArrayAdaptar from the String Array
        dataAdapter = new MyCustomAdapter(ProductList.this,R.layout.profile_listitems, productList);
        ListView listView = (ListView) findViewById(R.id.profileList);
        // Assign adapter to ListView
        listView.setAdapter(dataAdapter);

    }

    private class MyCustomAdapter extends ArrayAdapter<SelectUser> {

        private ArrayList<SelectUser> productList;

        public MyCustomAdapter(Context context, int textViewResourceId,
                               ArrayList<SelectUser> productList) {
            super(context, textViewResourceId, productList);
            this.productList = new ArrayList<SelectUser>();
            this.productList.addAll(productList);
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {


            SelectUser product = productList.get(position);

            if (view == null) {
                LayoutInflater vi = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = vi.inflate(R.layout.profile_listitems, null);
                EditText quantity = (EditText) view.findViewById(R.id.values);
                //attach the TextWatcher listener to the EditText
                quantity.addTextChangedListener(new MyTextWatcher(view));

            }

            EditText values = (EditText) view.findViewById(R.id.values);
            values.setTag(product);

                values.setText(product.getValues());


            TextView label = (TextView) view.findViewById(R.id.label);
            label.setText(product.getLabel());


            return view;

        }

    }

    private class MyTextWatcher implements TextWatcher{

        private View view;
        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //do nothing
        }
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //do nothing
        }
        public void afterTextChanged(Editable s) {


            String editedText = s.toString().trim();
//            int value = editedText.equals("") ? 0 : Integer.valueOf(editedText);

            EditText qtyView = (EditText) view.findViewById(R.id.values);
            SelectUser product = (SelectUser) qtyView.getTag();

            if (product.getValues() != editedText) {
                product.setValues(String.valueOf(editedText));
            }


            if (product.getValues() != null) {
                qtyView.setText(product.getValues());
            } else {
                qtyView.setText("");
            }
        return;
        }


        }
    }



