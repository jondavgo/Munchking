package com.example.munchking.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import org.parceler.Parcel;

@Parcel(analyze = {Friends.class})
@ParseClassName("Friends")
public class Friends extends ParseObject {
}
