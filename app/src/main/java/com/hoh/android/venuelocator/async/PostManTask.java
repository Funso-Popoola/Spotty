package com.hoh.android.venuelocator.async;

import android.os.AsyncTask;
import android.util.Log;

import com.hoh.android.venuelocator.blueprints.OnPostFinish;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * Created by funso on 3/4/15.
 */
public class PostManTask extends AsyncTask<PostRequest, Void, String> {

    private final String LOG_TAG = PostManTask.class.getSimpleName();
    private OnPostFinish callback;

    public PostManTask(OnPostFinish callback){
        this.callback = callback;
    }

    @Override
    protected String doInBackground(PostRequest... params) {

        PostRequest request = params[0];
        HttpClient client = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(request.getUrl().toString());

        HttpResponse response = null;

        try{
            httpPost.setEntity(new UrlEncodedFormEntity(request.getData()));
            response = client.execute(httpPost);
            return EntityUtils.toString(response.getEntity());
        }
        catch (Exception ex){
            ex.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        if (null != s){
            try{
                callback.parsePostResponse(s);
            }
            catch (JSONException ex){
                ex.printStackTrace();
            }
        }
    }

    @Override
    protected void onCancelled(String s) {
        super.onCancelled(s);
        Log.i(LOG_TAG, "Cancelled With Extra: " + s);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }
}
