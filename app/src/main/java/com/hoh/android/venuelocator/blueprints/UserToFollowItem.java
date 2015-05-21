package com.hoh.android.venuelocator.blueprints;

import android.content.Intent;

/**
 * Created by funso on 3/4/15.
 */
public class UserToFollowItem {

    private String name;
    private String email;

    public UserToFollowItem(String name, String email){
        this.name = name;
        this.email = email;
    }

//    public UserToFollow(Intent intent){
//
//    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
