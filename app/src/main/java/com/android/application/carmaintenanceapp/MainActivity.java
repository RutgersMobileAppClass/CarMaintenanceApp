package com.android.application.carmaintenanceapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import com.facebook.FacebookSdk;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends FragmentActivity {

    EditText passwordEditText;
    EditText userNameEditText;
    String recentUserName;
    static SharedPreferences file;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    Boolean rememberMeisChecked;
    FirebaseAuth mAuth;
    FirebaseAuth.AuthStateListener mAuthListener;
    GoogleApiClient mGoogleApiClient;
    String google_client_id = "577942935082-u4srtria118c8pukqn7bdkvuffvjvv42.apps.googleusercontent.com";
    CallbackManager callbackManager;
    LoginButton login_button;

    // ADD LOADING ANIMATION
    // ADD FACEBOOK LOGIN
    // ADD GMAIL LOGIN
    // FIGURE OUT WHAT NEEDS TO BE IN SERVICE AND WHAT NEEDS TO BE IN ASYNCTASK!!


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
                    Intent intent = new Intent(getApplicationContext(), FirstScreen.class);
                    startActivity(intent);

                    Log.d("MainActivity", "onAuthStateChanged:signed_in:" + user.getUid());
                    Toast.makeText(getApplicationContext(), "Entered the next intent", Toast.LENGTH_LONG).show();

                } else {
                    // User is signed out
                    Log.d("MainActivity", "onAuthStateChanged:signed_out");
                    Toast.makeText(getApplicationContext(), "Signed out the user", Toast.LENGTH_LONG).show();

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
                //login_button.setVisibility(View.GONE);

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

                            Intent intent = new Intent(getApplicationContext(), FirstScreen.class);
                            startActivity(intent);

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

            }

            @Override
            public void onError(FacebookException exception)
            {
                Log.i("Facebook Login" , "There was an error " + exception.toString());
            }
        });

    }

    public void LogInPerson(View view) {

        final String username = userNameEditText.getText().toString().trim();
        final String password = passwordEditText.getText().toString().trim();

        passwordEditText.setText("");

        // Log the person in
        if(checkIfValidInputs(username, password)) {
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
                                Log.i("MainActivity", "We did not find the person\n Username: " + username  + "\nPassword: " + password);

                            }

                            Intent intent = new Intent(getApplicationContext(), FirstScreen.class);
                            startActivity(intent);

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

    // TEST THIS!!!! 
    public void LoginToGmail(View view){
        Log.i("MainActivity", "Logged into Gmail");

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestServerAuthCode(google_client_id, false)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        // connection failed, should be handled
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, 101);

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

                            // SHOULD PUT A LOADING ANIMATION

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

            Intent intent = new Intent(getApplicationContext(), FirstScreen.class);
            startActivity(intent);
            // Signed in successfully, show authenticated UI.
            //GoogleSignInAccount acct = result.getSignInAccount();
            //mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));
            //updateUI(true);
        } else {

            Toast.makeText(getApplicationContext(), "Gmail did not work", Toast.LENGTH_LONG).show();
            Log.i("MainActivity", "Was not able to log into Gmail");
            // Signed out, show unauthenticated UI.
            //updateUI(false);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }


}
