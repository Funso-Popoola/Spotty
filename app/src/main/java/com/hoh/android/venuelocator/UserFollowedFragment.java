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
import com.hoh.android.venuelocator.blueprints.UserFollowedItem;
import com.hoh.android.venuelocator.blueprints.UserFollowedListAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.hoh.android.venuelocator.blueprints.OnFragmentInteractionListener;
import com.hoh.android.venuelocator.data.VenueLocatorContract;

public class UserFollowedFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String LOG_TAG = UserFollowedFragment.class.getSimpleName();
    private static final int LOADER_ID = 4;

    private UserFollowedListAdapter adapter;
    private CustomPreferenceManager preferenceManager;

    private OnFragmentInteractionListener mListener;

    public static UserFollowedFragment newInstance() {
        UserFollowedFragment fragment = new UserFollowedFragment();
        return fragment;
    }

    public UserFollowedFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_user_followed, container, false);

        preferenceManager = new CustomPreferenceManager(getActivity());
        adapter = new UserFollowedListAdapter(getActivity(), null, 0);
        // find the list view
        ListView userFollowedListView = (ListView) rootView.findViewById(R.id.user_followed_list);

        // create a UserFollowedListAdapter
        List<UserFollowedItem> userFollowedItems = new ArrayList<>();

        UserFollowedItem item;

//        for (int i = 0; i < 6; i++){
//            item = new UserFollowedItem("User", "foo@foo.com");
//            userFollowedItems.add(item);
//            adapter.add(item);
//        }

        userFollowedListView.setAdapter(adapter);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    public void loadUserFollowed(){
        // make an async call to the web-end to get list of followed users
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

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.i("USER FOLLOWED FRAGMENT", " LOADER CREATED");
        int userId = preferenceManager.getUserId();
        if (userId != -1){
            return new CursorLoader(
                    getActivity(),
                    VenueLocatorContract.LeaderFollowerEntry.buildFollowerLeaderUriWithFollowerId(preferenceManager.getUserId()),
                    null,
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
