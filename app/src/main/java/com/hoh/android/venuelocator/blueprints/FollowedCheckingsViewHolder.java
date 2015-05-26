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
public class FollowedCheckingsViewHolder implements OnDownloadFinished{

    public Context context;

    public ImageView userProfileImage;
    public TextView userActivityTextView;
    public TextView userLocationTextView;
    public TextView userActivityTimeStamp;

    public FollowedCheckingsViewHolder(View view, Context context){
        userProfileImage = (ImageView)view.findViewById(R.id.followed_user_profile_image);
        userActivityTextView = (TextView)view.findViewById(R.id.followed_activity_tv);
        userLocationTextView = (TextView)view.findViewById(R.id.followed_checking_venue_tv);
        userActivityTimeStamp = (TextView)view.findViewById(R.id.followed_time_stamp_tv);
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
        userProfileImage.setImageBitmap(bitmap);
    }

    @Override
    public Context getContext() {
        return context;
    }
}
