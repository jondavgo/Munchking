package com.example.munchking.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.munchking.R;
import com.example.munchking.adapters.FriendsAdapter;
import com.example.munchking.models.Friends;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class FriendsFragment extends Fragment {

    private RecyclerView rvFriends;
    private List<Friends> friends;
    private FriendsAdapter adapter;
    private ParseUser user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_friends, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        user = Parcels.unwrap(getArguments().getParcelable("user"));
        friends = new ArrayList<>();
        adapter = new FriendsAdapter(getContext(), friends, user);

        rvFriends = view.findViewById(R.id.rvFriends);

        rvFriends.setAdapter(adapter);
        rvFriends.setLayoutManager(new LinearLayoutManager(getContext()));

        queryFriends();
    }

    private void queryFriends() {
        ParseRelation<Friends> relation = user.getRelation(ProfileFragment.KEY_FRIEND);
        ParseQuery<Friends> query = relation.getQuery();
        query.findInBackground(new FindCallback<Friends>() {
            @Override
            public void done(List<Friends> objects, ParseException e) {
                adapter.addAll(objects);
            }
        });
    }
}