package com.hoh.android.venuelocator;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.hoh.android.venuelocator.blueprints.OnFragmentInteractionListener;
import com.hoh.android.venuelocator.blueprints.Utility;
import com.hoh.android.venuelocator.blueprints.VenueItem;
import com.hoh.android.venuelocator.blueprints.VenueListAdapter;

import java.util.List;

public class ShowVenueFragment extends Fragment {

    private List<VenueItem> venueItems;
    private OnFragmentInteractionListener mListener;

    // TODO: Rename and change types and number of parameters
    public static ShowVenueFragment newInstance(List<VenueItem> venueItems) {
        ShowVenueFragment fragment = new ShowVenueFragment();
        fragment.venueItems = venueItems;
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

        VenueListAdapter adapter = new VenueListAdapter(getActivity());

        ListView listView = (ListView) root.findViewById(R.id.show_venue_list_view);

        if(venueItems.size() > 0){
            for (VenueItem venueItem : venueItems){
                adapter.add(venueItem);
            }
        }

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
