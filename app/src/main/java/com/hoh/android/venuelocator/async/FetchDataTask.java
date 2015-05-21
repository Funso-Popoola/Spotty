package com.hoh.android.venuelocator.async;

import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.hoh.android.venuelocator.blueprints.OnDownloadFinished;
import com.hoh.android.venuelocator.blueprints.Utility;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by funso on 3/4/15.
 */
public class FetchDataTask extends AsyncTask<String, Void, String> {

    private final String LOG_TAG = FetchDataTask.class.getSimpleName();
    private OnDownloadFinished callback;
    private String url;
    private LinearLayout spinnerLayout;

    public FetchDataTask(OnDownloadFinished callback, LinearLayout spinnerLayout) {
        this.callback = callback;
        this.spinnerLayout = spinnerLayout;
    }

    @Override
    protected void onPreExecute() {
        this.spinnerLayout.setVisibility(View.VISIBLE);
    }

    @Override
    protected String doInBackground(String... params) {
        String fetchedDataString = null;
        URL url = null;
        HttpURLConnection urlConnection = null;
        try{
            url = new URL(params[0]);
            Log.i(LOG_TAG, params[0]);
        }
        catch (MalformedURLException malUrlEx){
            malUrlEx.printStackTrace();
        }

        try{
            if (null == url){
                return null;
            }
            this.url = url.toString();
            urlConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = urlConnection.getInputStream();
            fetchedDataString = Utility.getNetworkResponse(inputStream);
        }
        catch (IOException ioEx){
            ioEx.printStackTrace();
        }
        finally {
            if (urlConnection != null){
                urlConnection.disconnect();
            }
        }

        return fetchedDataString;
    }

    @Override
    protected void onPostExecute(String s) {
        if (null != s){
            try{
                callback.parseResponse(this.url, s);
            }
            catch (JSONException jsonEx){
                jsonEx.printStackTrace();
            }
        }
    }

    @Override
    protected void onCancelled(String s) {
        super.onCancelled(s);
        Log.i(LOG_TAG, "Data Fetch Cancelled WITH INFO: " + s);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        Log.i(LOG_TAG, "Data Fetch Cancelled WITHOUT INFO");
    }
}
