package com.android.application.carmaintenanceapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.facebook.FacebookSdk;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends FragmentActivity {

    EditText passwordEditText;
    EditText userNameEditText;
    static SharedPreferences file;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    Boolean rememberMeisChecked;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    static GoogleApiClient mGoogleApiClient;
    CallbackManager callbackManager;
    LoginButton login_button;
    Boolean ShutOffButtons;


    // NEED TO CREATE A FIREBASE INSTANCE THAT IS STATIC ACROSS THE ENTIRE APP TO SAVE AND LOAD SHIT!

    public static class car {
        private String current_mileage;
        private String starting_mileage;
        private String last_maintenance_mileage;
        private String total_expenses;
        private ArrayList<String> type_of_expenses;
        private ArrayList<String> expenses;
        private String initial_investment;    // How much you payed for the car
        private String date_of_next_inspection;

        public String getCurrent_mileage(){ return current_mileage; }
        public String getStarting_mileage() {return starting_mileage; }
        public String getLast_maintenance_mileage() {return last_maintenance_mileage;}
        public String getTotal_expenses() { return total_expenses;}
        public String getInitial_investment() { return initial_investment;}
        public String getDate_of_next_inspection() {return date_of_next_inspection;}
        public ArrayList<String> getType_of_expenses() { return type_of_expenses; }
        public ArrayList<String> getExpenses() { return expenses;}

        public void setCurrent_mileage(String current_mileage) {
            this.current_mileage = current_mileage;
        }
        public void setDate_of_next_inspection(String date_of_next_inspection) {
            this.date_of_next_inspection = date_of_next_inspection;
        }
        public void setInitial_investment(String initial_investment) {
            this.initial_investment = initial_investment;
        }
        public void setLast_maintenance_mileage(String last_maintenance_mileage) {
            this.last_maintenance_mileage = last_maintenance_mileage;
        }
        public void setStarting_mileage(String starting_mileage) {
            this.starting_mileage = starting_mileage;
        }
        public void setTotal_expenses(String total_expenses) {
            this.total_expenses = total_expenses;
        }
        public void setType_of_expenses(ArrayList<String> type_of_expenses) {
            this.type_of_expenses = type_of_expenses;
        }
        public void setExpenses(ArrayList<String> expenses) {
            this.expenses = expenses;
        }

    }

    public static class LoadedPerson {

        // WE SHOULD NOT STORE THE USER'S username AND password ON THE DEVICE OR IN PLAIN TEXT ON OUR FIREBASE!!
        // https://developer.android.com/reference/android/accounts/AccountManager.html
        // https://developer.android.com/samples/index.html
        private String username;
        private String email;
        private ArrayList<String> car_models;


        public LoadedPerson (String _name, ArrayList<String> _car_models){
            this.username = _name;
            this.car_models = _car_models;
        }

        public LoadedPerson() {}

        public String getUsername(){
            return this.username;
        }
        public String getEmail(){
            return  email;
        }


        public ArrayList<String> getCar_models() {return car_models;}
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(this.getApplicationContext());
        setContentView(R.layout.activity_main);

        // Used so facebook can track downloads and uses
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(getApplication());

        file = this.getSharedPreferences(MY_PREFS_NAME, this.MODE_PRIVATE);
        rememberMeisChecked = file.getBoolean("RemeberMeCheckBox", false);

        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        userNameEditText = (EditText) findViewById(R.id.userNameEditText);

        passwordEditText.setText("");
        userNameEditText.setText(file.getString("recentUserName", ""));

        findViewById(R.id.progress_bar).setVisibility(View.GONE);
        ShutOffButtons = false;

        //////////////// Firebase code
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {

                    SharedPreferences.Editor editor = file.edit();
                    editor.putString("recentUserName", user.getEmail());
                    editor.apply();

                    // User is signed in
                    MoveToFirstScreen();
                    ShutOffButtons = false;

                    Log.d("MainActivity", "onAuthStateChanged:signed_in:" + user.getUid());
                    Toast.makeText(getApplicationContext(), "Entered the next intent", Toast.LENGTH_LONG).show();

                } else {
                    // User is signed out
                    Log.d("MainActivity", "onAuthStateChanged:signed_out");
                    Toast.makeText(getApplicationContext(), "Signed out the user", Toast.LENGTH_LONG).show();

                    findViewById(R.id.progress_bar).setVisibility(View.GONE);
                    ShutOffButtons = false;

                }

            }
        };

        //////////////////// Facebook code
        callbackManager = CallbackManager.Factory.create();

        login_button = (LoginButton) findViewById(R.id.facebook_login_button);
        login_button.setReadPermissions(Arrays.asList("public_profile","email"));
        login_button.registerCallback(callbackManager, new FacebookCallback<LoginResult>()
        {
            @Override
            public void onSuccess(LoginResult loginResult)
            {

                GraphRequest graphRequest   =   GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback()
                {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response)
                    {
                        Log.d("JSON", ""+response.getJSONObject().toString());

                        try
                        {
                            String email       =   object.getString("email");
                            String name        =   object.getString("name");
                            String first_name  =   object.optString("first_name");
                            String last_name   =   object.optString("last_name");

                            Log.i("Facebook Login", email + " " + first_name + " " + last_name + " " + name);
                            Toast.makeText(getApplicationContext(), "Mother of god it worked!", Toast.LENGTH_LONG).show();
                            LoginManager.getInstance().logOut();

                            SharedPreferences.Editor editor = file.edit();
                            editor.putString("recentUserName", "");
                            editor.apply();

                            MoveToFirstScreen();
                            ShutOffButtons = false;

                        }
                        catch (JSONException e)
                        {
                            e.printStackTrace();
                        }
                    }
                });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,first_name,last_name,email");
                graphRequest.setParameters(parameters);
                graphRequest.executeAsync();
            }

            @Override
            public void onCancel()
            {
                Log.i("Facebook Login", "It was canceled");
                findViewById(R.id.progress_bar).setVisibility(View.GONE);
                ShutOffButtons = false;

            }

            @Override
            public void onError(FacebookException exception)
            {
                Log.i("Facebook Login" , "There was an error " + exception.toString());
                findViewById(R.id.progress_bar).setVisibility(View.GONE);
                ShutOffButtons = false;
            }
        });

        //////////////// Google code
        View.OnClickListener googleLogin = new View.OnClickListener() {
            public void onClick(View v) {

                findViewById(R.id.progress_bar).setVisibility(View.VISIBLE);
                ShutOffButtons = true;

                Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
                startActivityForResult(signInIntent, 101);
            }
        };

        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(googleLogin);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                //.requestScopes(new Scope(Scopes.DRIVE_APPFOLDER))
                //.requestServerAuthCode(server_client_id, false)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        // connection failed, should be handled
                        Log.i("MainActivity", "Gmail connection failed");
                        findViewById(R.id.progress_bar).setVisibility(View.GONE);
                        ShutOffButtons = false;
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

    }

    public void LogInPerson(View view) {
        if(!ShutOffButtons) {
            ShutOffButtons = true;
            findViewById(R.id.progress_bar).setVisibility(View.VISIBLE);

            final String username = userNameEditText.getText().toString().trim();
            final String password = passwordEditText.getText().toString().trim();

            passwordEditText.setText("");

            // Log the person in
            if (checkIfValidInputs(username, password)) {
                Log.i("MainActivity", "Call LogIn Function");

                SharedPreferences.Editor editor = file.edit();
                editor.putBoolean("RemeberMeCheckBox", rememberMeisChecked);
                editor.apply();

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
                                    Log.i("MainActivity", "We did not find the person\n Username: " + username + "\nPassword: " + password);

                                    findViewById(R.id.progress_bar).setVisibility(View.GONE);
                                    ShutOffButtons = false;

                                }

                                MoveToFirstScreen();

                            }
                        });
            } else {
                findViewById(R.id.progress_bar).setVisibility(View.GONE);
                ShutOffButtons = false;
            }
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

    public void CreateNewAccount(View view) {
        if(!ShutOffButtons) {
            findViewById(R.id.progress_bar).setVisibility(View.VISIBLE);
            ShutOffButtons = true;

            final String username = userNameEditText.getText().toString().trim();
            final String password = passwordEditText.getText().toString().trim();

            if (checkIfValidInputs(username, password)) {

                mAuth.createUserWithEmailAndPassword(username, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Log.d("MainActivity", "createUserWithEmail:onComplete:" + task.isSuccessful());

                                // SHOULD PUT A LOADING ANIMATION

                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Toast.makeText(getApplicationContext(), "Was not able to " +
                                                    "create new user.\nEmail is already taken\nUse 'Login' " +
                                                    "button if you have already created an account",
                                            Toast.LENGTH_LONG).show();

                                    Log.i("MainActivity", "We could not create the user\n Username: " +
                                            username + "\nPassword: " + password);

                                    findViewById(R.id.progress_bar).setVisibility(View.GONE);
                                    ShutOffButtons = false;
                                }

                                // ...
                            }
                        });
            }
        } else {
            findViewById(R.id.progress_bar).setVisibility(View.GONE);
            ShutOffButtons = false;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == 101) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }

        callbackManager.onActivityResult(requestCode, resultCode, data);

    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d("MainActivity", "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            GoogleSignInAccount acct = result.getSignInAccount();

            MoveToFirstScreen();
            ShutOffButtons = false;

        } else {
            ShutOffButtons = false;
            Toast.makeText(getApplicationContext(), "Gmail did not work", Toast.LENGTH_LONG).show();
            Log.i("MainActivity", "Was not able to log into Gmail");

            findViewById(R.id.progress_bar).setVisibility(View.GONE);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    public void MoveToFirstScreen(){

        Intent intent = new Intent(getApplicationContext(), FirstScreen.class);
        startActivity(intent);

        findViewById(R.id.progress_bar).setVisibility(View.GONE);
    }



}
