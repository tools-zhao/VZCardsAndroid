package com.bitjini.vzcards;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by bitjini on 11/1/16.
 */

public class ListViewActivity extends Activity {
    Button editbtn;
    ListView listView;
    int clickCount = 0;
    private String[] arrText =
            new String[]{"Text1", "Text2", "Text3", "Text4"
                    , "Text5", "Text6"};
    private String[] arrTemp=new String[]{"Veena", "Mawrarkar", "Text3", "Text4"
            , "Text5", "Text6"};
    MyListAdapter myListAdapter = new MyListAdapter();
     @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_layout);

//        arrTemp = new String[arrText.length];
         editbtn = (Button)findViewById(R.id.edit);


        listView = (ListView) findViewById(R.id.profileList);
         listView.setItemsCanFocus(true);
         listView.setAdapter(myListAdapter);

         editbtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View view) {

                 if (clickCount == 0) {

                     editbtn.setText("Save");

                     myListAdapter.actv(true);
                     myListAdapter.notifyDataSetChanged();
//                     listView.setAdapter(myListAdapter);
//                    Log.e("selected position of textview", "" + position);
                     Toast.makeText(ListViewActivity.this, "click 0", Toast.LENGTH_LONG).show();
                     clickCount = 1;

                 } else if (clickCount == 1) {

                     editbtn.setText("Edit");

                     myListAdapter.actv(false);
                     listView.setAdapter(myListAdapter);
                     Toast.makeText(ListViewActivity.this, "click 1", Toast.LENGTH_LONG).show();
                     clickCount = 0;

                 }

             }
         });

     }

    private class MyListAdapter extends BaseAdapter {
        ViewHolder holder;
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            if (arrText != null && arrText.length != 0) {
                return arrText.length;
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return arrText[position];
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            //ViewHolder holder = null;

            if (convertView == null) {

                holder = new ViewHolder();
                LayoutInflater inflater = ListViewActivity.this.getLayoutInflater();
                convertView = inflater.inflate(R.layout.profile_listitems, null);
                holder.textView1 = (TextView) convertView.findViewById(R.id.label);
                holder.editText1 = (EditText) convertView.findViewById(R.id.values);

                holder.editText1.setTag(holder);
//                actv(false);
                convertView.setTag(holder);

            } else {

                holder = (ViewHolder) convertView.getTag();
            }

            holder.ref = position;

            holder.textView1.setText(arrText[position]);
            holder.editText1.setText(arrTemp[position]);
            holder.editText1.setId(position);
            Log.e("postion", "" + holder.ref);
            if(clickCount==0) {
                actv(false);
            }else{
//                holder.editText1.setText(arrTemp[position]);
//                TextWatcher code handles the issue of duplicating of one editText value to other
                holder.editText1.addTextChangedListener(new TextWatcher() {

                    @Override
                    public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                                  int arg3) {
                        // TODO Auto-generated method stub

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        // TODO Auto-generated method stub

//                        String editedText = s.toString().trim();
                        arrTemp[position] = s.toString();
                        Log.e("edited Positon", "" + holder.ref);

                    }


                });

            }
            if(clickCount==1) {
                actv(true);
            }
            return convertView;
        }
        protected void actv(final boolean active) {


            holder.editText1.setTextColor(Color.GREEN);
            holder.editText1.setEnabled(active);
if(active) {
    holder.editText1.requestFocus();

}

        }
        private class ViewHolder {
            TextView textView1;
            EditText editText1;
            int ref;
        }


    }
}
