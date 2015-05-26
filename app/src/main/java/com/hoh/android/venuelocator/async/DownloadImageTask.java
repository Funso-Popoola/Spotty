package com.hoh.android.venuelocator.async;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.hoh.android.venuelocator.blueprints.CustomPreferenceManager;
import com.hoh.android.venuelocator.blueprints.OnDownloadFinished;

import java.net.URL;

/**
 * Created by funso on 3/16/15.
 */
public class DownloadImageTask extends AsyncTask<URL, Void, Bitmap> {

//    private CustomPreferenceManager preferenceManager;
    private OnDownloadFinished callback;
    private URL url;

    public DownloadImageTask(Context context, OnDownloadFinished callback){
//        this.preferenceManager = new CustomPreferenceManager(context);
        this.callback = callback;
    }

    @Override
    protected Bitmap doInBackground(URL... params) {
        Bitmap bitmap = null;
        URL url = params[0];
        this.url = url;
        try{
            bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        }
        catch (Exception ex){
            ex.printStackTrace();
        }
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (null != bitmap){
            callback.setImageBitmap(url.toString(), bitmap);
        }
    }
}