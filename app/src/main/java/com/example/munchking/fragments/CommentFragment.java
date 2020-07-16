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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.munchking.R;
import com.example.munchking.adapters.CommentsAdapter;
import com.example.munchking.models.CharPost;
import com.example.munchking.models.Comment;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class CommentFragment extends Fragment {

    CommentsAdapter adapter;
    List<Comment> comments;
    CharPost post;
    RecyclerView rvComments;
    Button btnCPost;
    EditText etComment;
    TextView tvCharName;

    public CommentFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_comment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        comments = new ArrayList<>();
        adapter = new CommentsAdapter(getContext(), comments);
        post = Parcels.unwrap(getArguments().getParcelable("post"));

        rvComments = view.findViewById(R.id.rvComments);
        btnCPost = view.findViewById(R.id.btnCPost);
        etComment = view.findViewById(R.id.etComment);
        tvCharName = view.findViewById(R.id.tvCharName);

        rvComments.setAdapter(adapter);
        rvComments.setLayoutManager(new LinearLayoutManager(getContext()));

        tvCharName.setText(String.format("Comments about %s", post.getName()));

        btnCPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Comment comment = new Comment();
                comment.setPost(post);
                comment.setUser(ParseUser.getCurrentUser());
                comment.setText(etComment.getText().toString());
                comment.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e != null){
                            Log.e("CommentFragment", "Error while commenting", e);
                            Toast.makeText(getContext(), "Error while commenting!", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        etComment.setText("");
                        comments.add(0,comment);
                        adapter.notifyItemInserted(0);
                    }
                });
            }
        });
        queryComments();
    }

    private void queryComments() {
        ParseQuery<Comment> query = ParseQuery.getQuery(Comment.class);
        query.include(Comment.KEY_USER);
        query.whereEqualTo(Comment.KEY_POST, post);
        query.findInBackground(new FindCallback<Comment>() {
            @Override
            public void done(List<Comment> objects, ParseException e) {
                if(e == null){
                    adapter.addAll(objects);
                } else {
                    Log.e("CommentFragment", "Query error!", e);
                    Toast.makeText(getContext(), "Something went wrong while grabbing comments!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}