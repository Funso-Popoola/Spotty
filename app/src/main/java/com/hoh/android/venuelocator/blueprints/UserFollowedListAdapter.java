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

import com.hoh.android.venuelocator.data.VenueLocatorContract.UserEntry;
import com.hoh.android.venuelocator.data.VenueLocatorContract.LeaderFollowerEntry;

/**
 * Created by funso on 3/4/15.
 */
public class UserFollowedListAdapter extends CursorAdapter {

    private Context context;

    public UserFollowedListAdapter(Context context, Cursor cursor, int flag){
        super(context, cursor, flag);
        this.context = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = (LayoutInflater.from(context)).inflate(R.layout.user_followed_item, parent, false);
        UserFollowedViewHolder viewHolder = new UserFollowedViewHolder(view, context);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        UserFollowedViewHolder viewHolder = (UserFollowedViewHolder) view.getTag();
        viewHolder.downloadUserImage(cursor.getString(cursor.getColumnIndex(UserEntry.COLUMN_IMG_URL)));
        viewHolder.fullNameTextView.setText(cursor.getString(cursor.getColumnIndex(UserEntry.COLUMN_USERNAME)));
        viewHolder.emailTextView.setText(cursor.getString(cursor.getColumnIndex(UserEntry.COLUMN_EMAIL)));
        final long rowId = cursor.getLong(cursor.getColumnIndex(LeaderFollowerEntry.TABLE_NAME + "." + LeaderFollowerEntry._ID));
        viewHolder.removeLeaderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.getContentResolver().delete(
                        LeaderFollowerEntry.buildFollowerLeaderUriWithId(rowId),
                        null,
                        null
                );
            }
        });
    }

}
