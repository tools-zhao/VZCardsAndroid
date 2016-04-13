package com.bitjini.vzcards;

import android.animation.ObjectAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadImagesTask extends AsyncTask<ImageView, Void, Bitmap> {

    ImageView imageView = null;
    Bitmap bmp =null;
    Context context;
    ProgressBar progressBar;

    public DownloadImagesTask(Context c) {
        this.context = c;

    }
    protected void onPreExecute() {
      if(progressBar.getTag()!=null) {
            ObjectAnimator animation = ObjectAnimator.ofInt(progressBar, "progress", 0, 500); // see this max value coming back here, we animale towards that value
            animation.setDuration(5000); //in milliseconds
            animation.setInterpolator(new DecelerateInterpolator());
            animation.start();
         }
    }


    @Override
    protected Bitmap doInBackground(ImageView... imageViews) {
        this.imageView = imageViews[0];
        return download_Image((String)imageView.getTag());
    }



    private Bitmap download_Image(String url) {
        try{
            URL ulrn = new URL(url);
            HttpURLConnection con = (HttpURLConnection)ulrn.openConnection();
            InputStream is = con.getInputStream();
            bmp = BitmapFactory.decodeStream(is);

            if (null != bmp)
                return bmp;

        }catch(Exception e){}
        return bmp;
    }
    @Override
    protected void onPostExecute(Bitmap result) {
        progressBar.clearAnimation();
        if(result!=null){
        imageView.setImageBitmap(result);}
    }

}
