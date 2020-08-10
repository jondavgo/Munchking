package com.example.munchking.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.Fade;
import androidx.transition.Slide;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.munchking.R;
import com.example.munchking.adapters.TraitEquipAdapter;
import com.example.munchking.dialogs.AddItemDialog;
import com.example.munchking.models.CharPost;
import com.google.android.material.transition.MaterialElevationScale;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.parceler.Parcels;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailFragment extends Fragment implements AddItemDialog.EditDialogListener {

    private final String TAG = "DetailFragment";
    private CharPost charPost;
    private List<Pair<String, String>> traits;
    private List<Pair<String, String>> equipment;
    private TraitEquipAdapter traitAdapter;
    private TraitEquipAdapter equipAdapter;
    private FragmentManager fragmentManager;
    private boolean isAuthor;
    private int ratingPos;

    private ImageView ivPhoto;
    private TextView tvName;
    private TextView tvTtrpg;
    private TextView tvUser;
    private Button btnComments;
    private Button btnTrait;
    private Button btnEquip;
    private Button btnDetail;
    private LinearLayout llDesc;
    private TextView tvDescription;
    private LinearLayout llTrait;
    private RecyclerView rvTraits;
    private LinearLayout llEquip;
    private RecyclerView rvEquipment;
    private TextView tvRace;
    private TextView tvClass;
    private RatingBar rbRatings;
    private TextView tvRatings;

    public DetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
        llDesc = itemView.findViewById(R.id.llDesc);
        tvDescription = itemView.findViewById(R.id.tvDesc);
        llTrait = itemView.findViewById(R.id.llTraits);
        llEquip = itemView.findViewById(R.id.llEquipment);
        rvEquipment = itemView.findViewById(R.id.rvEquipment);
        rvTraits = itemView.findViewById(R.id.rvTraits);
        tvRace = itemView.findViewById(R.id.tvRace);
        tvClass = itemView.findViewById(R.id.tvClass);
        btnEquip = itemView.findViewById(R.id.btnAddEquip);
        btnTrait = itemView.findViewById(R.id.btnAddTrait);
        rbRatings = itemView.findViewById(R.id.rbRatings);
        tvRatings = itemView.findViewById(R.id.tvRatings);
        btnDetail = itemView.findViewById(R.id.btnEdit);

        charPost = Parcels.unwrap(getArguments().getParcelable("post"));
        isAuthor = ParseUser.getCurrentUser().getUsername().equals(charPost.getUser().getUsername());
        traits = new ArrayList<>();
        equipment = new ArrayList<>();
        traitAdapter = new TraitEquipAdapter(getContext(), traits, isAuthor, true, this);
        equipAdapter = new TraitEquipAdapter(getContext(), equipment, isAuthor, false, this);
        fragmentManager = getActivity().getSupportFragmentManager();
        ratingPos = -1;

        // Set RVs
        LinearLayoutManager Tmanager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        LinearLayoutManager Emanager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        rvTraits.setLayoutManager(Tmanager);
        rvTraits.setAdapter(traitAdapter);
        rvEquipment.setLayoutManager(Emanager);
        rvEquipment.setAdapter(equipAdapter);

        //Set DESC, TRAIT, and EQUIP to GONE
        tvDescription.setVisibility(View.GONE);
        rvTraits.setVisibility(View.GONE);
        rvEquipment.setVisibility(View.GONE);

        // Disallow post editing when the user isn't the creator.
        if(!isAuthor){
            btnEquip.setVisibility(View.GONE);
            btnTrait.setVisibility(View.GONE);
            btnDetail.setVisibility(View.GONE);
        }

        // Fill view with data
        tvName.setText(charPost.getName());
        tvTtrpg.setText(charPost.getTtrpg());
        tvUser.setText(String.format("By: %s", charPost.getUser().getUsername()));
        tvClass.setText(String.format("Class: %s", charPost.getClasses()));
        tvRace.setText(String.format("Race: %s", charPost.getRace()));
        tvDescription.setText(charPost.getDesc());
        ParseFile photo = charPost.getPhoto();
        if(photo != null) {
            Glide.with(getContext()).load(photo.getUrl()).transform(new RoundedCorners(30)).into(ivPhoto);
        }
        try {
            equipAdapter.addAll(charPost.toArrayList(false));
            traitAdapter.addAll(charPost.toArrayList(true));
            getRatings();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Set Transition names
        itemView.setTransitionName(charPost.getObjectId());

        // Set clickables
        btnComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CommentFragment fragment = new CommentFragment();
                Bundle args = new Bundle();
                args.putParcelable("post", Parcels.wrap(charPost));
                fragment.setArguments(args);
                fragment.setEnterTransition(new Slide(Gravity.BOTTOM));
                fragment.setReturnTransition(new Slide(Gravity.BOTTOM));
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment,"comments")
                        .addToBackStack("details").commit();
            }
        });
        tvUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProfileFragment fragment = ProfileFragment.newInstance(charPost.getUser());
                fragment.setEnterTransition(new Slide(Gravity.BOTTOM));
                fragment.setReturnTransition(new Slide(Gravity.BOTTOM));
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment,"profile")
                        .addToBackStack("details").commit();
            }
        });
        llEquip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleVisibility(rvEquipment);
            }
        });
        llTrait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleVisibility(rvTraits);
            }
        });
        llDesc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleVisibility(tvDescription);
            }
        });
        btnEquip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItem(equipAdapter, false);
            }
        });
        btnTrait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addItem(traitAdapter, true);
            }
        });
        btnDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editThis();
            }
        });
        rbRatings.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                if (b) {
                    try {
                        setRating((int) v);
                    } catch (JSONException e) {
                        Log.e(TAG, "Exception getting ratings", e);
                    }
                }
            }
        });
    }

    private void editThis() {
        Fragment fragment = ComposeFragment.newInstance(charPost);
        fragment.setEnterTransition(new MaterialElevationScale(true));
        fragment.setExitTransition(new Fade());
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.flContainer, fragment, "edit");
        fragmentTransaction.addToBackStack("details");
        fragmentTransaction.commit();
    }

    private void setRating(int v) throws JSONException {
        if (ratingPos == -1) {
            ratingPos = charPost.addRating();
        }
        if (v == 0) {
            charPost.removeRating(ratingPos);
            ratingPos = -1;
        } else {
            charPost.setRating(v, ratingPos);
        }
        updateText();
        charPost.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e != null){
                    Log.e(TAG, "Exception saving rating", e);
                }
                Log.i(TAG, "rating updated!");
            }
        });
    }

    private void updateText() {
        double score;
        JSONArray arr = charPost.getRatings();
        if(arr.length() != 0) {
            score = ((double) charPost.getRatingScore()) / ((double)arr.length());
        } else {
            score = 0;
        }
        String pattern = "#.##";
        DecimalFormat format = new DecimalFormat(pattern);
        String scoreText = getResources().getString(R.string.score) + " "+ format.format(score) + "/6";
        tvRatings.setText(scoreText);
    }

    private void getRatings() throws JSONException {
        updateText();
        JSONArray ratings = charPost.getRatings();
        for (int i = 0; i < ratings.length(); i++) {
            if (ratings.getJSONObject(i).getString("name").equals(ParseUser.getCurrentUser().getUsername())) {
                ratingPos = i;
            }
        }
        if(ratingPos != -1){
            rbRatings.setRating(ratings.getJSONObject(ratingPos).getInt("rating"));
        }
    }

    private void addItem(TraitEquipAdapter adapter, final boolean trait) {
        Pair<String, String> item = new Pair<>("New Item", "Press and hold onto me to edit what's inside!");
        try {
            charPost.addTraitEquip(item, trait);
            charPost.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    Log.i(TAG, "Added item to backend! Trait?: " + trait);
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
        adapter.add(item);
        adapter.notifyDataSetChanged();
        if(trait){
            rvTraits.setVisibility(View.VISIBLE);
        } else{
            rvEquipment.setVisibility(View.VISIBLE);
        }
    }

    private void toggleVisibility(View view){
        switch (view.getVisibility()) {
            case View.VISIBLE:
                view.setVisibility(View.GONE);
                if (view.getClass().equals(RecyclerView.class)) {
                    ((RecyclerView) view).scrollToPosition(0);
                }
                break;
            case View.GONE:
            case View.INVISIBLE:
                view.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void onFinishEditDialog(String name, String desc, int pos, boolean deleted, boolean isTrait) {
        TraitEquipAdapter adapter;
        if(isTrait){
            adapter = traitAdapter;
        } else{
            adapter = equipAdapter;
        }
        if(deleted){
            adapter.remove(pos);
            charPost.removeTraitEquip(pos, isTrait);
        } else {
            Pair<String, String> pair = new Pair<>(name, desc);
            try {
                charPost.setTraitEquip(pos, pair, isTrait);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            adapter.set(pair, pos);
        }
        charPost.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Log.i(TAG, "Item Successfully updated!");
            }
        });
    }
}