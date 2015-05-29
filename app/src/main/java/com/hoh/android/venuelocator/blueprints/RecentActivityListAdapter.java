package com.hoh.android.venuelocator.blueprints;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CursorAdapter;

import com.hoh.android.venuelocator.R;
import com.hoh.android.venuelocator.data.VenueLocatorContract.CheckingEntry;
import com.hoh.android.venuelocator.data.VenueLocatorContract.UserEntry;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by funso on 3/4/15.
 */
public class RecentActivityListAdapter extends CursorAdapter{

    private List<RecentVenueActivityItem> recentVenueActivityItems;
    private Context context;

    public RecentActivityListAdapter(Context context, Cursor cursor, int flag){
        super(context, cursor, flag);
        recentVenueActivityItems = new ArrayList<>();
        this.context = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = (LayoutInflater.from(context)).inflate(R.layout.recent_venue_activity_item, parent, false);
        RecentActivityViewHolder viewHolder = new RecentActivityViewHolder(view, context);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        RecentActivityViewHolder viewHolder = (RecentActivityViewHolder) view.getTag();
        viewHolder.downloadUserImage(cursor.getString(
                cursor.getColumnIndex(UserEntry.COLUMN_IMG_URL)));
        viewHolder.activityTextView.setText(
                cursor.getString(cursor.getColumnIndex(CheckingEntry.COLUMN_CHECK_TYPE)));
        viewHolder.activityTimeTextView.setText(Utility.getFriendlyDayString(context, Long.parseLong(cursor.getString(cursor.getColumnIndex(CheckingEntry.COLUMN_CREATED_AT)))));
    }
}
