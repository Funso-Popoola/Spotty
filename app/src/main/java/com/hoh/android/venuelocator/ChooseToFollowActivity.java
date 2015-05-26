package com.hoh.android.venuelocator;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;

import com.hoh.android.venuelocator.async.FetchDataTask;
import com.hoh.android.venuelocator.async.PostManTask;
import com.hoh.android.venuelocator.async.PostRequest;
import com.hoh.android.venuelocator.blueprints.CustomPreferenceManager;
import com.hoh.android.venuelocator.blueprints.OnDownloadFinished;
import com.hoh.android.venuelocator.blueprints.OnFragmentInteractionListener;
import com.hoh.android.venuelocator.blueprints.OnPostFinish;
import com.hoh.android.venuelocator.blueprints.RecentVenueActivityItem;
import com.hoh.android.venuelocator.blueprints.UserItem;
import com.hoh.android.venuelocator.blueprints.UserToFollowListAdapter;
import com.hoh.android.venuelocator.blueprints.Utility;
import com.hoh.android.venuelocator.data.VenueLocatorContract.UserEntry;
import com.hoh.android.venuelocator.data.VenueLocatorContract.LeaderFollowerEntry;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;


public class ChooseToFollowActivity extends FragmentActivity implements
        OnFragmentInteractionListener, OnDownloadFinished, OnPostFinish{

    private final String LOG_TAG = ChooseToFollowActivity.class.getSimpleName();
    private CustomPreferenceManager preferenceManager;

    private Button doneButton;
    private LinearLayout spinnerLayout;

    private String userUrl;
    private List<UserItem> userItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        super.onCreate(savedInstanceState);

        preferenceManager = new CustomPreferenceManager(this);
        setContentView(R.layout.activity_choose_to_follow);
        userItemList = new ArrayList<>();

        spinnerLayout = (LinearLayout)findViewById(R.id.followSpinnerLayout);
        doneButton = (Button)findViewById(R.id.done_btn);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Integer> chosenUsers = UserToFollowListAdapter.chosenUsers;

                //insert all the chosen users into the LeaderFollowers' table with the active user
                // as the follower

                insertLeaders(chosenUsers);
                uploadLeaders(chosenUsers);

                Intent intent = new Intent(getApplication(), ProfileActivity.class);
                startActivity(intent);
                finish();
            }
        });

//        setProgressBarIndeterminateVisibility(true);
        fetchAllUsers();
    }

    public void uploadLeaders(List<Integer> leaders){
        for (int i = 0; i < leaders.size(); i++){
            try {
                PostRequest request = new PostRequest(new URL(Utility.USER_FOLLOW_URL));
                List<NameValuePair> nameValuePairs = new ArrayList<>();
                nameValuePairs.add(new BasicNameValuePair("action", "insert"));
                nameValuePairs.add(new BasicNameValuePair("user_id", preferenceManager.getUserId() + ""));
                nameValuePairs.add(new BasicNameValuePair("leader_id",leaders.get(i) + ""));

                request.setData(nameValuePairs);
                PostManTask postManTask = new PostManTask(this);
                postManTask.execute(request);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

    }

    public void insertLeaders(List<Integer> leaders){
        Vector<ContentValues> contentValuesVector = new Vector<>();
        ContentValues contentValues;

        for (int i = 0; i < leaders.size(); i++){
            contentValues = new ContentValues();
            contentValues.put(LeaderFollowerEntry.COLUMN_LEADER_ID, leaders.get(i));
            contentValues.put(LeaderFollowerEntry.COLUMN_FOLLOWER_ID, preferenceManager.getUserId());
            contentValues.put(LeaderFollowerEntry.COLUMN_CREATED_AT, System.currentTimeMillis());
            contentValues.put(LeaderFollowerEntry.COLUMN_MODIFIED_AT, System.currentTimeMillis());
            contentValues.put(LeaderFollowerEntry.COLUMN_ACTIVE_STATUS, 1);

            contentValuesVector.add(contentValues);
        }

        int numOfRowsAffected = 0;

        if (contentValuesVector.size() > 0){
            ContentValues [] contentValuesArr = new ContentValues[contentValuesVector.size()];
            contentValuesVector.toArray(contentValuesArr);
            numOfRowsAffected = getContentResolver().bulkInsert(
                    LeaderFollowerEntry.CONTENT_URI,
                    contentValuesArr
            );
        }

        Log.i(LOG_TAG, "Number of LEADERS inserted ===> " + numOfRowsAffected);
    }


    public void fetchAllUsers(){

        FetchDataTask fetchDataTask = new FetchDataTask(this, spinnerLayout);
        userUrl = Utility.USER_URL + "/" + preferenceManager.getUserId();
        fetchDataTask.execute(userUrl);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_choose_to_follow, menu);
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

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onListItemClicked(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void parseResponse(String url, String responseString) throws JSONException {

        spinnerLayout.setVisibility(View.GONE);

//        setProgressBarIndeterminateVisibility(false);
        Log.i(LOG_TAG, responseString);

        userItemList.clear();


        if (url.contains(Utility.USER_URL)){

            JSONObject jsonObject = new JSONObject(responseString);
            JSONArray notFollowedArr = jsonObject.getJSONArray("not_followed");
            UserItem userItem;
            JSONObject userObject;

            int count = notFollowedArr.length();
            Vector<ContentValues> contentValuesVector = new Vector<ContentValues>(count);
            ContentValues contentValues;

            int userId;
            String username, email, image_url, google_profile_url;

            for (int i = 0; i < count; i++){
                userObject = notFollowedArr.getJSONObject(i);

                userId = Integer.parseInt(userObject.getString("user_id"));
                username = userObject.getString("username");
                email = userObject.getString("email");
                image_url = userObject.getString("image_url");
                google_profile_url = userObject.getString("google_plus_profile");
                userItem = new UserItem(
                        username,
                        email,
                        image_url
                );
                userItemList.add(userItem);

                contentValues = new ContentValues();
                contentValues.put(UserEntry.COLUMN_USER_ID, userId);
                contentValues.put(UserEntry.COLUMN_USERNAME, username);
                contentValues.put(UserEntry.COLUMN_EMAIL, email);
                contentValues.put(UserEntry.COLUMN_IMG_URL, image_url);
                contentValues.put(UserEntry.COLUMN_GOOGLE_PLUS_PROFILE, google_profile_url);
                contentValues.put(UserEntry.COLUMN_CREATED_AT, System.currentTimeMillis());
                contentValues.put(UserEntry.COLUMN_MODIFIED_AT, System.currentTimeMillis());
                contentValues.put(UserEntry.COLUMN_ACTIVE_STATUS, 1);

                contentValuesVector.add(contentValues);
            }

            int numOfRowsInserted = 0;

            if ( contentValuesVector.size() > 0 ) {
                ContentValues [] contentValuesArr = new ContentValues[contentValuesVector.size()];
                contentValuesVector.toArray(contentValuesArr);
                numOfRowsInserted = getContentResolver().bulkInsert(
                        UserEntry.CONTENT_URI,
                        contentValuesArr
                );
            }

            Log.i(LOG_TAG, "Number of USERS inserted ===> " + numOfRowsInserted);

            getSupportFragmentManager().beginTransaction().
                    replace(R.id.choose_to_follow_container, ChooseUserToFollowFragment.newInstance(userItemList))
                    .commit();

        }
    }

    @Override
    public void setImageBitmap(String url, Bitmap bitmap) {

    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void parsePostResponse(String respondString) throws JSONException {
        Log.i(LOG_TAG, respondString);
    }
}
