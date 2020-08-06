package com.example.munchking.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import org.json.JSONArray;
import org.parceler.Parcel;

@Parcel(analyze = {Friends.class})
@ParseClassName("Friends")
public class Friends extends ParseObject {
    public static final String KEY_FRIENDS = "friendship";

    public JSONArray getFriends(){
        return getJSONArray(KEY_FRIENDS);
    }

    public void setFriends(JSONArray arr){
        put(KEY_FRIENDS, arr);
    }
}
