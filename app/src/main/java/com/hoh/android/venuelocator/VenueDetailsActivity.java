package com.hoh.android.venuelocator;

import android.app.ActionBar;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hoh.android.venuelocator.async.FetchDataTask;
import com.hoh.android.venuelocator.blueprints.OnDownloadFinished;
import com.hoh.android.venuelocator.blueprints.RecentVenueActivityItem;
import com.hoh.android.venuelocator.blueprints.UserItem;
import com.hoh.android.venuelocator.blueprints.Utility;
import com.hoh.android.venuelocator.blueprints.VenueItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class VenueDetailsActivity extends FragmentActivity implements
        OnMapReadyCallback, OnDownloadFinished {

    private List<RecentVenueActivityItem> recentVenueActivityItems;
    private VenueItem venueItem;
    private TextView headingTextView;

    private LinearLayout recentActivitiesLayout;
    private LinearLayout spinnerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venue_details);

        recentVenueActivityItems = new ArrayList<>();
        venueItem = new VenueItem(getIntent());

        recentActivitiesLayout = (LinearLayout) findViewById(R.id.recent_activities_list_layout);
        spinnerLayout = (LinearLayout) findViewById(R.id.venue_recent_spinner_layout);
        headingTextView = (TextView) findViewById(R.id.venue_details_heading_tv);

        headingTextView.setText(venueItem.getName());

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.venue_details_map);
        mapFragment.getMapAsync(this);


        UserItem userItem = new UserItem("David", "david@users.com", "david/img");
        RecentVenueActivityItem item;
        for (int i = 0; i < 10; i++){
            item = new RecentVenueActivityItem(venueItem, "02/03/2014", userItem, RecentVenueActivityItem.Activity.CHECK_IN);
            recentVenueActivityItems.add(item);
        }

        appendRecentActivityListItem();
//        setProgressBarIndeterminateVisibility(true);
        spinnerLayout.setVisibility(View.VISIBLE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_venue_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void appendRecentActivityListItem(){

        for (RecentVenueActivityItem recentVenueActivityItem : recentVenueActivityItems){
            LinearLayout activityItemLayout = (LinearLayout) (LayoutInflater.from(this)).
                    inflate(R.layout.recent_venue_activity_item, recentActivitiesLayout, true);
            TextView recentActivity = (TextView) activityItemLayout.findViewById(R.id.recent_activity_tv);
            TextView recentActivityTime = (TextView) activityItemLayout.findViewById(R.id.recent_activity_time_tv);
            ImageView actor = (ImageView) activityItemLayout.findViewById(R.id.venue_actor_img_view);

            recentActivity.setText(recentVenueActivityItem.getActivity());
            recentActivityTime.setText(recentVenueActivityItem.getTimeStamp());
            actor.setImageDrawable(Drawable.createFromPath(recentVenueActivityItem.getActor().getImageUrl()));
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        LatLng venue = new LatLng(venueItem.getLatitude(), venueItem.getLongitude());
        googleMap.addMarker(new MarkerOptions()
                .position(venue)
                .title("Marker"));
        googleMap.setMyLocationEnabled(true);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(venue, 17));


    }

    public void loadRecentActivities(){

    }

    public void fetchRecentActivities(){
        FetchDataTask fetchDataTask = new FetchDataTask(this, spinnerLayout);
        fetchDataTask.execute(Utility.CHECKING_URL + "/venue/" + venueItem.getId());
    }

    @Override
    public void parseResponse(String url, String responseString) throws JSONException {

        JSONArray response = new JSONArray(responseString);
        JSONObject checkingObject;
        RecentVenueActivityItem item;
        VenueItem venue;
        UserItem actor;

        recentVenueActivityItems.clear();
        for (int i = 0, limit = response.length(); i < limit; i++){
            checkingObject = response.getJSONObject(i);
            actor = new UserItem(
                    checkingObject.getString("username"),
                    checkingObject.getString("email"),
                    checkingObject.getString("image_url")
            );
            item = new RecentVenueActivityItem(
                    venueItem,
                    checkingObject.getString("modified_at"),
                    actor,
                    RecentVenueActivityItem.stringToActivity(checkingObject.getString("checking_type"))
            );

            recentVenueActivityItems.add(item);
        }

//        setProgressBarIndeterminateVisibility(false);
        spinnerLayout.setVisibility(View.GONE);
    }

    @Override
    public void setImageBitmap(String url, Bitmap bitmap) {

    }
}
