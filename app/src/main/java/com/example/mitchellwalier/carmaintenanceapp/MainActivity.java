package com.example.mitchellwalier.carmaintenanceapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    EditText passwordEditText;
    EditText userNameEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // See if "Remeber Me" Check box is checked, if it was than log person in

        // Else, this
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        userNameEditText = (EditText) findViewById(R.id.userNameEditText);

        CheckBox checkBox = (CheckBox) findViewById(R.id.checkBox);
        checkBox.setChecked(true);

    }

    public void LogInPerson(View view) {

        String username = userNameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if(username.length() < 1 && password.length() < 1){
            Toast.makeText(this, "Invalid UserName", Toast.LENGTH_LONG);

        } else {
            // Log into firebase

            // If not a person, or wrong password, don't start intent, make toast

            // Start intent, save to shared preferences if the checkbox is checked
        }
    }

    // Create onlclick method for login with facebook, need to register app with them
    // https://code.tutsplus.com/tutorials/quick-tip-add-facebook-login-to-your-android-app--cms-23837




}
