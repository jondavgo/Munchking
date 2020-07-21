package com.example.munchking.dialogs;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.munchking.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddItemDialog extends DialogFragment {

    public AddItemDialog(){
        // required empty constructor
    }

    public static AddItemDialog newInstance(String title) {
        AddItemDialog frag = new AddItemDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }
}