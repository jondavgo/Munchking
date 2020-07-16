package com.example.munchking.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.munchking.R;
import com.example.munchking.models.CharPost;
import com.parse.ParseFile;

import org.parceler.Parcels;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailFragment extends Fragment {

    ImageView ivPhoto;
    TextView tvName;
    TextView tvTtrpg;
    TextView tvUser;
    Button btnComments;
    CharPost charPost;

    public DetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View itemView, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(itemView, savedInstanceState);
        ivPhoto = itemView.findViewById(R.id.ivPhoto);
        tvName = itemView.findViewById(R.id.tvName);
        tvTtrpg = itemView.findViewById(R.id.tvTtrpg);
        tvUser = itemView.findViewById(R.id.tvUser);
        btnComments = itemView.findViewById(R.id.btnComments);
        charPost = Parcels.unwrap(getArguments().getParcelable("post"));

        tvName.setText(charPost.getName());
        tvTtrpg.setText(charPost.getTtrpg());
        tvUser.setText(charPost.getUser().getUsername());
        ParseFile photo = charPost.getPhoto();
        if(photo != null) {
            Glide.with(getContext()).load(photo.getUrl()).into(ivPhoto);
        }

        btnComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommentFragment fragment = new CommentFragment();
                Bundle args = new Bundle();
                args.putParcelable("post", Parcels.wrap(charPost));
                fragment.setArguments(args);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.flContainer, fragment,"comments");
                fragmentTransaction.addToBackStack("details");
                fragmentTransaction.commit();
            }
        });
    }
}