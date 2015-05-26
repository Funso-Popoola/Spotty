package com.hoh.android.venuelocator.blueprints;


import android.content.Context;
import android.graphics.Bitmap;

import org.json.JSONException;

/**
 * Created by funso on 3/4/15.
 */
public interface OnDownloadFinished {

    public void parseResponse(String url, String responseString) throws JSONException;
    public void setImageBitmap(String url, Bitmap bitmap);
    public Context getContext();

}
