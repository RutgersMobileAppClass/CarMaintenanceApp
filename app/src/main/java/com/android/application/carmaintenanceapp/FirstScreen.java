package com.android.application.carmaintenanceapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class FirstScreen extends AppCompatActivity {

    private ArrayList<MainActivity.Car> information_array;
    private listAdapter information_array_adapter;
    private ListView carList;

    public class replaceWithPersonClass {
        private ArrayList<replaceWithCarClass> carList;

        public replaceWithPersonClass(ArrayList<replaceWithCarClass> carList) {
            this.carList = carList;
        }

        public void addCar(String carName, String estimatedVal, String expenses) {
            replaceWithCarClass newCar = new replaceWithCarClass(carName, estimatedVal, expenses);
            this.carList.add(newCar);
        }
    }

    public class replaceWithCarClass {
        private String carName;
        private String estimatedVal;
        private String expenses;

        public replaceWithCarClass(String carName, String estimatedVal, String expenses) {
            this.carName = carName;
            this.estimatedVal = estimatedVal;
            this.expenses = expenses;
        }
    }

    public class listAdapter extends ArrayAdapter<MainActivity.Car> {
        public listAdapter(Context context, ArrayList<MainActivity.Car> listItems) {
            super(context, 0, listItems);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MainActivity.Car car = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.car_list_layout, parent, false);
            }

            TextView car_name = (TextView) convertView.findViewById(R.id.car_name);
            TextView car_value = (TextView) convertView.findViewById(R.id.car_value);
            TextView car_expenses = (TextView) convertView.findViewById(R.id.car_expenses);

            car_name.setText(car.getCar_name());
            car_value.setText(car.getEstimatedValue());
            car_expenses.setText(car.getTotal_expenses());

            return convertView;
        }

    }

    String TAG = "First Screen";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_screen);

        carList = (ListView) findViewById(R.id.carList);

        //get LoadedPerson object from log in screen
        Intent i = getIntent();
        MainActivity.LoadedPerson person = (MainActivity.LoadedPerson) i.getSerializableExtra("LoadedPerson");

        //array of data (Car name, estimated value, expenses), get from LoadedPerson object that was passed in from log in screen
        information_array = new ArrayList<MainActivity.Car>();
        information_array = person.getCars();

        //array adapter stuff
        information_array_adapter = new listAdapter(this, information_array);
        carList.setAdapter(information_array_adapter);

        information_array_adapter.notifyDataSetChanged();

        //onClickListener for car list
        //when list item is clicked we want to go to the second screen and pull up details about the car that was clicked
        carList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MainActivity.Car clickedCar = information_array.get(position);
                Intent intent = new Intent(getApplicationContext(), SecondScreen.class);
                intent.putExtra("clickedCar", clickedCar);
                startActivity(intent);
            }
        });

        //onLongClickListener for car list
        //delete the car from the car list on a long click
        carList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                information_array.remove(position);
                information_array_adapter.notifyDataSetChanged();
                return true;
            }
        });

    }

    //Add a car to the list
    //make a new Car object and put into the array, and notify the list adapter
    public void addCar(View view) {
        EditText carNameEntry = (EditText) findViewById(R.id.carNameEntry);
        EditText expensesEntry = (EditText) findViewById(R.id.expensesEntry);
        String car_name = carNameEntry.getText().toString();
        String expenses = expensesEntry.getText().toString();
        MainActivity.Car newCar = new MainActivity.Car();
        newCar.setCar_name(car_name);
        newCar.setTotal_expenses(expenses);
        //CHANGE SO THAT ESTIMATED VALUE IS PULLED FROM CARS.COM INSTEAD OF BEING SET HERE
        newCar.setEstimatedValue("test value");
        information_array.add(newCar);
        information_array_adapter.notifyDataSetChanged();
    }


    public void onBackPressed() {

        new AlertDialog.Builder(this)
                .setTitle("Log Out")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        // Sign out of the three possible ways
                        try {
                            FirebaseAuth.getInstance().signOut();
                        } catch(Exception e) {
                        }
                        try {
                            LoginManager.getInstance().logOut();
                        } catch(Exception e) {
                        }

                        try { revokeAccess(); }
                        catch(Exception e) {
                        }


                        finish();
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

    }


    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(MainActivity.mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // ...
                    }
                });
    }
}
