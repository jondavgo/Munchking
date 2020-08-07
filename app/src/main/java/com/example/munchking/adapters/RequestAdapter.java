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
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.munchking.R;
import com.example.munchking.fragments.ProfileFragment;
import com.example.munchking.models.FriendRequest;
import com.example.munchking.models.Friends;
import com.parse.DeleteCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ViewHolder> {

    public static final String TAG = "RequestAdapter";
    Context context;
    List<FriendRequest> requests;

    public RequestAdapter(Context context, List<FriendRequest> requests) {
        this.context = context;
        this.requests = requests;
    }

    public void addAll(List<FriendRequest> c) {
        requests.addAll(c);
        notifyDataSetChanged();
    }

    public void clear() {
        requests.clear();
        notifyDataSetChanged();
    }

    public void delete(int i) {
        requests.remove(i);
        notifyItemRemoved(i);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_friend, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FriendRequest request = requests.get(position);
        holder.bind(request, position);
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvUser;
        private Button btnRemove;
        private Button btnAccept;
        private FriendRequest request;
        private ImageView ivPhoto;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUser = itemView.findViewById(R.id.tvName);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnRemove = itemView.findViewById(R.id.btnRemove);
            ivPhoto = itemView.findViewById(R.id.ivPhoto);
        }

        public void bind(final FriendRequest request, final int position) {
            this.request = request;
            tvUser.setText(request.getSender().getUsername());
            tvUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
            ParseFile photo = request.getSender().getParseFile("profilePic");
            if (photo != null) {
                Glide.with(context).load(photo.getUrl()).transform(new RoundedCorners(30)).into(ivPhoto);
            }
            btnAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Friends friends = new Friends();
                    ParseRelation relation = friends.getFriends();
                    relation.add(ViewHolder.this.request.getSender());
                    relation.add(ViewHolder.this.request.getReceiver());
                    friends.setUser(ViewHolder.this.request.getSender());
                    try {
                        friends.save();
                        ViewHolder.this.request.delete();
                        delete(position);
                    } catch (ParseException e) {
                        Log.e(TAG, "Error adding friend!", e);
                    }
                    ParseUser.getCurrentUser().getRelation(ProfileFragment.KEY_FRIEND).add(friends);
                    ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null) {
                                Log.e(TAG, "Error adding friend!", e);
                            }
                        }
                    });
                }
            });
            btnRemove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ViewHolder.this.request.deleteInBackground(new DeleteCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e != null) {
                                Log.e(TAG, "Error removing friend request!", e);
                                Toast.makeText(context, "Error deleting friend request!", Toast.LENGTH_SHORT).show();
                            }
                            delete(position);
                        }
                    });
                }
            });
        }
    }
}