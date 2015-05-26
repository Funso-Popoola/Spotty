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


    private VenueListAdapter venueListAdapter;
    private MovesListAdapter movesListAdapter;
    private FollowedCheckingsListAdapter followedCheckingsListAdapter;

    public ShowVenuePagerAdapter(FragmentManager fragmentManager,
                                 VenueListAdapter venueListAdapter,
                                 MovesListAdapter movesListAdapter,
                                 FollowedCheckingsListAdapter followedCheckingsListAdapter){

        super(fragmentManager);
        this.venueListAdapter = venueListAdapter;
        this.movesListAdapter = movesListAdapter;
        this.followedCheckingsListAdapter = followedCheckingsListAdapter;

    }

    public void updateVenueItems(){

    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return ShowVenueFragment.newInstance(venueListAdapter);
            case 1:
                return MovesFragment.newInstance(movesListAdapter);
            case 2:
                return FollowedCheckingsFragment.newInstance(followedCheckingsListAdapter);

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

