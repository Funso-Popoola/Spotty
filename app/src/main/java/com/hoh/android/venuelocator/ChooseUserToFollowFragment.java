package com.hoh.android.venuelocator;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.hoh.android.venuelocator.blueprints.OnFragmentInteractionListener;
import com.hoh.android.venuelocator.blueprints.UserItem;
import com.hoh.android.venuelocator.blueprints.UserToFollowItem;
import com.hoh.android.venuelocator.blueprints.UserToFollowListAdapter;

import java.util.List;

public class ChooseUserToFollowFragment extends Fragment {

    private List<UserItem> userItemList;
    private OnFragmentInteractionListener mListener;

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

        UserToFollowListAdapter adapter = new UserToFollowListAdapter(getActivity());
        ListView listView = (ListView)root.findViewById(R.id.user_to_follow_list_view);

        listView.setAdapter(adapter);
        UserItem item;

        for (int i = 0; i < 10; i++){
            item = new UserItem("user_" + i, "emial_" + i, "");
            adapter.add(item);
        }
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
