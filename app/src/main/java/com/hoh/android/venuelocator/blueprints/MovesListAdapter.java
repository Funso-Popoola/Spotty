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
public class MovesListAdapter extends BaseAdapter{

    private Context context;
    private List<MovesItem> movesItems;

    public MovesListAdapter(Context context){
        this.context = context;
        this.movesItems = new ArrayList<>();
    }

    public void add(MovesItem item){
        movesItems.add(item);
        notifyDataSetChanged();
    }

    public void clear(){
        movesItems.clear();
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return movesItems.size();
    }

    @Override
    public Object getItem(int position) {
        return movesItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        MovesItem movesItem = movesItems.get(position);

        View view = convertView;

        if (view == null){
            view = (LayoutInflater.from(context)).inflate(R.layout.moves_item, parent, false);
        }

        final TextView dateTextView = (TextView)view.findViewById(R.id.move_date_tv);
        final TextView activityTextView = (TextView)view.findViewById(R.id.move_activity_tv);
        final TextView locationTextView = (TextView)view.findViewById(R.id.move_location_tv);

        dateTextView.setText(movesItem.getTimeStamp());
        activityTextView.setText(movesItem.getActivity());
        locationTextView.setText(movesItem.getVenue().getAddress());

        return view;
    }
}
