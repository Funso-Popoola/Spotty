package com.hoh.android.venuelocator.blueprints;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.hoh.android.venuelocator.FollowedCheckingsFragment;
import com.hoh.android.venuelocator.MovesFragment;
import com.hoh.android.venuelocator.ShowVenueFragment;

import java.util.List;

/**
 * Created by funso on 3/4/15.
 */
public class ShowVenuePagerAdapter extends FragmentStatePagerAdapter {


    private List<VenueItem> venueItems;
    private List<MovesItem> movesItems;
    private List<RecentVenueActivityItem> followedCheckings;

    public ShowVenuePagerAdapter(FragmentManager fragmentManager, List<VenueItem> venueItems, List<MovesItem> movesItems, List<RecentVenueActivityItem> followedCheckings){

        super(fragmentManager);
        this.venueItems = venueItems;
        this.movesItems = movesItems;
        this.followedCheckings = followedCheckings;

    }

    public void updateVenueItems(){

    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return ShowVenueFragment.newInstance(venueItems);
            case 1:
                return MovesFragment.newInstance(movesItems);
            case 2:
                return FollowedCheckingsFragment.newInstance(followedCheckings);

        }
        return null;
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position){
            case 0:
                return "Near By";
            case 1:
                return "My Moves";
            case 2:
                return "Followed";

        }
        return "UnKnown";
    }
}

