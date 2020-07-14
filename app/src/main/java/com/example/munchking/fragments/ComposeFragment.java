package com.example.munchking.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;

import com.example.munchking.R;
import com.example.munchking.models.CharPost;

/**
 * A simple {@link Fragment} subclass.
 */
public class ComposeFragment extends Fragment {

    public static final int KEY_CAMERA = 1024;
    public static final int KEY_GALLERY = 4201;

    private Button btnCam;
    private Button btnGallery;
    private Button btnPost;
    private ImageView ivPreview;
    private Spinner spinner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_compose, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnCam = view.findViewById(R.id.btnCam);
        btnGallery = view.findViewById(R.id.btnGallery);
        btnPost = view.findViewById(R.id.btnPost);
        ivPreview = view.findViewById(R.id.ivPreview);
        spinner = view.findViewById(R.id.spinner);

        btnCam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GoToCamera();
            }
        });

        btnGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GoToGallery();
            }
        });

        btnPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savePost();
            }
        });
    }

    private void savePost() {
        // TODO
        CharPost character = new CharPost();
        //character.setName();
    }

    private void GoToGallery() {
        // TODO
    }

    private void GoToCamera() {
        // TODO
    }
}