package com.bitjini.vzcards;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;

/**
 * Created by bitjini on 18/10/16.
 */

public class CheckDensity {
    String densityStr;
    Activity activity;
    public CheckDensity(Activity activity) {
        this.activity=activity;

    }

    public int getDensity() {
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);
        int density = metrics.densityDpi;
//        Toast.makeText(getActivity(),"Density   "+density,Toast.LENGTH_LONG).show();
        System.out.println("Density  ....... : "+density);
        System.out.println("High Density........ : "+DisplayMetrics.DENSITY_HIGH+"Low Density....... : "+DisplayMetrics.DENSITY_LOW+"Medium Density........ : "+DisplayMetrics.DENSITY_MEDIUM);

        switch (density) {
            case DisplayMetrics.DENSITY_HIGH:

                Log.e("Density is high ", "" + String.valueOf(density));


                break;
            case 480:
                Log.e("Density is very high ", "" + String.valueOf(density));
                break;
            case DisplayMetrics.DENSITY_MEDIUM:
                Log.e("Density is medium ", "" + String.valueOf(density));
                break;
            case DisplayMetrics.DENSITY_LOW:
                Log.e("Density is low ", "" + String.valueOf(density));
                break;
            default:
                Log.e("Density is neither h l ", "" + String.valueOf(density));
                break;
        }
        return density;
    }
}
