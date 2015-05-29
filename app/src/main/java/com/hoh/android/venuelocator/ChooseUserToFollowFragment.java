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
import com.hoh.android.venuelocator.blueprints.OnFragmentInteractionListener;
import com.hoh.android.venuelocator.blueprints.UserItem;
import com.hoh.android.venuelocator.blueprints.UserToFollowItem;
import com.hoh.android.venuelocator.blueprints.UserToFollowListAdapter;
import com.hoh.android.venuelocator.data.VenueLocatorContract;
import com.hoh.android.venuelocator.data.VenueLocatorContract.UserEntry;

import java.util.Arrays;
import java.util.List;

public class ChooseUserToFollowFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String LOG_TAG = ChooseUserToFollowFragment.class.getSimpleName();
    private static final int LOADER_ID = 5;

    private CustomPreferenceManager preferenceManager;
    private List<UserItem> userItemList;
    private OnFragmentInteractionListener mListener;
    private UserToFollowListAdapter adapter;

    public static ChooseUserToFollowFragment newInstance(List<UserItem> userItemList) {
        ChooseUserToFollowFragment fragment = new ChooseUserToFollowFragment();
        fragment.userItemList = userItemList;
        return fragment;
    }

    public ChooseUserToFollowFragment() {
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
        View root = inflater.inflate(R.layout.fragment_choose_user_to_follow, container, false);

        preferenceManager = new CustomPreferenceManager(getActivity());
        adapter = new UserToFollowListAdapter(getActivity(), null, 0);
        ListView listView = (ListView)root.findViewById(R.id.user_to_follow_list_view);

        listView.setAdapter(adapter);
        UserItem item;

//        for (int i = 0; i < 10; i++){
//            item = new UserItem("user_" + i, "emial_" + i, "");
//            adapter.add(item);
//        }
        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID, null, this);
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
        Log.i("FOLLOWED FRAGMENT", " LOADER CREATED");
        int userId = preferenceManager.getUserId();
        if (userId != -1){
            return new CursorLoader(
                    getActivity(),
                    VenueLocatorContract.LeaderFollowerEntry.buildFollowerLeaderUriWithNotFollowerId(preferenceManager.getUserId()),
                    null,
                    null,
                    null,
                    VenueLocatorContract.LeaderFollowerEntry.TABLE_NAME + "." + VenueLocatorContract.LeaderFollowerEntry.COLUMN_CREATED_AT + " DESC"
            );
        }
        else{
            return null;
        }    }

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
