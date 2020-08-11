package com.example.munchking.fragments;

import android.os.Bundle;
import android.transition.ChangeBounds;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.munchking.EndlessRecyclerViewScrollListener;
import com.example.munchking.R;
import com.example.munchking.activities.PreferencesActivity;
import com.example.munchking.adapters.CharactersAdapter;
import com.example.munchking.models.CharPost;
import com.example.munchking.models.Friends;
import com.google.android.material.transition.MaterialElevationScale;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    public static final String TAG = "HomeFragment";
    public static final int CHAR_MAX = 10;
    private Fragment map;
    private FragmentManager fragMan;
    private int selectorPos;
    protected JSONArray array;
    protected CharactersAdapter adapter;
    protected List<CharPost> charPosts;
    private boolean filterFriends;
    private ParseQuery<CharPost> query;

    private ImageView ivSelect;
    private TextView tvSelDate;
    private TextView tvSelDist;
    private TextView tvSelMap;
    private TextView tvSelRating;
    private ConstraintLayout clConstraints;
    private ProgressBar pbLoading;
    private SwipeRefreshLayout swipeContainer;
    private EndlessRecyclerViewScrollListener scrollListener;
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
        array = ParseUser.getCurrentUser().getJSONArray("favGames");
        fragMan = getFragmentManager();
        map = new MapsFragment();
        filterFriends = ParseUser.getCurrentUser().getBoolean("filterFriends");

        rvChars = view.findViewById(R.id.rvChars);
        ivSelect = view.findViewById(R.id.ivSelect);
        tvSelDate = view.findViewById(R.id.tvSelDate);
        tvSelDist = view.findViewById(R.id.tvSelDist);
        tvSelMap = view.findViewById(R.id.tvSelMap);
        tvSelRating = view.findViewById(R.id.tvSelRating);
        clConstraints = view.findViewById(R.id.clConstraints);
        pbLoading = view.findViewById(R.id.pbLoading);
        swipeContainer = view.findViewById(R.id.swipeContainer);

        query = ParseQuery.getQuery(CharPost.class);
        query.include(CharPost.KEY_USER);
        query.whereNotEqualTo(CharPost.KEY_USER, ParseUser.getCurrentUser());
        if (!filterFriends) {
            try {
                query.whereContainedIn(CharPost.KEY_TTRPG, PreferencesActivity.fromJSONArray(array));
            } catch (JSONException e) {
                Log.e(TAG, "JSON Exception during home query!", e);
            }
        } else {
            ParseRelation<Friends> friends = ParseUser.getCurrentUser().getRelation(ProfileFragment.KEY_FRIEND);
            ParseQuery<Friends> friendsQuery = friends.getQuery();
            try {
                List<ParseUser> userList = new ArrayList<>();
                List<Friends> friendsList = friendsQuery.find();
                for (Friends friend : friendsList) {
                    ParseQuery<ParseUser> userQuery = friend.getFriends().getQuery();
                    List<ParseUser> users = userQuery.find();
                    for (ParseUser obj : users) {
                        if (!obj.getUsername().equals(ParseUser.getCurrentUser().getUsername())) {
                            userList.add(obj);
                        }
                    }
                }
                query.whereContainedIn(CharPost.KEY_USER, userList);
            } catch (ParseException e) {
                Log.e(TAG, "Query error!", e);
            }
        }

        rvChars.setAdapter(adapter);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        rvChars.setLayoutManager(manager);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchTimelineAsync();
                rvChars.scrollToPosition(0);
            }
        });
        // Configure the refreshing colors
        swipeContainer.setColorSchemeResources(R.color.colorPrimaryDark,
                R.color.colorAccent,
                R.color.colorPurpAccent,
                R.color.colorPrimary);

        tvSelDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectorPos = 0;
                checkPosition();
            }
        });

        tvSelRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectorPos = 1;
                checkPosition();
            }
        });

        tvSelDist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectorPos = 2;
                checkPosition();
            }
        });

        tvSelMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectorPos = 3;
                checkPosition();
            }
        });

        scrollListener = new EndlessRecyclerViewScrollListener(manager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to the bottom of the list
                loadNextDataFromApi(page);
            }
        };
        rvChars.addOnScrollListener(scrollListener);

        checkPosition();
        setReenterTransition(new MaterialElevationScale(true));
    }

    private void loadNextDataFromApi(int page) {
        if(selectorPos == 0) {
            query(page);
        }
    }

    private void fetchTimelineAsync() {
        checkPosition();
        swipeContainer.setRefreshing(false);
    }

    private void toPosition(TextView textView) {
        ConstraintSet set = new ConstraintSet();
        Transition transition = new ChangeBounds();
        transition.setInterpolator(new AnticipateOvershootInterpolator(1.0f));
        transition.setDuration(750);
        set.clone(clConstraints);
        set.connect(ivSelect.getId(), ConstraintSet.START, textView.getId(), ConstraintSet.START, 0);
        set.connect(ivSelect.getId(), ConstraintSet.END, textView.getId(), ConstraintSet.END, 0);
        TransitionManager.beginDelayedTransition(clConstraints, transition);
        set.applyTo(clConstraints);
        resetColor();
        textView.setTextColor(getResources().getColor(R.color.black));
    }

    private void resetColor() {
        tvSelDate.setTextColor(getResources().getColor(R.color.colorAccent));
        tvSelDist.setTextColor(getResources().getColor(R.color.colorAccent));
        tvSelMap.setTextColor(getResources().getColor(R.color.colorAccent));
        tvSelRating.setTextColor(getResources().getColor(R.color.colorAccent));
    }

    private void checkPosition() {
        pbLoading.setVisibility(View.VISIBLE);
        adapter.clear();
        scrollListener.resetState();
        switch(selectorPos){
            case 0:
                dismissMapFragment();
                query(0);
                toPosition(tvSelDate);
                break;
            case 1:
                dismissMapFragment();
                queryByRating();
                toPosition(tvSelRating);
                break;
            case 2:
                dismissMapFragment();
                queryByDistance();
                toPosition(tvSelDist);
                break;
            case 3:
                loadMapFragment();
                toPosition(tvSelMap);
                break;

        }
    }

    private void dismissMapFragment() {
        fragMan.beginTransaction().remove(map).commit();
        rvChars.setVisibility(View.VISIBLE);
        swipeContainer.setVisibility(View.VISIBLE);
    }

    private void loadMapFragment() {
        rvChars.setVisibility(View.INVISIBLE);
        swipeContainer.setVisibility(View.INVISIBLE);
        Log.d(TAG, "Map Fragment Loading...");
        fragMan.beginTransaction().replace(R.id.flMap, map,"map").commit();
        pbLoading.setVisibility(View.INVISIBLE);
    }

    protected void query(int i) {
        adapter.setSort(0);
        query.orderByDescending(CharPost.KEY_DATE);
        query.setLimit(CHAR_MAX);
        query.setSkip(i * CHAR_MAX);
        query.findInBackground(new FindCallback<CharPost>() {
            @Override
            public void done(List<CharPost> objects, ParseException e) {
                if (e == null) {
                    adapter.addAll(objects);
                    pbLoading.setVisibility(View.INVISIBLE);
                } else {
                    Log.e(TAG, "Query error!", e);
                    Toast.makeText(getContext(), "Something went wrong while grabbing posts!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        query.setSkip(0);
        query.setLimit(-1);
    }

    private void queryByDistance(){
        adapter.setSort(1);
        query.findInBackground(new FindCallback<CharPost>() {
            @Override
            public void done(List<CharPost> objects, ParseException e) {
                if(e == null){
                    List<CharPost> sorted = mergeSort(objects, ParseUser.getCurrentUser().getParseGeoPoint(MapsFragment.KEY_LOCATION), false);
                    adapter.addAll(sorted);
                    pbLoading.setVisibility(View.INVISIBLE);
                } else {
                    Log.e(TAG, "Query error!", e);
                    Toast.makeText(getContext(), "Something went wrong while grabbing posts!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void queryByRating(){
        adapter.setSort(2);
        query.findInBackground(new FindCallback<CharPost>() {
            @Override
            public void done(List<CharPost> objects, ParseException e) {
                if(e == null){
                    List<CharPost> sorted = mergeSort(objects, ParseUser.getCurrentUser().getParseGeoPoint(MapsFragment.KEY_LOCATION), true);
                    adapter.addAll(sorted);
                    pbLoading.setVisibility(View.INVISIBLE);
                } else {
                    Log.e(TAG, "Query error!", e);
                    Toast.makeText(getContext(), "Something went wrong while grabbing posts!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private List<CharPost> mergeSort(List<CharPost> list, ParseGeoPoint parseGeoPoint, boolean rating) {
        if (list.size() <= 1) {
            return list;
        }
        int mid = list.size()/2;
        ArrayList<CharPost> left = new ArrayList<>();
        ArrayList<CharPost> right = new ArrayList<>();

        for (int i = 0; i < mid; i++) {
            left.add(list.get(i));
        }
        for (int i = mid; i < list.size(); i++) {
            right.add(list.get(i));
        }

        mergeSort(left, parseGeoPoint, rating);
        mergeSort(right, parseGeoPoint, rating);
        merge(left, right, list, parseGeoPoint, rating);
        return list;
    }

    private void merge(ArrayList<CharPost> left, ArrayList<CharPost> right, List<CharPost> list, ParseGeoPoint parseGeoPoint, boolean rating) {
        int leftI = 0;
        int rightI = 0;
        int listI = 0;

        while (leftI < left.size() && rightI < right.size()){
            double leftDist;
            double rightDist;
            if(!rating) {
                leftDist = parseGeoPoint.distanceInMilesTo(left.get(leftI).getUser().getParseGeoPoint(MapsFragment.KEY_LOCATION));
                rightDist = parseGeoPoint.distanceInMilesTo(right.get(rightI).getUser().getParseGeoPoint(MapsFragment.KEY_LOCATION));
            } else {
                leftDist = getScore(left.get(leftI)) * -1;
                rightDist = getScore(right.get(rightI)) * -1;
            }
            if(leftDist < rightDist){
                list.set(listI, left.get(leftI));
                leftI++;
            } else if(leftDist == rightDist){
                // Date is time breaker
                if(left.get(leftI).getCreatedAt().after(right.get(rightI).getCreatedAt())){
                    list.set(listI, left.get(leftI));
                    leftI++;
                } else {
                    list.set(listI, right.get(rightI));
                    rightI++;
                }
            } else {
                list.set(listI, right.get(rightI));
                rightI++;
            }
            listI++;
        }
        ArrayList<CharPost> rest;
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

    private double getScore(CharPost charPost) {
        double score = 0;
        JSONArray arr = charPost.getRatings();
        if(arr.length() != 0) {
            score = ((double) charPost.getRatingScore()) / ((double)arr.length());
        }
        return score;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(selectorPos == 2 && fragMan != null) {
            dismissMapFragment();
        }
        selectorPos = 0;
    }
}