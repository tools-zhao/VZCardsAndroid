package com.bitjini.vzcards;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.FrameLayout;
import android.widget.ImageView;

/**
 * Created by bitjini on 11/5/16.
 */
public class SplashScreen  extends Activity {

    ImageView splashBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        splashBtn=(ImageView)findViewById(R.id.splashbtn);


        CountDownTimer countDownTimer = new CountDownTimer(2000, 1000) {
            public void onTick(long millisUntilFinished) {

            }

            public void onFinish() {
                Intent positveActivity = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(positveActivity);
                finish();
            }
        }.start();


    }
}
