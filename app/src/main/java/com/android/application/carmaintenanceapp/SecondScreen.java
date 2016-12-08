package com.android.application.carmaintenanceapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
        /*Intent i = getIntent();                                                                       *********COMMENTED FOR NEW IMPLEMENTATION*************
        final MainActivity.Car clickedCar = (MainActivity.Car) i.getSerializableExtra("clickedCar");*/
        Intent i = getIntent();
        ArrayList<String> issuesList = i.getStringArrayListExtra("issuesList");
        ArrayList<String> expensesList = i.getStringArrayListExtra("expensesList");
        String currentMileage = i.getStringExtra("currentMileage");


        //Get mileage for odometer display
        TextView sixthDigit = (TextView) findViewById(R.id.odometer6);
        TextView fifthDigit = (TextView) findViewById(R.id.odometer5);
        TextView fourthDigit = (TextView) findViewById(R.id.odometer4);
        TextView thirdDigit = (TextView) findViewById(R.id.odometer3);
        TextView secondDigit = (TextView) findViewById(R.id.odometer2);
        TextView firstDigit = (TextView) findViewById(R.id.odometer1);
        //String currentMileage = clickedCar.getCurrent_mileage();                                  *********COMMENTED FOR NEW IMPLEMENTATION*************
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
        /*ArrayList<String> expensesList = clickedCar.getType_of_expenses();                                                        *********COMMENTED FOR NEW IMPLEMENTATION*************
        ArrayList<MainActivity.maintenanceIssue> upcomingMaintenanceIssuesList = clickedCar.getUpcomingMaintenanceIssues();
        //ArrayList<String> upcomingMaintenanceIssuesList = clickedCar.getUpcomingMaintenanceIssues();
        ArrayList<String> issuesList = new ArrayList<>();
        for (int x = 0; x < upcomingMaintenanceIssuesList.size(); x++) {
            issuesList.add(upcomingMaintenanceIssuesList.get(x).getNameOfIssue());
        }*/

        //Populate the upcoming maintenance issues list
        ListView secondScreenIssuesList = (ListView) findViewById(R.id.secondScreenIssuesList);
        ArrayAdapter<String> maintenanceIssuesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, issuesList);
        secondScreenIssuesList.setAdapter(maintenanceIssuesAdapter);
        maintenanceIssuesAdapter.notifyDataSetChanged();

        //Populate the expenses list
        ListView secondScreenExpensesList = (ListView) findViewById(R.id.secondScreenExpensesList);
        ArrayAdapter<String> expensesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, expensesList);
        secondScreenExpensesList.setAdapter(expensesAdapter);
        expensesAdapter.notifyDataSetChanged();

        //onClickListener for upcoming maintenance issues list
        //want to move to third screen and pass issue, and array of possible price, company, location, and distance of fix
        secondScreenIssuesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //pass the array of possible fixes...since its annoying to pass the array itself, lets just pass the index that we need along with the clicked car object
                /*int index = position;                                                                 *********COMMENTED FOR NEW IMPLEMENTATION*************
                Intent intent = new Intent(getApplicationContext(), ThirdScreen.class);
                intent.putExtra("index", index);
                intent.putExtra("clickedCar", clickedCar);
                startActivity(intent);*/

                Intent intent = new Intent(getApplicationContext(), ThirdScreen.class);         //assuming we are gonnna get these values from firebase?
                ArrayList<String> fixCompany = new ArrayList<String>();
                ArrayList<String> fixLocation = new ArrayList<String>();
                ArrayList<String> fixPrice = new ArrayList<String>();
                ArrayList<String> fixDistance = new ArrayList<String>();
                fixCompany.add("Midas");
                fixCompany.add("Mitchell's Auto Shop");
                fixCompany.add("Costco");
                fixLocation.add("25 Poop Street");
                fixLocation.add("23 Hardenberg Street");
                fixLocation.add("69 Box Ave");
                fixPrice.add("$300");
                fixPrice.add("$2500");
                fixPrice.add("$6969.69");
                fixDistance.add("2 miles");
                fixDistance.add("94 miles");
                fixDistance.add("69 kilometers");
                intent.putStringArrayListExtra("fixCompany", fixCompany);
                intent.putStringArrayListExtra("fixLocation", fixLocation);
                intent.putStringArrayListExtra("fixPrice", fixPrice);
                intent.putStringArrayListExtra("fixDistance", fixDistance);
                startActivity(intent);

                /*String upcomingIssue = clickedCar.getUpcomingMaintenanceIssues().get(position);
                String fixPrice = clickedCar.getFixPrice().get(position);
                String fixCompany = clickedCar.getFixCompany().get(position);
                String fixLocation = clickedCar.getFixLocation().get(position);
                String fixDistance = clickedCar.getFixDistance().get(position);

                Intent intent = new Intent(getApplicationContext(), ThirdScreen.class);
                intent.putExtra("upcomingIssue", upcomingIssue);
                intent.putExtra("fixPrice", fixPrice);
                intent.putExtra("fixCompany", fixCompany);
                intent.putExtra("fixLocation", fixLocation);
                intent.putExtra("fixDistance", fixDistance);
                startActivity(intent);*/
            }
        });

        //onClickListener for expenses list
        //want to move to third screen and pass car object
    }
}
