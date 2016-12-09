package com.android.application.carmaintenanceapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class FourthScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fourth_screen);

        Intent i = getIntent();
        int indexOfIssue = i.getIntExtra("indexOfExpense", 0);
        int indexOfClickedCar = i.getIntExtra("indexOfClickedCar", 0);
        MainActivity.LoadedPerson loadedPerson = (MainActivity.LoadedPerson) i.getSerializableExtra("loadedPerson");


        MainActivity.LoadedPerson.Car clickedCar = loadedPerson.getCars().get(indexOfClickedCar);
        MainActivity.LoadedPerson.Car.Expense clickedExpense = clickedCar.getHistory_of_expenses().get(indexOfIssue);

        TextView expenseName = (TextView) findViewById(R.id.expense_name);
        TextView expensePrice = (TextView) findViewById(R.id.expense_price);
        TextView expenseDate = (TextView) findViewById(R.id.date_of_expense);
        TextView expenseCompany = (TextView) findViewById(R.id.company_of_expense);
        TextView expenseLocation = (TextView) findViewById(R.id.location_of_expense);

        String name_of_expense = clickedExpense.getNameOfExpense();
        String price_of_expense = clickedExpense.getPriceOfExpense();
        String date_of_expense = clickedExpense.getDateOfExpense();
        String company_of_expense = clickedExpense.getCompanyOfExpense();
        String location_of_expense = clickedExpense.getLocationOfExpense();

        expenseName.setText(name_of_expense);
        expensePrice.setText(price_of_expense);
        expenseDate.setText(date_of_expense);
        expenseCompany.setText(company_of_expense);
        expenseLocation.setText(location_of_expense);
    }
}
