package com.hoh.android.venuelocator.blueprints;

import android.content.Intent;
import android.content.SharedPreferences;

/**
 * Created by funso on 3/4/15.
 */
public class VenueItem {

    public static String VENUE_INTERNAL_ID = "_id";
    public static String VENUE_ID = "venue_id";
    public static String VENUE_NAME = "venue_name";
    public static String VENUE_ADDRESS = "address";
    public static String VENUE_PLACE_ID = "place_id";
    public static String VENUE_LAT = "lat";
    public static String VENUE_LNG = "lng";

    private long id;
    private long internal_id;
    private String name;
    private String address;
    private String placeId;
    private double latitude;
    private double longitude;

    public VenueItem(long internalId,long id, String name, String address, String placeId, double latitude, double longitude){
        this.internal_id = internalId;
        this.id = id;
        this.name = name;
        this.address = address;
        this.placeId = placeId;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public VenueItem(Intent intent){
        this(intent.getLongExtra(VENUE_INTERNAL_ID, -1),
                intent.getLongExtra(VENUE_ID, -1),
                intent.getStringExtra(VENUE_NAME),
                intent.getStringExtra(VENUE_ADDRESS),
                intent.getStringExtra(VENUE_PLACE_ID),
                intent.getDoubleExtra(VENUE_LAT, 0),
                intent.getDoubleExtra(VENUE_LNG, 0));
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String[] toStringArray(){
        return new String[]{this.address, this.placeId, this.latitude + "", this.longitude + ""};
    }

    public long getInternal_id() {
        return internal_id;
    }

    public void setInternal_id(long internal_id) {
        this.internal_id = internal_id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
