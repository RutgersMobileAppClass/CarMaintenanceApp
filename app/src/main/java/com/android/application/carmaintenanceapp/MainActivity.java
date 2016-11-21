package com.android.application.carmaintenanceapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    EditText passwordEditText;
    EditText userNameEditText;
    static SharedPreferences file;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    Boolean rememberMeisChecked;


    public static class LoadedPerson {
        private int milage;
        // WE SHOULD NOT STORE THE USER'S username AND password ON THE DEVICE OR IN PLAIN TEXT ON OUR FIREBASE!!
        // https://developer.android.com/reference/android/accounts/AccountManager.html
        // https://developer.android.com/samples/index.html
        private String username;
        private String password;
        private Boolean remeberMeisChecked;

        public LoadedPerson (String name, String pass, Boolean boxChecked){
            this.username = name;
            this.password = pass;
            this.remeberMeisChecked = boxChecked;
        }

        public String getUsername(){
            return this.username;
        }

        public String getPassword(){
            return this.password;
        }

        public Boolean getRemeberMeisChecked(){
            return this.remeberMeisChecked;
        }

        public int getMilage(){
            return milage;
        }

    }

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

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
               @Override
               public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                   SharedPreferences.Editor editor = file.edit();
                   editor.putBoolean("RemeberMeCheckBox", isChecked);
                   editor.apply();

                   rememberMeisChecked = isChecked;
               }
           }
        );

    }

    public void LogInPerson(View view) {

        String username = userNameEditText.getText().toString();
        String password = passwordEditText.getText().toString();
        Boolean correctLength = true;

        // Password must have 1 digit, one lowercase, one uppercase, and between 6 and 20 characters
        Pattern pattern = Pattern.compile("((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{6,20})");
        Matcher matcher = pattern.matcher(password);
        Boolean correctPassword = matcher.matches();


        // Username must have one lowercase, and one uppercase
        // ALL NEED TO MAKE SURE IT DOES NOT HAVE ANY SPACES
        pattern = Pattern.compile("((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{6,20})");
        matcher = pattern.matcher(username);
        Boolean correctUsername = (matcher.matches());

        // Check username and password are the correct length
        if(username.length() < 1 && password.length() < 1){
            Toast.makeText(this, "Invalid UserName", Toast.LENGTH_LONG).show();
            correctLength = false;
        }

        // Check the password is valid
        if (!correctPassword){
            Toast.makeText(this, "Password must have 1 digit, one lowercase letter, one uppercase letter and between 6 and 20 characters", Toast.LENGTH_LONG).show();
            Log.i("MainActivity", "The invalid password was " + password);
        }

        // Check the username is valid
        if(!correctUsername){
            Toast.makeText(this, "Username must have one lowercase letter, one uppercase letter and between 6 and 20 characters with no spaces", Toast.LENGTH_LONG).show();
            Log.i("MainActivity", "The invalid username was " + username);
        }

        // Log the person in
        if(correctPassword && correctUsername && correctLength) {
            Log.i("MainActivity", "Call LogIn Function");

            SharedPreferences.Editor editor = file.edit();
            editor.putBoolean("RemeberMeCheckBox", rememberMeisChecked);
            editor.apply();

            // Put Object into Shared Preferences
            LoadedPerson person = new LoadedPerson(username, password, rememberMeisChecked);
            saveSharedPreferences(this, person);

            LogInToFirebase();
        }
    }

    public void LogInToFirebase(){

        // Load user from Shared Preferences
        LoadedPerson person = loadSharedPreferences(this);

        // Log into firebase
        if(true /*found in firebase with username and correct password*/){

            // SEE IF PERSON IS IN FIRE BASE

            passwordEditText.setText("");

        } else {
            // If not a person, or wrong password, don't start intent, make toast
            Toast.makeText(this, "Username & Password did not match any in our database", Toast.LENGTH_LONG).show();
            Log.i("MainActivity", "We did not find the person\n Username: " + person.getUsername() + "\nPassword: " + person.getUsername());
        }



        // Start intent, save to shared preferences if the checkbox is checked

    }

    public void LoginToGmail(View view){
        // Find person based off of their gmail name
        Toast.makeText(this, "Logged In to Gmail", Toast.LENGTH_LONG).show();

    }

    public void LoginToFacebook(View view){
        // Find the person based off of their usename
        Toast.makeText(this, "Logged In to Facebook", Toast.LENGTH_LONG).show();

    }

    public void CreateNewAccount(View view){
        // Create a connection with Firebase and see if the username is already taken
        Toast.makeText(this, "Created a new account and logged in", Toast.LENGTH_LONG).show();

    }

    public static void saveSharedPreferences(Context context, LoadedPerson person) {
        SharedPreferences.Editor editor = file.edit();
        Gson gson = new Gson();
        editor.putString("myJson", gson.toJson(person));
        editor.apply();
    }

    public LoadedPerson loadSharedPreferences(Context context) {
        LoadedPerson person;
        Gson gson = new Gson();
        if (file.getString("myJson", "").isEmpty()) {
            person = new LoadedPerson("FAKE PERSON", "FAKE PERSON", true);
        } else {
            Type type = new TypeToken<LoadedPerson>() {}.getType();
            person = gson.fromJson(file.getString("myJson", ""), type);
        }

        return person;
    }

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
    } */



    /*

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
