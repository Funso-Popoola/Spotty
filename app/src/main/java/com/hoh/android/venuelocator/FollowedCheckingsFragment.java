package com.hoh.android.venuelocator;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.hoh.android.venuelocator.blueprints.FollowedCheckingsListAdapter;
import com.hoh.android.venuelocator.blueprints.OnFragmentInteractionListener;
import com.hoh.android.venuelocator.blueprints.RecentVenueActivityItem;
import com.hoh.android.venuelocator.blueprints.UserFollowedItem;
import com.hoh.android.venuelocator.blueprints.UserFollowedListAdapter;
import com.hoh.android.venuelocator.blueprints.UserItem;
import com.hoh.android.venuelocator.blueprints.VenueItem;

import java.util.ArrayList;
import java.util.List;

public class FollowedCheckingsFragment extends Fragment {

    private List<RecentVenueActivityItem> followedCheckings;
    private OnFragmentInteractionListener mListener;

    // TODO: Rename and change types and number of parameters
    public static FollowedCheckingsFragment newInstance(List<RecentVenueActivityItem> followedCheckings) {
        FollowedCheckingsFragment fragment = new FollowedCheckingsFragment();
        fragment.followedCheckings = followedCheckings;
        return fragment;
    }

    public FollowedCheckingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_followed_checkings, container, false);

        FollowedCheckingsListAdapter adapter = new FollowedCheckingsListAdapter(getActivity());

        List<VenueItem> venueItems = new ArrayList<>();


        for (RecentVenueActivityItem followedChecking : followedCheckings){
            adapter.add(followedChecking);
        }

        ListView listView = (ListView) root.findViewById(R.id.followed_checkings_list_view);

        listView.setAdapter(adapter);
        return root;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


}
