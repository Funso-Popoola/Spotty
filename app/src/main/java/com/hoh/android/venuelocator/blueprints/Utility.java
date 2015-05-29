package com.hoh.android.venuelocator.blueprints;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.text.format.Time;

import com.hoh.android.venuelocator.R;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by funso on 3/4/15.
 */
public class Utility {

    public static final String API_KEY = "AIzaSyCey2nTKJ3NrZl6sgMVhK6qg3sMDq25WHA";
    public static final String BASE_URL = "http://vlback-hoh.rhcloud.com/api";
    public static final String USER_URL =  BASE_URL + "/users";
    public static final String CHECKING_URL = BASE_URL + "/checking";
    public static final String USER_FOLLOW_URL = BASE_URL + "/user_follow";
    public static final String VENUE_TYPES_URL = BASE_URL + "/venue_types";
    public static final String VENUE_URL = BASE_URL + "/venues";
    public static final String PLACE_API_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json";
    public static final String PLACE_API_TEXT_SEARCH_URL = "https://maps.googleapis.com/maps/api/place/textsearch/json";

    public static enum RequestType{
        POST,
        GET
    }

    public static long normalizeDate(long startDate){
        Time time = new Time();
        time.set(startDate);
        int julianDay = Time.getJulianDay(startDate, time.gmtoff);
        return time.setJulianDay(julianDay);
    }

    public static String getReadableDateString(Long date){
//        date = WeatherContract.normalizeDate(date);
        String pattern = "E, MMM d";
        Date dateObj = new Date(date * 1000);
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, Locale.ENGLISH);
        return dateFormat.format(dateObj);
    }

    static String formatDate(long dateInMillis) {
        Date date = new Date(dateInMillis);
        return DateFormat.getDateInstance().format(date);
    }

    // Format used for storing dates in the database. ALso used for converting those strings
// back into date objects for comparison/processing.
    public static final String DATE_FORMAT = "yyyyMMdd";
    /**
     * Helper method to convert the database representation of the date into something to display
     * to users. As classy and polished a user experience as "20140102" is, we can do better.
     *
     * @param context Context to use for resource localization
     * @param dateInMillis The date in milliseconds
     * @return a user-friendly representation of the date.
     */
    public static String getFriendlyDayString(Context context, long dateInMillis) {
// The day string for forecast uses the following logic:
// For today: "Today, June 8"
// For tomorrow: "Tomorrow"
// For the next 5 days: "Wednesday" (just the day name)
// For all days after that: "Mon Jun 8"
        Time time = new Time();
        time.setToNow();
        long currentTime = System.currentTimeMillis();
        int julianDay = Time.getJulianDay(dateInMillis, time.gmtoff);
        int currentJulianDay = Time.getJulianDay(currentTime, time.gmtoff);
// If the date we're building the String for is today's date, the format
// is "Today, June 24"
        if (julianDay == currentJulianDay) {
            String today = context.getString(R.string.today);
            int formatId = R.string.format_full_friendly_date;
            return String.format(context.getString(
                    formatId,
                    today,
                    getFormattedMonthDay(context, dateInMillis)));
        } else if ( julianDay < currentJulianDay + 7 ) {
// If the input date is less than a week in the future, just return the day name.
            return getDayName(context, dateInMillis);
        } else {
// Otherwise, use the form "Mon Jun 3"
            SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
            return shortenedDateFormat.format(dateInMillis);
        }
    }
    /**
     * Given a day, returns just the name to use for that day.
     * E.g "today", "tomorrow", "wednesday".
     *
     * @param context Context to use for resource localization
     * @param dateInMillis The date in milliseconds
     * @return
     */
    public static String getDayName(Context context, long dateInMillis) {
// If the date is today, return the localized version of "Today" instead of the actual
// day name.
        Time t = new Time();
        t.setToNow();
        int julianDay = Time.getJulianDay(dateInMillis, t.gmtoff);
        int currentJulianDay = Time.getJulianDay(System.currentTimeMillis(), t.gmtoff);
        if (julianDay == currentJulianDay) {
            return context.getString(R.string.today);
        } else if ( julianDay == currentJulianDay +1 ) {
            return context.getString(R.string.tomorrow);
        } else {
            Time time = new Time();
            time.setToNow();
// Otherwise, the format is just the day of the week (e.g "Wednesday".
            SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");
            return dayFormat.format(dateInMillis);
        }
    }
    /**
     * Converts db date format to the format "Month day", e.g "June 24".
     * @param context Context to use for resource localization
     * @param dateInMillis The db formatted date string, expected to be of the form specified
     * in Utility.DATE_FORMAT
     * @return The day in the form of a string formatted "December 6"
     */
    public static String getFormattedMonthDay(Context context, long dateInMillis ) {
        Time time = new Time();
        time.setToNow();
        SimpleDateFormat dbDateFormat = new SimpleDateFormat(Utility.DATE_FORMAT);
        SimpleDateFormat monthDayFormat = new SimpleDateFormat("MMMM dd");
        String monthDayString = monthDayFormat.format(dateInMillis);
        return monthDayString;
    }

    public static String getFormattedTemp(Double minDayTemp, Double maxDayTemp){
        return Math.round(minDayTemp) + "/" + Math.round(maxDayTemp);
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
