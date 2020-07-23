package com.example.munchking.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.util.Pair;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.munchking.R;
import com.example.munchking.activities.MainActivity;
import com.example.munchking.dialogs.AddItemDialog;

import java.util.ArrayList;
import java.util.List;

public class TraitEquipAdapter extends RecyclerView.Adapter<TraitEquipAdapter.ViewHolder> {
    Context context;
    List<Pair<String, String>> items;

    public TraitEquipAdapter(Context context, List<Pair<String, String>> items) {
        this.context = context;
        this.items = items;
    }

    public void add(Pair<String, String> item){
        items.add(item);
        notifyItemInserted(items.size()-1);
    }

    public void remove(int pos){
        items.remove(pos);
        notifyItemRemoved(pos);
    }

    public void addAll(List<Pair<String, String>> i){
        items.addAll(i);
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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        private TextView tvName;
        private TextView tvDesc;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvTraitName);
            tvDesc = itemView.findViewById(R.id.tvTraitDesc);
            itemView.setOnLongClickListener(this);
        }

        public void bind(Pair<String, String> item) {
            tvName.setText(item.first);
            tvDesc.setText(item.second);
        }

        @Override
        public boolean onLongClick(View view) {
            FragmentManager fragmentManager = ((MainActivity) context).getSupportFragmentManager();
            AddItemDialog alertDialog = AddItemDialog.newInstance("Edit Me!!!");
            alertDialog.show(fragmentManager, "fragment_alert");
            return true;
        }
    }
}