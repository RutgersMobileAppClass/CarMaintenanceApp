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

    public class expensesListAdapter extends ArrayAdapter<MainActivity.LoadedPerson.Car.Expense> {
        public expensesListAdapter(Context context, ArrayList<MainActivity.LoadedPerson.Car.Expense> listItems) {
            super(context, 0, listItems);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MainActivity.LoadedPerson.Car.Expense expenseItem = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.expenses_list_layout, parent, false);
            }

            TextView expenseName = (TextView) convertView.findViewById(R.id.expense_name);
            TextView expensePrice = (TextView) convertView.findViewById(R.id.price_of_expense);

            expenseName.setText(expenseItem.getNameOfExpense());
            expensePrice.setText(expenseItem.getPriceOfExpense());

            return convertView;
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second_screen);

        //get LoadedPerson object from log in screen
        Intent i = getIntent();
        final MainActivity.LoadedPerson loadedPerson = (MainActivity.LoadedPerson) i.getSerializableExtra("LoadedPerson");
        final int index = i.getIntExtra("index", 0);
        final MainActivity.LoadedPerson.Car clickedCar = loadedPerson.getCars().get(index);

        /*Intent i = getIntent();                                                                       ****************COMMENTED - STRING ONLY IMPLEMENTATION*******************
        ArrayList<String> issuesList = i.getStringArrayListExtra("issuesList");
        ArrayList<String> expensesList = i.getStringArrayListExtra("expensesList");
        String currentMileage = i.getStringExtra("currentMileage");*/


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
        ArrayList<MainActivity.LoadedPerson.Car.Expense> expensesList = clickedCar.getHistory_of_expenses();
        ArrayList<MainActivity.LoadedPerson.Car.maintenanceIssue> upcomingMaintenanceIssuesList = clickedCar.getUpcomingMaintenanceIssues();
        //ArrayList<String> upcomingMaintenanceIssuesList = clickedCar.getUpcomingMaintenanceIssues();
        ArrayList<String> issuesList = new ArrayList<>();
        for (int x = 0; x < upcomingMaintenanceIssuesList.size(); x++) {
            issuesList.add(upcomingMaintenanceIssuesList.get(x).getNameOfIssue());
        }

        //Populate the upcoming maintenance issues list
        ListView secondScreenIssuesList = (ListView) findViewById(R.id.secondScreenIssuesList);
        ArrayAdapter<String> maintenanceIssuesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, issuesList);
        secondScreenIssuesList.setAdapter(maintenanceIssuesAdapter);
        maintenanceIssuesAdapter.notifyDataSetChanged();

        //Populate the expenses list
        ListView secondScreenExpensesList = (ListView) findViewById(R.id.secondScreenExpensesList);
        ArrayAdapter<MainActivity.LoadedPerson.Car.Expense> expensesAdapter = new expensesListAdapter(this, expensesList);
        secondScreenExpensesList.setAdapter(expensesAdapter);
        expensesAdapter.notifyDataSetChanged();

        //onClickListener for expenses list
        //want to move to fourth screen and pass car object
        secondScreenExpensesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //INSTEAD OF PASSING EXPENSES OBJECT, PASS LOADED PERSON AND INDEX OF CLICKED CAR AND INDEX OF EXPENSE IN EXPENSES LIST TO ACCESS EXPENSE
                int indexOfExpense = position;
                int indexOfClickedCar = index;
                Intent intent = new Intent(getApplicationContext(), FourthScreen.class);
                intent.putExtra("indexOfExpense", indexOfExpense);
                intent.putExtra("indexOfClickedCar", indexOfClickedCar);
                intent.putExtra("loadedPerson", loadedPerson);
                startActivity(intent);

            }
        });


        //onClickListener for upcoming maintenance issues list
        //want to move to third screen and pass issue, and array of possible price, company, location, and distance of fix
        secondScreenIssuesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //pass the array of possible fixes...since its annoying to pass the array itself, lets just pass the index that we need along with the clicked car object
                //INSTEAD OF PASSING THE CLICKED CAR OBJECT, PASS THE LOADED PERSON, THE INDEX OF THE CLICKED CAR IN CAR LIST, AND INDEX OF MAINTENANCE ISSUE IN ISSUE LIST TO ACCESS ISSUE
                int indexOfIssue = position;
                int indexOfClickedCar = index;
                Intent intent = new Intent(getApplicationContext(), ThirdScreen.class);
                intent.putExtra("indexOfIssue", indexOfIssue);
                intent.putExtra("indexOfClickedCar", indexOfClickedCar);
                intent.putExtra("loadedPerson", loadedPerson);
                //intent.putExtra("clickedCar", clickedCar);
                startActivity(intent);

                /*Intent intent = new Intent(getApplicationContext(), ThirdScreen.class);         //assuming we are gonnna get these values from firebase?
                ArrayList<String> fixCompany = new ArrayList<String>();                             ****************COMMENTED - STRING ONLY IMPLEMENTATION*******************
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
                startActivity(intent);*/

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


    }
}
