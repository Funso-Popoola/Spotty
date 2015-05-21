package com.hoh.android.venuelocator.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.format.Time;

/**
 * Created by funso on 3/4/15.
 */
public class VenueLocatorContract {

    public final static String CONTENT_AUTHORITY = "com.hoh.android.venuelocator";
    public final static Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public final static String PATH_LOCATION = "location";
    public final static String PATH_VENUE = "venue";
    public final static String PATH_USER = "user";
    public final static String PATH_CHECKING = "checking";

    public static long normalizeDate(long startDate){
        Time time = new Time();
        time.set(startDate);
        int julianDay = Time.getJulianDay(startDate, time.gmtoff);
        return time.setJulianDay(julianDay);
    }


    public static class LocationEntry implements BaseColumns{

        public final static String TABLE_NAME = "locations";
        public final static String COLUMN_LAT = "latitude";
        public final static String COLUMN_LNG = "longitude";
        public final static String COLUMN_PLACE_ID = "place_id";
        public final static String COLUMN_ADDRESS = "address";
        public final static String COLUMN_CREATED_AT = "created_at";
        public final static String COLUMN_MODIFIED_AT = "modified_at";
        public final static String COLUMN_ACTIVE_STATUS = "active_status";

        public final static Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_LOCATION)
                .build();

        public final static String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE
                + "/" + CONTENT_AUTHORITY
                + "/" + PATH_LOCATION;

        public final static String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE
                        + "/" + CONTENT_AUTHORITY
                        + "/" + PATH_LOCATION;

        // location/:id
        public static Uri buildLocationUriWithId(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static long getLocationIdFromLocationUri(Uri uri){
            return Long.parseLong(uri.getPathSegments().get(1));
        }

    }

    public static class VenueEntry implements BaseColumns {

        public final static String TABLE_NAME = "venues";
        public final static String COLUMN_LOC_ID = "location_id";
        public final static String COLUMN_NAME = "venue_name";
        public final static String COLUMN_CREATED_AT = "created_at";
        public final static String COLUMN_MODIFIED_AT = "modified_at";
        public final static String COLUMN_ACTIVE_STATUS = "active_status";

        public final static Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_VENUE)
                .build();

        public final static String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE
                        + "/" + CONTENT_AUTHORITY
                        + "/" + PATH_VENUE;

        public final static String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE
                        + "/" + CONTENT_AUTHORITY
                        + "/" + PATH_VENUE;

        // venue/:id
        public static Uri buildVenueUriWithId(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        // venue?lat=# & lng=#
        public static Uri buildVenueUriWithLatAndLng(double lat, double lng){
            return CONTENT_URI.buildUpon()
                    .appendQueryParameter("lat", Double.toString(lat))
                    .appendQueryParameter("lng", Double.toString(lng)).build();
        }

        public static long getVenueIdFromVenueUri(Uri uri){
            return Long.parseLong(uri.getPathSegments().get(1));
        }

        public static double[] getLatFromVenueUri(Uri uri){
            String lat = uri.getQueryParameter("lat");
            String lng = uri.getQueryParameter("lng");

            if (null != lat && null != lng){
                return new double[]{Double.parseDouble(lat), Double.parseDouble(lng)};
            }
            return null;
        }


    }

    public static class UserEntry implements BaseColumns{

        public final static String TABLE_NAME = "users";
        public final static String COLUMN_USERNAME = "username";
        public final static String COLUMN_EMAIL = "user_email";
        public final static String COLUMN_IMG_URL = "user_image_url";
        public final static String COLUMN_GOOGLE_PLUS_PROFILE = "google_plus_profile";
        public final static String COLUMN_IS_FOLLOWED = "is_followed";
        public final static String COLUMN_CREATED_AT = "created_at";
        public final static String COLUMN_MODIFIED_AT = "modified_at";
        public final static String COLUMN_ACTIVE_STATUS = "active_status";

        public final static Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_USER)
                .build();

        public final static String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE
                        + "/" + CONTENT_AUTHORITY
                        + "/" + PATH_USER;

        public final static String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE
                        + "/" + CONTENT_AUTHORITY
                        + "/" + PATH_USER;

        // user/:id
        public static Uri buildUserUriWithId(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        // user/followed/:follower_id
        public static Uri buildUserUriWithFollowerId(long followerId){
            return CONTENT_URI.buildUpon().appendPath("followed")
                    .appendPath(Long.toString(followerId)).build();
        }

        public static long getUserIdFromUserUri(Uri uri){
            return Long.parseLong(uri.getPathSegments().get(1));
        }

        public static long getFollowerIdFromUserUri(Uri uri){
            return Long.parseLong(uri.getPathSegments().get(2));
        }
    }

    public static class CheckingEntry implements BaseColumns{

        public final static String TABLE_NAME = "checking";
        public final static String COLUMN_CHECKER_ID = "checker_id";
        public final static String COLUMN_VENUE_ID = "venue_id";
        public final static String COLUMN_CHECK_TYPE = "check_type";
        public final static String COLUMN_CREATED_AT = "created_at";
        public final static String COLUMN_MODIFIED_AT = "modified_at";
        public final static String COLUMN_ACTIVE_STATUS = "active_status";

        public final static Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_CHECKING)
                .build();

        public final static String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE
                        + "/" + CONTENT_AUTHORITY
                        + "/" + PATH_CHECKING;

        public final static String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE
                        + "/" + CONTENT_AUTHORITY
                        + "/" + PATH_CHECKING;

        // checking/:id
        public static Uri buildCheckingUriWithId(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        // checking/user/:checker_id
        public static Uri buildCheckingUriWithCheckerId(long checkerId){
            return CONTENT_URI.buildUpon().appendPath("user")
                    .appendPath(Long.toString(checkerId)).build();
        }

        // checking/followed/:leader_id
        public static Uri buildCheckingUriWithLeaderId(long leaderId){
            return CONTENT_URI.buildUpon().appendPath("followed")
                    .appendPath(Long.toString(leaderId)).build();
        }

        // checking/venue/:venue_id
        public static Uri buildCheckingUriWithVenueId(long venueId){
            return CONTENT_URI.buildUpon().appendPath("venue")
                    .appendPath(Long.toString(venueId)).build();
        }

        public static long getFirstIdFromCheckingUri(Uri uri){
            return Long.parseLong(uri.getPathSegments().get(1));
        }

        public static long getSecondIdFromCheckingUri(Uri uri){
            return Long.parseLong(uri.getPathSegments().get(2));
        }
    }
}
