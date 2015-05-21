package com.hoh.android.venuelocator.blueprints;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hoh.android.venuelocator.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by funso on 3/4/15.
 */
public class VenueListAdapter extends BaseAdapter {

    // a list of items
    private List<VenueItem> venueItems;
    private Context context;

    public VenueListAdapter(Context context){
        this.context = context;
        venueItems = new ArrayList<>();
    }

    public void add(VenueItem venueItem){
        venueItems.add(venueItem);
        notifyDataSetChanged();
    }

    public void clear(){
        venueItems.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return this.venueItems.size();
    }

    @Override
    public Object getItem(int position) {
        return venueItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // get the current venueItem
        final VenueItem currentVenueItem = venueItems.get(position);

        View view = convertView;

        // if the returned convert view is not already in use, create afresh
        if (view == null){
            view = (LayoutInflater.from(context)).inflate(R.layout.venue_item, parent, false);
        }

        // fill in the venue item data

        // grab the component views in venue_item.xml and set their content to the
        TextView venueNameTextView = (TextView) view.findViewById(R.id.venue_name_tv);
        TextView venueAddressTextView = (TextView)view.findViewById(R.id.venue_address_tv);
        TextView venueDistanceTextView = (TextView)view.findViewById(R.id.venue_distance_tv);

        // data in currentViewItem
        double distance = -1;
        String estimatedDistance = "Distance Unknown";

        CustomPreferenceManager preferenceManager = new CustomPreferenceManager(context);
        double lat = preferenceManager.getCurrentLat();
        double lng = preferenceManager.getCurrentLng();

        if (lat != -1 && lng != -1){
            distance = Math.sqrt(Math.pow(lat - currentVenueItem.getLatitude(), 2) +
                    Math.pow(lng - currentVenueItem.getLongitude(), 2));
            estimatedDistance = Math.round(distance * 1000) + "Km From You";
        }

        venueDistanceTextView.setText(estimatedDistance);
        venueNameTextView.setText(currentVenueItem.getName());
        venueAddressTextView.setText(currentVenueItem.getAddress());

        return view;
    }
}
