package com.bitjini.vzcards;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by bitjini on 10/2/16.
 */
public class Feed_detail_has extends Fragment {

    TextView title, description,name;
    ImageView profilePhoto,item_photo;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View feed_has = inflater.inflate(R.layout.feed_detail_has, container, false);

        title = (TextView) feed_has.findViewById(R.id.title);
        description = (TextView) feed_has.findViewById(R.id.description);
        name = (TextView) feed_has.findViewById(R.id.name);
        profilePhoto = (ImageView) feed_has.findViewById(R.id.profilePic);
        item_photo=(ImageView) feed_has.findViewById(R.id.item_photo);
        //Retrieve the value
        String item = getArguments().getString("title");
        String desc = getArguments().getString("desc");
        String profileName = getArguments().getString("name");
        String photo = getArguments().getString("photo");
        String itemPic=getArguments().getString("item_photo");

        Log.e("photo url ",""+photo);
        title.setText(item);
        description.setText(desc);
        name.setText(profileName);
        profilePhoto.setImageBitmap(getBitmapFromURL(photo));
        item_photo.setImageBitmap(getBitmapFromURL(itemPic));

        return feed_has;
    }

    public static class Feed_detail_needs extends Fragment {

        TextView title, description,name;
        ImageView profilePhoto,item_photo;

        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View feed_needs = inflater.inflate(R.layout.feed_detail_need, container, false);

            title = (TextView) feed_needs.findViewById(R.id.title);
            description = (TextView) feed_needs.findViewById(R.id.description);
            name = (TextView) feed_needs.findViewById(R.id.name);
            profilePhoto = (ImageView) feed_needs.findViewById(R.id.profilepic);
            item_photo=(ImageView) feed_needs.findViewById(R.id.item_photo);
            //Retrieve the value
            String item = getArguments().getString("title");
            String desc = getArguments().getString("desc");
            String profileName = getArguments().getString("name");
            String photo = getArguments().getString("photo");
            String itemPic=getArguments().getString("item_photo");

            Log.e("photo url ",""+photo);
            title.setText(item);
            description.setText(desc);
            name.setText(profileName);
            profilePhoto.setImageBitmap(getBitmapFromURL(photo));
            item_photo.setImageBitmap(getBitmapFromURL(itemPic));

            return feed_needs;
        }
    }
    public static Bitmap getBitmapFromURL(String src) {
        try {
            Log.e("src", src);
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            Log.e("Bitmap","returned");
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("Exception",e.getMessage());
            return null;
        }
    }
}
