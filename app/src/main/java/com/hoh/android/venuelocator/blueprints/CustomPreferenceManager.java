package com.hoh.android.venuelocator.blueprints;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.hoh.android.venuelocator.R;

/**
 * Created by funso on 3/16/15.
 */
public class CustomPreferenceManager {

    private Context context;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public CustomPreferenceManager(Context context){
        this.context = context;
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.editor = this.sharedPreferences.edit();
    }

    public boolean isLoggedIn(){
        return this.sharedPreferences.getBoolean(context.getString(R.string.pref_logged_in_key), false);
    }

    public void setLoggedIn(){
        editor.putBoolean(context.getString(R.string.pref_logged_in_key), true);
        editor.apply();
    }

    public int getUserId(){
        return this.sharedPreferences.getInt(context.getString(R.string.pref_user_id_key), -1);
    }

    public void setUserId(int userId){
        editor.putInt(context.getString(R.string.pref_user_id_key), userId);
        editor.apply();
    }

    public String  getUserName(){
        return this.sharedPreferences.getString(context.getString(R.string.pref_username_key), null);
    }

    public void setUserName(String userName){
        editor.putString(context.getString(R.string.pref_username_key), userName);
        editor.apply();
    }

    public String getUserEmail(){
        return this.sharedPreferences.getString(context.getString(R.string.pref_user_email_key), null);
    }

    public void setUserEmail(String email){
        editor.putString(context.getString(R.string.pref_user_email_key), email);
        editor.apply();
    }

    public String getUserImgUrl(){
        return this.sharedPreferences.getString(context.getString(R.string.pref_user_img_url_key), null);
    }

    public void setUserImgUrl(String img_url){
        editor.putString(context.getString(R.string.pref_user_img_url_key), img_url);
        editor.apply();
    }

    public String getUserPlusProfile(){
        return this.sharedPreferences.getString(context.getString(R.string.pref_user_google_plus_profile_key), null);
    }

    public void setUserPlusProfile(String plusProfile){
        editor.putString(context.getString(R.string.pref_user_google_plus_profile_key), plusProfile);
        editor.apply();
    }

    public double getCurrentLat(){
        return Double.parseDouble(this.sharedPreferences.getString("lat", "-1"));
    }

    public void setCurrentLat(double lat){
        editor.putString("lat", lat + "");
        editor.apply();
    }

    public double getCurrentLng(){
        return Double.parseDouble(this.sharedPreferences.getString("lng", "-1"));
    }

    public void setCurrentLng(double lng){
        editor.putString("lng", lng + "");
        editor.apply();
    }
}
