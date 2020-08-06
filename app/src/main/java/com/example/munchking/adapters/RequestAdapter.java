package com.example.munchking.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.munchking.R;
import com.example.munchking.models.Comment;
import com.example.munchking.models.FriendRequest;

import java.util.List;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ViewHolder> {

    Context context;
    List<FriendRequest> requests;

    public RequestAdapter(Context context, List<FriendRequest> requests) {
        this.context = context;
        this.requests = requests;
    }

    public void addAll(List<FriendRequest> c){
        requests.addAll(c);
        notifyDataSetChanged();
    }

    public void clear(){
        requests.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_comment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FriendRequest request = requests.get(position);
        holder.bind(request);
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvUser;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUser = itemView.findViewById(R.id.tvName);
        }

        public void bind(FriendRequest request) {
            tvUser.setText(request.getSender().getUsername());
        }
    }
}