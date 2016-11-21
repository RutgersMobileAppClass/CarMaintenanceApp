package com.example.mitchellwalier.carmaintenanceapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    EditText passwordEditText;
    EditText userNameEditText;
    static SharedPreferences file;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    Boolean rememberMeisChecked;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        file = this.getSharedPreferences(MY_PREFS_NAME, this.MODE_PRIVATE);

        rememberMeisChecked = file.getBoolean("RemeberMeCheckBox", true);

        // See if "Remeber Me" Check box is checked, if it was than log person in

        // Else, this
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        userNameEditText = (EditText) findViewById(R.id.userNameEditText);

        CheckBox checkBox = (CheckBox) findViewById(R.id.checkBox);
        checkBox.setChecked(rememberMeisChecked);

    }

    public void LogInPerson(View view) {

        String username = userNameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if(username.length() < 1 && password.length() < 1){
            Toast.makeText(this, "Invalid UserName", Toast.LENGTH_LONG);

        } else {

            SharedPreferences.Editor editor = file.edit();
            editor.putBoolean("RemeberMeCheckBox", rememberMeisChecked);
            editor.apply();

            // Log into firebase

            // If not a person, or wrong password, don't start intent, make toast

            // Start intent, save to shared preferences if the checkbox is checked


        }
    }

    public void LoginToGmail(View view){
        // Find person based off of their gmail name
        Toast.makeText(this, "Logged In to Gmail", Toast.LENGTH_LONG);

    }

    public void LoginToFacebook(View view){
        // Find the person based off of their usename
        Toast.makeText(this, "Logged In to Facebook", Toast.LENGTH_LONG);

    }

    public void CreateNewAccount(View view){
        // Create a connection with Firebase and see if the username is already taken
        Toast.makeText(this, "Created a new account and logged in", Toast.LENGTH_LONG);

    }

    /*

    public static void saveSharedPreferences(Context context, ArrayList<Location> contacts) {
        SharedPreferences.Editor editor = file.edit();
        Gson gson = new Gson();
        editor.putString("myJson", gson.toJson(contacts));
        editor.apply();
    }

    public ArrayList<Location> loadSharedPreferences(Context context) {
        ArrayList<Location> contacts;
        Gson gson = new Gson();
        if (file.getString("myJson", "").isEmpty()) {
            contacts = new ArrayList<>();
        } else {
            Type type = new TypeToken<ArrayList<Location>>() {}.getType();
            contacts = gson.fromJson(file.getString("myJson", ""), type);
        }
        return contacts;
    }

    public static void saveSharedPreferencesInt(Context context, int temp) {
        SharedPreferences.Editor editor = file.edit();
        editor.putInt("Wifi Connection", temp);
        editor.apply();
    }

    public static int loadSharedPreferencesInt(Context context) {
        return file.getInt("Wifi Connection", 3);
    }


    // Create onlclick method for login with facebook, need to register app with them
    // https://code.tutsplus.com/tutorials/quick-tip-add-facebook-login-to-your-android-app--cms-23837



    /*
    public static class DownLoadLocalDataBase extends AsyncTask {

        Context context;

        public DownLoadLocalDataBase(Context con) {
            this.context = con;
        }

        public DownLoadLocalDataBase() {
        }

        @Override
        protected Object doInBackground(Object... args) {
            Object obj = new Object();

            // Write a message to the database IT WORKED!!
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            String id = "mjw230";
            DatabaseReference myRef = database.getReference("Student").child(id);

            for (int i = 0; i < unsavedData.size(); i++) {
                Log.i("myApp", i + "    " + unsavedData.size());
                DatabaseReference temper = myRef.child(unsavedData.get(i).getdate());
                temper.child("date").setValue(unsavedData.get(i).getdate());
                temper.child("netid").setValue(id);
                temper.child("x").setValue(unsavedData.get(i).getx());
                temper.child("y").setValue(unsavedData.get(i).gety());
            }

            unsavedData.clear();
            saveSharedPreferences(context, unsavedData);

            database.goOffline();
            Log.i("myApp", "went off line");

            return obj;

        }
    }

    public static class LocationReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            String date = intent.getStringExtra("Date");
            String lon = intent.getStringExtra("Long");
            String lat = intent.getStringExtra("Lat");

            String answer = date + "\n" + lon + ", " + lat;

            Log.i("myApp", "Found a location");
            locations.add(0, answer);

            if (offline) {
                FragLocalData.updateAdapter();

            } else {
                FragServerData.updateAdapter();
            }

            MainActivity.helper.insertEntry(date, lat + ", " + lon);
            unsavedData.add(new Location(date, lon, lat));
            saveSharedPreferences(context, unsavedData);

            if(onWifi) {
                try {
                    new DownLoadLocalDataBase(context).execute();

                } catch (Exception e) {
                    Log.i("myApp", e.toString());
                }
            }
        }
    } */


}
