package com.bitjini.vzcards;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

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
    ProgressDialog progress;

    public DownloadImagesTask(Context c) {
        this.context = c;

    }
    @Override
    protected void onPreExecute() {
        progress = new ProgressDialog(this.context);
        progress.setMessage("Loading...");
        progress.setCancelable(false);
        progress.show();
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
        if(progress.isShowing())
        {
            progress.dismiss();
            progress=null;
        }

//        BlurBuilder b=new BlurBuilder();
//        Bitmap blurredBitmap = b.blur(context,result);
//        imageView.setImageBitmap(blurredBitmap);
        imageView.setImageBitmap(result);
    }

}
