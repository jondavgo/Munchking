package com.example.munchking.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.munchking.R;
import com.example.munchking.activities.PreferencesActivity;
import com.example.munchking.adapters.CharactersAdapter;
import com.example.munchking.models.CharPost;
import com.example.munchking.models.FriendRequest;
import com.example.munchking.models.Friends;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.transition.MaterialElevationScale;
import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends HomeFragment {

    public static final String TAG = "ProfileFragment";
    public static final String KEY_FRIEND = "friendList";
    private ParseRelation<Friends> friendList;
    private int friendCount;
    private int friendPos;
    private ParseUser user;
    private ImageView ivPfp;
    private TextView tvUsername;
    private TextView tvFavs;
    private FloatingActionButton fabEdit;
    private FrameLayout flProfile;
    private TextView tvFriends;
    private boolean isFriend;
    private boolean sentRequest;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance(CharPost post) {
        ProfileFragment frag = new ProfileFragment();
        Bundle args = new Bundle();
        args.putParcelable("profile", Parcels.wrap(post.getUser()));
        args.putString("objID", post.getObjectId());
        frag.setArguments(args);
        return frag;
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
        charPosts = new ArrayList<>();
        adapter = new CharactersAdapter(charPosts, getContext());
        user = Parcels.unwrap(getArguments().getParcelable("profile"));
        friendList = user.getRelation(KEY_FRIEND);

        ivPfp = view.findViewById(R.id.ivPfp);
        tvUsername = view.findViewById(R.id.tvUsername);
        tvFavs = view.findViewById(R.id.tvFavs);
        fabEdit = view.findViewById(R.id.fabPreferences);
        rvChars = view.findViewById(R.id.rvChars);
        flProfile = view.findViewById(R.id.flProfile);
        tvFriends = view.findViewById(R.id.tvFriends);

        flProfile.setTransitionName(getArguments().getString("objID"));
        setFriendStatus();
        getFriendCount();
        checkRequest();

        rvChars.setAdapter(adapter);
        rvChars.setLayoutManager(new LinearLayoutManager(getContext()));
        tvUsername.setText(user.getUsername());
        ParseFile photo = user.getParseFile("profilePic");
        if (photo != null) {
            Glide.with(getContext()).load(photo.getUrl()).transform(new RoundedCorners(30)).into(ivPfp);
        }
        try {
            tvFavs.setText(showFavs());
        } catch (JSONException e) {
            Log.e(TAG, "Error getting favs!!!", e);
            tvFavs.setText(R.string.favorites);
        }
        tvFriends.setText(String.format(getString(R.string.friends), friendCount));
        tvFriends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FriendsFragment fragment = new FriendsFragment();
                fragment.setEnterTransition(new MaterialElevationScale(true));
                fragment.setExitTransition(new MaterialElevationScale(false));
                Bundle args = new Bundle();
                args.putParcelable("user", Parcels.wrap(user));
                fragment.setArguments(args);
                getFragmentManager().beginTransaction()
                        .replace(R.id.flContainer, fragment, "friends")
                        .addToBackStack("profile")
                        .commit();
            }
        });

        if (!user.getUsername().equals(ParseUser.getCurrentUser().getUsername())) {
            fabEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (!isFriend) {
                        sendFriendRequest();
                        Log.d(TAG, "Sending Friend Request");
                    } else {
                        removeFriend();
                        Log.d(TAG, "Removing Friend");
                    }
                    fabEdit.setImageResource(checkFriends());
                }
            });
        } else {
            fabEdit.setImageResource(R.drawable.ic_baseline_build_24);
            fabEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    toPreferences();
                }
            });
        }
        query(0);
    }

    private void checkRequest() {
        ParseQuery<FriendRequest> query = ParseQuery.getQuery(FriendRequest.class);
        query.whereEqualTo(FriendRequest.KEY_FROM, ParseUser.getCurrentUser());
        query.whereEqualTo(FriendRequest.KEY_TO, user);
        query.countInBackground(new CountCallback() {
            @Override
            public void done(int count, ParseException e) {
                if(e == null && count != 0){
                    sentRequest = true;
                }
            }
        });
    }

    private void setFriendStatus() {
        isFriend = false;
        ParseRelation<Friends> relation = ParseUser.getCurrentUser().getRelation(KEY_FRIEND);
        ParseQuery<Friends> query = relation.getQuery();
        query.findInBackground(new FindCallback<Friends>() {
            @Override
            public void done(List<Friends> objects, ParseException e) {
                for (int i = 0; i < objects.size(); i++) {
                    objects.get(i).getFriends().getQuery().whereEqualTo("username", user.getUsername());
                    objects.get(i).getFriends().getQuery().findInBackground(new FindCallback<ParseUser>() {
                        @Override
                        public void done(List<ParseUser> objects, ParseException e) {
                            for (ParseUser person : objects) {
                                if (person.getUsername().equals(user.getUsername())) {
                                    isFriend = true;
                                    Log.i(TAG, "Found a friend!");
                                    fabEdit.setImageResource(checkFriends());
                                }
                            }
                        }
                    });
                }
            }
        });
    }

    private void getFriendCount() {
        ParseQuery<Friends> query = friendList.getQuery();
        try {
            friendCount = query.count();
        } catch (ParseException e) {
            Log.e(TAG, "Error getting friend count", e);
            friendCount = 0;
        }
    }

    private void removeFriend(){
        ParseQuery<Friends> query = friendList.getQuery();
        query.findInBackground(new FindCallback<Friends>() {
            @Override
            public void done(List<Friends> objects, ParseException e) {
                for (int i = 0; i < objects.size(); i++) {
                    objects.get(i).getFriends().getQuery().whereEqualTo("username", user.getUsername());
                    objects.get(i).getFriends().getQuery().findInBackground();
                    if (e != null) {
                        Log.e(TAG, "Error removing friend!", e);
                        Toast.makeText(getContext(), "Error removing friend from friends list!", Toast.LENGTH_SHORT).show();
                    }
                    Log.i(TAG, "Removed friend successfully!");
                }
            }
        });
    }

    private void sendFriendRequest() {
        if(!sentRequest) {
            FriendRequest request = new FriendRequest();
            request.setSender(ParseUser.getCurrentUser());
            request.setReceiver(user);
            request.setStatus("pending");
            request.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        Log.e(TAG, "Error sending friend request!", e);
                        Toast.makeText(getContext(), R.string.request_error, Toast.LENGTH_SHORT);
                        return;
                    }
                    Log.i(TAG, "Request sent to " + user.getUsername());
                    Toast.makeText(getContext(), "Request sent to " + user.getUsername() + "!", Toast.LENGTH_SHORT);
                }
            });
            sentRequest = true;
        } else {
            Toast.makeText(getContext(), "You already sent " + user.getUsername() + " a friend request!", Toast.LENGTH_SHORT).show();
        }
    }

    private int checkFriends() {
        if (isFriend || sentRequest) {
            return R.drawable.ic_baseline_group_add_24;
        }
        return R.drawable.ic_outline_group_add_24;
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
    protected void query(int i) {
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