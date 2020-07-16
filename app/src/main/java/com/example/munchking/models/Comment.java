package com.example.munchking.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

@ParseClassName("Comment")
public class Comment extends ParseObject {
    public static final String KEY_USER = "user";
    public static final String KEY_POST = "post";
    public static final String KEY_TEXT = "message";

    // Required empty constructor
    public Comment(){ }

    public ParseUser getUser(){
        return getParseUser(KEY_USER);
    }

    public void setUser(ParseUser user){
        put(KEY_USER, user);
    }

    public CharPost getPost(){
        return (CharPost) getParseObject(KEY_POST);
    }

    public void setPost(CharPost post){
        put(KEY_POST, post);
    }

    public String getText(){
        return getString(KEY_TEXT);
    }

    public void setText(String text){
        put(KEY_TEXT, text);
    }
}

