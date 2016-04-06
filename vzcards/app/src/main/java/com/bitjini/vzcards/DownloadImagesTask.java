package com.bitjini.vzcards;

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


    public DownloadImagesTask(Context c) {
        this.context = c;

    }


    @Override
    protected Bitmap doInBackground(ImageView... imageViews) {
        this.imageView = imageViews[0];
        return download_Image((String)imageView.getTag());
    }

    @Override
    protected void onPostExecute(Bitmap result) {

        imageView.setImageBitmap(result);
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
    //decodes image and scales it to reduce memory consumption
    private Bitmap decodeFile(File f){
        try {
            //decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f),null,o);

            //Find the correct scale value.  // The new size we want to scale to
            final int REQUIRED_SIZE=1024;
            // Find the correct scale value.
            int width_tmp=o.outWidth, height_tmp=o.outHeight;
            int scale=1;
            while(true){
                if(width_tmp<REQUIRED_SIZE || height_tmp<REQUIRED_SIZE)
                    break;
                width_tmp/=2;
                height_tmp/=2;
                scale*=2;
            }

            //decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize=scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {}
        return null;
    }


}
