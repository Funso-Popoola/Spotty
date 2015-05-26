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
 * Created by funso on 5/23/15.Android
 */
public class UserToFollowViewHolder implements OnDownloadFinished{
    public Context context;
    public ImageView proposedUserImg;
    public TextView nameTextView;
    public TextView emailTextView;

    public UserToFollowViewHolder(View view, Context context){
        this.context = context;
        proposedUserImg = (ImageView) view.findViewById(R.id.proposed_user_image);
        nameTextView = (TextView)view.findViewById(R.id.proposed_user_name);
        emailTextView = (TextView)view.findViewById(R.id.proposed_user_email);
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
        proposedUserImg.setImageBitmap(bitmap);
    }

    @Override
    public Context getContext() {
        return context;
    }
}
