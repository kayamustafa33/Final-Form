package com.mustafakaya.fiform;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;

class LoadingDialog {
    private AlertDialog dialog;

    private Activity activity;

    public LoadingDialog(Activity activity){
        this.activity = activity;
    }

    void showLoading(){
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.alert_dialog,null));
        builder.setCancelable(false);
        dialog = builder.create();
        dialog.show();
    }

    void disMiss(){
        dialog.dismiss();
    }
}
