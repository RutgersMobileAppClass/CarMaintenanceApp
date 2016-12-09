package com.android.application.carmaintenanceapp;

import android.app.Activity;
import android.content.Intent;
import android.icu.text.DateFormat;
import android.net.ParseException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.Date;

public class AddNewCar extends AppCompatActivity {
    EditText car_model;
    EditText inspection_date;
    EditText tire_change;
    EditText tire_rotation;
    EditText oil_change;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_car);

        car_model = (EditText) findViewById(R.id.car_model_EditText);
        inspection_date = (EditText) findViewById(R.id.Next_inspection_EditText);
        tire_change = (EditText) findViewById(R.id.last_tire_change);
        tire_rotation = (EditText) findViewById(R.id.last_rotation_EditText);
        oil_change = (EditText) findViewById(R.id.last_oil_change);


    }

    public void FinishNAndCar(View view){
        MainActivity.LoadedPerson.Car new_car = new MainActivity.LoadedPerson.Car();

        new_car.setCar_name(car_model.getText().toString().trim());
        new_car.setDate_of_next_inspection(inspection_date.getText().toString().trim());
        new_car.setMiles_since_last_tire_change(tire_change.getText().toString().trim());
        new_car.setMiles_since_last_tire_rotation(tire_rotation.getText().toString().trim());
        new_car.setMiles_since_last_oil_change(oil_change.getText().toString().trim());
        new_car.setCurrent_mileage("000000");


        Intent i = getIntent();
        i.putExtra("Updated Person", new_car);
        setResult(Activity.RESULT_OK, i);
        System.out.println("Activity resilt is okay");
        finish();

    }
}
