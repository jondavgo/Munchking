package com.example.munchking.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.munchking.R;
import com.example.munchking.adapters.TraitEquipAdapter;
import com.example.munchking.dialogs.AddItemDialog;
import com.example.munchking.models.CharPost;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;
import org.parceler.Parcels;

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

    private ImageView ivPhoto;
    private TextView tvName;
    private TextView tvTtrpg;
    private TextView tvUser;
    private Button btnComments;
    private Button btnTrait;
    private Button btnEquip;
    private LinearLayout llDesc;
    private TextView tvDescription;
    private LinearLayout llTrait;
    private RecyclerView rvTraits;
    private LinearLayout llEquip;
    private RecyclerView rvEquipment;
    private TextView tvRace;
    private TextView tvClass;
    private ViewGroup parent;

    public DetailFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        parent = container;
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

        charPost = Parcels.unwrap(getArguments().getParcelable("post"));
        isAuthor = ParseUser.getCurrentUser().getUsername().equals(charPost.getUser().getUsername());
        traits = new ArrayList<>();
        equipment = new ArrayList<>();
        traitAdapter = new TraitEquipAdapter(getContext(), traits, isAuthor, true, this);
        equipAdapter = new TraitEquipAdapter(getContext(), equipment, isAuthor, false, this);
        fragmentManager = getActivity().getSupportFragmentManager();

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
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment,"comments")
                        .addToBackStack("details").commit();
            }
        });
        tvUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProfileFragment fragment = ProfileFragment.newInstance(charPost.getUser());
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
            rvTraits.smoothScrollToPosition(0);
        } else{
            rvEquipment.setVisibility(View.VISIBLE);
            rvEquipment.smoothScrollToPosition(0);
        }
    }

    private void toggleVisibility(View view){
        switch (view.getVisibility()){
            case View.VISIBLE:
                view.setVisibility(View.GONE);
                break;
            case View.GONE:
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
            try {
                charPost.removeTraitEquip(pos, isTrait);
            } catch (JSONException e) {
                e.printStackTrace();
            }
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