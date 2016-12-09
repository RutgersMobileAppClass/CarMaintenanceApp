package com.android.application.carmaintenanceapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Array;
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


    static LoadedPerson current_user;


    public static class LoadedPerson implements Serializable {

        private String username;
        private String email;
        private ArrayList<Car> cars;


        public LoadedPerson (String _email){
            this.email = _email;
            cars = new ArrayList<Car>();
        }

        public LoadedPerson() {}

        public String getUsername(){
            return this.username;
        }
        public String getEmail(){
            return  email;
        }
        public ArrayList<Car> getCars() {return cars;}

        public void setUsername(String _username) {this.username = _username;}
        public void setEmail(String _email) {this.email = _email;}
        public void setCars(ArrayList<Car> _cars) {this.cars = _cars;}
        public void addCar(Car _car){
            cars.add(_car);
        }

        public static class Car implements Serializable{
            private String current_mileage;
            private String starting_mileage;
            private String miles_since_last_tire_change;
            private String miles_since_last_tire_rotation;
            private String miles_since_last_oil_change;
            private String total_expenses;
            //private ArrayList<String> type_of_expenses;
            //private ArrayList<String> expenses;
            private String initial_investment;    // How much you payed for the car
            private String date_of_last_inspection;
            private String car_name;

            //YO MITCH I MADE A FAKE "estimatedValue" ATTRIBUTE AND A FUNCTION TO GET AND SET IT
            //WE WE WILL USE THIS UNTIL WE GET THE CARS.COM API
            private String estimatedValue;
            public String getEstimatedValue() { return estimatedValue; }
            public void setEstimatedValue(String _estimatedValue) {
                this.estimatedValue = _estimatedValue;
            }

            public String getMiles_since_last_tire_change() {return miles_since_last_tire_change;}
            public String getMiles_since_last_oil_change() {return miles_since_last_oil_change;}
            public String getMiles_since_last_tire_rotation() {return miles_since_last_tire_rotation;}
            public String getDate_of_last_inspection() {return date_of_last_inspection;}

            public void setDate_of_last_inspection(String date_of_last_inspection) {
                this.date_of_last_inspection = date_of_last_inspection;
            }

            public void setMiles_since_last_oil_change(String miles_since_last_oil_change) {
                this.miles_since_last_oil_change = miles_since_last_oil_change;
            }

            public void setMiles_since_last_tire_change(String miles_since_last_tire_change) {
                this.miles_since_last_tire_change = miles_since_last_tire_change;
            }

            public void setMiles_since_last_tire_rotation(String miles_since_last_tire_rotation) {
                this.miles_since_last_tire_rotation = miles_since_last_tire_rotation;
            }

            //YO MITCH MAKING AN ARRAY OF MAINTENANCE ISSUES THAT ARE COMING UP FOR SECOND SCREEN
            private ArrayList<maintenanceIssue> upcoming_maintenance_issues = new ArrayList<maintenanceIssue>();
            public ArrayList<maintenanceIssue> getUpcomingMaintenanceIssues() {
                return upcoming_maintenance_issues;
            }

            //MAKING AN ARRAY OF EXPENSES FOR SECOND SCREEN
            private ArrayList<Expense> history_of_expenses = new ArrayList<Expense>();
            public ArrayList<Expense> getHistory_of_expenses() {
                return history_of_expenses;
            }

            public void setHistory_of_expenses(ArrayList<Expense> history_of_expenses) {
                this.history_of_expenses = history_of_expenses;
            }

            public void setUpcoming_maintenance_issues(ArrayList<maintenanceIssue> upcoming_maintenance_issues) {
                this.upcoming_maintenance_issues = upcoming_maintenance_issues;
            }

            public ArrayList<maintenanceIssue> getUpcoming_maintenance_issues() {
                return upcoming_maintenance_issues;
            }

            public void Car(){
                history_of_expenses = new ArrayList<Car.Expense>();
                upcoming_maintenance_issues = new ArrayList<Car.maintenanceIssue>();
            };


            public String getCurrent_mileage(){ return current_mileage; }
            public String getStarting_mileage() {return starting_mileage; }
            public String getTotal_expenses() { return total_expenses;}
            public String getInitial_investment() { return initial_investment;}
            public String getDate_of_next_inspection() {return date_of_last_inspection;}
            public String getCar_name() {return car_name;}
            /*public ArrayList<String> getType_of_expenses() { return type_of_expenses; }*/
            /*public ArrayList<String> getExpenses() { return expenses;}*/
            public void setCar_name(String _car_name) {this.car_name = _car_name;}
            public void setCurrent_mileage(String current_mileage) {
                this.current_mileage = current_mileage;
            }
            public void setDate_of_next_inspection(String date_of_next_inspection) {
                this.date_of_last_inspection = date_of_next_inspection;
            }
            public void setInitial_investment(String initial_investment) {
                this.initial_investment = initial_investment;
            }
            public void setStarting_mileage(String starting_mileage) {
                this.starting_mileage = starting_mileage;
            }
            public void setTotal_expenses(String total_expenses) {
                this.total_expenses = total_expenses;
            }

            public void addExpense(Expense ex){
                history_of_expenses.add(ex);
            }
            /*public void setType_of_expenses(ArrayList<String> type_of_expenses) {
                this.type_of_expenses = type_of_expenses;
            }*/
            /*public void setExpenses(ArrayList<String> expenses) {
                this.expenses = expenses;
            }*/




            public static class Fix implements Serializable {
                private String Company;
                private String Location;
                private String Price;
                private String Distance;
                //private String fixDate;

                public String getCompany() {
                    return Company;
                }
                public String getLocation() {
                    return Location;
                }
                public String getPrice() {
                    return Price;
                }
                public String getDistance() {
                    return Distance;
                }
                /*public String getFixDate() { return fixDate; }*/

                public void setCompany(String company) { this.Company = company; }
                public void setLocation(String location) { this.Location = location; }
                public void setPrice(String price) { this.Price = price; }
                public void setDistance(String distance) { this.Distance = distance; }
                /*public void setFixDate(String date) { this.fixDate = date; }*/

                Fix() {}

                //TEST FIX CONSTRUCTOR
                Fix(String company, String location, String price, String distance) {
                    this.Company = company;
                    this.Location = location;
                    this.Price = price;
                    this.Distance = distance;
                }
            }

            public static class Expense implements  Serializable {
                private String nameOfExpense;
                private String dateOfExpense;
                private String priceOfExpense;
                private String companyOfExpense;
                private String locationOfExpense;

                public String getNameOfExpense() { return nameOfExpense; }
                public String getDateOfExpense() { return dateOfExpense; }
                public String getPriceOfExpense() { return priceOfExpense; }
                public String getCompanyOfExpense() { return companyOfExpense; }
                public String getLocationOfExpense() { return locationOfExpense; }

                public void setNameOfExpense(String name) { this.nameOfExpense = name; }
                public void setDateOfExpense(String date) { this.dateOfExpense = date; }
                public void setPriceOfExpense(String price) { this.priceOfExpense = price; }
                public void setCompanyOfExpense(String company) { this.companyOfExpense = company; }
                public void setLocationOfExpense(String location) { this.locationOfExpense = location; }

                Expense() {}

                //TEST EXPENSE CONSTRUCTOR
                Expense(String name, String date, String price, String company, String location) {
                    this.nameOfExpense = name;
                    this.dateOfExpense = date;
                    this.priceOfExpense = price;
                    this.companyOfExpense = company;
                    this.locationOfExpense = location;
                }
            }


            public static class maintenanceIssue implements Serializable {
                private String nameOfIssue;
                private ArrayList<Fix> possibleFixes = new ArrayList<Fix>();
                private String inspectionDateMain;

                public String getNameOfIssue() {
                    return nameOfIssue;
                }

                public void setNameOfIssue(String nameOfIssue) {
                    this.nameOfIssue = nameOfIssue;
                }

                public ArrayList<Fix> getListOfPossibleFixes() {
                    return possibleFixes;
                }

                public void setPossibleFixes(ArrayList<Fix> fix_array) { this.possibleFixes = fix_array; }

                public void setInspectionDateMain(String inspectionDateMain) {
                    this.inspectionDateMain = inspectionDateMain;
                }

                public ArrayList<Fix> getPossibleFixes() {
                    return possibleFixes;
                }

                public String getInspectionDateMain() {
                    return inspectionDateMain;
                }

                //TEST MAINTENANCE ISSUE CONSTRUCTOR
                maintenanceIssue() {
                    possibleFixes = new ArrayList<Fix>();
                }

            }
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

        findViewById(R.id.progress_bar).setVisibility(View.GONE);
        ShutOffButtons = false;

        //////////////// Firebase code
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {

                    SharedPreferences.Editor editor = file.edit();
                    editor.putString("recentUserName", user.getEmail());
                    editor.apply();

                    // User is signed in
                    MoveToFirstScreen(user.getEmail());
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
                            LoginManager.getInstance().logOut();

                            SharedPreferences.Editor editor = file.edit();
                            editor.putString("recentUserName", "");
                            editor.apply();

                            MoveToFirstScreen(object.getString("email"));
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
                    public void onConnectionFailed( ConnectionResult connectionResult) {
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
                            public void onComplete( Task<AuthResult> task) {
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

                                else {
                                    MoveToFirstScreen(username);
                                }

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
                            public void onComplete( Task<AuthResult> task) {
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

                                else {
                                    MoveToFirstScreen(username);
                                }

                                // ...
                            }
                        });
            }
            else {
                findViewById(R.id.progress_bar).setVisibility(View.GONE);
                ShutOffButtons = false;
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

            MoveToFirstScreen(acct.getEmail());
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

    public void MoveToFirstScreen(String email){
        String[] split = email.split("[@._]");
        final String named_email = split[0] + "-" + split[1] + "-" + split[2];


        Log.i("MainActivity", "The email passed to the change screen function was :" + named_email);


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference(named_email);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                LoadedPerson person;
                if (dataSnapshot.exists()){
                    // Get everything from the person
                    person = dataSnapshot.getValue(LoadedPerson.class);
                    System.out.println("We found the user in our database");
                } else {
                    // Make a new person in database
                    person = new LoadedPerson(named_email);
                    myRef.setValue(current_user);
                    System.out.println("We didnt find the user in our database");
                }

                Intent intent = new Intent(getApplicationContext(), FirstScreen.class);
                intent.putExtra("LoadedPerson", person);
                startActivity(intent);

                findViewById(R.id.progress_bar).setVisibility(View.GONE);

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                System.out.println("The read failed: " + databaseError.getCode());
            }
        });

    }

    public void saveToFireBase(LoadedPerson _person){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef;

        try {
            String[] split = _person.getEmail().split("[@._]");
            final String named_email = split[0] + "-" + split[1] + "-" + split[2];
            myRef = database.getReference(named_email);
        } catch(Exception e){
            System.out.println("The email is already formatted");
            myRef = database.getReference(_person.getEmail());
        }


        myRef.setValue(_person);
    }
}
