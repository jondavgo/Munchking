package com.example.munchking.dialogs;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;

import com.example.munchking.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddItemDialog extends DialogFragment {

    private EditText etName;
    private EditText etDesc;
    private int pos;
    private boolean delete;
    private boolean isTrait;

    public AddItemDialog(){
        // required empty constructor
    }

    public static AddItemDialog newInstance(String title, int pos, boolean trait) {
        AddItemDialog frag = new AddItemDialog();
        Bundle args = new Bundle();
        args.putString("title", title);
        args.putInt("pos", pos);
        args.putBoolean("trait", trait);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString("title");
        pos = getArguments().getInt("pos");
        isTrait = getArguments().getBoolean("trait");
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setTitle(title);
        View v = LayoutInflater.from(getContext()).inflate(R.layout.fragment_add_item_dialog, null);
        alertDialogBuilder.setView(v);
        etDesc = v.findViewById(R.id.etDesc);
        etName = v.findViewById(R.id.etName);
        alertDialogBuilder.setPositiveButton("OK",  new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // on success
                delete = false;
                sendBackResult();
            }
        });
        alertDialogBuilder.setNeutralButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // on failure
                delete = true;
                sendBackResult();
            }
        });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        return alertDialogBuilder.create();
    }

    public interface EditDialogListener {
        void onFinishEditDialog(String name, String desc, int pos, boolean deleted, boolean trait);
    }

    public void sendBackResult() {
        EditDialogListener listener = (EditDialogListener) getTargetFragment();
        listener.onFinishEditDialog(etName.getText().toString(), etDesc.getText().toString(), pos, delete, isTrait);
        dismiss();
    }
}