package com.bitjini.vzcards;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * Created by bitjini on 28/12/15.
 */
public class Add1_Activity extends Fragment implements View.OnClickListener {

    Button referVZbtn,referContactbtn;
    Context context;

    public View onCreateView(LayoutInflater inflater,ViewGroup container,
                             Bundle savedInstanceState)
    {
        View add1=inflater.inflate(R.layout.feed_detail_has,container,false);

        context=add1.getContext();

        referVZbtn=(Button)add1.findViewById(R.id.refer_vzfrnd);
        referContactbtn=(Button)add1.findViewById(R.id.refer_contact);

        referVZbtn.setOnClickListener(this);
        referContactbtn.setOnClickListener(this);

        return add1;
    }
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.refer_vzfrnd:
                Intent intent = new Intent(context, Refer_VZfriends.class);
                startActivity(intent);
                break;

            case R.id.refer_contact:
                Intent intent1=new Intent(context,ContactsMainActivity.class);
                startActivity(intent1);
                break;




        }
    }

}
