package com.example.munchking.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.munchking.R;
import com.example.munchking.models.Comment;
import com.example.munchking.models.Friends;
import com.parse.DeleteCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {

    public static final String TAG = "FriendsAdapter";
    Context context;
    List<Friends> friends;

    public FriendsAdapter(Context context, List<Friends> friends) {
        this.context = context;
        this.friends = friends;
    }

    public void addAll(List<Friends> c){
        friends.addAll(c);
        notifyDataSetChanged();
    }

    public void clear(){
        friends.clear();
        notifyDataSetChanged();
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
        holder.bind(friend);
    }

    @Override
    public int getItemCount() {
        return friends.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvUser;
        private Button btnRemove;
        private Button btnAccept;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUser = itemView.findViewById(R.id.tvName);
            btnAccept = itemView.findViewById(R.id.btnAccept);
            btnRemove = itemView.findViewById(R.id.btnRemove);
        }

        public void bind(final Friends friend) {
            JSONArray array = friend.getFriends();
            btnAccept.setVisibility(View.INVISIBLE);
            ParseUser user = null;
            for (int i = 0; i < array.length(); i++) {
                try {
                    if(array.get(i).equals(ParseUser.getCurrentUser())){
                        user = (ParseUser) array.get(i);
                    }
                } catch (JSONException e) {
                    Log.e("FriendsAdapter", "Exception getting Friends", e);
                }
            }
            if(user != null) {
                tvUser.setText(user.getUsername());
                btnRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        friend.deleteInBackground(new DeleteCallback() {
                            @Override
                            public void done(ParseException e) {
                                Log.e(TAG, "Error removing friend!", e);
                                Toast.makeText(context, "Error removing friend from friends list!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }
        }
    }
}
