package com.bitjini.vzcards;

/**
 * Created by bitjini on 11/4/16.
 */

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by VEENA on 12/7/2015.
 */
public class Friends_Profile extends Activity implements View.OnClickListener {


    public ImageView imageProfile, imageCompany,imageCall;

    View profile;


    ArrayList<String> label;
    ArrayList<String> values;
    public Bitmap output;
    int clickCount = 0;
    //Declaring widgets
    Button editbtn, profilebtn, vzfrndsbtn, referralbtn;
    TextView textViewName;

    public ProgressDialog progress;

    ListView listView;
    EditTextAdapter editTextAdapter;

    ArrayList<ListItem> arrayList = new ArrayList<ListItem>();
    ArrayList<ListItem> adapterArrayList = new ArrayList<ListItem>();
    public ArrayList<ListItem> groupItem = new ArrayList<ListItem>();

    VerifyScreen p = new VerifyScreen();
    Bitmap bm = null;
    String json, json2;
    public   String  firstname = "", lastname = "", email = "", industry = "", company = "", address_line_1 = "", address_line_2 = "",
            city, pin_code = "",phone="";
    public static String photo="",company_photo="";
    public Bitmap bitmap;
    public static String picturePath;
    LinearLayout linearLayout;
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
       setContentView(R.layout.frnds_profile);

       listView = (ListView) findViewById(R.id.profileList);
       linearLayout=(LinearLayout) findViewById(R.id.l2);

        textViewName = (TextView)findViewById(R.id.name);
        //Picking Profile picture
        imageProfile = (ImageView)findViewById(R.id.profilePic);
        imageCompany = (ImageView) findViewById(R.id.btn_pick);
        imageCall = (ImageView) findViewById(R.id.btn_call);

        //image listeners
        imageCall.setOnClickListener(this);

        label = new ArrayList<String>();
        label.add("Firstname");
        label.add("Lastname");
        label.add("Email");
        label.add("Phone");
        label.add("Industry");
        label.add("Company");
        label.add("Address_line_1");
        label.add("Address_line_2");
        label.add("City");
        label.add("Pin_code");
        // Making http get request to load profile details

        Intent intent=getIntent();
        // Receiving data

        firstname = intent.getStringExtra("fname");
        lastname = intent.getStringExtra("lname");
        email =  intent.getStringExtra("email");
        phone = intent.getStringExtra("phone");
        industry =  intent.getStringExtra("industry");
        company =  intent.getStringExtra("company");
        address_line_1 = intent.getStringExtra("address_line_1");
        address_line_2 =intent.getStringExtra("address_line_2");
        city =  intent.getStringExtra("city");
        pin_code =intent.getStringExtra("pin_code");
        photo=  intent.getStringExtra("photo");
        company_photo= intent.getStringExtra("company_photo");


        if(!photo.isEmpty()) {
            Picasso.with(getApplicationContext()).load(photo).resize(180, 180).placeholder(R.drawable.profile_pic_placeholder).into(imageProfile);
//            imageProfile.setTag(photo);
//            new DownloadImagesTask(getActivity()).execute(imageProfile);// Download item_photo from AsynTask
        }

        if(!company_photo.isEmpty()) {
            Picasso.with(getApplicationContext()).load(company_photo).resize(70, 70).placeholder(R.drawable.com_logo).into(imageCompany);
//            imageCompany.setTag(company_photo);
//            new DownloadImagesTask(getActivity()).execute(imageCompany);// Download item_photo from AsynTask
        }


        textViewName.setText(firstname+ " "+lastname);
        values = new ArrayList<String>();
        values.add(firstname);
        values.add(lastname);
        values.add(email);
        values.add(phone);
        values.add(industry);
        values.add(company);
        values.add(address_line_1);
        values.add(address_line_2);
        values.add(city);
        values.add(pin_code);

        for (int i = 0; i < label.size(); i++) {
            ListItem item = new ListItem();
            item.setLabel(label.get(i));
            item.setValue(values.get(i));
            arrayList.add(item);
        }


        // send the adapterArraylist to the adapter and set it to listview
        editTextAdapter = new EditTextAdapter(Friends_Profile.this, arrayList, R.layout.profile_layout);
        listView.setAdapter(editTextAdapter);

//

    }



    public void decodeFile(String filePath) {
        // First decode with inJustDecodeBounds=true to check dimensions
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, o);

        // The new size we want to scale to
        final int REQUIRED_SIZE = 1024;
        // Calculate inSampleSize
        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE)
                break;
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }
        output=null;
        // Decode bitmap with inSampleSize set
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        bitmap = BitmapFactory.decodeFile(filePath, o2);
//        getRoundedCornerBitmap(bitmap, 100);

    }

    public void onClick(View v) {
        switch (v.getId()) {

            //setting profile picture
            case R.id.btn_call:
                try{
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:"+phone));
                    startActivity(callIntent);
                }
                catch (android.content.ActivityNotFoundException ex)
                {
                    Toast.makeText(getApplicationContext(),"your Activity is not found",Toast.LENGTH_LONG).show();
                }
                break;

            default:
                break;

        }
    }



    /**
     * The object we have a list of
     */
    static class ListItem {
        public String value;
        public String label;


        ListItem() {
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }
    }

    //    /**
//     * ViewHolder which also tracks the TextWatcher for an EditText
//     */
    static class ViewHolder {
        public TextView textView;
        public TextView editText;
        public TextWatcher textWatcher;
    }

    // custom adapter class
    class EditTextAdapter extends BaseAdapter {
        ViewHolder holder = new ViewHolder();
        Context _c;

        EditTextAdapter(Context context, ArrayList<ListItem> groupItem, int resource) {

            this._c = context;
            Friends_Profile.this.groupItem = groupItem;
        }

        @Override
        public int getCount() {
            return groupItem.size();
        }

        @Override
        public ListItem getItem(int i) {
            return groupItem.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View rowView = null;
            convertView = null;
            if (convertView == null) {
                // Not recycled, inflate a new view
                LayoutInflater li = (LayoutInflater) _c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                rowView = li.inflate(R.layout.frnds_profile_details, null);

                rowView.setTag(holder);
            }
            holder.textView = (TextView) rowView.findViewById(R.id.labels);
            holder.editText = (TextView) rowView.findViewById(R.id.values1);
            ViewHolder holder = (ViewHolder) rowView.getTag();

            final ListItem listItem = groupItem.get(position);

            holder.editText.setText(listItem.value);

            holder.textView.setText(listItem.getLabel().toString());
//              holder.editText.setEnabled(false);

            return rowView;
        }

    }

}