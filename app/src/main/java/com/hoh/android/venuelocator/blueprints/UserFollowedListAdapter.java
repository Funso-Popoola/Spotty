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
public class UserFollowedListAdapter extends BaseAdapter {

    private Context context;
    private List<UserFollowedItem> userFollowedItems;

    public UserFollowedListAdapter(Context context){
        this.context = context;
        this.userFollowedItems = new ArrayList<>();
    }

    public void add(UserFollowedItem item){
        if (item != null){
            userFollowedItems.add(item);
            notifyDataSetChanged();
        }

    }

    public void clear(){
        userFollowedItems.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return userFollowedItems.size();
    }

    @Override
    public Object getItem(int position) {
        return userFollowedItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        UserFollowedItem userFollowedItem = userFollowedItems.get(position);
        View view = convertView;

        if (view == null){
            view = (LayoutInflater.from(context)).inflate(R.layout.user_followed_item, parent, false);
        }

        final TextView fullNameTextView = (TextView) view.findViewById(R.id.full_name_tv);
        final TextView emailTextView = (TextView) view.findViewById(R.id.email_tv);

        fullNameTextView.setText(userFollowedItem.getFullName());
        emailTextView.setText(userFollowedItem.getEmail());

        return view;
    }
}
