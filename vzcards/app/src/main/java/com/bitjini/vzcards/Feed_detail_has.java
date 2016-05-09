package com.bitjini.vzcards;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
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

/**
 * Created by bitjini on 10/2/16.
 */
public class Feed_detail_has extends Fragment implements View.OnClickListener {

    TextView title, description, name;
    ImageView profilePhoto, item_photo;
    Button referVZbtn, referContactbtn;

    String ticket_id_1, phone1, connector_vz_id;
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View feed_has = inflater.inflate(R.layout.feed_detail_has, container, false);


        title = (TextView) feed_has.findViewById(R.id.title);
        description = (TextView) feed_has.findViewById(R.id.description);
        name = (TextView) feed_has.findViewById(R.id.name);
        profilePhoto = (ImageView) feed_has.findViewById(R.id.profilePic);
        item_photo = (ImageView) feed_has.findViewById(R.id.item_photo);

        referVZbtn = (Button) feed_has.findViewById(R.id.refer_vzfrnd);
        referContactbtn = (Button) feed_has.findViewById(R.id.refer_contact);

        referVZbtn.setOnClickListener(this);
        referContactbtn.setOnClickListener(this);

        //Retrieve the value
        String item = getArguments().getString("titleHas");
        String desc = getArguments().getString("descHas");
        String profileName = getArguments().getString("nameHas");
        String photo = getArguments().getString("photoHas");
        String itemPic = getArguments().getString("item_photoHas");

        ticket_id_1 = getArguments().getString("ticket_idHas");
        phone1 = getArguments().getString("phone1Has");
        connector_vz_id = getArguments().getString("connector_vz_idHas");



        Log.e("photo url ", "" + photo);
        title.setText(item);
        description.setText(desc);
        name.setText(profileName);

        if(!itemPic.isEmpty())
        {
            Picasso.with(getActivity()).load(itemPic).into(item_photo);
//            item_photo.setTag(itemPic);
//            new DownloadImagesTask(getActivity()).execute(item_photo);
        } else
        {
            item_photo.setImageResource(R.drawable.no_pic_placeholder_with_border_800x800);
        }

        if(!photo.isEmpty())
        {

            Picasso.with(getActivity()).load(photo).into(profilePhoto);}
        else  {
            profilePhoto.setImageResource(R.drawable.profile_pic_placeholder);
//            profilePhoto.setTag(photo);
//            new DownloadImagesTask(getActivity()).execute(profilePhoto);


        }
        return feed_has;
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
                String itemHAs = getArguments().getString("titleHas");
                String descHas = getArguments().getString("descHas");
                String profileNameHas = getArguments().getString("nameHas");
                String photo = getArguments().getString("photoHas");
                String itemPicHas = getArguments().getString("item_photoHas");

                String ticket_id_1Has = getArguments().getString("ticket_idHas");
                String phone1Has = getArguments().getString("phone1Has");
                String connector_vz_idHas = getArguments().getString("connector_vz_idHas");
                String questionHas = getArguments().getString("questionHas");

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
                getFragmentManager().beginTransaction().add(R.id.feed_detail_has_Frame, connect).addToBackStack(connect.toString())
                        .commit();
                break;


        }
    }
}


