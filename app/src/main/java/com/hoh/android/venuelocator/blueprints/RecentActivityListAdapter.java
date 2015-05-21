package com.hoh.android.venuelocator.blueprints;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.hoh.android.venuelocator.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by funso on 3/4/15.
 */
public class RecentActivityListAdapter extends BaseAdapter{

    private List<RecentVenueActivityItem> recentVenueActivityItems;
    private Context context;

    public RecentActivityListAdapter(Context context){
        recentVenueActivityItems = new ArrayList<>();
        this.context = context;
    }

    public void add(RecentVenueActivityItem item){
        recentVenueActivityItems.add(item);
        notifyDataSetChanged();
    }

    public void clear(){
        recentVenueActivityItems.clear();
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return recentVenueActivityItems.size();
    }

    @Override
    public Object getItem(int position) {
        return recentVenueActivityItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        RecentVenueActivityItem recentVenueActivityItem = recentVenueActivityItems.get(position);
        View view = convertView;

        if (view == null){
            view = (LayoutInflater.from(context)).inflate(R.layout.recent_venue_activity_item, parent);
        }


        return view;
    }
}
