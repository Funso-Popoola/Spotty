package com.hoh.android.venuelocator.blueprints;

import android.view.View;
import android.widget.TextView;

import com.hoh.android.venuelocator.R;

import org.w3c.dom.Text;

/**
 * Created by funso on 5/22/15.
 */
public class VenueViewHolder {

    public TextView nameTextView;
    public TextView addressTextView;
    public TextView distanceTextView;

    public VenueViewHolder(View view){
        nameTextView = (TextView) view.findViewById(R.id.venue_name_tv);
        addressTextView = (TextView) view.findViewById(R.id.venue_address_tv);
        distanceTextView = (TextView) view.findViewById(R.id.venue_distance_tv);
    }


}
