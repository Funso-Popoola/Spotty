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
import com.hoh.android.venuelocator.blueprints.MovesItem;
import com.hoh.android.venuelocator.blueprints.MovesListAdapter;
import com.hoh.android.venuelocator.blueprints.OnFragmentInteractionListener;
import com.hoh.android.venuelocator.blueprints.OnLoaderFinished;
import com.hoh.android.venuelocator.blueprints.VenueItem;
import com.hoh.android.venuelocator.data.VenueLocatorContract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MovesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private final int LOADER_ID = 1;
    private final String LOG_TAG = MovesFragment.class.getSimpleName();
    public final static String[] MOVES_COLUMNS = {
            VenueLocatorContract.CheckingEntry.TABLE_NAME + "." + VenueLocatorContract.CheckingEntry._ID,
            VenueLocatorContract.CheckingEntry.TABLE_NAME + "." + VenueLocatorContract.CheckingEntry.COLUMN_CHECK_TYPE,
            VenueLocatorContract.VenueEntry.TABLE_NAME + "." + VenueLocatorContract.VenueEntry.COLUMN_NAME,
            VenueLocatorContract.CheckingEntry.TABLE_NAME + "." + VenueLocatorContract.CheckingEntry.COLUMN_CREATED_AT
    };

    private CustomPreferenceManager preferenceManager;
    MovesListAdapter adapter;

    private OnFragmentInteractionListener mListener;

    public static MovesFragment newInstance(MovesListAdapter adapter) {
        MovesFragment fragment = new MovesFragment();
//        fragment.adapter = adapter;
        return fragment;
    }

    public MovesFragment() {
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
        View root = inflater.inflate(R.layout.fragment_moves, container, false);
        preferenceManager = new CustomPreferenceManager(getActivity());

        adapter = new MovesListAdapter(getActivity(), null, 0);

//        for (MovesItem movesItem : movesItems){
//            adapter.add(movesItem);
//        }

        ListView listView = (ListView) root.findViewById(R.id.moves_list_view);
        listView.setAdapter(adapter);

        return root;
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
//        if (!data.isClosed()){
//            adapter.swapCursor(data);
//        }
//
//    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.i("MOVES FRAGMENT", " LOADER CREATED");
        int userId = preferenceManager.getUserId();
        if (userId != -1){
            return new CursorLoader(
                    getActivity(),
                    VenueLocatorContract.CheckingEntry.buildCheckingUriWithCheckerId(preferenceManager.getUserId()),
                    MOVES_COLUMNS,
                    null,
                    null,
                    VenueLocatorContract.CheckingEntry.TABLE_NAME + "." + VenueLocatorContract.CheckingEntry.COLUMN_CREATED_AT + " DESC"
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
