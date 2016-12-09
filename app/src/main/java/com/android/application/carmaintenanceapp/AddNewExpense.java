package com.android.application.carmaintenanceapp;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class AddNewExpense extends AppCompatActivity {

    EditText name;
    EditText price;
    EditText date;
    EditText company;
    EditText addres;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_expense);

        name = (EditText) findViewById(R.id.name_of_expense_EditText);
        price = (EditText) findViewById(R.id.price_EditText);
        date = (EditText) findViewById(R.id.date_of_expense_EditText);
        company = (EditText) findViewById(R.id.company_of_expense_EditText);
        addres = (EditText) findViewById(R.id.address);

    }


    public void AddNewExpenseFunction(View view){

        MainActivity.LoadedPerson.Car.Expense ex = new MainActivity.LoadedPerson.Car.Expense(
                name.getText().toString().trim(),
                date.getText().toString().trim(),
                price.getText().toString().trim(),
                company.getText().toString().trim(),
                addres.getText().toString().trim());




        Intent i = getIntent();
        i.putExtra("Expense", ex);
        setResult(Activity.RESULT_OK, i);
        System.out.println("Activity resilt is okay");
        finish();
    }
}
