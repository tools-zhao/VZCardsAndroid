package com.bitjini.vzcards;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by bitjini on 10/2/16.
 */
public class Feed_detail_has extends Activity implements View.OnClickListener {

    TextView title, description, name;
    ImageView profilePhoto, item_photo;
    Button referVZbtn, referContactbtn;

    String ticket_id_1, phone1, connector_vz_id;

    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
         setContentView(R.layout.feed_detail_has);


        title = (TextView)findViewById(R.id.title);
        description = (TextView) findViewById(R.id.description);
        name = (TextView) findViewById(R.id.name);
        profilePhoto = (ImageView) findViewById(R.id.profilePic);
        item_photo = (ImageView) findViewById(R.id.item_photo);
        // to make the textview scroll
        description.setMovementMethod(new ScrollingMovementMethod());
        referVZbtn = (Button) findViewById(R.id.refer_vzfrnd);
        referContactbtn = (Button) findViewById(R.id.refer_contact);

        referVZbtn.setOnClickListener(this);
        referContactbtn.setOnClickListener(this);

        //Retrieve the value
        Intent intent=getIntent();
        String item = intent.getStringExtra("titleHas");
        String desc = intent.getStringExtra("descHas");
        String profileName = intent.getStringExtra("nameHas");
        String photo = intent.getStringExtra("photoHas");
        String itemPic = intent.getStringExtra("item_photoHas");

        ticket_id_1 = intent.getStringExtra("ticket_idHas");
        phone1 =intent.getStringExtra("phone1Has");
        connector_vz_id = intent.getStringExtra("connector_vz_idHas");


//        int currentapiVersion = android.os.Build.VERSION.SDK_INT;
//        if (currentapiVersion >= android.os.Build.VERSION_CODES.LOLLIPOP){
//            // Do something for lollipop and above versions
//            referVZbtn.setBackgroundResource(R.drawable.ripple_effect);
//            referContactbtn.setBackgroundResource(R.drawable.ripple_effect);
//        } else{
//            // do something for phones running an SDK before lollipop
//            referVZbtn.setBackgroundResource(R.drawable.addimage);
//            referContactbtn.setBackgroundResource(R.drawable.addimage);
//        }
//        Log.e("photo url ", "" + photo);
        title.setText(item);
        description.setText(desc);
        name.setText(profileName);

        if(!itemPic.isEmpty())
        {
            Picasso.with(getApplicationContext()).load(itemPic).resize(500,350).into(item_photo);
//            item_photo.setTag(itemPic);
//            new DownloadImagesTask(getActivity()).execute(item_photo);
        } else
        {
            item_photo.setImageResource(R.drawable.no_pic_placeholder_full);
        }

        if(!photo.isEmpty())
        {

            Picasso.with(getApplicationContext()).load(photo).fit().into(profilePhoto);}
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

                Log.e("Refer to needs","");
                NeedFeeds ldf = new NeedFeeds();
                //Retrieve the value from feeds
                Intent i=getIntent();
                String itemHAs = i.getStringExtra("titleHas");
                String descHas = i.getStringExtra("descHas");
                String profileNameHas = i.getStringExtra("nameHas");
                String photo = i.getStringExtra("photoHas");
                String itemPicHas = i.getStringExtra("item_photoHas");

                String ticket_id_1Has = i.getStringExtra("ticket_idHas");
                String phone1Has = i.getStringExtra("phone1Has");
                String connector_vz_idHas = i.getStringExtra("connector_vz_idHas");
                String questionHas = i.getStringExtra("questionHas");

                Log.e("itemPic ",""+itemPicHas);
                // Send values to HasFeeds
                Bundle args = new Bundle();
                args.putString("titleHas", itemHAs);
                args.putString("descHas", descHas);
                args.putString("nameHas", profileNameHas);
                args.putString("photoHas", photo);
                args.putString("ticket_idHas", ticket_id_1Has);
                args.putString("item_photoHas", itemPicHas);
                args.putString("phone1Has", phone1Has);
                args.putString("connector_vz_idHas", connector_vz_idHas);
                args.putString("questionHas",questionHas);

                ldf.setArguments(args);

                //Inflate the fragment
                getFragmentManager().beginTransaction().add(R.id.feed_detail_has_Frame, ldf).addToBackStack(ldf.toString())
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
                getFragmentManager().beginTransaction().replace(R.id.feed_detail_has_Frame, connect).addToBackStack(connect.toString())
                        .commit();
                break;


        }
    }
}


