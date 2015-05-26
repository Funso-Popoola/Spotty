package com.hoh.android.venuelocator.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import com.hoh.android.venuelocator.data.VenueLocatorContract;
import com.hoh.android.venuelocator.data.VenueLocatorContract.UserEntry;
import com.hoh.android.venuelocator.data.VenueLocatorContract.VenueEntry;
import com.hoh.android.venuelocator.data.VenueLocatorContract.CheckingEntry;
import com.hoh.android.venuelocator.data.VenueLocatorContract.LeaderFollowerEntry;

/**
 * Created by funso on 3/14/15.
 */
public class VenueProvider extends ContentProvider{

    private VenueDbHelper dbHelper;
    private UriMatcher uriMatcher = buildUriMatcher();

    //uri match codes
    private final static int VENUE = 200;
    private final static int VENUE_WITH_ID = 201;
    private final static int USER = 300;
    private final static int USER_WITH_ID = 301;
    private final static int USER_FOLLOWED = 302;
    private final static int USER_NOT_FOLLOWED = 303;
    private final static int CHECKING = 400;
    private final static int CHECKING_WITH_ID = 401;
    private final static int CHECKING_BY_VENUE = 402;
    private final static int CHECKING_BY_USER = 403;
    private final static int CHECKING_BY_FOLLOWED = 404;
    private final static int FOLLOWER_LEADER = 500;
    private final static int FOLLOWER_LEADER_WITH_ID = 501;

    // selections

    private final static String venueSelectionById =
            VenueEntry.TABLE_NAME + "." + VenueEntry._ID + " = ?";

    private final static String venueSelectionByLatLng =
            VenueEntry.TABLE_NAME + "." + VenueEntry.COLUMN_LAT + " = ? AND "
                    + VenueEntry.COLUMN_LNG + " = ?";

    private final static String userSelectionById =
            UserEntry.TABLE_NAME + "." + UserEntry._ID + " = ?";

    private final static String usersFollowedSelection =
            LeaderFollowerEntry.TABLE_NAME + "." + LeaderFollowerEntry.COLUMN_FOLLOWER_ID + " = ?";

    private final static String usersNotFollowedSelection =
            LeaderFollowerEntry.TABLE_NAME + "." + LeaderFollowerEntry.COLUMN_FOLLOWER_ID + " != ?";

    private final static String followerLeaderSelectionById =
            LeaderFollowerEntry.TABLE_NAME + "." + LeaderFollowerEntry._ID + " = ?";

    private final static String checkingSelectionById =
            CheckingEntry.TABLE_NAME + "." + CheckingEntry._ID + " = ?";

    private final static String checkingSelectionByVenue =
            CheckingEntry.TABLE_NAME + "." + CheckingEntry.COLUMN_VENUE_ID + " = ?";

    private final static String checkingFollowedSelection =
            CheckingEntry.TABLE_NAME + "." + CheckingEntry.COLUMN_CHECKER_ID + " = ?";

    private final static String userCheckingFollowedSelection =
            LeaderFollowerEntry.TABLE_NAME + "." + LeaderFollowerEntry.COLUMN_FOLLOWER_ID + " = ?";

    // SQLQueryBuilders
    private static SQLiteQueryBuilder venueLocationQueryBuilder;
    private static SQLiteQueryBuilder checkingVenueQueryBuilder;
    private static SQLiteQueryBuilder checkingVenueLocationQueryBuilder;
    private static SQLiteQueryBuilder userCheckingQueryBuilder;

    private static SQLiteQueryBuilder userFollowedQueryBuilder;
    private static SQLiteQueryBuilder userFollowedCheckingsQueryBuilder;

    static {
        venueLocationQueryBuilder = new SQLiteQueryBuilder();
        checkingVenueQueryBuilder = new SQLiteQueryBuilder();
        checkingVenueLocationQueryBuilder = new SQLiteQueryBuilder();
        userCheckingQueryBuilder = new SQLiteQueryBuilder();

        userFollowedQueryBuilder = new SQLiteQueryBuilder();
        userFollowedCheckingsQueryBuilder = new SQLiteQueryBuilder();

        checkingVenueQueryBuilder.setTables(
                CheckingEntry.TABLE_NAME + " INNER JOIN " + VenueEntry.TABLE_NAME
                + " ON " + CheckingEntry.TABLE_NAME + "." + CheckingEntry.COLUMN_VENUE_ID + " = "
                + VenueEntry.TABLE_NAME + "." + VenueEntry._ID
        );

        checkingVenueLocationQueryBuilder.setTables(
                CheckingEntry.TABLE_NAME + " INNER JOIN " + VenueEntry.TABLE_NAME
                + " ON " + CheckingEntry.TABLE_NAME + "." + CheckingEntry.COLUMN_VENUE_ID + " = "
                + VenueEntry.TABLE_NAME + "." + VenueEntry.COLUMN_VENUE_ID
                + " INNER JOIN " + UserEntry.TABLE_NAME
                + " ON " + CheckingEntry.TABLE_NAME + "." + CheckingEntry.COLUMN_CHECKER_ID + " = "
                + UserEntry.TABLE_NAME + "." + UserEntry.COLUMN_USER_ID
        );

        userCheckingQueryBuilder.setTables(
                CheckingEntry.TABLE_NAME + " INNER JOIN " + UserEntry.TABLE_NAME
                + " ON " + CheckingEntry.TABLE_NAME + "." + CheckingEntry.COLUMN_CHECKER_ID + " = "
                + UserEntry.TABLE_NAME + "." + UserEntry._ID
        );

        userFollowedQueryBuilder.setTables(
                LeaderFollowerEntry.TABLE_NAME + " INNER JOIN " + UserEntry.TABLE_NAME
                + " ON " + LeaderFollowerEntry.TABLE_NAME + "." + LeaderFollowerEntry.COLUMN_FOLLOWER_ID + " = "
                + UserEntry.TABLE_NAME + "." + UserEntry.COLUMN_USER_ID
        );

        userFollowedCheckingsQueryBuilder.setTables(
                LeaderFollowerEntry.TABLE_NAME + " INNER JOIN " + UserEntry.TABLE_NAME
                + " ON " + LeaderFollowerEntry.TABLE_NAME + "." + LeaderFollowerEntry.COLUMN_FOLLOWER_ID + " = "
                + UserEntry.TABLE_NAME + "." + UserEntry.COLUMN_USER_ID
                + " INNER JOIN " + CheckingEntry.TABLE_NAME
                + " ON " + CheckingEntry.TABLE_NAME + "." + CheckingEntry.COLUMN_CHECKER_ID + " = "
                + UserEntry.TABLE_NAME + "." + UserEntry.COLUMN_USER_ID
                + " INNER JOIN " + VenueEntry.TABLE_NAME
                + " ON " + VenueEntry.TABLE_NAME + "." + VenueEntry.COLUMN_VENUE_ID + " = "
                + CheckingEntry.TABLE_NAME + "." + CheckingEntry.COLUMN_VENUE_ID
        );
    }


    @Override
    public boolean onCreate() {
        dbHelper = new VenueDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        final SQLiteDatabase database = dbHelper.getReadableDatabase();
        final int match = uriMatcher.match(uri);
        switch (match){
            case VENUE:
                return database.query(
                        VenueEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
            case VENUE_WITH_ID:
                return database.query(
                        VenueEntry.TABLE_NAME,
                        projection,
                        venueSelectionById,
                        new String[]{Long.toString(VenueEntry.getVenueIdFromVenueUri(uri))},
                        null,
                        null,
                        sortOrder
                );
            case USER:
                return database.query(
                        UserEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
            case USER_WITH_ID:
                return database.query(
                        UserEntry.TABLE_NAME,
                        projection,
                        userSelectionById,
                        new String[]{Long.toString(UserEntry.getUserIdFromUserUri(uri))},
                        null,
                        null,
                        sortOrder
                );
            case USER_FOLLOWED:
                return getFollowedUsers(uri, projection, sortOrder);
            case USER_NOT_FOLLOWED:
                return getNotFollowedUsers(uri, projection, sortOrder);
            case FOLLOWER_LEADER:
                return database.query(
                        LeaderFollowerEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
            case FOLLOWER_LEADER_WITH_ID:
                return database.query(
                        LeaderFollowerEntry.TABLE_NAME,
                        projection,
                        followerLeaderSelectionById,
                        new String[]{Long.toString(LeaderFollowerEntry.getIdFromFollowerLeaderUri(uri))},
                        null,
                        null,
                        sortOrder
                );
            case CHECKING:
                return database.query(
                        CheckingEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
            case CHECKING_BY_FOLLOWED:
                return getCheckingFollowed(uri, projection, sortOrder);
            case CHECKING_BY_USER:
                return getUserChecking(uri, projection, sortOrder);
            case CHECKING_BY_VENUE:
                return getVenueChecking(uri, projection, sortOrder);
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }

    }

    public Cursor getCheckingFollowed(Uri uri, String[] projection, String sortOrder){

        final SQLiteDatabase database = dbHelper.getReadableDatabase();
        long id = CheckingEntry.getSecondIdFromCheckingUri(uri);

        return userFollowedCheckingsQueryBuilder.query(
                database,
                projection,
                userCheckingFollowedSelection,
                new String[]{Long.toString(id)},
                null,
                null,
                sortOrder
        );
    }

    public Cursor getFollowedUsers(Uri uri, String[] projection, String sortOrder){

        final SQLiteDatabase database = dbHelper.getReadableDatabase();
        long id = LeaderFollowerEntry.getFollowerIdFromFollowerLeaderUri(uri);

        return userFollowedQueryBuilder.query(
                database,
                projection,
                usersFollowedSelection,
                new String[]{Long.toString(id)},
                null,
                null,
                sortOrder
        );
    }

    public Cursor getNotFollowedUsers(Uri uri, String[] projection, String sortOrder){

        final SQLiteDatabase database = dbHelper.getReadableDatabase();
        long id = LeaderFollowerEntry.getFollowerIdFromFollowerLeaderUri(uri);

        return userFollowedQueryBuilder.query(
                database,
                projection,
                usersNotFollowedSelection,
                new String[]{Long.toString(id)},
                null,
                null,
                sortOrder
        );
    }

    public Cursor getUserChecking(Uri uri, String[] projection, String sortOrder){

        final SQLiteDatabase database = dbHelper.getReadableDatabase();
        long id = CheckingEntry.getSecondIdFromCheckingUri(uri);

        return checkingVenueLocationQueryBuilder.query(
                database,
                projection,
                checkingFollowedSelection,
                new String[]{Long.toString(id)},
                null,
                null,
                sortOrder
        );
    }

    public Cursor getVenueChecking(Uri uri, String[] projection, String sortOrder){

        final SQLiteDatabase database = dbHelper.getReadableDatabase();
        long id = CheckingEntry.getSecondIdFromCheckingUri(uri);

        return checkingVenueLocationQueryBuilder.query(
                database,
                projection,
                checkingSelectionByVenue,
                new String[]{Long.toString(id)},
                null,
                null,
                sortOrder
        );
    }

    @Override
    public String getType(Uri uri) {
        final int match = uriMatcher.match(uri);
        switch (match){
            case VENUE:
                return VenueEntry.CONTENT_TYPE;
            case VENUE_WITH_ID:
                return VenueEntry.CONTENT_ITEM_TYPE;
            case USER:
                return UserEntry.CONTENT_TYPE;
            case USER_FOLLOWED:
                return UserEntry.CONTENT_TYPE;
            case USER_NOT_FOLLOWED:
                return UserEntry.CONTENT_TYPE;
            case USER_WITH_ID:
                return UserEntry.CONTENT_ITEM_TYPE;
            case FOLLOWER_LEADER:
                return LeaderFollowerEntry.CONTENT_TYPE;
            case FOLLOWER_LEADER_WITH_ID:
                return LeaderFollowerEntry.CONTENT_ITEM_TYPE;
            case CHECKING:
                return CheckingEntry.CONTENT_TYPE;
            case CHECKING_BY_FOLLOWED:
                return CheckingEntry.CONTENT_TYPE;
            case CHECKING_BY_USER:
                return CheckingEntry.CONTENT_TYPE;
            case CHECKING_BY_VENUE:
                return CheckingEntry.CONTENT_TYPE;
            case CHECKING_WITH_ID:
                return CheckingEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {

        final SQLiteDatabase database = dbHelper.getWritableDatabase();
        long _id;

        final int match = uriMatcher.match(uri);
        switch (match){
            case VENUE:
                _id = database.insert(VenueEntry.TABLE_NAME, null, values);
                return VenueEntry.buildVenueUriWithId(_id);
            case USER:
                _id = database.insert(UserEntry.TABLE_NAME, null, values);
                return UserEntry.buildUserUriWithId(_id);
            case FOLLOWER_LEADER:
                _id = database.insert(LeaderFollowerEntry.TABLE_NAME, null, values);
                return LeaderFollowerEntry.buildFollowerLeaderUriWithId(_id);
            case CHECKING:
                _id = database.insert(CheckingEntry.TABLE_NAME, null, values);
                return CheckingEntry.buildCheckingUriWithId(_id);
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        if (null == values || values.length <= 0){
            return 0;
        }

        int rowsAffected = 0;
        final SQLiteDatabase db = dbHelper.getWritableDatabase();

        final int match = uriMatcher.match(uri);
        switch (match){
            case VENUE:
                db.beginTransaction();
                try {
                    for (ContentValues contentValues : values){
                        long _id = db.insert(VenueEntry.TABLE_NAME, null, contentValues);
                        if (_id != -1){
                            rowsAffected++;
                        }
                    }
                    db.setTransactionSuccessful();
                    Log.i("VENUE_PROVIDER", " Bulk insert Completed " + rowsAffected + " Rows affected");
                }
                finally {
                    db.endTransaction();
                }
                break;
            case USER:
                db.beginTransaction();
                try {
                    for (ContentValues contentValues : values){
                        long _id = db.insert(UserEntry.TABLE_NAME, null, contentValues);
                        if (_id != -1){
                            rowsAffected++;
                        }
                    }
                    db.setTransactionSuccessful();
                }
                finally {
                    db.endTransaction();
                }
                break;
            case FOLLOWER_LEADER:
                db.beginTransaction();
                try {
                    for (ContentValues contentValues : values){
                        long _id = db.insert(LeaderFollowerEntry.TABLE_NAME, null, contentValues);
                        if (_id != -1){
                            rowsAffected++;
                        }
                    }
                    db.setTransactionSuccessful();
                }
                finally {
                    db.endTransaction();
                }
                break;
            case CHECKING:
                db.beginTransaction();
                try {
                    for (ContentValues contentValues : values){
                        long _id = db.insert(CheckingEntry.TABLE_NAME, null, contentValues);
                        if (_id != -1){
                            rowsAffected++;
                        }
                    }
                    db.setTransactionSuccessful();
                }
                finally {
                    db.endTransaction();
                }
                break;
            default:
                return super.bulkInsert(uri, values);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return rowsAffected;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase database = dbHelper.getWritableDatabase();

        final int match = uriMatcher.match(uri);
        int rowsDeleted = 0;
        switch (match){
            case VENUE:
                rowsDeleted = database.delete(VenueEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case USER:
                rowsDeleted = database.delete(UserEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case FOLLOWER_LEADER:
                rowsDeleted = database.delete(LeaderFollowerEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case CHECKING:
                rowsDeleted = database.delete(CheckingEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        final SQLiteDatabase database = dbHelper.getWritableDatabase();
        int rowsUpdated = 0;

        final int match = uriMatcher.match(uri);
        switch (match){
            case VENUE:
                rowsUpdated = database.update(VenueEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case USER:
                rowsUpdated = database.update(UserEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case FOLLOWER_LEADER:
                rowsUpdated = database.update(LeaderFollowerEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case CHECKING:
                rowsUpdated = database.update(CheckingEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown Uri: " + uri);
        }
        return rowsUpdated;
    }

    public UriMatcher buildUriMatcher(){
        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        String authority = VenueLocatorContract.CONTENT_AUTHORITY;
        matcher.addURI(authority, VenueLocatorContract.PATH_VENUE, VENUE);
        matcher.addURI(authority, VenueLocatorContract.PATH_VENUE + "/#", VENUE_WITH_ID);
        matcher.addURI(authority, VenueLocatorContract.PATH_USER, USER);
        matcher.addURI(authority, VenueLocatorContract.PATH_USER + "/#", USER_WITH_ID);
        matcher.addURI(authority, VenueLocatorContract.PATH_FOLLOWER_LEADER , FOLLOWER_LEADER);
        matcher.addURI(authority, VenueLocatorContract.PATH_FOLLOWER_LEADER + "/#", FOLLOWER_LEADER_WITH_ID);
        matcher.addURI(authority, VenueLocatorContract.PATH_FOLLOWER_LEADER + "/followed/#", USER_FOLLOWED);
        matcher.addURI(authority, VenueLocatorContract.PATH_FOLLOWER_LEADER + "/not_followed/#", USER_NOT_FOLLOWED);
        matcher.addURI(authority, VenueLocatorContract.PATH_CHECKING, CHECKING);
        matcher.addURI(authority, VenueLocatorContract.PATH_CHECKING + "/#", CHECKING_WITH_ID);
        matcher.addURI(authority, VenueLocatorContract.PATH_CHECKING + "/followed/#", CHECKING_BY_FOLLOWED);
        matcher.addURI(authority, VenueLocatorContract.PATH_CHECKING + "/user/#", CHECKING_BY_USER);
        matcher.addURI(authority, VenueLocatorContract.PATH_CHECKING + "/venue/#", CHECKING_BY_VENUE);
        return matcher;
    }
}
