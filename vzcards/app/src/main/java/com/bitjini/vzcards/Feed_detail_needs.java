package com.bitjini.vzcards;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class Feed_detail_needs extends Activity implements View.OnClickListener {

    TextView title, description,name;
    ImageView profilePhoto,item_photo;
    Button referVZbtn, referContactbtn;


    String ticket_id_1, phone1, connector_vz_id;
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
   setContentView(R.layout.feed_detail_need);

        title = (TextView) findViewById(R.id.title);
        description = (TextView) findViewById(R.id.description);
        name = (TextView) findViewById(R.id.name);
        profilePhoto = (ImageView) findViewById(R.id.profilepic);
        item_photo=(ImageView) findViewById(R.id.item_photo);

        referVZbtn = (Button) findViewById(R.id.refer_vzfrnd);
        referContactbtn = (Button) findViewById(R.id.refer_contact);

        referVZbtn.setOnClickListener(this);
        referContactbtn.setOnClickListener(this);
        //Retrieve the value
        Intent intent=getIntent();
        String item = intent.getStringExtra("titleNeeds");
        String desc = intent.getStringExtra("descNeeds");
        String profileName = intent.getStringExtra("nameNeeds");
        String photo = intent.getStringExtra("photoNeeds");
        String itemPic=intent.getStringExtra("item_photoNeeds");
        ticket_id_1 = intent.getStringExtra("ticket_idNeeds");
        phone1 = intent.getStringExtra("phone1Needs");
        connector_vz_id =intent.getStringExtra("connector_vz_idNeeds");

        Log.e("photo url ", "" + photo);
        title.setText(item);
        description.setText(desc);
        name.setText(profileName);

//        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
//        if (currentapiVersion >= android.os.Build.VERSION_CODES.LOLLIPOP){
//            // Do something for lollipop and above versions
//            referVZbtn.setBackgroundResource(R.drawable.ripple_effect_red);
//            referContactbtn.setBackgroundResource(R.drawable.ripple_effect_red);
//        } else{
//            // do something for phones running an SDK before lollipop
//            referVZbtn.setBackgroundResource(R.drawable.addimage_red);
//            referContactbtn.setBackgroundResource(R.drawable.addimage_red);
//        }
        if(!itemPic.isEmpty())
        {
            Picasso.with(getApplicationContext()).load(itemPic).resize(480,350).into(item_photo);
//            item_photo.setTag(itemPic);
//            new DownloadImagesTask(getActivity()).execute(item_photo);
        } else
        {
            item_photo.setImageResource(R.drawable.no_pic_placeholder_full);
        }

        if(!photo.isEmpty())
        {

            Picasso.with(getApplicationContext()).load(photo).into(profilePhoto);}
        else  {
            profilePhoto.setImageResource(R.drawable.profile_pic_placeholder);
//            profilePhoto.setTag(photo);
//            new DownloadImagesTask(getActivity()).execute(profilePhoto);


        }


    }
    public void onClick(View v) {
        switch (v.getId()) {
//            case R.id.refer_vzfrnd:
//                Intent intent = new Intent(getActivity(), Refer_VZfriends.class);
//
//                intent.putExtra("ticket_id", ticket_id_1);
//                intent.putExtra("phone1", phone1);
//                intent.putExtra("connector_vz_id", connector_vz_id);
//                startActivity(intent);
//                break;
            case R.id.refer_vzfrnd:
               Log.e("Refer to has","");
                HasFeeds ldf = new HasFeeds();
                //Retrieve the value from feeds
                Intent i=getIntent();
                String itemNeeds = i.getStringExtra("titleNeeds");
                String descNeeds = i.getStringExtra("descNeeds");
                String profileNameNeeds = i.getStringExtra("nameNeeds");
                String photoNeeds = i.getStringExtra("photoNeeds");
                String itemPicNeeds=i.getStringExtra("item_photoNeeds");
                String ticket_id_1Needs = i.getStringExtra("ticket_idNeeds");
                String phone1Needs = i.getStringExtra("phone1Needs");
                String connector_vz_idNeeds = i.getStringExtra("connector_vz_idNeeds");
                String questionNeeds = i.getStringExtra("questionNeeds");

                // Send values to HasFeeds
                Bundle args = new Bundle();
                // sending values of needs
                args.putString("titleNeeds", itemNeeds);
                args.putString("descNeeds", descNeeds);
                args.putString("nameNeeds", profileNameNeeds);
                args.putString("photoNeeds", photoNeeds);
                args.putString("ticket_idNeeds", ticket_id_1Needs);
                args.putString("item_photoNeeds", itemPicNeeds);
                args.putString("phone1Needs", phone1Needs);
                args.putString("connector_vz_idNeeds", connector_vz_idNeeds);
                args.putString("questionNeeds",questionNeeds);
                ldf.setArguments(args);
                ldf.setArguments(args);

                //Inflate the fragment
                getFragmentManager().beginTransaction().replace(R.id.feed_detail_need_frame, ldf).addToBackStack(ldf.toString())
                        .commit();
                break;

            case R.id.refer_contact:

                // send the parameters to refer contact
                ReferContacts connect = new ReferContacts();

                Bundle args2 = new Bundle();
                args2.putString("ticket_id", ticket_id_1);
                args2.putString("phone1", phone1);
                args2.putString("connector_vz_id", connector_vz_id);

                connect.setArguments(args2);

                //Inflate the fragment
                getFragmentManager().beginTransaction().replace(R.id.feed_detail_need_frame, connect).addToBackStack(connect.toString())
                        .commit();
                break;


        }
    }
}