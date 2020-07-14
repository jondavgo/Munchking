package com.example.munchking.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Character")
public class CharPost extends ParseObject {

    // Keys
    public static final String KEY_NAME = "name";
    public static final String KEY_PHOTO = "photo";
    public static final String KEY_TTRPG = "ttrpg";
    public static final String KEY_USER = "creator";

    // Required empty constructor
    public CharPost(){}

    // Getters
    public String getName() {
        return getString(KEY_NAME);
    }

    public ParseFile getPhoto() {
        return getParseFile(KEY_PHOTO);
    }

    public String getTtrpg() {
        return getString(KEY_TTRPG);
    }

    public ParseUser getUser() {
        return getParseUser(KEY_USER);
    }

    // Setters
    public void setName(String name){
        put(KEY_NAME, name);
    }

    public void setPhoto(ParseFile photo){
        put(KEY_PHOTO, photo);
    }

    public void setTtrpg(String ttrpg){
        put(KEY_TTRPG, ttrpg);
    }

    public void setUser(ParseUser user){
        put(KEY_USER, user);
    }
}
