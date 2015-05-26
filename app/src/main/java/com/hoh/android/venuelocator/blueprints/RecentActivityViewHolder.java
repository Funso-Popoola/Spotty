package com.hoh.android.venuelocator.blueprints;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hoh.android.venuelocator.R;
import com.hoh.android.venuelocator.async.DownloadImageTask;

import org.json.JSONException;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by funso on 5/23/15.
 */
public class RecentActivityViewHolder implements OnDownloadFinished{

    public ImageView actorImageView;
    public TextView activityTextView;
    public TextView activityTimeTextView;

    private Context context;

    public RecentActivityViewHolder(View view, Context context){
        actorImageView = (ImageView) view.findViewById(R.id.venue_actor_img_view);
        activityTextView = (TextView) view.findViewById(R.id.recent_activity_tv);
        activityTimeTextView = (TextView) view.findViewById(R.id.recent_activity_time_tv);

        this.context = context;
    }

    public void downloadUserImage(String urlString){
        try{
            final URL url = new URL(urlString);
            (new DownloadImageTask(this.context, this)).execute(url);
        }
        catch (MalformedURLException ex){
            ex.printStackTrace();
        }
    }

    @Override
    public void parseResponse(String url, String responseString) throws JSONException {

    }

    @Override
    public void setImageBitmap(String url, Bitmap bitmap) {
        actorImageView.setImageBitmap(bitmap);
    }

    @Override
    public Context getContext() {
        return context;
    }
}
