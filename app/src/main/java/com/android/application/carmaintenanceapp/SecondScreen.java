package com.android.application.carmaintenanceapp;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class SecondScreen extends AppCompatActivity {

    String inspection_Date;

   /* public class maintenanceListAdapter extends ArrayAdapter<MainActivity.LoadedPerson.Car.maintenanceIssue> {
        public maintenanceListAdapter(Context context, ArrayList<MainActivity.LoadedPerson.Car.maintenanceIssue> listItems) {
            super(context, 0, listItems);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MainActivity.LoadedPerson.Car.maintenanceIssue maintenanceIssue = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.third_screen_list_layout, parent, false);
            }

            TextView issueName = (TextView) convertView.findViewById(R.id.issueName);
            TextView dateMileage = (TextView) convertView.findViewById(R.id.dateMileage);


            issueName.setText(maintenanceIssue.getNameOfIssue());

            if (maintenanceIssue.getNameOfIssue() == "Inspection") {
                dateMileage.setText(maintenanceIssue.getInspectionDate());
            }

            else if (maintenanceIssue.getNameOfIssue() == "Rotate Tires") {
                int milesOfRotate = 25000 - Integer.parseInt(maintenanceIssue.getMilesSinceLasteTireRotation());
                String mileageOfRotate = Integer.toString(milesOfRotate);
                dateMileage.setText(mileageOfRotate);
            }

            else if (maintenanceIssue.getNameOfIssue() == "Change Tires") {
                int milesOfChange = 50000 - Integer.parseInt(maintenanceIssue.getMilesSinceLastTireChange());
                String mileageOfChange = Integer.toString(milesOfChange);
                dateMileage.setText(mileageOfChange);
            }

            else if (maintenanceIssue.getNameOfIssue() == "Oil Change") {
                int milesOfOilChange = 10000 - Integer.parseInt(maintenanceIssue.getMilesSinceLastOilChange());
                String mileageOfOilChange = Integer.toString(milesOfOilChange);
                dateMileage.setText(mileageOfOilChange);
            }



            return convertView;
        }

    }*/

    MainActivity.LoadedPerson loadedPerson;
    MainActivity.LoadedPerson.Car clickedCar;
    TextView sixthDigit;
    TextView fifthDigit;
    TextView fourthDigit;
    TextView thirdDigit;
    TextView secondDigit;
    TextView firstDigit;
    EditText adjust_mileage;
    int index;
    ArrayList<MainActivity.LoadedPerson.Car.Expense> expensesList;
    ArrayAdapter<MainActivity.LoadedPerson.Car.Expense> expensesAdapter;

    public void AdjustMileage(View view) {
        String _new = adjust_mileage.getText().toString().trim();

        if(_new.length() != 6){
            Toast.makeText(getApplicationContext(), "Please make the mileage 6 digits long with no spaces",Toast.LENGTH_LONG).show();
        } else {
            clickedCar.setCurrent_mileage(_new);
            setMileageIcon(_new);
            adjust_mileage.setText("");

            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            adjust_mileage.setCursorVisible(false);

            saveToFireBase(loadedPerson);
        }

    }

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

        //Hardcoding the maintenance issue array and fixes just for show
        //each fix will be the same (for now...just for show remember)
        ArrayList<MainActivity.LoadedPerson.Car.Fix> fixList = new ArrayList<>();
        MainActivity.LoadedPerson.Car.Fix fix1 = new MainActivity.LoadedPerson.Car.Fix("Midas", "56 Pinewood Street", "$30", "3.4 miles");
        MainActivity.LoadedPerson.Car.Fix fix2 = new MainActivity.LoadedPerson.Car.Fix("Gradeigh's Auto Shop", "34 Address Avenue", "$45", "13 miles");
        MainActivity.LoadedPerson.Car.Fix fix3 = new MainActivity.LoadedPerson.Car.Fix("Mitch's Crib", "Somewhere in Holmdel", "$0.39", "6.9 miles");
        fixList.add(fix1);
        fixList.add(fix2);
        fixList.add(fix3);

        //will include Oil Change, Inspection, Tire Rotation, Tire Change
        ArrayList<MainActivity.LoadedPerson.Car.maintenanceIssue> issueList = new ArrayList<>();
        MainActivity.LoadedPerson.Car.maintenanceIssue oilChange = new MainActivity.LoadedPerson.Car.maintenanceIssue();
        MainActivity.LoadedPerson.Car.maintenanceIssue inspection = new MainActivity.LoadedPerson.Car.maintenanceIssue();
        MainActivity.LoadedPerson.Car.maintenanceIssue tireRotation = new MainActivity.LoadedPerson.Car.maintenanceIssue();
        MainActivity.LoadedPerson.Car.maintenanceIssue tireChange = new MainActivity.LoadedPerson.Car.maintenanceIssue();

        oilChange.setNameOfIssue("Oil Change");
        oilChange.setPossibleFixes(fixList);
        inspection.setNameOfIssue("Inspection");
        inspection.setPossibleFixes(fixList);
        tireRotation.setNameOfIssue("Rotate Tires");
        tireRotation.setPossibleFixes(fixList);
        tireChange.setNameOfIssue("Change Tires");
        tireChange.setPossibleFixes(fixList);

        issueList.add(inspection);
        issueList.add(tireRotation);
        issueList.add(tireChange);
        issueList.add(oilChange);




        //get LoadedPerson object from log in screen
        Intent i = getIntent();
        loadedPerson = (MainActivity.LoadedPerson) i.getSerializableExtra("LoadedPerson");
        index = i.getIntExtra("index", 0);
        clickedCar = loadedPerson.getCars().get(index);

        //set hardcoded stuff to loaded person
        clickedCar.setUpcoming_maintenance_issues(issueList);

        //Get mileage for odometer display
        sixthDigit = (TextView) findViewById(R.id.odometer6);
        fifthDigit = (TextView) findViewById(R.id.odometer5);
        fourthDigit = (TextView) findViewById(R.id.odometer4);
        thirdDigit = (TextView) findViewById(R.id.odometer3);
        secondDigit = (TextView) findViewById(R.id.odometer2);
        firstDigit = (TextView) findViewById(R.id.odometer1);
        adjust_mileage = (EditText) findViewById(R.id.adjust_mileage);


        setMileageIcon(clickedCar.getCurrent_mileage());


        //get expenses for expenses list and upcoming maintenance issues for upcoming maintenance issues list
        expensesList = clickedCar.getHistory_of_expenses();
        /*
        ArrayList<MainActivity.LoadedPerson.Car.maintenanceIssue> upcomingMaintenanceIssuesList = clickedCar.getUpcomingMaintenanceIssues();
        //ArrayList<String> upcomingMaintenanceIssuesList = clickedCar.getUpcomingMaintenanceIssues();*/
        ArrayList<String> issuesList = new ArrayList<>();
        for (int x = 0; x < issueList.size(); x++) {                //changed this to issueList for hardcoding...change back to upcomingMaintenanceIssuesList later
            issuesList.add(issueList.get(x).getNameOfIssue());
        }

        //Populate the upcoming maintenance issues list
        ListView secondScreenIssuesList = (ListView) findViewById(R.id.secondScreenIssuesList);
        ArrayAdapter<String> maintenanceIssuesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, issuesList);
        //maintenanceListAdapter maintenanceIssuesAdapter = new maintenanceListAdapter(this, issueList);
        secondScreenIssuesList.setAdapter(maintenanceIssuesAdapter);
        //maintenanceIssuesAdapter.notifyDataSetChanged();

        //Populate the expenses list
        ListView secondScreenExpensesList = (ListView) findViewById(R.id.secondScreenExpensesList);
        expensesAdapter = new expensesListAdapter(this, expensesList);
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

        secondScreenExpensesList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                final int temp = position;

                final AlertDialog.Builder box = new AlertDialog.Builder(SecondScreen.this);
                box.setTitle("Remove Car");
                box.setMessage("Are you sure you want to remove this expense?\nAll data associated with this expense will be lost.");
                box.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        //NEED TO ALSO REMOVE FROM FIREBASE DATABASE
                        expensesList.remove(temp);
                        expensesAdapter.notifyDataSetChanged();

                        clickedCar.setHistory_of_expenses(expensesList);
                        saveToFireBase(loadedPerson);

                    }
                });
                box.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                });
                box.setIcon(android.R.drawable.ic_dialog_alert);
                box.show();

                return true;
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

            }
        });
    }

    public void setMileageIcon(String _miles){
        int curDigit = Integer.parseInt(_miles);
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
    }

    public void addExpense(View view){
        Intent intent = new Intent(getApplicationContext(), AddNewExpense.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivityForResult(intent, 10200);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        // Check which request we're responding to
        System.out.println("We made it back to activity");
        if (requestCode == 10200) {
            // Make sure the request was successful
            System.out.println("Inside request Code");
            if (resultCode != Activity.RESULT_CANCELED) {

                System.out.println("Inside result Code");
                MainActivity.LoadedPerson.Car.Expense ex = (MainActivity.LoadedPerson.Car.Expense) data.getSerializableExtra("Expense");

                clickedCar.addExpense(ex);

                System.out.println("We added it to the clicked car class and saved it here");
                saveToFireBase(loadedPerson);
            }
        }
    }

    public void saveToFireBase(MainActivity.LoadedPerson _person){
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
