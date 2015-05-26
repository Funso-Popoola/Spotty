package com.hoh.android.venuelocator.blueprints;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hoh.android.venuelocator.R;
import com.hoh.android.venuelocator.async.DownloadImageTask;

import org.json.JSONException;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by funso on 5/23/15.Android
 */
public class UserFollowedViewHolder implements OnDownloadFinished{

    public Context context;

    public ImageView userProfileImg;
    public TextView fullNameTextView;
    public TextView emailTextView;
    public Button removeLeaderBtn;

    public UserFollowedViewHolder(View view, Context context){
        this.context = context;

        userProfileImg = (ImageView) view.findViewById(R.id.user_profile_image_view);
        fullNameTextView = (TextView) view.findViewById(R.id.full_name_tv);
        emailTextView = (TextView) view.findViewById(R.id.email_tv);
        removeLeaderBtn = (Button) view.findViewById(R.id.remove_leader_btn);
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
        userProfileImg.setImageBitmap(bitmap);
    }

    @Override
    public Context getContext() {
        return context;
    }
}
