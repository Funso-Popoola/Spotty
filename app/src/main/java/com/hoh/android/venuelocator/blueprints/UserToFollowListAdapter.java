package com.hoh.android.venuelocator.blueprints;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.hoh.android.venuelocator.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by funso on 3/4/15.
 */

public class UserToFollowListAdapter extends BaseAdapter{

    private Context context;
    private List<UserItem> userItems;
    public static List<UserItem> chosenUsers;

    public UserToFollowListAdapter(Context context){
        this.context = context;
        this.userItems = new ArrayList<>();
        chosenUsers = new ArrayList<>();
    }

    public void add(UserItem item){
        userItems.add(item);
    }

    public void clear(){
        userItems.clear();
        notifyDataSetChanged();
    }
    @Override
    public int getCount() {
        return userItems.size();
    }

    @Override
    public Object getItem(int position) {
        return userItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final UserItem userItem = userItems.get(position);
        View view = convertView;

        if (view == null){
            view = (LayoutInflater.from(context)).inflate(R.layout.user_to_follow_item, parent, false);
        }

        final TextView nameTextView = (TextView)view.findViewById(R.id.proposed_user_name);
        final TextView emailTextView = (TextView)view.findViewById(R.id.proposed_user_email);

        nameTextView.setText(userItem.getName());
        emailTextView.setText(userItem.getEmail());

        ((CheckBox)view.findViewById(R.id.decision)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    chosenUsers.add(userItem);
                }
                else{
                    chosenUsers.remove(userItem);
                }
            }
        });

        return view;
    }

    public List<UserItem> getChosenUsers() {
        return chosenUsers;
    }
}
