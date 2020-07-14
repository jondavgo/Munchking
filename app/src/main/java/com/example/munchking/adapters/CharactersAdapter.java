package com.example.munchking.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.munchking.R;
import com.example.munchking.models.CharPost;
import com.parse.ParseFile;

import java.util.List;

public class CharactersAdapter extends RecyclerView.Adapter<CharactersAdapter.ViewHolder> {

    List<CharPost> charPosts;
    Context context;

    public CharactersAdapter(List<CharPost> charPosts, Context context) {
        this.charPosts = charPosts;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.item_character, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        CharPost charPost = charPosts.get(position);
        holder.bind(charPost);
    }

    @Override
    public int getItemCount() {
        return charPosts.size();
    }

    public void addAll(List<CharPost> c){
        charPosts.addAll(c);
        notifyDataSetChanged();
    }

    public void clear(){
        charPosts.clear();
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView ivPhoto;
        TextView tvName;
        TextView tvTtrpg;
        TextView tvUser;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPhoto = itemView.findViewById(R.id.ivPhoto);
            tvName = itemView.findViewById(R.id.tvName);
            tvTtrpg = itemView.findViewById(R.id.tvTtrpg);
            tvUser = itemView.findViewById(R.id.tvUser);
        }

        public void bind(CharPost charPost) {
            tvName.setText(charPost.getName());
            tvTtrpg.setText(charPost.getTtrpg());
            tvUser.setText(charPost.getUser().getUsername());
            ParseFile photo = charPost.getPhoto();
            if(photo != null) {
                Glide.with(context).load(photo.getUrl()).into(ivPhoto);
            }
        }
    }
}
