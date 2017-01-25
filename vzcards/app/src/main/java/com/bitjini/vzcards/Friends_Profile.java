package com.bitjini.vzcards;

/**
 * Created by bitjini on 11/4/16.
 */

import android.Manifest;
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
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.bitjini.vzcards.Constants.PERMISSIONS_REQUEST_CALL_CONTACTS;

/**
 * Created by VEENA on 12/7/2015.
 */
public class Friends_Profile extends Activity implements View.OnClickListener {


    public ImageView imageProfile, imageCompany, imageCall;

    ArrayList<String> label;
    ArrayList<String> values;
    TextView textViewName;

    public ProgressDialog progress;

    ListView listView;
    EditTextAdapter editTextAdapter;

    ArrayList<ListItem> arrayList = new ArrayList<ListItem>();

    VerifyScreen p = new VerifyScreen();
    String json;
    public String firstname = "", lastname = "", email = "", industry = "", company = "", address_line_1 = "", address_line_2 = "",
            city = "", pin_code = "", phone = "", title = "", phoneName = "";
    public static String photo = "", company_photo = "";
    public Bitmap bitmap;
    public static String picturePath;

    //    LinearLayout linearLayout;
    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.frnds_profile);

        listView = (ListView) findViewById(R.id.profileList);
//       linearLayout=(LinearLayout) findViewById(R.id.l2);

        textViewName = (TextView) findViewById(R.id.name);
        //Picking Profile picture
        imageProfile = (ImageView) findViewById(R.id.profilePic);
        imageCompany = (ImageView) findViewById(R.id.btn_pick);
        imageCall = (ImageView) findViewById(R.id.btn_call);

        //image listeners
        imageCall.setOnClickListener(this);

        label = new ArrayList<String>();
        label.add("Firstname");
        label.add("Lastname");
        label.add("What do you do?");
        label.add("Email");
        label.add("Address");
        label.add("City");
        label.add("Pin code");
        // Making http get request to load profile details

        Intent intent = getIntent();
        // Receiving data

        phoneName = intent.getStringExtra("phoneName");
        firstname = intent.getStringExtra("fname");
        lastname = intent.getStringExtra("lname");
        email = intent.getStringExtra("email");
        phone = intent.getStringExtra("phone");
        industry = intent.getStringExtra("industry");
        company = intent.getStringExtra("company");
        address_line_1 = intent.getStringExtra("address_line_1");
        address_line_2 = intent.getStringExtra("address_line_2");
        city = intent.getStringExtra("city");
        pin_code = intent.getStringExtra("pin_code");
        photo = intent.getStringExtra("photo");
        title = intent.getStringExtra("title");
        company_photo = intent.getStringExtra("company_photo");

        DisplayMetrics metrics = getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int density = metrics.densityDpi;
        Log.e("width=", "" + width);
        int height = metrics.heightPixels;
        RelativeLayout.LayoutParams paramImage = new RelativeLayout.LayoutParams(width / 2, width / 2);
        imageProfile.setLayoutParams(paramImage);
        RelativeLayout.LayoutParams textParams;
        if (density == 480) {
            textParams = new RelativeLayout.LayoutParams(width / 2, 64);
            textParams.topMargin = ((width / 2) - 64);
        } else if (density == 240) {
            textParams = new RelativeLayout.LayoutParams(width / 2, 34);
            textParams.topMargin = ((width / 2) - 34);
        } else {
            textParams = new RelativeLayout.LayoutParams(width / 2, 45);
            textParams.topMargin = ((width / 2) - 45);
        }
//        textParams.addRule(RelativeLayout.ALIGN_BOTTOM);

//
        Log.e("width=", "" + width / 2);
        textViewName.setTextColor(Color.WHITE);
        textViewName.setLayoutParams(textParams);

        RelativeLayout.LayoutParams paramImage2 = new RelativeLayout.LayoutParams(width / 2, width / 3);
        paramImage2.leftMargin = width / 2;
        imageCompany.setLayoutParams(paramImage2);
        imageCompany.setScaleType(ImageView.ScaleType.CENTER_CROP);

        RelativeLayout.LayoutParams paramImage3 = new RelativeLayout.LayoutParams(width / 2, width / 6);
        paramImage3.leftMargin = width / 2;
        paramImage3.topMargin = ((width / 2) - (width / 6));
        imageCall.setLayoutParams(paramImage3);
        imageCall.setBackgroundResource(R.drawable.callgreen);
        imageCall.setPadding(30, 30, 30, 30);
        imageCall.setCropToPadding(true);
        imageCall.setImageResource(R.drawable.ic_call_white_);


        if (!photo.isEmpty()) {
            Picasso.with(getApplicationContext()).load(photo).resize(400, 400).into(imageProfile);
//            imageProfile.setTag(photo);
//            new DownloadImagesTask(getActivity()).execute(imageProfile);// Download item_photo from AsynTask
        } else {
            imageProfile.setImageResource(R.drawable.profile_pic_placeholder);
            imageProfile.setPadding(60, 60, 60, 60);
        }
        if (!company_photo.isEmpty()) {
            Picasso.with(getApplicationContext()).load(company_photo).resize(250, 260).placeholder(R.drawable.com_logo).into(imageCompany);
//            imageCompany.setTag(company_photo);
//            new DownloadImagesTask(getActivity()).execute(imageCompany);// Download item_photo from AsynTask
        } else {
            imageCompany.setImageResource(R.drawable.no_pic_placeholder_2);
            imageCompany.setPadding(60, 60, 60, 60);
        }


        textViewName.setText(phoneName);
//        textViewName.setText(firstname + " " + lastname);
        textViewName.setTextSize(16);
        values = new ArrayList<String>();
        values.add(firstname + " " + lastname);
//        values.add(lastname);
        if (title != null)
            values.add(title);  // contains value for what do you do?

        if (email != null)
            values.add(email);

        if (address_line_1 != null)
            values.add(address_line_1);

        if (city != null)
            values.add(city);

        if (pin_code != null)
            values.add(pin_code);

        addValues(values);

//

    }

    public void addValues(ArrayList<String> values) {
        for (int i = 0; i < values.size(); i++) {
            ListItem item = new ListItem();
//            item.setLabel(label.get(i));
            item.setValue(values.get(i));
            arrayList.add(item);
        }


        // send the adapterArraylist to the adapter and set it to listview
        editTextAdapter = new EditTextAdapter(Friends_Profile.this, arrayList, R.layout.profile_layout);
        listView.setAdapter(editTextAdapter);

    }


    public void onClick(View v) {
        switch (v.getId()) {

            //setting profile picture
            case R.id.btn_call:
                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, PERMISSIONS_REQUEST_CALL_CONTACTS);
                        //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
                    } else {
                        Intent callIntent = new Intent(Intent.ACTION_CALL);
                        callIntent.setData(Uri.parse("tel:" + "+" + phone));
                        startActivity(callIntent);
                    }
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(getApplicationContext(), "your Activity is not found", Toast.LENGTH_LONG).show();
                }
                break;

            default:
                break;

        }

    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_CALL_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + "+" + phone));
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                startActivity(callIntent);
            } else {
                Toast.makeText(Friends_Profile.this, " No Permission", Toast.LENGTH_SHORT).show();
            }
        }
    }


    /**
     * The object we have a list of
     */


    //    /**
//     * ViewHolder which also tracks the TextWatcher for an EditText
//     */
    class ViewHolder {
        public TextView textView;
        public TextView editText;
        public TextWatcher textWatcher;
    }

    // custom adapter class
    class EditTextAdapter extends BaseAdapter {
        ViewHolder holder = new ViewHolder();
        Context _c;
        //        Friends_Profile f=new Friends_Profile();
        ArrayList<ListItem> groupItem;

        public EditTextAdapter(Context context, ArrayList<ListItem> groupItem, int resource) {

            this._c = context;
            this.groupItem = groupItem;
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
//            holder.textView = (TextView) rowView.findViewById(R.id.labels);
            holder.editText = (TextView) rowView.findViewById(R.id.values1);
            ViewHolder holder = (ViewHolder) rowView.getTag();

            final ListItem listItem = groupItem.get(position);

            if(position==0)
            {
                holder.editText.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
                holder.editText.setTextSize(18);
                holder.editText.setTypeface(null, Typeface.BOLD);

            }
            holder.editText.setText(listItem.value);

//            holder.textView.setText(listItem.getLabel().toString());
//              holder.editText.setEnabled(false);

            return rowView;
        }

    }
}
