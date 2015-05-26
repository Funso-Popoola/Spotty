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

import com.hoh.android.venuelocator.data.VenueLocatorContract.CheckingEntry;
import com.hoh.android.venuelocator.data.VenueLocatorContract.UserEntry;
import com.hoh.android.venuelocator.data.VenueLocatorContract.VenueEntry;

/**
 * Created by funso on 3/4/15.
 */
public class MovesListAdapter extends CursorAdapter{

    private Context context;
    private List<MovesItem> movesItems;

    public MovesListAdapter(Context context, Cursor cursor, int flag){
        super(context, cursor, flag);
        this.context = context;
        this.movesItems = new ArrayList<>();
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = (LayoutInflater.from(context)).inflate(R.layout.moves_item, parent, false);
        MovesViewHolder viewHolder = new MovesViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        if (cursor != null && !cursor.isClosed()){
            MovesViewHolder viewHolder = (MovesViewHolder) view.getTag();
            viewHolder.dateTextView.setText(cursor.getString(
                    cursor.getColumnIndex(CheckingEntry.TABLE_NAME + "." + CheckingEntry.COLUMN_CREATED_AT)));
            viewHolder.activityTextView.setText(cursor.getString(
                    cursor.getColumnIndex(CheckingEntry.TABLE_NAME + "." + CheckingEntry.COLUMN_CHECK_TYPE)));
            viewHolder.locationTextView.setText(cursor.getString(
                    cursor.getColumnIndex(VenueEntry.TABLE_NAME + "." + VenueEntry.COLUMN_ADDRESS)));
        }

    }

}
