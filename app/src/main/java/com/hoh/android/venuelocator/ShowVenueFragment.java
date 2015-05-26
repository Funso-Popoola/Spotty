package com.hoh.android.venuelocator;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.hoh.android.venuelocator.blueprints.OnFragmentInteractionListener;
import com.hoh.android.venuelocator.blueprints.OnLoaderFinished;
import com.hoh.android.venuelocator.blueprints.Utility;
import com.hoh.android.venuelocator.blueprints.VenueItem;
import com.hoh.android.venuelocator.blueprints.VenueListAdapter;
import com.hoh.android.venuelocator.data.VenueLocatorContract;

import java.util.Arrays;
import java.util.List;

public class ShowVenueFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private final int LOADER_ID = 0;
    private final String LOG_TAG = ShowVenueFragment.class.getSimpleName();

    private OnFragmentInteractionListener mListener;
    private VenueListAdapter adapter;

    // TODO: Rename and change types and number of parameters
    public static ShowVenueFragment newInstance(VenueListAdapter adapter) {
        ShowVenueFragment fragment = new ShowVenueFragment();
//        fragment.adapter = adapter;
        return fragment;
    }

    public ShowVenueFragment() {
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
        View root = inflater.inflate(R.layout.fragment_show_venue, container, false);

        adapter = new VenueListAdapter(getActivity(), null, 0);

        ListView listView = (ListView) root.findViewById(R.id.show_venue_list_view);

//        if(venueItems.size() > 0){
//            for (VenueItem venueItem : venueItems){
//                adapter.add(venueItem);
//            }
//        }

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mListener.onListItemClicked(parent, view, position, id);
            }
        });
        return root;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID, null, this);
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
        Log.i("VENUE FRAGMENT", " LOADER CREATED");
        return new CursorLoader(
                getActivity(),
                VenueLocatorContract.VenueEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
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
