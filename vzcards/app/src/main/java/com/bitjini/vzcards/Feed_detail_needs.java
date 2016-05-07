package com.bitjini.vzcards;

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

public class Feed_detail_needs extends Fragment implements View.OnClickListener {

    TextView title, description,name;
    ImageView profilePhoto,item_photo;
    Button referVZbtn, referContactbtn;


    String ticket_id_1, phone1, connector_vz_id;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View feed_needs = inflater.inflate(R.layout.feed_detail_need, container, false);

        title = (TextView) feed_needs.findViewById(R.id.title);
        description = (TextView) feed_needs.findViewById(R.id.description);
        name = (TextView) feed_needs.findViewById(R.id.name);
        profilePhoto = (ImageView) feed_needs.findViewById(R.id.profilepic);
        item_photo=(ImageView) feed_needs.findViewById(R.id.item_photo);

        referVZbtn = (Button) feed_needs.findViewById(R.id.refer_vzfrnd);
        referContactbtn = (Button) feed_needs.findViewById(R.id.refer_contact);

        referVZbtn.setOnClickListener(this);
        referContactbtn.setOnClickListener(this);
        //Retrieve the value
        String item = getArguments().getString("title");
        String desc = getArguments().getString("desc");
        String profileName = getArguments().getString("name");
        String photo = getArguments().getString("photo");
        String itemPic=getArguments().getString("item_photo");
        ticket_id_1 = getArguments().getString("ticket_id");
        phone1 = getArguments().getString("phone1");
        connector_vz_id = getArguments().getString("connector_vz_id");

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


        return feed_needs;
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
                break;

            case R.id.refer_contact:

                // send the parameters to refer contact
                ReferContacts connect = new ReferContacts();

                Bundle args = new Bundle();
                args.putString("ticket_id", ticket_id_1);
                args.putString("phone1", phone1);
                args.putString("connector_vz_id", connector_vz_id);

                connect.setArguments(args);

                //Inflate the fragment
                getFragmentManager().beginTransaction().add(R.id.feed_detail_need_frame, connect).addToBackStack(connect.toString())
                        .commit();
                break;


        }
    }
}