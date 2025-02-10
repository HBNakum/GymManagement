package com.example.gymmanagement.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public abstract class BaseFragment extends Fragment {
    ProgressDialog mProgess;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mProgess = new ProgressDialog(getActivity());
    }

    public void showDialog(String msg){
        try {
            mProgess.setMessage(msg);
            mProgess.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgess.setIndeterminate(false);
            mProgess.setCancelable(false);
            mProgess.show();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public  void CloseDialog(){
        if (mProgess!=null){
            mProgess.dismiss();
        }
    }

}
