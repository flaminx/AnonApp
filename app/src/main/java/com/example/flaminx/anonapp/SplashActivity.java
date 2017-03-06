package com.example.flaminx.anonapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.example.flaminx.anonapp.Pojo.NetworkCheck;

/**
 * Created by Flaminx on 06/03/2017.
 */

public class SplashActivity extends  AppCompatActivity {
    Context c = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);

        setupApp();
    }

    private void setupApp()
    {
        if(!NetworkCheck.isConnected(this)) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setTitle(getString(R.string.friendly_error));
            alertDialogBuilder.setMessage(getString(R.string.no_connection));
            alertDialogBuilder.setCancelable(false);



            alertDialogBuilder.setPositiveButton(getString(R.string.retry), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    setupApp();
                }
            });

            alertDialogBuilder.setNegativeButton(getString(R.string.quit), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                    System.exit(0);
                }
            });

            AlertDialog dialog = alertDialogBuilder.create();
            dialog.show();
        }

        else{
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
