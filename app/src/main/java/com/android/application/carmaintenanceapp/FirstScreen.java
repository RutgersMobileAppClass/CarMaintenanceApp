package com.android.application.carmaintenanceapp;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FirstScreen extends AppCompatActivity {

    public class MyAdapter extends BaseAdapter {

        private Context context;
        private String[] texts = {"car name TEST TEXT", "estimated value TEST TEXT", "expenses TEST TEXT" };

        public MyAdapter(Context context) {
            this.context = context;
        }

        public int getCount() {
            return 3;
        }

        public Object getItem(int position) {
            return texts[position];
        }

        public long getItemId(int position) {
            return 0;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            TextView tv;

            if (convertView == null) {
                tv = new TextView(context);
                tv.setLayoutParams(new GridView.LayoutParams(85, 85));
            }

            else {
                tv = (TextView) convertView;
            }

            tv.setText(texts[position]);
            return tv;
        }
    }

    String TAG = "First Screen";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_screen);

        //inflate GridView
        GridView gridView = (GridView) findViewById(R.id.carList);
        gridView.setAdapter(new MyAdapter(this));
    }

    public void onBackPressed() {

        new AlertDialog.Builder(this)
                .setTitle("Log Out")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        // Sign out of the three possible ways
                        try {
                            FirebaseAuth.getInstance().signOut();
                        } catch(Exception e) {
                        }
                        try {
                            LoginManager.getInstance().logOut();
                        } catch(Exception e) {
                        }

                        try { revokeAccess(); }
                        catch(Exception e) {
                        }


                        finish();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

    }


    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(MainActivity.mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // ...
                    }
                });
    }
}
