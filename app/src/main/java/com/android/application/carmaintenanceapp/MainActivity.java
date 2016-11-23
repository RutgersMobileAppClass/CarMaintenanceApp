package com.android.application.carmaintenanceapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;


    public static class LoadedPerson {

        // WE SHOULD NOT STORE THE USER'S username AND password ON THE DEVICE OR IN PLAIN TEXT ON OUR FIREBASE!!
        // https://developer.android.com/reference/android/accounts/AccountManager.html
        // https://developer.android.com/samples/index.html
        private String username;
        private String password;
        private String email;
        private Boolean remeberMeisChecked;
        private int milage;

        public LoadedPerson (String name, String pass, Boolean boxChecked, String email){
            this.username = name;
            this.password = pass;
            this.remeberMeisChecked = boxChecked;
            this.email = email;
        }

        public String getUsername(){
            return this.username;
        }
        public String getPassword(){
            return this.password;
        }
        public String getEmail(){
            return  email;
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

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("MainActivity", "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d("MainActivity", "onAuthStateChanged:signed_out");
                }

            }
        };


        file = this.getSharedPreferences(MY_PREFS_NAME, this.MODE_PRIVATE);
        rememberMeisChecked = file.getBoolean("RemeberMeCheckBox", false);

        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        userNameEditText = (EditText) findViewById(R.id.userNameEditText);
    }

    public void LogInPerson(View view) {

        final String username = userNameEditText.getText().toString().trim();
        final String password = passwordEditText.getText().toString().trim();

        // Log the person in
        if(checkIfValidInputs(username, password)) {
            Log.i("MainActivity", "Call LogIn Function");

            SharedPreferences.Editor editor = file.edit();
            editor.putBoolean("RemeberMeCheckBox", rememberMeisChecked);
            editor.apply();

            // Put Object into Shared Preferences
            LoadedPerson person = new LoadedPerson(username, password, rememberMeisChecked, "mjameswalier@gmail.com");
            saveSharedPreferences(this, person);

            mAuth.signInWithEmailAndPassword(username, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d("MainActivity", "signInWithEmail:onComplete:" + task.isSuccessful());

                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {

                                Toast.makeText(getApplicationContext(), "Email & Password did not match any in our database", Toast.LENGTH_LONG).show();
                                Log.i("MainActivity", "We did not find the person\n Username: " + username  + "\nPassword: " + password);

                            }
                        }
                    });
        }
    }
    
    public Boolean checkIfValidInputs(String username, String password){

        // Password must have 2 digit, one lowercase, one uppercase, and between 6 and 20 characters
        Pattern pattern = Pattern.compile("((?=(.*\\d){2})(?=.*[a-z])(?=.*[A-Z]).{6,20})");
        Matcher matcher = pattern.matcher(password);
        Boolean correctPassword = matcher.matches();

        Boolean correctUsername = android.util.Patterns.EMAIL_ADDRESS.matcher(username).matches();

        // Check username and password do not have any spaces
        if(username.contains(" ")|| password.contains(" ")){
            Toast.makeText(this, "Usernames and Passwords should not have spaces", Toast.LENGTH_LONG).show();
            return false;
        }

        // Check the password is valid
        if (!correctPassword){
            Toast.makeText(this, "Password must have 1 digit, one lowercase letter, one uppercase letter and between 6 and 20 characters", Toast.LENGTH_LONG).show();
            Log.i("MainActivity", "The invalid password was " + password);
            return false;
        }

        // Check the username is valid
        if(!correctUsername){
            Toast.makeText(this, "Username must have one lowercase letter, one uppercase letter and between 6 and 20 characters with no spaces", Toast.LENGTH_LONG).show();
            Log.i("MainActivity", "The invalid username was " + username);
            return false;
        }

        return true;
    }

    public void LoginToGmail(View view){
        // Find person based off of their gmail name
        Toast.makeText(this, "Logged In to Gmail", Toast.LENGTH_LONG).show();

    }

    public void LoginToFacebook(View view){
        // Find the person based off of their usename
        Toast.makeText(this, "Logged In to Facebook", Toast.LENGTH_LONG).show();

    }

    public void CreateNewAccount(View view) {

        final String username = userNameEditText.getText().toString().trim();
        final String password = passwordEditText.getText().toString().trim();

        if (checkIfValidInputs(username, password)){

            mAuth.createUserWithEmailAndPassword(username, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d("MainActivity", "createUserWithEmail:onComplete:" + task.isSuccessful());

                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Was not able to create new user", Toast.LENGTH_LONG).show();
                                Log.i("MainActivity", "We could not create the user\n Username: " + username + "\nPassword: " + password);
                            }

                            // ...
                        }
                    });
        }
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
            person = new LoadedPerson("FAKE PERSON", "FAKE PERSON", true, "FAKE EMAIL!");
        } else {
            Type type = new TypeToken<LoadedPerson>() {}.getType();
            person = gson.fromJson(file.getString("myJson", ""), type);
        }

        return person;
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
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
