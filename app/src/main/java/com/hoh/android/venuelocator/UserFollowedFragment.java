package com.hoh.android.venuelocator;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.hoh.android.venuelocator.blueprints.UserFollowedItem;
import com.hoh.android.venuelocator.blueprints.UserFollowedListAdapter;

import java.util.ArrayList;
import java.util.List;

import com.hoh.android.venuelocator.blueprints.OnFragmentInteractionListener;

public class UserFollowedFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserFollowedFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserFollowedFragment newInstance(String param1, String param2) {
        UserFollowedFragment fragment = new UserFollowedFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public UserFollowedFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_user_followed, container, false);

        // find the list view
        ListView userFollowedListView = (ListView) rootView.findViewById(R.id.user_followed_list);

        // create a UserFollowedListAdapter
        UserFollowedListAdapter adapter = new UserFollowedListAdapter(getActivity());

        List<UserFollowedItem> userFollowedItems = new ArrayList<>();

        UserFollowedItem item;

        for (int i = 0; i < 6; i++){
            item = new UserFollowedItem("User", "foo@foo.com");
            userFollowedItems.add(item);
            adapter.add(item);
        }

        userFollowedListView.setAdapter(adapter);

        return rootView;
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


}
