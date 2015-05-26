package com.hoh.android.venuelocator.blueprints;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.hoh.android.venuelocator.R;

import java.util.ArrayList;
import java.util.List;

import com.hoh.android.venuelocator.data.VenueLocatorContract.VenueEntry;

/**
 * Created by funso on 3/4/15.
 */
public class VenueListAdapter extends CursorAdapter {

    // a list of items
    private List<VenueItem> venueItems;
    private Context context;

    public VenueListAdapter(Context context, Cursor cursor, int flag){
        super(context, cursor, 0);
        this.context = context;
        venueItems = new ArrayList<>();
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = (LayoutInflater.from(context)).inflate(R.layout.venue_item, parent, false);
        VenueViewHolder viewHolder = new VenueViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        if (cursor != null && !cursor.isClosed()){
            String name = cursor.getString(cursor.getColumnIndex(VenueEntry.COLUMN_NAME));
            String address = cursor.getString(cursor.getColumnIndex(VenueEntry.COLUMN_ADDRESS));
            double lat = cursor.getDouble(cursor.getColumnIndex(VenueEntry.COLUMN_LAT));
            double lng = cursor.getDouble(cursor.getColumnIndex(VenueEntry.COLUMN_LNG));

            double distance = -1;
            String estimatedDistance = "Distance Unknown";

            CustomPreferenceManager preferenceManager = new CustomPreferenceManager(context);
            double user_lat = preferenceManager.getCurrentLat();
            double user_lng = preferenceManager.getCurrentLng();

            if (user_lat != -1 && user_lng != -1){
                distance = Math.sqrt(Math.pow(user_lat - lat, 2) +
                        Math.pow(user_lng - lng, 2));
                estimatedDistance = Math.round(distance * 1000) + "Km From You";
            }

            VenueViewHolder viewHolder = (VenueViewHolder) view.getTag();
            viewHolder.nameTextView.setText(name);
            viewHolder.addressTextView.setText(address);
            viewHolder.distanceTextView.setText(estimatedDistance);

        }

    }
}
