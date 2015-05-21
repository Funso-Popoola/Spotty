package com.hoh.android.venuelocator;

import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.hoh.android.venuelocator.async.PostManTask;
import com.hoh.android.venuelocator.async.PostRequest;
import com.hoh.android.venuelocator.blueprints.CustomPreferenceManager;
import com.hoh.android.venuelocator.blueprints.OnPostFinish;
import com.hoh.android.venuelocator.blueprints.Utility;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


public class LogInActivity extends FragmentActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        OnPostFinish{

    private final String LOG_TAG = LogInActivity.class.getSimpleName();
    private CustomPreferenceManager preferenceManager;

    private static final int RC_SIGN_IN = 0;
    private GoogleApiClient googleApiClient;
    private boolean intentInProgress;
    private ConnectionResult connectionResult;

    private boolean signInBtnClicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        preferenceManager = new CustomPreferenceManager(this);

        findViewById(R.id.sign_in_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(LOG_TAG, "Sign In Button Clicked");
                if (v.getId() == R.id.sign_in_button
                        && !googleApiClient.isConnecting()) {
                    signInBtnClicked = true;
                    resolveSignInError();
                }
            }
        });

//        findViewById(R.id.sign_out_button).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                logOut();
//            }
//        });

        // initialize the google api client
        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SIGN_IN) {
            if (resultCode != RESULT_OK) {
                signInBtnClicked = false;
            }

            intentInProgress = false;

            if (!googleApiClient.isConnecting()) {
                googleApiClient.connect();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_log_in, menu);
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
    protected void onStart() {
        super.onStart();

        if (!preferenceManager.isLoggedIn())
            googleApiClient.connect();
        else{
            startActivity(new Intent(this, ProfileActivity.class));
            finish();
        }


    }

    @Override
    protected void onStop() {
        super.onStop();
        googleApiClient.disconnect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        signInBtnClicked = false;
        Toast.makeText(this, "You're now connected!", Toast.LENGTH_LONG).show();
        retrieveUserProfile();

        //indicate that this user is logged-in in the sharedPreferences
        preferenceManager.setLoggedIn();

        startActivity(new Intent(getApplication(), ProfileActivity.class));
        finish();

    }

    public void uploadUserData(){
        try {
            PostRequest request = new PostRequest(new URL(Utility.USER_URL));
            List<NameValuePair> nameValuePairs = new ArrayList<>();
            nameValuePairs.add(new BasicNameValuePair("action", "insert"));
            nameValuePairs.add(new BasicNameValuePair("username", preferenceManager.getUserName()));
            nameValuePairs.add(new BasicNameValuePair("email", preferenceManager.getUserEmail()));
            nameValuePairs.add(new BasicNameValuePair("profile_image_url", preferenceManager.getUserImgUrl()));
            nameValuePairs.add(new BasicNameValuePair("image_alt_text", preferenceManager.getUserPlusProfile()));

            request.setData(nameValuePairs);
            PostManTask postManTask = new PostManTask(this);
            postManTask.execute(request);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onConnectionSuspended(int i) {
        googleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (!intentInProgress) {
            // Store the ConnectionResult so that we can use it later when the user clicks
            // 'sign-in'.
            Log.i(LOG_TAG, "ConnectionFailed");
            this.connectionResult = connectionResult;

            if (signInBtnClicked) {
                // The user has already clicked 'sign-in' so we attempt to resolve all
                // errors until the user is signed in, or they cancel.
                resolveSignInError();
            }
        }
    }

    public void logOut(){
        if (googleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(googleApiClient);
            googleApiClient.disconnect();
            googleApiClient.connect();
        }
    }

    public void resolveSignInError(){
        if (!intentInProgress && connectionResult.hasResolution()) {
            try {
                intentInProgress = true;
                startIntentSenderForResult(connectionResult.getResolution().getIntentSender(),
                        RC_SIGN_IN, null, 0, 0, 0);
            } catch (IntentSender.SendIntentException e) {
                // The intent was canceled before it was sent.  Return to the default
                // state and attempt to connect to get an updated ConnectionResult.
                intentInProgress = false;
                googleApiClient.connect();
            }
        }
    }

    public void retrieveUserProfile(){
        if (Plus.PeopleApi.getCurrentPerson(googleApiClient) != null) {
            Person currentPerson = Plus.PeopleApi.getCurrentPerson(googleApiClient);
            String personName = currentPerson.getDisplayName();
            String personPhoto = currentPerson.getImage().getUrl();
            String personGooglePlusProfile = currentPerson.getUrl();

            preferenceManager.setUserName(personName);
            preferenceManager.setUserImgUrl(personPhoto);
            preferenceManager.setUserPlusProfile(personGooglePlusProfile);

            Toast.makeText(this, "name: " + personName + " photo: " + personPhoto + " profile: " + personGooglePlusProfile, Toast.LENGTH_LONG).show();
            (new FetchUserEmailTask()).execute();

        }

    }

    public class UserInfo{
        String id;
        String email;
        String verified_email;
    }



    public class FetchUserEmailTask extends AsyncTask<Void,Void,String>{

        private String account;

        public FetchUserEmailTask() {
            account = Plus.AccountApi.getAccountName(googleApiClient);
        }

        @Override
        protected String doInBackground(Void... params) {

            HttpURLConnection httpURLConnection = null;

            try {
                URL url = new URL("https://www.googleapis.com/plus/v1/people/me");

                String sAccessToken = GoogleAuthUtil.getToken(LogInActivity.this, account,
                        "oauth2:" + Scopes.PLUS_LOGIN + " https://www.googleapis.com/auth/plus.profile.emails.read");

                httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestProperty("Authorization", "Bearer " + sAccessToken);

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                StringBuilder content = new StringBuilder();

                String line = null;

                while ((line=bufferedReader.readLine())!=null){
                    content.append(line);
                }

                String response = content.toString();
                if (!TextUtils.isEmpty(response)){

                    JSONArray emailArray = new JSONObject(response).getJSONArray("emails");

                    for (int i = 0; i < emailArray.length(); i++) {
                        JSONObject obj = (JSONObject)emailArray.get(i);

                        // Find and return the primary email associated with the account
                        if (obj.getString("type").equals("account")) {
                            return obj.getString("value");
                        }
                    }
                }
            }
            catch (UserRecoverableAuthException userAuthEx){
                // Start the user recoverable action using the intent returned by
                // getIntent()
                startActivityForResult(userAuthEx.getIntent(), RC_SIGN_IN);
            }
            catch (Exception ex){
                Log.i(LOG_TAG, "Error while fetching email :-");
                ex.printStackTrace();
            }
            finally {
                if (httpURLConnection != null){
                    httpURLConnection.disconnect();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if (null != s){
                Log.i(LOG_TAG, s);
                preferenceManager.setUserEmail(s);
                uploadUserData();
            }
        }
    }

    @Override
    public void parsePostResponse(String respondString) throws JSONException{
        Log.i(LOG_TAG, respondString);
        JSONObject response = new JSONObject(respondString);
        JSONArray lastInsertIdArr = response.getJSONArray("last_insert_id");
        int userId = Integer.parseInt(lastInsertIdArr.getJSONObject(0).getString("user_id"));

        Log.i(LOG_TAG, "User Id = " + userId);
        preferenceManager.setUserId(userId);
    }
}
