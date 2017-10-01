package com.example.onurp.myapplication.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.onurp.myapplication.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by onurp on 20.09.2017.
 */

public class DeleteDialog extends DialogFragment implements View.OnClickListener{
    @BindView(R.id.yesbtn)Button yesBtn;
    @BindView(R.id.nobtn)Button noBtn;
    Communicator communicator;



    @Override
    public void onAttach(Context activity) {

        super.onAttach(activity);

        if (activity instanceof Communicator) {
            communicator = (Communicator) getActivity();
        } else {
            throw new ClassCastException(activity.toString()
                    +"must implemenet MyListFragment.communicator");
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setCancelable(false);
        getDialog().setTitle("title");

        View view = inflater.inflate(R.layout.delete_dialog, null, false);
        ButterKnife.bind(this,view);

        // setting onclick listener for buttons
        yesBtn.setOnClickListener(this);
        noBtn.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.yesbtn :
                dismiss();
                communicator.message("yes");
                break;

            case R.id.nobtn :
                dismiss();
                communicator.message("no");
                break;
        }

    }

    public interface Communicator {
        public void message(String data);
    }
}
