package com.hoh.android.venuelocator;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.hoh.android.venuelocator.async.DownloadImageTask;
import com.hoh.android.venuelocator.blueprints.CustomPreferenceManager;
import com.hoh.android.venuelocator.blueprints.OnDownloadFinished;
import com.hoh.android.venuelocator.blueprints.OnFragmentInteractionListener;

import org.json.JSONException;

import java.net.MalformedURLException;
import java.net.URL;


public class ProfileActivity extends FragmentActivity implements OnFragmentInteractionListener, OnDownloadFinished{

    private CustomPreferenceManager preferenceManager;
    private TextView fullNameTextView, emailTextView;
    private ImageView profileImageView;

    private Button followMoreButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        preferenceManager = new CustomPreferenceManager(this);

        fullNameTextView = (TextView)findViewById(R.id.user_profile_name);
        emailTextView = (TextView) findViewById(R.id.user_profile_email);
        profileImageView = (ImageView) findViewById(R.id.user_profile_image_view);
        followMoreButton = (Button) findViewById(R.id.follow_more_btn);

        fullNameTextView.setText(fullNameTextView.getText() + " " + preferenceManager.getUserName());
        emailTextView.setText(emailTextView.getText() + " " + preferenceManager.getUserEmail());

        try{
            final URL url = new URL(preferenceManager.getUserImgUrl());
            (new DownloadImageTask(this, this)).execute(url);
        }
        catch (MalformedURLException ex){
            ex.printStackTrace();
        }

        followMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplication(), ChooseToFollowActivity.class));
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_profile, menu);
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

    public void loadProfileData(){

    }

    public void loadLeaders(){

    }

    public void fetchLeaders(){

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onListItemClicked(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void parseResponse(String url, String responseString) throws JSONException {

    }

    @Override
    public void setImageBitmap(String url, Bitmap bitmap) {
        if (url.equals(preferenceManager.getUserImgUrl())){
            profileImageView.setImageBitmap(bitmap);
        }
    }
}
