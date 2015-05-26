package com.hoh.android.venuelocator.blueprints;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.hoh.android.venuelocator.R;

import java.util.ArrayList;
import java.util.List;

import com.hoh.android.venuelocator.data.VenueLocatorContract.UserEntry;
/**
 * Created by funso on 3/4/15.
 */

public class UserToFollowListAdapter extends CursorAdapter{

    private Context context;
    public static List<Integer> chosenUsers;

    public UserToFollowListAdapter(Context context, Cursor cursor, int flag){
        super(context, cursor, flag);
        this.context = context;
        chosenUsers = new ArrayList<>();
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = (LayoutInflater.from(context)).inflate(R.layout.user_to_follow_item, parent, false);
        UserToFollowViewHolder viewHolder = new UserToFollowViewHolder(view, context);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, final Cursor cursor) {
        UserToFollowViewHolder viewHolder = (UserToFollowViewHolder) view.getTag();
        viewHolder.downloadUserImage(cursor.getString(cursor.getColumnIndex(UserEntry.COLUMN_IMG_URL)));
        viewHolder.emailTextView.setText(cursor.getString(cursor.getColumnIndex(UserEntry.COLUMN_EMAIL)));
        viewHolder.nameTextView.setText(cursor.getString(cursor.getColumnIndex(UserEntry.COLUMN_USERNAME)));

        ((CheckBox)view.findViewById(R.id.decision)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    chosenUsers.add(cursor.getInt(cursor.getColumnIndex(UserEntry.COLUMN_USER_ID)));
                } else {
                    chosenUsers.remove(cursor.getInt(cursor.getColumnIndex(UserEntry.COLUMN_USER_ID)));
                }
            }
        });
    }

    public List<Integer> getChosenUsers() {
        return chosenUsers;
    }
}
