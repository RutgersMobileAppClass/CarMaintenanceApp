package com.android.application.carmaintenanceapp;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class FirstScreen extends AppCompatActivity {

    private ArrayList<replaceWithCarClass> information_array;
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

    public class listAdapter extends ArrayAdapter<replaceWithCarClass> {
        public listAdapter(Context context, ArrayList<replaceWithCarClass> listItems) {
            super(context, 0, listItems);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            replaceWithCarClass car = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.car_list_layout, parent, false);
            }

            TextView car_name = (TextView) convertView.findViewById(R.id.car_name);
            TextView car_value = (TextView) convertView.findViewById(R.id.car_value);
            TextView car_expenses = (TextView) convertView.findViewById(R.id.car_expenses);

            car_name.setText(car.carName);
            car_value.setText(car.estimatedVal);
            car_expenses.setText(car.expenses);

            return convertView;
        }

    }

    String TAG = "First Screen";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_screen);


        carList = (ListView) findViewById(R.id.carList);
        //INSTEAD OF MAKING A NEW CAR ARRAY HERE, WE WILL LOAD IT IN FROM INTENT DATA
        replaceWithCarClass carItem = new replaceWithCarClass("Tesla", "$1000", "$100000");

        //array of data (Car name, estimated value, expenses)
        information_array = new ArrayList<replaceWithCarClass>();
        information_array.add(carItem);

        information_array_adapter = new listAdapter(this, information_array);
        carList.setAdapter(information_array_adapter);
        information_array_adapter.notifyDataSetChanged();

        /*
        //inflate GridView
        GridView gridView = (GridView) findViewById(R.id.carList);
        information_array_adapter = new MyAdapter(this, information_array);
        gridView.setAdapter(information_array_adapter);
        */
    }

    //TEST FUNCTION TO POPULATE LIST
    public void addCar(View view) {
        replaceWithCarClass testCar = new replaceWithCarClass("test car", "$232", "$1524");
        information_array.add(testCar);
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
