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
import android.widget.Toast;

import com.example.munchking.R;
import com.example.munchking.activities.PreferencesActivity;
import com.example.munchking.adapters.CharactersAdapter;
import com.example.munchking.models.CharPost;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    public static final String TAG = "HomeFragment";
    protected CharactersAdapter adapter;
    protected List<CharPost> charPosts;

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

        rvChars.setAdapter(adapter);
        rvChars.setLayoutManager(new LinearLayoutManager(getContext()));

        query();
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
                    Log.e("HomeFragment", "Query error!", e);
                    Toast.makeText(getContext(), "Something went wrong while grabbing posts!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}