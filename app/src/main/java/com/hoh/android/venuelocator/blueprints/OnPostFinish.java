package com.hoh.android.venuelocator.blueprints;

import org.json.JSONException;

/**
 * Created by funso on 3/16/15.
 */
public interface OnPostFinish {

    public void parsePostResponse(String respondString) throws JSONException;
}
