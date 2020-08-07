package com.example.munchking.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.munchking.R;
import com.example.munchking.activities.MainActivity;
import com.example.munchking.fragments.ProfileFragment;
import com.example.munchking.models.Friends;
import com.parse.DeleteCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.List;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {

    public static final String TAG = "FriendsAdapter";
    Context context;
    List<Friends> friends;
    ParseUser profileUser;

    public FriendsAdapter(Context context, List<Friends> friends, ParseUser profileUser) {
        this.context = context;
        this.friends = friends;
        this.profileUser = profileUser;
    }

    public void addAll(List<Friends> c) {
        friends.addAll(c);
        notifyDataSetChanged();
    }

    public void clear() {
        friends.clear();
        notifyDataSetChanged();
    }

    private void delete(int pos) {
        friends.remove(pos);
        notifyItemRemoved(pos);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_friend, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Friends friend = friends.get(position);
        holder.bind(friend, position);
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvUser;
        private Button btnRemove;
        private Button btnAccept;
        private ParseUser user;
        private ImageView ivPhoto;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUser = itemView.findViewById(R.id.tvName);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnRemove = itemView.findViewById(R.id.btnRemove);
            ivPhoto = itemView.findViewById(R.id.ivPhoto);
            itemView.setOnClickListener(this);
        }

        public void bind(final Friends friend, final int pos) {
            ParseRelation relation = friend.getFriends();
            btnAccept.setVisibility(View.INVISIBLE);
            user = null;
            if (!profileUser.getUsername().equals(ParseUser.getCurrentUser().getUsername())) {
                btnRemove.setVisibility(View.INVISIBLE);
            }
            ParseQuery<ParseUser> query = relation.getQuery();
            try {
                List<ParseUser> users = query.find();
                for (ParseUser obj : users) {
                    if (!obj.getUsername().equals(profileUser.getUsername())) {
                        user = obj;
                    }
                }
            } catch (ParseException e) {
                Log.e(TAG, "Error getting user");
            }
            if (user != null) {
                tvUser.setText(user.getUsername());
                btnRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        friend.deleteInBackground(new DeleteCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e != null) {
                                    Log.e(TAG, "Error removing friend!", e);
                                    Toast.makeText(context, "Error removing friend from friends list!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        delete(pos);
                    }
                });
                ParseFile photo = user.getParseFile("profilePic");
                if (photo != null) {
                    Glide.with(context).load(photo.getUrl()).transform(new RoundedCorners(30)).into(ivPhoto);
                }
            }
        }

        @Override
        public void onClick(View view) {
            if (user != null) {
                ProfileFragment fragment = ProfileFragment.newInstance(user);
                FragmentManager fragmentManager = ((MainActivity) context).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.flContainer, fragment, "profile");
                fragmentTransaction.addToBackStack("home");
                fragmentTransaction.commit();
            }
        }
    }
}
