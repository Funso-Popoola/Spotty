package com.hoh.android.venuelocator;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.hoh.android.venuelocator.blueprints.CustomPreferenceManager;
import com.hoh.android.venuelocator.blueprints.FollowedCheckingsListAdapter;
import com.hoh.android.venuelocator.blueprints.OnFragmentInteractionListener;
import com.hoh.android.venuelocator.blueprints.OnLoaderFinished;
import com.hoh.android.venuelocator.blueprints.RecentVenueActivityItem;
import com.hoh.android.venuelocator.blueprints.UserFollowedItem;
import com.hoh.android.venuelocator.blueprints.UserFollowedListAdapter;
import com.hoh.android.venuelocator.blueprints.UserItem;
import com.hoh.android.venuelocator.blueprints.VenueItem;
import com.hoh.android.venuelocator.data.VenueLocatorContract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FollowedCheckingsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private final int LOADER_ID = 2;
    private final String LOG_TAG = FollowedCheckingsFragment.class.getSimpleName();
    public final static String[] FOLLOWED_COLUMNS = {
            VenueLocatorContract.CheckingEntry.TABLE_NAME + "." + VenueLocatorContract.CheckingEntry._ID,
            VenueLocatorContract.UserEntry.TABLE_NAME + "." + VenueLocatorContract.UserEntry.COLUMN_IMG_URL,
            VenueLocatorContract.VenueEntry.TABLE_NAME + "." + VenueLocatorContract.VenueEntry.COLUMN_ADDRESS,
            VenueLocatorContract.CheckingEntry.TABLE_NAME + "." + VenueLocatorContract.CheckingEntry.COLUMN_CHECK_TYPE,
            VenueLocatorContract.CheckingEntry.TABLE_NAME + "." + VenueLocatorContract.CheckingEntry.COLUMN_CREATED_AT
    };

    private CustomPreferenceManager preferenceManager;
    private FollowedCheckingsListAdapter adapter;
    private OnFragmentInteractionListener mListener;

    // TODO: Rename and change types and number of parameters
    public static FollowedCheckingsFragment newInstance(FollowedCheckingsListAdapter followedCheckingsListAdapter) {
        FollowedCheckingsFragment fragment = new FollowedCheckingsFragment();
//        fragment.adapter = followedCheckingsListAdapter;
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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_followed_checkings, container, false);

//        FollowedCheckingsListAdapter adapter = new FollowedCheckingsListAdapter(getActivity());
        preferenceManager = new CustomPreferenceManager(getActivity());
        List<VenueItem> venueItems = new ArrayList<>();

        adapter = new FollowedCheckingsListAdapter(getActivity(), null, 0);
//        for (RecentVenueActivityItem followedChecking : followedCheckings){
//            adapter.add(followedChecking);
//        }

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

//    @Override
//    public void refreshLoader(Cursor data) {
//        Log.i(LOG_TAG, "REFRESHING LOADER ===> " + Arrays.toString(data.getColumnNames()));
//
//        if (!data.isClosed()){
//            adapter.swapCursor(data);
//        }
//
//    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.i("FOLLOWED FRAGMENT", " LOADER CREATED");
        int userId = preferenceManager.getUserId();
        if (userId != -1){
            return new CursorLoader(
                    getActivity(),
                    VenueLocatorContract.CheckingEntry.buildCheckingUriWithLeaderId(preferenceManager.getUserId()),
                    FOLLOWED_COLUMNS,
                    null,
                    null,
                    null
            );
        }
        else{
            return null;
        }

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (!data.isClosed()){
            Log.i(LOG_TAG, " DATA LOADED ====> " + Arrays.toString(data.getColumnNames()) + " COUNT " + data.getCount());
            adapter.swapCursor(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }
}
