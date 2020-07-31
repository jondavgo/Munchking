package com.example.munchking.adapters;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.munchking.R;
import com.example.munchking.activities.MainActivity;
import com.example.munchking.fragments.DetailFragment;
import com.example.munchking.fragments.MapsFragment;
import com.example.munchking.fragments.ProfileFragment;
import com.example.munchking.models.CharPost;
import com.google.android.material.transition.MaterialContainerTransform;
import com.google.android.material.transition.MaterialElevationScale;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CharactersAdapter extends RecyclerView.Adapter<CharactersAdapter.ViewHolder> {

    List<CharPost> charPosts;
    Context context;
    boolean distanceSort;

    public CharactersAdapter(List<CharPost> charPosts, Context context) {
        this.charPosts = charPosts;
        this.context = context;
    }

    public void setDistanceSort(boolean distanceSort) {
        this.distanceSort = distanceSort;
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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView ivPhoto;
        TextView tvName;
        TextView tvTtrpg;
        TextView tvUser;
        TextView tvDate;
        CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPhoto = itemView.findViewById(R.id.ivPhoto);
            tvName = itemView.findViewById(R.id.tvName);
            tvTtrpg = itemView.findViewById(R.id.tvTtrpg);
            tvUser = itemView.findViewById(R.id.tvUser);
            tvDate = itemView.findViewById(R.id.tvDate);
            cardView = itemView.findViewById(R.id.cardView);
            itemView.setOnClickListener(this);
        }

        public void bind(final CharPost charPost) {
            // TODO replace date with distance when sorted by distance
            tvName.setText(charPost.getName());
            tvTtrpg.setText(charPost.getTtrpg());
            tvUser.setText(String.format("By: %s", charPost.getUser().getUsername()));
            if(!distanceSort) {
                Date date = charPost.getCreatedAt();
                String pattern = "'Created' dd/MM/yyyy 'at' hh:mm a";
                SimpleDateFormat format = new SimpleDateFormat(pattern);
                tvDate.setText(format.format(date));
            } else{
                double distance = ParseUser.getCurrentUser().getParseGeoPoint(MapsFragment.KEY_LOCATION)
                        .distanceInMilesTo(charPost.getUser().getParseGeoPoint(MapsFragment.KEY_LOCATION));
                String pattern = "###,###,###.## 'miles away'";
                DecimalFormat format = new DecimalFormat(pattern);
                tvDate.setText(format.format(distance));
            }
            ParseFile photo = charPost.getPhoto();
            if(photo != null) {
                Glide.with(context).load(photo.getUrl()).transform(new RoundedCorners(30)).into(ivPhoto);
            }
            cardView.setTransitionName(charPost.getObjectId());

            tvUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ProfileFragment fragment = ProfileFragment.newInstance(charPost);
                    fragment.setSharedElementEnterTransition(new MaterialContainerTransform());
                    FragmentManager fragmentManager = ((MainActivity)context).getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.addSharedElement(cardView, cardView.getTransitionName());
                    fragmentTransaction.replace(R.id.flContainer, fragment,"profile");
                    fragmentTransaction.addToBackStack("home");
                    fragmentTransaction.commit();
                }
            });
        }

        @Override
        public void onClick(View view) {
            int pos = getAdapterPosition();
            CharPost post = charPosts.get(pos);
            DetailFragment fragment = new DetailFragment();
            fragment.setSharedElementEnterTransition(new MaterialContainerTransform());
            fragment.setExitTransition(new MaterialElevationScale(false));

            Bundle args = new Bundle();
            args.putParcelable("post", Parcels.wrap(post));
            fragment.setArguments(args);

            FragmentManager fragmentManager = ((MainActivity)context).getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.addSharedElement(cardView, cardView.getTransitionName());
            fragmentTransaction.replace(R.id.flContainer, fragment,"details");
            fragmentTransaction.addToBackStack("home");
            fragmentTransaction.commit();
        }
    }
}
