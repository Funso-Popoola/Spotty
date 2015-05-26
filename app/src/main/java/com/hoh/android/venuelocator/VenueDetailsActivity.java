package com.hoh.android.venuelocator;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.hoh.android.venuelocator.async.FetchDataTask;
import com.hoh.android.venuelocator.async.PostManTask;
import com.hoh.android.venuelocator.async.PostRequest;
import com.hoh.android.venuelocator.blueprints.CustomPreferenceManager;
import com.hoh.android.venuelocator.blueprints.OnDownloadFinished;
import com.hoh.android.venuelocator.blueprints.OnPostFinish;
import com.hoh.android.venuelocator.blueprints.RecentActivityListAdapter;
import com.hoh.android.venuelocator.blueprints.RecentVenueActivityItem;
import com.hoh.android.venuelocator.blueprints.UserItem;
import com.hoh.android.venuelocator.blueprints.Utility;
import com.hoh.android.venuelocator.blueprints.VenueItem;

import com.hoh.android.venuelocator.data.VenueLocatorContract.CheckingEntry;
import com.hoh.android.venuelocator.data.VenueLocatorContract.UserEntry;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class VenueDetailsActivity extends FragmentActivity implements
        OnMapReadyCallback, OnDownloadFinished, LoaderManager.LoaderCallbacks<Cursor>, OnPostFinish{

    private final String LOG_TAG = VenueDetailsActivity.class.getSimpleName();
    private List<RecentVenueActivityItem> recentVenueActivityItems;
    private RecentActivityListAdapter adapter;
    private VenueItem venueItem;
    private TextView headingTextView;
    private Button checkInBtn;
    private Button checkOutBtn;

    private ListView recentActivitiesList;
    private LinearLayout spinnerLayout;

    private CustomPreferenceManager preferenceManager;

    public static String [] CHECKING_COLUMNS = {
            CheckingEntry.TABLE_NAME + "." + CheckingEntry._ID,
            UserEntry.TABLE_NAME + "." + UserEntry.COLUMN_IMG_URL,
            CheckingEntry.TABLE_NAME + "." + CheckingEntry.COLUMN_CHECK_TYPE,
            CheckingEntry.TABLE_NAME + "." + CheckingEntry.COLUMN_CREATED_AT
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_venue_details);

        // initialize loader
        getSupportLoaderManager().initLoader(1, null, this);
        adapter = new RecentActivityListAdapter(this, null, 0);

        preferenceManager = new CustomPreferenceManager(this);

        recentVenueActivityItems = new ArrayList<>();
        venueItem = new VenueItem(getIntent());

        recentActivitiesList = (ListView) findViewById(R.id.recent_activities_list_layout);
        spinnerLayout = (LinearLayout) findViewById(R.id.venue_recent_spinner_layout);
        spinnerLayout.setVisibility(View.GONE);
        headingTextView = (TextView) findViewById(R.id.venue_details_heading_tv);
        checkInBtn = (Button) findViewById(R.id.venue_check_in_btn);
        checkOutBtn = (Button) findViewById(R.id.venue_check_out_btn);

        headingTextView.setText(venueItem.getName());

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.venue_details_map);
        mapFragment.getMapAsync(this);

        recentActivitiesList.setAdapter(adapter);

        checkInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(CheckingEntry.COLUMN_CHECKER_ID, preferenceManager.getUserId());
                contentValues.put(CheckingEntry.COLUMN_VENUE_ID, venueItem.getId());
                contentValues.put(CheckingEntry.COLUMN_CHECK_TYPE,
                        RecentVenueActivityItem.getActivity(RecentVenueActivityItem.Activity.CHECK_IN));
                contentValues.put(CheckingEntry.COLUMN_CREATED_AT, System.currentTimeMillis());
                contentValues.put(CheckingEntry.COLUMN_MODIFIED_AT, System.currentTimeMillis());
                contentValues.put(CheckingEntry.COLUMN_ACTIVE_STATUS, 1);

                Uri checkingUri = getContentResolver().insert(
                        CheckingEntry.CONTENT_URI,
                        contentValues
                );

                Log.i(LOG_TAG, "CHECKING-IN URI ===> " + checkingUri);
                Log.i(LOG_TAG, "CHECKING-IN CONTENT ===> " + contentValues.valueSet().toString());

                uploadCheckIn(RecentVenueActivityItem.Activity.CHECK_IN);
            }
        });

        checkOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(CheckingEntry.COLUMN_CHECKER_ID, preferenceManager.getUserId());
                contentValues.put(CheckingEntry.COLUMN_VENUE_ID, venueItem.getId());
                contentValues.put(CheckingEntry.COLUMN_CHECK_TYPE,
                        RecentVenueActivityItem.getActivity(RecentVenueActivityItem.Activity.CHECK_OUT));
                contentValues.put(CheckingEntry.COLUMN_CREATED_AT, System.currentTimeMillis());
                contentValues.put(CheckingEntry.COLUMN_MODIFIED_AT, System.currentTimeMillis());
                contentValues.put(CheckingEntry.COLUMN_ACTIVE_STATUS, 1);

                Uri checkingUri = getContentResolver().insert(
                        CheckingEntry.CONTENT_URI,
                        contentValues
                );


                Log.i(LOG_TAG, "CHECKING-OUT URI ===> " + checkingUri);
                Log.i(LOG_TAG, "CHECKING_OUT CONTENT ===>" + contentValues.valueSet().toString());

                uploadCheckIn(RecentVenueActivityItem.Activity.CHECK_OUT);
            }
        });
        UserItem userItem = new UserItem("David", "david@users.com", "david/img");
        RecentVenueActivityItem item;
        for (int i = 0; i < 10; i++){
            item = new RecentVenueActivityItem(venueItem, "02/03/2014", userItem, RecentVenueActivityItem.Activity.CHECK_IN);
            recentVenueActivityItems.add(item);
        }

//        appendRecentActivityListItem();
//        setProgressBarIndeterminateVisibility(true);
//        spinnerLayout.setVisibility(View.GONE);
    }

    public void uploadCheckIn(RecentVenueActivityItem.Activity activity){
        try {
            PostRequest request = new PostRequest(new URL(Utility.CHECKING_URL));
            List<NameValuePair> nameValuePairs = new ArrayList<>();
            nameValuePairs.add(new BasicNameValuePair("action", "insert"));
            nameValuePairs.add(new BasicNameValuePair("user_id", preferenceManager.getUserId() + ""));
            nameValuePairs.add(new BasicNameValuePair("venue_id", venueItem.getId() + ""));
            nameValuePairs.add(new BasicNameValuePair("checking_type", RecentVenueActivityItem.getActivity(activity)));

            request.setData(nameValuePairs);
            PostManTask postManTask = new PostManTask(this);
            postManTask.execute(request);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
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
                    inflate(R.layout.recent_venue_activity_item, recentActivitiesList, true);
            TextView recentActivity = (TextView) activityItemLayout.findViewById(R.id.recent_activity_tv);
            TextView recentActivityTime = (TextView) activityItemLayout.findViewById(R.id.recent_activity_time_tv);
            ImageView actor = (ImageView) activityItemLayout.findViewById(R.id.venue_actor_img_view);

            recentActivity.setText(recentVenueActivityItem.getActivity());
            recentActivityTime.setText(recentVenueActivityItem.getTimeStamp());
            actor.setImageDrawable(Drawable.createFromPath(recentVenueActivityItem.getActor().getImageUrl()));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchRecentActivities();
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

        int userId;
        String username, email, imageUrl, profile_url, checkingType;

        ContentValues userContentValues, checkingContentValues;
        recentVenueActivityItems.clear();
        for (int i = 0, limit = response.length(); i < limit; i++){
            checkingObject = response.getJSONObject(i);

            userId = checkingObject.getInt("user_id");
            username = checkingObject.getString("username");
            email = checkingObject.getString("email");
            imageUrl = checkingObject.getString("image_url");
            profile_url = checkingObject.getString("google_plus_profile");

            actor = new UserItem(
                    checkingObject.getString("username"),
                    checkingObject.getString("email"),
                    checkingObject.getString("image_url")
            );

            userContentValues = new ContentValues();
            userContentValues.put(UserEntry.COLUMN_USER_ID, userId);
            userContentValues.put(UserEntry.COLUMN_USERNAME, username);
            userContentValues.put(UserEntry.COLUMN_EMAIL, email);
            userContentValues.put(UserEntry.COLUMN_IMG_URL, imageUrl);
            userContentValues.put(UserEntry.COLUMN_GOOGLE_PLUS_PROFILE, profile_url);
            userContentValues.put(UserEntry.COLUMN_CREATED_AT, System.currentTimeMillis());
            userContentValues.put(UserEntry.COLUMN_MODIFIED_AT, System.currentTimeMillis());
            userContentValues.put(UserEntry.COLUMN_ACTIVE_STATUS, 1);

            Uri userUri = getContentResolver().insert(
                    UserEntry.CONTENT_URI,
                    userContentValues
            );

            Log.i(LOG_TAG, "Newly inserted USER while fetching venue recent activities ===> " + userUri);

//            long newUserId = UserEntry.getUserIdFromUserUri(userUri);

            item = new RecentVenueActivityItem(
                    venueItem,
                    checkingObject.getString("modified_at"),
                    actor,
                    RecentVenueActivityItem.stringToActivity(checkingObject.getString("checking_type"))
            );
            checkingType = checkingObject.getString("checking_type");

            checkingContentValues = new ContentValues();
            checkingContentValues.put(CheckingEntry.COLUMN_VENUE_ID, venueItem.getId());
            checkingContentValues.put(CheckingEntry.COLUMN_CHECKER_ID, userId);
            checkingContentValues.put(CheckingEntry.COLUMN_CHECK_TYPE, checkingType);
            checkingContentValues.put(CheckingEntry.COLUMN_CREATED_AT, System.currentTimeMillis());
            checkingContentValues.put(CheckingEntry.COLUMN_MODIFIED_AT, System.currentTimeMillis());
            checkingContentValues.put(CheckingEntry.COLUMN_ACTIVE_STATUS, 1);

            Uri checkingUri = getContentResolver().insert(
                    CheckingEntry.CONTENT_URI,
                    checkingContentValues
            );

            Log.i(LOG_TAG, "Newly inserted CHECKING while fetching venue recent activities ===> " + checkingUri);

            recentVenueActivityItems.add(item);
        }

//        setProgressBarIndeterminateVisibility(false);
//        spinnerLayout.setVisibility(View.GONE);
    }

    @Override
    public void setImageBitmap(String url, Bitmap bitmap) {

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                this,
                CheckingEntry.buildCheckingUriWithVenueId(getIntent().getLongExtra(VenueItem.VENUE_ID, -1)),
                CHECKING_COLUMNS,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        Log.i(LOG_TAG, "LOADING FINISHED ====> " + Arrays.toString(data.getColumnNames()) + " COUNT " + data.getCount());
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

    @Override
    public void parsePostResponse(String respondString) throws JSONException {
        Log.i(LOG_TAG, respondString);
    }

    @Override
    public Context getContext() {
        return this;
    }
}
