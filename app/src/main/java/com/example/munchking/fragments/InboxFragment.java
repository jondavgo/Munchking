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
import com.example.munchking.adapters.RequestAdapter;
import com.example.munchking.models.CharPost;
import com.example.munchking.models.FriendRequest;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class InboxFragment extends Fragment {

    private RecyclerView rvInbox;
    private RequestAdapter adapter;
    private List<FriendRequest> requests;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_inbox, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        requests = new ArrayList<>();
        adapter = new RequestAdapter(getContext(), requests);

        rvInbox = view.findViewById(R.id.rvInbox);

        rvInbox.setLayoutManager(new LinearLayoutManager(getContext()));
        rvInbox.setAdapter(adapter);

        queryInbox();
    }

    private void queryInbox() {
        ParseQuery<FriendRequest> query = ParseQuery.getQuery(FriendRequest.class);
        query.whereEqualTo(FriendRequest.KEY_TO, ParseUser.getCurrentUser());
        query.include(FriendRequest.KEY_FROM);
        query.orderByDescending(CharPost.KEY_DATE);
        query.findInBackground(new FindCallback<FriendRequest>() {
            @Override
            public void done(List<FriendRequest> objects, ParseException e) {
                adapter.addAll(objects);
            }
        });
    }
}