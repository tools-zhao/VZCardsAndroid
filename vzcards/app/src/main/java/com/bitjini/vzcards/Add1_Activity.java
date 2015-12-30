package com.bitjini.vzcards;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

/**
 * Created by bitjini on 28/12/15.
 */
public class Add1_Activity extends Fragment implements View.OnClickListener {

    Button referbtn;
    Context context;

    public View onCreateView(LayoutInflater inflater,ViewGroup container,
                             Bundle savedInstanceState)
    {
        View add1=inflater.inflate(R.layout.add_1_layout,container,false);

        context=add1.getContext();
        referbtn=(Button)add1.findViewById(R.id.refer_vzfrnd);
        referbtn.setOnClickListener(this);
        return add1;
    }
    public void onClick(View v)
    {
        switch (v.getId())
        {
            case R.id.refer_vzfrnd:
                Intent intent = new Intent(context, Refer_VZfriends.class);
                startActivity(intent);



        }
    }

}
