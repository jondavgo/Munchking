package com.example.munchking.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.parceler.Parcel;

@Parcel(analyze = {Friends.class})
@ParseClassName("Friends")
public class Friends extends ParseObject {
    public static final String KEY_FRIENDS = "friendship";
    public static final String KEY_USER = "otherUser";
    public static final String KEY_CONNECT = "connected";

    public JSONArray getFriends() {
        return getJSONArray(KEY_FRIENDS);
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user) {
        put(KEY_USER, user);
    }

    public void setFriends(JSONArray arr) {
        put(KEY_FRIENDS, arr);
    }

    public boolean getConnection() {
        return getBoolean(KEY_CONNECT);
    }

    public void setConnection(boolean connection) {
        put(KEY_CONNECT, connection);
    }
}
