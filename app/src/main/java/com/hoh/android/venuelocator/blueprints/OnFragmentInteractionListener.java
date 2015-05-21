package com.hoh.android.venuelocator.blueprints;

import android.net.Uri;
import android.view.View;
import android.widget.AdapterView;

/**
 * Created by funso on 3/4/15.
 */

/**
 * This interface must be implemented by activities that contain this
 * fragment to allow an interaction in this fragment to be communicated
 * to the activity and potentially other fragments contained in that
 * activity.
 * <p/>
 * See the Android Training lesson <a href=
 * "http://developer.android.com/training/basics/fragments/communicating.html"
 * >Communicating with Other Fragments</a> for more information.
 */

public interface OnFragmentInteractionListener {
    // TODO: Update argument type and name
    public void onFragmentInteraction(Uri uri);
    public void onListItemClicked(AdapterView<?> parent, View view, int position, long id);
}
