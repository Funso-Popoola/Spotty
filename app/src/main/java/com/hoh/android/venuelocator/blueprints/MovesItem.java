package com.hoh.android.venuelocator.blueprints;

/**
 * Created by funso on 3/4/15.
 */
public class MovesItem {

    private VenueItem venue;
    private String timeStamp;
    private String activity;

    public MovesItem(VenueItem venue, String timeStamp){
        this.venue = venue;
        this.timeStamp = timeStamp;
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

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }
}
