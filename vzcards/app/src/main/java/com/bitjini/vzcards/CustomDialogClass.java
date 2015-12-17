package com.bitjini.vzcards;

import android.app.Activity;
import android.app.Dialog;
import android.app.usage.NetworkStats;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


/**
 * Created by VEENA on 12/8/2015.
 */
public class CustomDialogClass extends Dialog implements View.OnClickListener{

    public Activity c;
    public Dialog d;
    public Button ok,cancel,resend;
    public CustomDialogClass(Activity a) {
        super(a);
        this.c=a;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prompt);

        ok=(Button)findViewById(R.id.btnOK);
        resend=(Button)findViewById(R.id.btnResend);
        cancel=(Button)findViewById(R.id.btnCancel);

        ok.setOnClickListener(this);
        cancel.setOnClickListener(this);
        resend.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.btnOK:
                c.finish();
                Intent positveActivity = new Intent(c.getApplicationContext(),MainActivity.class);
                c.startActivity(positveActivity);

                break;
            case R.id.btnResend:
                    dismiss();
            case R.id.btnCancel:
                dismiss();
            default:

                        break;

        }
        dismiss();
    }
}
