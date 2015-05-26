package com.hoh.android.venuelocator;

import android.app.ActionBar;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.plus.Plus;
import com.hoh.android.venuelocator.async.FetchDataTask;
import com.hoh.android.venuelocator.blueprints.CustomPreferenceManager;
import com.hoh.android.venuelocator.blueprints.FollowedCheckingsListAdapter;
import com.hoh.android.venuelocator.blueprints.MovesItem;
import com.hoh.android.venuelocator.blueprints.MovesListAdapter;
import com.hoh.android.venuelocator.blueprints.OnDownloadFinished;
import com.hoh.android.venuelocator.blueprints.OnFragmentInteractionListener;
import com.hoh.android.venuelocator.blueprints.RecentVenueActivityItem;
import com.hoh.android.venuelocator.blueprints.ShowVenuePagerAdapter;
import com.hoh.android.venuelocator.blueprints.UserItem;
import com.hoh.android.venuelocator.blueprints.Utility;
import com.hoh.android.venuelocator.blueprints.VenueItem;
import com.hoh.android.venuelocator.blueprints.VenueListAdapter;
import com.hoh.android.venuelocator.data.VenueLocatorContract;
import com.hoh.android.venuelocator.data.VenueLocatorContract.CheckingEntry;
import com.hoh.android.venuelocator.data.VenueLocatorContract.UserEntry;
import com.hoh.android.venuelocator.data.VenueLocatorContract.VenueEntry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class ShowVenueActivity extends FragmentActivity implements ActionBar.TabListener,
        OnFragmentInteractionListener,
        OnDownloadFinished,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks{

    private final String LOG_TAG = ShowVenueActivity.class.getSimpleName();
    private final String RESULTS_KEY = "results";
    private final String GEOMETRY_KEY = "geometry";
    private final String LOCATION_KEY = "location";
    private final String LAT_KEY = "lat";
    private final String LNG_KEY = "lng";
    private final String PLACE_ID_KEY = "place_id";
    private final String FORMATTED_ADDRESS = "formatted_address";
    private final String NAME_KEY = "name";
    private final String VICINITY_KEY = "vicinity";

    private final int NEAR_BY = 0;
    private final int MOVES = 1;
    private final int FOLLOWED = 2;

    private ShowVenuePagerAdapter pagerAdapter;
    private ViewPager viewPager;
    private LinearLayout spinnerLayout;

    private Button profileViewButton;

    private List<VenueItem> venueItemList;
    private VenueListAdapter venueListAdapter;
    private MovesListAdapter movesListAdapter;
    private FollowedCheckingsListAdapter followedCheckingsListAdapter;
    private List<MovesItem> movesItemList;
    private List<RecentVenueActivityItem> followedVenueActivityItemList;

    private double latitude, longitude;
    private int radius;
    private String type;

    private GoogleApiClient googleApiClient;
    private Location mLastLocation;

    private String textSearchAsyncUrl;

    private FetchDataTask fetchDataTask;

    private CustomPreferenceManager preferenceManager;

    private boolean initialized = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_venue);

        preferenceManager = new CustomPreferenceManager(this);
        // initialize all lists
        venueItemList = new ArrayList<>();
        movesItemList = new ArrayList<>();
        followedVenueActivityItemList = new ArrayList<>();

        // initialize the adapters
        venueListAdapter = new VenueListAdapter(this, null, 0);
        movesListAdapter = new MovesListAdapter(this, null, 0);
        followedCheckingsListAdapter = new FollowedCheckingsListAdapter(this, null, 0);

//        loadDummyData();

        buildGoogleApiClient();

        profileViewButton = (Button) findViewById(R.id.profileViewBtn);

        final boolean isLoggedIn = preferenceManager.isLoggedIn();

        if (!isLoggedIn){
            profileViewButton.setText(R.string.login_text);
        }

        profileViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Class nextClass = ProfileActivity.class;

                if (!isLoggedIn){
                    nextClass = LogInActivity.class;
                }

                startActivity(new Intent(getApplication(), nextClass));
            }
        });

        spinnerLayout = (LinearLayout)findViewById(R.id.spinnerLayout);
        spinnerLayout.setVisibility(View.GONE);

        finishInitialization();
    }

    public void finishInitialization(){

        // create the adapter
        pagerAdapter = new ShowVenuePagerAdapter(getSupportFragmentManager(), venueListAdapter, movesListAdapter, followedCheckingsListAdapter);

        final ActionBar actionBar = getActionBar();

        if (!initialized){
            // set up the action bar

            if (null == actionBar){
                Log.e(LOG_TAG, "Action Bar is Null");
                return;
            }
            actionBar.setHomeButtonEnabled(false);

            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        }


        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(pagerAdapter);
        viewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener(){
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        if (!initialized){
            // add tabs to the action bar
            for (int i = 0, limit = pagerAdapter.getCount(); i < limit; i++){
                actionBar.addTab(
                        actionBar.newTab().setText(pagerAdapter.getPageTitle(i)).setTabListener(this)
                );
            }
            initialized = true;
        }

    }

    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_show_venue, menu);
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
            Intent settingsIntent = new Intent(getApplication(), SettingsActivity.class);
            startActivity(settingsIntent);
        }
        else if (id == R.id.action_log_out){
            logOut();
        }

        return super.onOptionsItemSelected(item);
    }

    public void logOut(){
        googleApiClient.connect();
        if (googleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(googleApiClient);
            googleApiClient.disconnect();
            googleApiClient.connect();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

//        if (!initialized)
//            fetchAllVenues();
        updateFooterButton();
        if (preferenceManager.getCurrentLat() == -1 || preferenceManager.getCurrentLng() == -1){
            Log.i(LOG_TAG, "Getting the Location");
            googleApiClient.connect();
        }

//        if (!initialized){
            spinnerLayout.setVisibility(View.VISIBLE);
//        }
        fetchAllVenues();
        fetchMoves();
        fetchLeaders();

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateFooterButton();
    }

    public void updateFooterButton(){
        final boolean isLoggedIn = preferenceManager.isLoggedIn();

        if (!isLoggedIn){
            profileViewButton.setText(R.string.login_text);
        }
        else{
            profileViewButton.setText(R.string.profile_text);
        }


        profileViewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Class nextClass = ProfileActivity.class;

                if (!isLoggedIn) {
                    nextClass = LogInActivity.class;
                }
                startActivity(new Intent(getApplication(), nextClass));
            }
        });
    }
    @Override
    protected void onStop() {
        super.onStop();
        if (googleApiClient.isConnected()){
            googleApiClient.disconnect();
        }
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {

        int position = tab.getPosition();
        viewPager.setCurrentItem(position);
        switch (position){
            case NEAR_BY:
//                fetchAllVenues();
//                getSupportLoaderManager().restartLoader(VENUE_LOADER_ID, null, this);
                spinnerLayout.setVisibility(View.GONE);
                break;
            case MOVES:
//                fetchMoves();
//                getSupportLoaderManager().restartLoader(VENUE_LOADER_ID, null, this);
                spinnerLayout.setVisibility(View.GONE);
                break;
            case FOLLOWED:
//                fetchLeaders();
//                getSupportLoaderManager().restartLoader(VENUE_LOADER_ID, null, this);
                spinnerLayout.setVisibility(View.GONE);
                break;
            default:
                Log.i(LOG_TAG, "UNKNOWN TAB SELECTED ");
        }
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, android.app.FragmentTransaction ft) {

    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onListItemClicked(AdapterView<?> parent, View view, int position, long id) {
       Adapter adapter =  parent.getAdapter();

       if (adapter instanceof VenueListAdapter){
           Intent intent = new Intent(getApplicationContext(), VenueDetailsActivity.class);
           Cursor cursor = (Cursor) adapter.getItem(position);
           intent.putExtra(VenueItem.VENUE_INTERNAL_ID, cursor.getLong(cursor.getColumnIndex(VenueEntry._ID)));
           intent.putExtra(VenueItem.VENUE_ID, cursor.getLong(cursor.getColumnIndex(VenueEntry.COLUMN_VENUE_ID)));
           intent.putExtra(VenueItem.VENUE_NAME, cursor.getString(cursor.getColumnIndex(VenueEntry.COLUMN_NAME)));
           intent.putExtra(VenueItem.VENUE_ADDRESS, cursor.getString(cursor.getColumnIndex(VenueEntry.COLUMN_ADDRESS)));
           intent.putExtra(VenueItem.VENUE_PLACE_ID, cursor.getString(cursor.getColumnIndex(VenueEntry.COLUMN_PLACE_ID)));
           intent.putExtra(VenueItem.VENUE_LAT, cursor.getDouble(cursor.getColumnIndex(VenueEntry.COLUMN_LAT)));
           intent.putExtra(VenueItem.VENUE_LNG, cursor.getDouble(cursor.getColumnIndex(VenueEntry.COLUMN_LNG)));

           startActivity(intent);
       }
    }

    @Override
    public void parseResponse(String url, String responseString) throws JSONException{

        if (url.equals(Utility.PLACE_API_URL)){
            parsePlaceAPINearbySearch(responseString);
        }
        else if (url.equals(textSearchAsyncUrl)){
            parsePlaceAPITextSearch(responseString);
        }
        else if (url.equals(Utility.VENUE_URL)){
            parseVenue(responseString);
        }
        else if (url.equals(Utility.CHECKING_URL)){

        }
    }


    public void parsePlaceAPINearbySearch(String responseString) throws JSONException{
        JSONObject jsonResponseObject = new JSONObject(responseString);
        JSONArray resultArr = jsonResponseObject.getJSONArray(RESULTS_KEY);
        JSONObject resultObject, geometryObject, locationObject;
        double lat, lng;
        String placeId, vicinity;
        VenueItem venueItem;

        for (int i = 0, limit = resultArr.length(); i < limit; i++){
            resultObject = resultArr.getJSONObject(i);
            geometryObject = resultObject.getJSONObject(GEOMETRY_KEY);
            locationObject = geometryObject.getJSONObject(LOCATION_KEY);
            lat = locationObject.getDouble(LAT_KEY);
            lng = locationObject.getDouble(LNG_KEY);
            placeId = resultObject.getString(PLACE_ID_KEY);
            vicinity = resultObject.getString(VICINITY_KEY);

            venueItem = new VenueItem(1,1, "", vicinity, placeId, lat, lng);
            venueItemList.add(venueItem);
        }
        Log.i(LOG_TAG, venueItemList.toString());
    }

    public void parsePlaceAPITextSearch(String responseString) throws JSONException{
        JSONObject jsonResponseObject = new JSONObject(responseString);
        JSONArray resultArr = jsonResponseObject.getJSONArray(RESULTS_KEY);
        JSONObject resultObject, geometryObject, locationObject;
        double lat, lng;
        String placeId, address, name;

        VenueItem venueItem;
        venueItemList = new ArrayList<>();

        for (int i = 0, limit = resultArr.length(); i < limit; i++){
            resultObject = resultArr.getJSONObject(i);
            geometryObject = resultObject.getJSONObject(GEOMETRY_KEY);
            locationObject = geometryObject.getJSONObject(LOCATION_KEY);
            lat = locationObject.getDouble(LAT_KEY);
            lng = locationObject.getDouble(LNG_KEY);
            placeId = resultObject.getString(PLACE_ID_KEY);
            address = resultObject.getString(FORMATTED_ADDRESS);
            name = resultObject.getString(NAME_KEY);

            venueItem = new VenueItem(1,1, "", address + " (" + name + ")", placeId, lat, lng);
            venueItemList.add(venueItem);
        }

        Log.i(LOG_TAG, venueItemList.toString());
        pagerAdapter.notifyDataSetChanged();
    }

    public void parseVenue(String responseString) throws JSONException{

        Log.i(LOG_TAG, responseString);

        JSONArray responseArr = new JSONArray(responseString);
        JSONObject venueObject;
        VenueItem venueItem;
        venueItemList.clear();

        int count = responseArr.length() - 1;

        Vector<ContentValues> contentValuesVector = new Vector<ContentValues>(count);
        ContentValues contentValues;
        double latitude, longitude;
        int venue_id;
        String name, address, place_id;
        for(int i = 0; i <= count; i++){
            venueObject = responseArr.getJSONObject(i);
            contentValues = new ContentValues();

            latitude = Double.parseDouble(venueObject.getString("latitude"));
            longitude = Double.parseDouble(venueObject.getString("longitude"));
            place_id = venueObject.getString("place_id");
            name = venueObject.getString("venue_name");
            address = venueObject.getString("address");
            venue_id = Integer.parseInt(venueObject.getString("venue_id"));

            contentValues.put(VenueEntry.COLUMN_VENUE_ID, venue_id);
            contentValues.put(VenueEntry.COLUMN_LAT, latitude);
            contentValues.put(VenueEntry.COLUMN_LNG, longitude);
            contentValues.put(VenueEntry.COLUMN_PLACE_ID, place_id);
            contentValues.put(VenueEntry.COLUMN_NAME, name);
            contentValues.put(VenueEntry.COLUMN_ADDRESS, address);
            contentValues.put(VenueEntry.COLUMN_CREATED_AT, System.currentTimeMillis());
            contentValues.put(VenueEntry.COLUMN_MODIFIED_AT, System.currentTimeMillis());
            contentValues.put(VenueEntry.COLUMN_ACTIVE_STATUS, 1);

            contentValuesVector.add(contentValues);

//            venueItem = new VenueItem(
//                    Long.parseLong(venueObject.getString("venue_id")),
//                    name,
//                    address,
//                    place_id,
//                    latitude,
//                    longitude
//            );

//            venueItemList.add(venueItem);
        }

        int numOfRowsInserted = 0;

        if ( contentValuesVector.size() > 0 ) {
            ContentValues [] contentValuesArr = new ContentValues[contentValuesVector.size()];
            contentValuesVector.toArray(contentValuesArr);
            numOfRowsInserted = getContentResolver().bulkInsert(
                    VenueEntry.CONTENT_URI,
                    contentValuesArr
            );
        }

        Log.i(LOG_TAG, "Number of VENUES inserted ===> " + numOfRowsInserted);
        venueItemList = filterVenueByProximity(venueItemList, 50);
        spinnerLayout.setVisibility(View.GONE);
        finishInitialization();
    }

    public void parseMoves(String responseString) throws JSONException{

        Log.i(LOG_TAG, responseString);

        JSONArray responseArr = new JSONArray(responseString);
        JSONObject venueObject;
        VenueItem venueItem;
        venueItemList.clear();

        for(int i = 0, limit = responseArr.length(); i < limit; i++){
            venueObject = responseArr.getJSONObject(i);
//            venueItem = new VenueItem(
//                    Long.parseLong(venueObject.getString("venue_id")),
//                    venueObject.getString("venue_name"),
//                    venueObject.getString("address"),
//                    venueObject.getString("place_id"),
//                    Double.parseDouble(venueObject.getString("latitude")),
//                    Double.parseDouble(venueObject.getString("longitude"))
//            );

//            venueItemList.add(venueItem);
        }

        venueItemList = filterVenueByProximity(venueItemList, 50);
        spinnerLayout.setVisibility(View.GONE);
        finishInitialization();
    }

    @Override
    public void setImageBitmap(String url, Bitmap bitmap) {

    }

    @Override
    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                googleApiClient);
        if (mLastLocation != null) {
            latitude = mLastLocation.getLatitude();
            longitude = mLastLocation.getLongitude();

            preferenceManager.setCurrentLat(latitude);
            preferenceManager.setCurrentLng(longitude);
            Log.i(LOG_TAG, "lat: " + latitude + " lng: " + longitude);

        }
    }

    public void loadDummyData(){

        VenueItem venueItem;
        for (int i = 0; i < 10; i++){
            venueItem = new VenueItem(1, 1, "Lecture Theatre Name", "Address", "adfadff", 5.23411, 9.1234);
            venueItemList.add(venueItem);
        }

        UserItem userItem = new UserItem("Adeola Onifade", "onifade@gmail.com", "http://www.plus.google.com/+onifade");
        RecentVenueActivityItem item;
        // populate the content of the adapter
        for (int i = 0; i < 10; i++){
            item = new RecentVenueActivityItem(venueItemList.get(i), "01/01/2020", userItem, RecentVenueActivityItem.Activity.CHECK_IN);
            followedVenueActivityItemList.add(item);
        }

        MovesItem movesItem;
        for (int i = 0; i < 10; i++){
            movesItem = new MovesItem(venueItemList.get(i), "01/01/2021");
            movesItemList.add(movesItem);
        }

    }

    public void fetchVenueByTextSearch(){
        fetchDataTask = new FetchDataTask(this, spinnerLayout);
        radius = 50;
        type = "";
        textSearchAsyncUrl = Utility.buildTextSearchUrl("Lecture+Theatre+in+OAU", latitude, longitude, radius);
        fetchDataTask.execute(textSearchAsyncUrl);
    }

    public void fetchAllVenues(){
        fetchDataTask = new FetchDataTask(this, spinnerLayout);
        fetchDataTask.execute(Utility.VENUE_URL);

    }

    public void fetchLeaders(){
        int userId = preferenceManager.getUserId();
        if (userId != -1){
            fetchDataTask = new FetchDataTask(this, spinnerLayout);
            fetchDataTask.execute(Utility.USER_URL + '/' + userId);
        }

    }

    public void fetchMoves(){
        int userId = preferenceManager.getUserId();
        if (userId != -1){
            fetchDataTask = new FetchDataTask(this, spinnerLayout);
            fetchDataTask.execute(Utility.CHECKING_URL + '/' + userId);
        }

    }

    public List<VenueItem> filterVenueByProximity(List<VenueItem> venueItems, int radius){
        List<VenueItem> result = new ArrayList<>();
        double lat, lng;

        for(VenueItem venueItem : venueItems){
            lat = venueItem.getLatitude();
            lng = venueItem.getLongitude();
            Log.i(LOG_TAG, calcDistance(lat, lng, latitude, longitude) + "");
            if (calcDistance(lat, lng, latitude, longitude) <= radius){

                result.add(venueItem);
            }
        }

        return result;
    }

    public double calcDistance(double x1, double y1, double x2, double y2){
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2)) * 1000;
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i(LOG_TAG, "Connection Suspended");
        googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

        Log.i(LOG_TAG, "Connection Failed");
    }

    @Override
    public Context getContext() {
        return this;
    }
}
