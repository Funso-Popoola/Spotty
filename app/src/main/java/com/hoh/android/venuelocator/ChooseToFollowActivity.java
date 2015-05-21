package com.hoh.android.venuelocator;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;

import com.hoh.android.venuelocator.async.FetchDataTask;
import com.hoh.android.venuelocator.blueprints.CustomPreferenceManager;
import com.hoh.android.venuelocator.blueprints.OnDownloadFinished;
import com.hoh.android.venuelocator.blueprints.OnFragmentInteractionListener;
import com.hoh.android.venuelocator.blueprints.UserItem;
import com.hoh.android.venuelocator.blueprints.UserToFollowListAdapter;
import com.hoh.android.venuelocator.blueprints.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class ChooseToFollowActivity extends FragmentActivity implements
        OnFragmentInteractionListener, OnDownloadFinished{

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
                List<UserItem> chosenUsers = UserToFollowListAdapter.chosenUsers;
                Intent intent = new Intent(getApplication(), ProfileActivity.class);
            }
        });

        setProgressBarIndeterminateVisibility(true);
        fetchAllUsers();
    }


    public void fetchAllUsers(){

        FetchDataTask fetchDataTask = new FetchDataTask(this, spinnerLayout);
        userUrl = Utility.USER_URL + "/" + preferenceManager.getUserId();
        fetchDataTask.execute();
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

        setProgressBarIndeterminateVisibility(false);
        Log.i(LOG_TAG, responseString);

        userItemList.clear();

        if (url.contains(Utility.USER_URL)){

            JSONObject jsonObject = new JSONObject(responseString);
            JSONArray notFollowedArr = jsonObject.getJSONArray("not_followed");
            UserItem userItem;
            JSONObject userObject;

            for (int i = 0, limit = notFollowedArr.length(); i < limit; i++){
                userObject = notFollowedArr.getJSONObject(i);
                userItem = new UserItem(
                        userObject.getString("username"),
                        userObject.getString("email"),
                        userObject.getString("image_url")
                );
                userItemList.add(userItem);
            }

            getFragmentManager().beginTransaction().
                    replace(R.id.choose_to_follow_container, ChooseUserToFollowFragment.newInstance(userItemList))
                    .commit();

        }
    }

    @Override
    public void setImageBitmap(String url, Bitmap bitmap) {

    }
}
