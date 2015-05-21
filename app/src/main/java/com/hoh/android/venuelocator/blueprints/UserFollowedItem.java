package com.hoh.android.venuelocator.blueprints;

/**
 * Created by funso on 3/4/15.
 */
public class UserFollowedItem {

    private String fullName;
    private String email;

    public UserFollowedItem(String fullName, String email){
        this.fullName = fullName;
        this.email = email;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
