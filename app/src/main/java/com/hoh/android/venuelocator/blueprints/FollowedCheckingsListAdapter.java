package com.hoh.android.venuelocator.blueprints;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;

import com.hoh.android.venuelocator.R;
import com.hoh.android.venuelocator.data.VenueLocatorContract.CheckingEntry;
import com.hoh.android.venuelocator.data.VenueLocatorContract.UserEntry;
import com.hoh.android.venuelocator.data.VenueLocatorContract.VenueEntry;
/**
 * Created by funso on 3/5/15.Android
 */
public class FollowedCheckingsListAdapter extends CursorAdapter {

    private Context context;

    public FollowedCheckingsListAdapter(Context context, Cursor cursor, int flag){
        super(context, cursor, flag);
        this.context = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = (LayoutInflater.from(context)).inflate(R.layout.followed_checkings_item, parent, false);
        FollowedCheckingsViewHolder viewHolder = new FollowedCheckingsViewHolder(view, this.context);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        if (cursor != null && !cursor.isClosed()){
            FollowedCheckingsViewHolder viewHolder = (FollowedCheckingsViewHolder) view.getTag();

            viewHolder.downloadUserImage(cursor.getString(
                    cursor.getColumnIndex(UserEntry.TABLE_NAME + "." + UserEntry.COLUMN_IMG_URL)));
            viewHolder.userActivityTextView.setText(cursor.getString(
                    cursor.getColumnIndex(VenueEntry.TABLE_NAME + "." + VenueEntry.COLUMN_ADDRESS)
            ));
            viewHolder.userActivityTextView.setText(cursor.getString(
                    cursor.getColumnIndex(CheckingEntry.TABLE_NAME + "." + CheckingEntry.COLUMN_CHECK_TYPE)
            ));
            viewHolder.userActivityTimeStamp.setText(cursor.getString(
                    cursor.getColumnIndex(CheckingEntry.TABLE_NAME + "." + CheckingEntry.COLUMN_CREATED_AT)
            ));
        }

    }
}
