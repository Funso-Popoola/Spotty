package com.hoh.android.venuelocator.blueprints;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.hoh.android.venuelocator.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by funso on 3/5/15.
 */
public class FollowedCheckingsListAdapter extends BaseAdapter {

    private Context context;
    private List<RecentVenueActivityItem> followedCheckingsItems;

    public FollowedCheckingsListAdapter(Context context){
        this.context = context;
        this.followedCheckingsItems = new ArrayList<>();
    }

    public void add(RecentVenueActivityItem item){
        followedCheckingsItems.add(item);
    }

    @Override
    public int getCount() {
        return followedCheckingsItems.size();
    }

    @Override
    public Object getItem(int position) {
        return followedCheckingsItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        RecentVenueActivityItem item = followedCheckingsItems.get(position);

        View view = convertView;
        if (view == null){
            view = (LayoutInflater.from(context)).inflate(R.layout.followed_checkings_item, parent, false);
        }

        final ImageView userProfileImage = (ImageView)view.findViewById(R.id.followed_user_profile_image);
        final TextView userActivityTextView = (TextView)view.findViewById(R.id.followed_activity_tv);
        final TextView userLocationTextView = (TextView)view.findViewById(R.id.followed_checking_venue_tv);
        final TextView userActivityTimeStamp = (TextView)view.findViewById(R.id.followed_time_stamp_tv);

        //userProfileImage.setImageDrawable(new BitmapDrawable());
        //userActivityTextView.setText("");
        userLocationTextView.setText(item.getVenue().getAddress());
        userActivityTimeStamp.setText(item.getTimeStamp());

        return view;
    }
}
