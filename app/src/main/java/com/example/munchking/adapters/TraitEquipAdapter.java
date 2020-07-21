package com.example.munchking.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;

import com.example.munchking.R;

import java.util.ArrayList;
import java.util.List;

public class TraitEquipAdapter extends RecyclerView.Adapter<TraitEquipAdapter.ViewHolder> {
    Context context;
    List<Pair<String, String>> items;

    public TraitEquipAdapter(Context context, List<Pair<String, String>> items) {
        this.context = context;
        this.items = items;
    }

    public void addAll(List<Pair<String, String>> c){
        items.addAll(c);
        notifyDataSetChanged();
    }

    public void clear(){
        items.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_trait_equip, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Pair<String, String> item = items.get(position);
        holder.bind(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvName;
        private TextView tvDesc;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvTraitName);
            tvDesc = itemView.findViewById(R.id.tvTraitDesc);
        }

        public void bind(Pair<String, String> item) {
            tvName.setText(item.first);
            tvDesc.setText(item.second);
        }
    }

    // TODO
    public static List<Pair<String, String>> fromJSONArray(){
        return new ArrayList<>();
    }
}