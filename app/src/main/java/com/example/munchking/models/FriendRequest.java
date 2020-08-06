package com.example.munchking.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.parceler.Parcel;

@Parcel(analyze = {FriendRequest.class})
@ParseClassName("FriendRequest")
public class FriendRequest extends ParseObject {
    // KEYS
    public static final String KEY_FROM = "fromUser";
    public static final String KEY_TO = "toUser";
    public static final String KEY_STATUS = "status";
    // STATUS
    public static final String PENDING = "pending";
    public static final String SENT = "sent";
    public static final String ACCEPTED = "accepted";

    public ParseUser getSender() {
        return getParseUser(KEY_FROM);
    }

    public ParseUser getReceiver() {
        return getParseUser(KEY_TO);
    }

    public String getStatus() {
        return getString(KEY_STATUS);
    }

    public void setSender(ParseUser user) {
        put(KEY_FROM, user);
    }

    public void setReceiver(ParseUser user) {
        put(KEY_TO, user);
    }

    public void setStatus(String status) {
        put(KEY_STATUS, status);
    }

}
