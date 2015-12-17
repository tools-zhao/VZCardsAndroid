package com.bitjini.vzcards;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by VEENA on 11/20/2015.
 */
public class DisplayContact extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_contact);

        TextView txtName=(TextView) findViewById(R.id.txtName);
        TextView txtPhone=(TextView) findViewById(R.id.txtNumber);
        ImageView contactImage=(ImageView)findViewById(R.id.img);
        Button close=(Button) findViewById(R.id.close);

        Intent i=getIntent();
        // Receiving data
        String name=i.getStringExtra("name");
        String phoneNo=i.getStringExtra("phoneNo");

        Bundle extras=getIntent().getExtras();
        Bitmap bmp=(Bitmap)extras.getParcelable("photo");

        Log.e("Second Screen", name + "." +phoneNo+"."+bmp);

        //Displaying data
        txtName.setText(name);
        txtPhone.setText(phoneNo);
        contactImage.setImageBitmap(bmp);


        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
