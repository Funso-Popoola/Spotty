package com.hoh.android.venuelocator.blueprints;

import android.view.View;
import android.widget.TextView;

import com.hoh.android.venuelocator.R;

/**
 * Created by funso on 5/23/15.
 */
public class MovesViewHolder {

    public TextView dateTextView;
    public TextView activityTextView;
    public TextView locationTextView;

    public MovesViewHolder(View view){
        dateTextView = (TextView)view.findViewById(R.id.move_date_tv);
        activityTextView = (TextView)view.findViewById(R.id.move_activity_tv);
        locationTextView = (TextView)view.findViewById(R.id.move_location_tv);
    }
}
