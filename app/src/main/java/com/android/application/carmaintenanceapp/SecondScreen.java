package com.android.application.carmaintenanceapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class SecondScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_screen);

        //get LoadedPerson object from log in screen
        Intent i = getIntent();
        MainActivity.Car clickedCar = (MainActivity.Car) i.getSerializableExtra("clickedCar");

        //Get mileage for odometer display
        TextView sixthDigit = (TextView) findViewById(R.id.odometer6);
        TextView fifthDigit = (TextView) findViewById(R.id.odometer5);
        TextView fourthDigit = (TextView) findViewById(R.id.odometer4);
        TextView thirdDigit = (TextView) findViewById(R.id.odometer3);
        TextView secondDigit = (TextView) findViewById(R.id.odometer2);
        TextView firstDigit = (TextView) findViewById(R.id.odometer1);
        String currentMileage = clickedCar.getCurrent_mileage();
        //NOTE!!!!!!!!!!!!!!!!!!!!
        //**********NEED TO PUT IF/ELSE STATEMENTS TO CHECK FOR DIGITS THAT ARE 0!!!!!!!!1
        //OTHERWISE YOU WILL GET NEGATIVE VALUES IN SOME TEXTVIEWS!!!!!!!!!!
        int curDigit = Integer.parseInt(currentMileage);
        int parsedDigit = curDigit % 10; //first digit
        firstDigit.setText(Integer.toString(parsedDigit));
        curDigit = (curDigit - parsedDigit)/10; //move to second digit
        parsedDigit = curDigit % 10; //second digit
        secondDigit.setText(Integer.toString(parsedDigit));
        curDigit = (curDigit - parsedDigit)/10; //move to third digit
        parsedDigit = curDigit % 10; //third digit
        thirdDigit.setText(Integer.toString(parsedDigit));
        curDigit = (curDigit - parsedDigit)/10; //move to fourth digit
        parsedDigit = curDigit % 10; //fourth digit
        fourthDigit.setText(Integer.toString(parsedDigit));
        curDigit = (curDigit - parsedDigit)/10; //move to fifth digit
        parsedDigit = curDigit % 10; //fifth digit
        fifthDigit.setText(Integer.toString(parsedDigit));
        curDigit = (curDigit - parsedDigit)/10; //move to sixth digit
        parsedDigit = curDigit % 10; //sixth digit
        sixthDigit.setText(Integer.toString(parsedDigit));

        //get expenses for expenses list and upcoming maintenance issues for upcoming maintenance issues list
        ArrayList<String> expensesList = clickedCar.getType_of_expenses();
        ArrayList<String> upcomingMaintenanceIssuesList = clickedCar.getUpcomingMaintenanceIssues();

        //Populate the upcoming maintenance issues list
        ListView secondScreenIssuesList = (ListView) findViewById(R.id.secondScreenIssuesList);
        ArrayAdapter<String> maintenanceIssuesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, upcomingMaintenanceIssuesList);
        secondScreenIssuesList.setAdapter(maintenanceIssuesAdapter);
        maintenanceIssuesAdapter.notifyDataSetChanged();

        //Populate the expenses list
        ListView secondScreenExpensesList = (ListView) findViewById(R.id.secondScreenExpensesList);
        ArrayAdapter<String> expensesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, expensesList);
        secondScreenExpensesList.setAdapter(expensesAdapter);
        expensesAdapter.notifyDataSetChanged();
    }
}
