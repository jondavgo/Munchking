package com.example.munchking.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.munchking.R;
import com.example.munchking.adapters.TraitEquipAdapter;
import com.example.munchking.models.CharPost;
import com.parse.ParseFile;

import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class DetailFragment extends Fragment {

    private CharPost charPost;
    private List<Pair<String, String>> traits;
    private List<Pair<String, String>> equipment;
    private TraitEquipAdapter traitAdapter;
    private TraitEquipAdapter equipAdapter;

    private ImageView ivPhoto;
    private TextView tvName;
    private TextView tvTtrpg;
    private TextView tvUser;
    private Button btnComments;
    private Button btnTrait;
    private Button btnEquip;
    private TextView tvDescName;
    private TextView tvDescription;
    private TextView tvTraitName;
    private RecyclerView rvTraits;
    private TextView tvEquipName;
    private RecyclerView rvEquipment;
    private TextView tvRace;
    private TextView tvClass;

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
        tvDescName = itemView.findViewById(R.id.tvWordDesc);
        tvDescription = itemView.findViewById(R.id.tvDesc);
        tvTraitName = itemView.findViewById(R.id.tvTraits);
        tvEquipName = itemView.findViewById(R.id.tvEquipment);
        rvEquipment = itemView.findViewById(R.id.rvEquipment);
        rvTraits = itemView.findViewById(R.id.rvTraits);
        tvRace = itemView.findViewById(R.id.tvRace);
        tvClass = itemView.findViewById(R.id.tvClass);
        btnEquip = itemView.findViewById(R.id.btnAddEquip);
        btnTrait = itemView.findViewById(R.id.btnAddTrait);

        charPost = Parcels.unwrap(getArguments().getParcelable("post"));
        traits = new ArrayList<>();
        equipment = new ArrayList<>();
        traitAdapter = new TraitEquipAdapter(getContext(), traits);
        equipAdapter = new TraitEquipAdapter(getContext(), equipment);

        // Set RVs
        LinearLayoutManager Tmanager = new LinearLayoutManager(getContext());
        LinearLayoutManager Emanager = new LinearLayoutManager(getContext());
        rvTraits.setLayoutManager(Tmanager);
        rvTraits.setAdapter(traitAdapter);
        rvEquipment.setLayoutManager(Emanager);
        rvEquipment.setAdapter(equipAdapter);

        //Set DESC, TRAIT, and EQUIP to GONE
        tvDescription.setVisibility(View.GONE);
        rvTraits.setVisibility(View.GONE);
        rvEquipment.setVisibility(View.GONE);

        // Fill view with data
        tvName.setText(charPost.getName());
        tvTtrpg.setText(charPost.getTtrpg());
        tvUser.setText(charPost.getUser().getUsername());
        tvClass.setText(String.format("Class: %s", charPost.getClasses()));
        tvRace.setText(String.format("Race: %s", charPost.getRace()));
        tvDescription.setText(charPost.getDesc());
        ParseFile photo = charPost.getPhoto();
        if(photo != null) {
            Glide.with(getContext()).load(photo.getUrl()).into(ivPhoto);
        }

        // Set clickables
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
        tvEquipName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleVisibility(rvEquipment);
            }
        });
        tvTraitName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleVisibility(rvTraits);
            }
        });
        tvDescName.setOnClickListener(new View.OnClickListener() {
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

    private void addItem(TraitEquipAdapter adapter, boolean trait) {

    }


    private void toggleVisibility(View view){
        if(view.getVisibility() == View.GONE){
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }
}