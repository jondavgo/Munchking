package com.example.munchking.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.transition.Slide;

import android.transition.ChangeBounds;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.transition.Visibility;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.munchking.R;
import com.example.munchking.activities.PreferencesActivity;
import com.example.munchking.adapters.CharactersAdapter;
import com.example.munchking.models.CharPost;
import com.google.android.material.transition.MaterialElevationScale;
import com.google.android.material.transition.MaterialSharedAxis;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
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
    private Fragment map;
    private FragmentManager fragMan;
    private int selectorPos;
    protected JSONArray array;
    protected CharactersAdapter adapter;
    protected List<CharPost> charPosts;
    private ImageView ivSelect;
    private TextView tvSelDate;
    private TextView tvSelDist;
    private TextView tvSelMap;
    private ConstraintLayout clConstraints;
    private ProgressBar pbLoading;
    private SwipeRefreshLayout swipeContainer;

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

        rvChars = view.findViewById(R.id.rvChars);
        ivSelect = view.findViewById(R.id.ivSelect);
        tvSelDate = view.findViewById(R.id.tvSelDate);
        tvSelDist = view.findViewById(R.id.tvSelDist);
        tvSelMap = view.findViewById(R.id.tvSelMap);
        clConstraints = view.findViewById(R.id.clConstraints);
        pbLoading = view.findViewById(R.id.pbLoading);
        swipeContainer = view.findViewById(R.id.swipeContainer);

        rvChars.setAdapter(adapter);
        rvChars.setLayoutManager(new LinearLayoutManager(getContext()));

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code to refresh the list here.
                // Make sure you call swipeContainer.setRefreshing(false)
                // once the network request has completed successfully.
                fetchTimelineAsync(0);
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

        tvSelDist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectorPos = 1;
                checkPosition();
            }
        });

        tvSelMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectorPos = 2;
                checkPosition();
            }
        });

        checkPosition();
        setReenterTransition(new MaterialElevationScale(true));
    }

    private void fetchTimelineAsync(int i) {
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
    }

    private void checkPosition() {
        pbLoading.setVisibility(View.VISIBLE);
        adapter.clear();
        switch(selectorPos){
            case 0:
                dismissMapFragment();
                query();
                toPosition(tvSelDate);
                break;
            case 1:
                dismissMapFragment();
                queryByDistance();
                toPosition(tvSelDist);
                break;
            case 2:
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

    protected void query() {
        adapter.setDistanceSort(false);
        ParseQuery<CharPost> query = ParseQuery.getQuery(CharPost.class);
        query.include(CharPost.KEY_USER);
        query.whereNotEqualTo(CharPost.KEY_USER, ParseUser.getCurrentUser());
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
                    pbLoading.setVisibility(View.INVISIBLE);
                } else {
                    Log.e(TAG, "Query error!", e);
                    Toast.makeText(getContext(), "Something went wrong while grabbing posts!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void queryByDistance(){
        adapter.setDistanceSort(true);
        ParseQuery<CharPost> query = ParseQuery.getQuery(CharPost.class);
        query.include(CharPost.KEY_USER);
        query.whereNotEqualTo(CharPost.KEY_USER, ParseUser.getCurrentUser());
        try {
            query.whereContainedIn(CharPost.KEY_TTRPG, PreferencesActivity.fromJSONArray(array));
        } catch (JSONException e) {
            Log.e(TAG, "JSON Exception during home query!", e);
        }
        query.findInBackground(new FindCallback<CharPost>() {
            @Override
            public void done(List<CharPost> objects, ParseException e) {
                if(e == null){
                    List<CharPost> sorted = mergeSort(objects, ParseUser.getCurrentUser().getParseGeoPoint(MapsFragment.KEY_LOCATION));
                    adapter.addAll(sorted);
                    pbLoading.setVisibility(View.INVISIBLE);
                } else {
                    Log.e(TAG, "Query error!", e);
                    Toast.makeText(getContext(), "Something went wrong while grabbing posts!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private List<CharPost> mergeSort(List<CharPost> list, ParseGeoPoint parseGeoPoint) {
        if(list.size() == 1){
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

        mergeSort(left, parseGeoPoint);
        mergeSort(right, parseGeoPoint);
        merge(left, right, list, parseGeoPoint);
        return list;
    }

    private void merge(ArrayList<CharPost> left, ArrayList<CharPost> right, List<CharPost> list, ParseGeoPoint parseGeoPoint) {
        int leftI = 0;
        int rightI = 0;
        int listI = 0;

        while (leftI < left.size() && rightI < right.size()){
            double leftDist = parseGeoPoint.distanceInMilesTo(left.get(leftI).getUser().getParseGeoPoint(MapsFragment.KEY_LOCATION));
            double rightDist = parseGeoPoint.distanceInMilesTo(right.get(rightI).getUser().getParseGeoPoint(MapsFragment.KEY_LOCATION));

            if(leftDist < rightDist){
                list.set(listI, left.get(leftI));
                leftI++;
            } else if(leftDist == rightDist){
                // Date is time breaker
                if(left.get(leftI).getCreatedAt().before(right.get(rightI).getCreatedAt())){
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(selectorPos == 2 && fragMan != null) {
            dismissMapFragment();
        }
    }
}