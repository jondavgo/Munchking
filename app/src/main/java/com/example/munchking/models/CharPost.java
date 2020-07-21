package com.example.munchking.models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.parceler.Parcel;

@Parcel(analyze={CharPost.class})
@ParseClassName("Character")
public class CharPost extends ParseObject {

    // Keys
    public static final String KEY_NAME = "name";
    public static final String KEY_PHOTO = "photo";
    public static final String KEY_TTRPG = "ttrpg";
    public static final String KEY_USER = "creator";
    public static final String KEY_CLASS = "class";
    public static final String KEY_RACE = "race";
    public static final String KEY_DESC = "description";
    public static final String KEY_TRAIT = "traits";
    public static final String KEY_EQUIP = "equipment";

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

    public String getClasses() {
        return getString(KEY_CLASS);
    }

    public String getRace() {
        return getString(KEY_RACE);
    }

    public String getDesc() {
        return getString(KEY_DESC);
    }

    public JSONArray getTraits() {
        return getJSONArray(KEY_TRAIT);
    }

    public JSONArray getEquipment() {
        return getJSONArray(KEY_EQUIP);
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

    public void setClasses(String classes) {
         put(KEY_CLASS, classes);
    }

    public void setRace(String race) {
         put(KEY_RACE, race);
    }

    public void setDesc(String desc) {
         put(KEY_DESC, desc);
    }

    public void setTraits(JSONArray traits) {
         put(KEY_TRAIT, traits);
    }

    public void setEquipment(JSONArray equipment) {
         put(KEY_EQUIP, equipment);
    }
}
