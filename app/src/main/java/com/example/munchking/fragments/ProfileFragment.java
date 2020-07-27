package com.example.munchking.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.munchking.R;
import com.example.munchking.activities.PreferencesActivity;
import com.example.munchking.models.CharPost;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends HomeFragment {

    public static final String TAG = "ProfileFragment";
    ParseUser user;
    ImageView ivPfp;
    TextView tvUsername;
    TextView tvFavs;
    FloatingActionButton fabEdit;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(ParseUser user) {
        ProfileFragment frag = new ProfileFragment();
        Bundle args = new Bundle();
        args.putParcelable("profile", Parcels.wrap(user));
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        user = Parcels.unwrap(getArguments().getParcelable("profile"));
        adapter.clear();
        query();

        ivPfp = view.findViewById(R.id.ivPfp);
        tvUsername = view.findViewById(R.id.tvUsername);
        tvFavs = view.findViewById(R.id.tvFavs);
        fabEdit = view.findViewById(R.id.fabPreferences);

        tvUsername.setText(user.getUsername());
        ParseFile photo = user.getParseFile("profilePic");
        if(photo != null) {
            Glide.with(getContext()).load(photo.getUrl()).into(ivPfp);
        }
        try {
            tvFavs.setText(showFavs());
        } catch (JSONException e) {
            Log.e(TAG, "Error getting favs!!!", e);
            tvFavs.setText(R.string.favorites);
        }

        if(!user.getUsername().equals(ParseUser.getCurrentUser().getUsername())){
            fabEdit.setVisibility(View.GONE);
        }

        fabEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toPreferences();
            }
        });
    }

    private void toPreferences() {
        Intent intent = new Intent(getContext(), PreferencesActivity.class);
        intent.putExtra("user", Parcels.wrap(user));
        getContext().startActivity(intent);
    }

    private String showFavs() throws JSONException {
        StringBuilder favs = new StringBuilder("Favorites: ");
        JSONArray array = user.getJSONArray(PreferencesActivity.KEY_PREFERENCES);
        for (int i = 0; i < array.length(); i++) {
            favs.append(array.getString(i));
            if(i != array.length() - 1){
                favs.append(", ");
            }
        }
        return favs.toString();
    }

    @Override
    protected void query() {
        ParseQuery<CharPost> query = ParseQuery.getQuery(CharPost.class);
        query.include(CharPost.KEY_USER);
        query.orderByDescending(CharPost.KEY_DATE);
        query.whereEqualTo(CharPost.KEY_USER, user);
        query.findInBackground(new FindCallback<CharPost>() {
            @Override
            public void done(List<CharPost> objects, ParseException e) {
                if(e == null){
                    Log.i(TAG, "Profile viewed: " + user.getUsername());
                    adapter.addAll(objects);
                } else {
                    Log.e(TAG, "Query error!", e);
                    Toast.makeText(getContext(), "Something went wrong while grabbing posts!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}