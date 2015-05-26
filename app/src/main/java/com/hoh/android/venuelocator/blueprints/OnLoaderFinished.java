package com.hoh.android.venuelocator.blueprints;

import android.database.Cursor;

/**
 * Created by funso on 5/22/15.
 */
public interface OnLoaderFinished {

    public void refreshLoader(Cursor data);
}
