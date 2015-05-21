package com.hoh.android.venuelocator;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.hoh.android.venuelocator.blueprints.MovesItem;
import com.hoh.android.venuelocator.blueprints.MovesListAdapter;
import com.hoh.android.venuelocator.blueprints.OnFragmentInteractionListener;
import com.hoh.android.venuelocator.blueprints.VenueItem;

import java.util.ArrayList;
import java.util.List;

public class MovesFragment extends Fragment {

    private List<MovesItem> movesItems;
    MovesListAdapter adapter;

    private OnFragmentInteractionListener mListener;

    public static MovesFragment newInstance(List<MovesItem> movesItems) {
        MovesFragment fragment = new MovesFragment();
        fragment.movesItems = movesItems;
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_moves, container, false);

        adapter = new MovesListAdapter(getActivity());

        for (MovesItem movesItem : movesItems){
            adapter.add(movesItem);
        }

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

}
