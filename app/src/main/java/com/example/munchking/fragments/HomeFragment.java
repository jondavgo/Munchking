package com.example.munchking.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.example.munchking.R;
import com.example.munchking.activities.PreferencesActivity;
import com.example.munchking.adapters.CharactersAdapter;
import com.example.munchking.models.CharPost;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    public static final String TAG = "HomeFragment";
    public static final String DATE = "Date";
    public static final String DIST = "Distance";
    protected CharactersAdapter adapter;
    protected List<CharPost> charPosts;
    private Switch swSort;

    protected RecyclerView rvChars;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        charPosts = new ArrayList<>();
        adapter = new CharactersAdapter(charPosts, getContext());

        rvChars = view.findViewById(R.id.rvChars);
        swSort = view.findViewById(R.id.swSort);

        rvChars.setAdapter(adapter);
        rvChars.setLayoutManager(new LinearLayoutManager(getContext()));

        if(swSort != null) {
            swSort.setText(DATE);
            swSort.setChecked(true);
            swSort.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b) {
                        swSort.setText(DIST);
                        adapter.clear();
                        queryByDistance();
                        return;
                    }
                    swSort.setText(DATE);
                    adapter.clear();
                    query();
                }
            });
            swSort.setChecked(false);
        }
    }

    protected void query() {
        ParseQuery<CharPost> query = ParseQuery.getQuery(CharPost.class);
        JSONArray array = ParseUser.getCurrentUser().getJSONArray("favGames");
        query.include(CharPost.KEY_USER);
        query.orderByDescending(CharPost.KEY_DATE);
        try {
            query.whereContainedIn(CharPost.KEY_TTRPG, PreferencesActivity.fromJSONArray(array));
        } catch (JSONException e) {
            Log.e(TAG, "JSON Exception during home query!", e);
        }
        query.findInBackground(new FindCallback<CharPost>() {
            @Override
            public void done(List<CharPost> objects, ParseException e) {
                if(e == null){
                    adapter.addAll(objects);
                } else {
                    Log.e(TAG, "Query error!", e);
                    Toast.makeText(getContext(), "Something went wrong while grabbing posts!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void queryByDistance(){
        ParseQuery<ParseUser> userQuery = ParseQuery.getQuery(ParseUser.class);
        final ParseUser user = ParseUser.getCurrentUser();
        // TODO: Uncomment below when ready for upscaling
        //userQuery.whereWithinMiles(MapsFragment.KEY_LOCATION, user.getParseGeoPoint(MapsFragment.KEY_LOCATION), 15);
        userQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if(e == null){
                    // Code to sort users by distance
                    List<ParseUser> sorted = mergeSort(objects, user.getParseGeoPoint(MapsFragment.KEY_LOCATION));
                    for (int i = 0; i < sorted.size(); i++) {
                        Log.d(TAG, "Querying for: " + sorted.get(i).getUsername());
                        queryByUser(sorted.get(i));
                    }
                } else {
                    Log.e(TAG, "Query error!", e);
                    Toast.makeText(getContext(), "Something went wrong while grabbing posts!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void queryByUser(ParseUser user) {
        ParseQuery<CharPost> query = ParseQuery.getQuery(CharPost.class);
        query.include(CharPost.KEY_USER);
        query.orderByDescending(CharPost.KEY_DATE);
        query.whereEqualTo(CharPost.KEY_USER, user);
//        query.findInBackground(new FindCallback<CharPost>() {
//            @Override
//            public void done(List<CharPost> objects, ParseException e) {
//                if(e == null){
//                    for(CharPost object: objects){
//                        Log.d(TAG, "Added character " + object.getName() + " by: " + object.getUser().getUsername());
//                    }
//                    adapter.addAll(objects);
//                } else {
//                    Log.e(TAG, "Query error!", e);
//                    Toast.makeText(getContext(), "Something went wrong while grabbing posts!", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
        try {
            List<CharPost> objects = query.find();
            for(CharPost object: objects){
                Log.d(TAG, "Added character " + object.getName() + " by: " + object.getUser().getUsername());
            }
            adapter.addAll(objects);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private List<ParseUser> mergeSort(List<ParseUser> list, ParseGeoPoint parseGeoPoint) {
        if(list.size() == 1){
            Log.d(TAG, "List (Base): " + printList(list));
            return list;
        }
        int mid = list.size()/2;
        ArrayList<ParseUser> left = new ArrayList<>();
        ArrayList<ParseUser> right = new ArrayList<>();

        for (int i = 0; i < mid; i++) {
            left.add(list.get(i));
        }
        for (int i = mid; i < list.size(); i++) {
            right.add(list.get(i));
        }

        Log.d(TAG, "List: " + printList(list));
        Log.d(TAG, "Left: " + printList(left));
        Log.d(TAG, "Right: " + printList(right));

        mergeSort(left, parseGeoPoint);
        mergeSort(right, parseGeoPoint);
        merge(left, right, list, parseGeoPoint);
        Log.d(TAG, "After Merge: " + printList(list));
        return list;
    }

    private void merge(ArrayList<ParseUser> left, ArrayList<ParseUser> right, List<ParseUser> list, ParseGeoPoint parseGeoPoint) {
        int leftI = 0;
        int rightI = 0;
        int listI = 0;

        while (leftI < left.size() && rightI < right.size()){
            double leftDist = parseGeoPoint.distanceInMilesTo(left.get(leftI).getParseGeoPoint(MapsFragment.KEY_LOCATION));
            double rightDist = parseGeoPoint.distanceInMilesTo(right.get(rightI).getParseGeoPoint(MapsFragment.KEY_LOCATION));

            Log.d(TAG, "LeftDist: " + leftDist);
            Log.d(TAG, "RightDist: " + rightDist);

            if(leftDist < rightDist){
                Log.d(TAG, "Added Left Item!!!");
                list.set(listI, left.get(leftI));
                leftI++;
            } else {
                Log.d(TAG, "Added Right Item!!!");
                list.set(listI, right.get(rightI));
                rightI++;
            }
            listI++;
        }
        ArrayList<ParseUser> rest;
        int restI;
        if (leftI >= left.size()) {
            // The left ArrayList has been used up.
            rest = right;
            restI = rightI;
        } else {
            // The right ArrayList has been used up.
            rest = left;
            restI = leftI;
        }

        // Copy the rest of whichever ArrayList (left or right) was not used up.
        for (int i=restI; i<rest.size(); i++) {
            list.set(listI, rest.get(i));
            listI++;
        }
    }

    public String printList(List<ParseUser> list){
        StringBuilder userList = new StringBuilder("[ ");
        for (ParseUser user : list) {
            userList.append(String.format("%s ", user.getUsername()));
        }
        return userList + "]";
    }
}