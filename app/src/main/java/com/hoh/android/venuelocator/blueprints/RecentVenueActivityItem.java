package com.hoh.android.venuelocator.blueprints;

import com.hoh.android.venuelocator.LogInActivity;

/**
 * Created by funso on 3/4/15.
 */
public class RecentVenueActivityItem {

    private VenueItem venue;
    private String timeStamp;
    private UserItem actor;
    private Activity activity;
    public enum Activity{
        CHECK_IN,
        CHECK_OUT
    }

    public static Activity stringToActivity(String str){
        if (str.equals("check-in")){
            return Activity.CHECK_IN;
        }
        else {
            return Activity.CHECK_OUT;
        }
    }

    public RecentVenueActivityItem(VenueItem venue, String timeStamp, UserItem actor, Activity activity){
        this.venue = venue;
        this.timeStamp = timeStamp;
        this.actor = actor;
        this.activity = activity;
    }

    public VenueItem getVenue() {
        return venue;
    }

    public void setVenue(VenueItem venue) {
        this.venue = venue;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public UserItem getActor() {
        return actor;
    }

    public void setActor(UserItem actor) {
        this.actor = actor;
    }

    public String getActivity(){
        if (this.activity == Activity.CHECK_IN)
            return "Checked In";
        else
            return "Checked Out";
    }

    public static String getActivity(Activity activity){
        if (activity == Activity.CHECK_IN)
            return "Checked In";
        else
            return "Checked Out";
    }
}
