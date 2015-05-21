package com.hoh.android.venuelocator.blueprints;

import android.net.Uri;
import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by funso on 3/4/15.
 */
public class Utility {

    public static final String API_KEY = "AIzaSyCey2nTKJ3NrZl6sgMVhK6qg3sMDq25WHA";
    public static final String BASE_URL = "http://vlback-hoh.rhcloud.com/api";
    public static final String USER_URL =  BASE_URL + "/users";
    public static final String USER_AUTH = BASE_URL + "/user_auth";
    public static final String CHECKING_URL = BASE_URL + "/checking";
    public static final String IMAGES_URL = BASE_URL + "/images";
    public static final String USER_FOLLOW_URL = BASE_URL + "/user_follow";
    public static final String VENUE_TYPES_URL = BASE_URL + "/venue_types";
    public static final String VENUE_URL = BASE_URL + "/venues";
    public static final String PLACE_API_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json";
    public static final String PLACE_API_TEXT_SEARCH_URL = "https://maps.googleapis.com/maps/api/place/textsearch/json";

    public static enum RequestType{
        POST,
        GET
    }

    public static String getNetworkResponse(InputStream inputStream) throws IOException{
        String responseStr = null;

        if (null != inputStream){
            BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder builder = new StringBuilder();

            String line = null;
            while ((line = bufferedReader.readLine()) != null){
                builder.append(line);
            }

            responseStr = builder.toString();
        }

        return responseStr;
    }

    public static String buildPlacesApiUrl(double latitude, double longitude, int radius, String type){
        String url = PLACE_API_URL;
        try{
            Uri uri = Uri.parse(url);
            uri = uri.buildUpon().appendQueryParameter("location", latitude + "," + longitude)
                    .appendQueryParameter("radius", radius + "")
                    .appendQueryParameter("types", type)
                    .appendQueryParameter("key", API_KEY)
                    .build();
            url = uri.toString();
        }
        catch (Exception malEx){
            malEx.printStackTrace();
        }
        return url;
    }

    public static String buildTextSearchUrl(String query, double latitude, double longitude, int radius){
        String url = PLACE_API_TEXT_SEARCH_URL;
        try{
            Uri uri = Uri.parse(url);
            uri = uri.buildUpon().appendQueryParameter("query", query)
                    .appendQueryParameter("location", latitude + "," + longitude)
                    .appendQueryParameter("radius", radius + "")
                    .appendQueryParameter("key", API_KEY)
                    .build();
            url = uri.toString();
        }
        catch (Exception malEx){
            malEx.printStackTrace();
        }
        return url;
    }

    public static List<String[]> venueItemListToArrayList(List<VenueItem> venueItems){
        List<String[]> list = new ArrayList<>();
        for (VenueItem venueItem : venueItems){
            list.add(venueItem.toStringArray());
        }
        return list;
    }
}
