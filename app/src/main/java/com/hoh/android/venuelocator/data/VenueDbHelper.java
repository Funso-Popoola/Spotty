package com.hoh.android.venuelocator.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.hoh.android.venuelocator.data.VenueLocatorContract.VenueEntry;
import com.hoh.android.venuelocator.data.VenueLocatorContract.UserEntry;
import com.hoh.android.venuelocator.data.VenueLocatorContract.CheckingEntry;
import com.hoh.android.venuelocator.data.VenueLocatorContract.LeaderFollowerEntry;
/**
 * Created by funso on 3/14/15.
 */
public class VenueDbHelper extends SQLiteOpenHelper {

    public final static String DATABASE_NAME = "venue_locator.db";
    public final static int DATABASE_VERSION = 1;

    public VenueDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_VENUES_TABLE =
                "CREATE TABLE IF NOT EXISTS " + VenueEntry.TABLE_NAME
                        + "("
                                + VenueEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                                + VenueEntry.COLUMN_VENUE_ID + " INTEGER NOT NULL, "
                                + VenueEntry.COLUMN_LAT + " REAL NOT NULL, "
                                + VenueEntry.COLUMN_LNG + " REAL NOT NULL, "
                                + VenueEntry.COLUMN_PLACE_ID + " TEXT NOT NULL, "
                                + VenueEntry.COLUMN_ADDRESS + " TEXT NOT NULL, "
                                + VenueEntry.COLUMN_NAME + " TEXT NOT NULL, "
                                + VenueEntry.COLUMN_CREATED_AT + " INTEGER NOT NULL, "
                                + VenueEntry.COLUMN_MODIFIED_AT + " INTEGER NOT NULL, "
                                + VenueEntry.COLUMN_ACTIVE_STATUS + " INTEGER NOT NULL DEFAULT 1, "
                                + " UNIQUE (" + VenueEntry.COLUMN_LAT + ", "
                                + VenueEntry.COLUMN_LNG + ") ON CONFLICT REPLACE "
                        + ");";

        final String SQL_CREATE_USERS_TABLE =
                "CREATE TABLE IF NOT EXISTS " + UserEntry.TABLE_NAME
                        + "("
                                + UserEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                                + UserEntry.COLUMN_USER_ID + " INTEGER NOT NULL, "
                                + UserEntry.COLUMN_USERNAME + " TEXT NOT NULL, "
                                + UserEntry.COLUMN_EMAIL + " TEXT UNIQUE NOT NULL, "
                                + UserEntry.COLUMN_IMG_URL + " TEXT NOT NULL, "
                                + UserEntry.COLUMN_GOOGLE_PLUS_PROFILE + " TEXT NOT NULL, "
                                + UserEntry.COLUMN_CREATED_AT + " INTEGER NOT NULL, "
                                + UserEntry.COLUMN_MODIFIED_AT + " INTEGER NOT NULL, "
                                + UserEntry.COLUMN_ACTIVE_STATUS + " INTEGER NOT NULL DEFAULT 1, "
                                + " UNIQUE (" + UserEntry.COLUMN_USER_ID + ") ON CONFLICT REPLACE "
                        + ");";

        final String SQL_CREATE_LEADER_FOLLOWER_TABLE =
                "CREATE TABLE IF NOT EXISTS " + LeaderFollowerEntry.TABLE_NAME
                        + "("
                        + LeaderFollowerEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + LeaderFollowerEntry.COLUMN_LEADER_ID + " INTEGER NOT NULL, "
                        + LeaderFollowerEntry.COLUMN_FOLLOWER_ID + " INTEGER NOT NULL, "
                        + LeaderFollowerEntry.COLUMN_CREATED_AT + " INTEGER NOT NULL, "
                        + LeaderFollowerEntry.COLUMN_MODIFIED_AT + " INTEGER NOT NULL, "
                        + LeaderFollowerEntry.COLUMN_ACTIVE_STATUS + " INTEGER NOT NULL DEFAULT 1, "
                        + " UNIQUE (" + LeaderFollowerEntry.COLUMN_LEADER_ID + ", "
                        + LeaderFollowerEntry.COLUMN_FOLLOWER_ID + ") ON CONFLICT REPLACE , "
                        + " FOREIGN KEY (" + LeaderFollowerEntry.COLUMN_LEADER_ID + ") REFERENCES "
                        + UserEntry.TABLE_NAME + "(" + UserEntry.COLUMN_USER_ID + "), "
                        + " FOREIGN KEY (" + LeaderFollowerEntry.COLUMN_FOLLOWER_ID + ") REFERENCES "
                        + UserEntry.TABLE_NAME + "(" + UserEntry.COLUMN_USER_ID + ")"
                        + ");";

        final String SQL_CREATE_CHECKING_TABLE =
                "CREATE TABLE IF NOT EXISTS " + CheckingEntry.TABLE_NAME
                        + "("
                                + CheckingEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                                + CheckingEntry.COLUMN_VENUE_ID + " INTEGER NOT NULL, "
                                + CheckingEntry.COLUMN_CHECKER_ID + " INTEGER NOT NULL, "
                                + CheckingEntry.COLUMN_CHECK_TYPE + " TEXT NOT NULL, "
                                + CheckingEntry.COLUMN_CREATED_AT + " INTEGER NOT NULL, "
                                + CheckingEntry.COLUMN_MODIFIED_AT + " INTEGER NOT NULL, "
                                + CheckingEntry.COLUMN_ACTIVE_STATUS + " INTEGER NOT NULL DEFAULT 1, "
                                + " FOREIGN KEY (" + CheckingEntry.COLUMN_VENUE_ID + ") REFERENCES "
                                + VenueEntry.TABLE_NAME + "(" + VenueEntry.COLUMN_VENUE_ID + "), "
                                + " FOREIGN KEY (" + CheckingEntry.COLUMN_CHECKER_ID + ") REFERENCES "
                                + UserEntry.TABLE_NAME + "(" + UserEntry.COLUMN_USER_ID + ")"
                        + ");";


        db.execSQL(SQL_CREATE_USERS_TABLE);
        db.execSQL(SQL_CREATE_VENUES_TABLE);
        db.execSQL(SQL_CREATE_CHECKING_TABLE);
        db.execSQL(SQL_CREATE_LEADER_FOLLOWER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS" + LeaderFollowerEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS" + CheckingEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS" + VenueEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS" + UserEntry.TABLE_NAME);

        onCreate(db);
    }
}
