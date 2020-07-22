package com.example.munchking.models;

import androidx.core.util.Pair;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

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
    public static final String KEY_DATE = "createdAt";

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

    // Misc. Methods
    public void addTraitEquip(Pair<String, String> item, boolean trait) throws JSONException {
        JSONArray arr;
        if(trait){
            arr = getTraits();
        } else {
            arr = getEquipment();
        }

        JSONObject object = new JSONObject();
        object.put("name", item.first);
        object.put("description", item.second);
        arr.put(object);

        if(trait){
            setTraits(arr);
        } else {
            setEquipment(arr);
        }
    }

    public void removeTraitEquip(int pos, boolean trait) throws JSONException{
        JSONArray arr;
        if(trait){
            arr = getTraits();
        } else {
            arr = getEquipment();
        }

        arr.remove(pos);

        if(trait){
            setTraits(arr);
        } else {
            setEquipment(arr);
        }
    }

    public List<Pair<String, String>> toArrayList(boolean trait) throws JSONException {
        JSONArray arr;
        if(trait){
            arr = getTraits();
        } else {
            arr = getEquipment();
        }
        List<Pair<String, String>> list = new ArrayList<>();
        for (int i = 0; i < arr.length(); i++) {
            String first = arr.getJSONObject(i).getString("name");
            String second = arr.getJSONObject(i).getString("description");
            list.add(new Pair<String, String>(first, second));
        }
        return list;
    }
}
