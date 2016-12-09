package com.android.application.carmaintenanceapp;

import android.app.Activity;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import static com.android.application.carmaintenanceapp.MainActivity.current_user;

public class FirstScreen extends AppCompatActivity {

    private ArrayList<MainActivity.LoadedPerson.Car> information_array;
    private listAdapter information_array_adapter;
    private ListView carList;
    String TAG = "First Screen";
    MainActivity.LoadedPerson person;

    public class listAdapter extends ArrayAdapter<MainActivity.LoadedPerson.Car> {
        public listAdapter(Context context, ArrayList<MainActivity.LoadedPerson.Car> listItems) {
            super(context, 0, listItems);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MainActivity.LoadedPerson.Car car = getItem(position);

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_screen);

        carList = (ListView) findViewById(R.id.carList);

        //get LoadedPerson object from log in screen
        Intent i = getIntent();
        person = (MainActivity.LoadedPerson) i.getSerializableExtra("LoadedPerson");
        //ArrayList<String> car_list = i.getStringArrayListExtra("car_list");                                                   ****************COMMENTED - STRING ONLY IMPLEMENTATION*******************

        /*ArrayAdapter<String> carListAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, car_list);      ****************COMMENTED - STRING ONLY IMPLEMENTATION*******************
        carList.setAdapter(carListAdapter);
        carListAdapter.notifyDataSetChanged();*/


        //array of data (Car name, estimated value, expenses), get from LoadedPerson object that was passed in from log in screen
        information_array = new ArrayList<MainActivity.LoadedPerson.Car>();
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


                //MainActivity.LoadedPerson.Car clickedCar = information_array.get(position);
                Intent intent = new Intent(getApplicationContext(), SecondScreen.class);
                //intent.putExtra("clickedCar", clickedCar);
                //INSTEAD OF PASSING THE CLICKED CAR WE ARE GOING TO PASS THE INDEX OF THE CLICKED CAR IN THE ARRAY AND THE ENTIRE LOADEDPERSON OBJECT
                intent.putExtra("LoadedPerson", person);
                intent.putExtra("index", position);
                startActivity(intent);

            }
        });

        //onLongClickListener for car list
        //delete the car from the car list on a long click
        carList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                final int temp = position;

                final AlertDialog.Builder box = new AlertDialog.Builder(FirstScreen.this);
                box.setTitle("Remove Car");
                box.setMessage("Are you sure you want to remove this car?\nAll data associated with this car will be lost.");
                box.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //NEED TO ALSO REMOVE FROM FIREBASE DATABASE
                                    information_array.remove(temp);
                                    information_array_adapter.notifyDataSetChanged();

                                    person.setCars(information_array);

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

    }

    //Add a car to the list
    //make a new Car object and put into the array, and notify the list adapter
    public void addCar(View view) {
        Intent intent = new Intent(getApplicationContext(), AddNewCar.class);
        startActivityForResult(intent, 101);

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        // Check which request we're responding to
        System.out.println("We made it back to activity");
        if (requestCode == 101) {
            // Make sure the request was successful
            System.out.println("Inside request Code");
            if (resultCode != Activity.RESULT_CANCELED) {

                System.out.println("Inside result Code");
                MainActivity.LoadedPerson.Car _car = (MainActivity.LoadedPerson.Car) data.getSerializableExtra("Updated Person");



                person.addCar(_car);

                information_array_adapter.notifyDataSetChanged();
                System.out.println(person.getEmail());
                saveToFireBase(person);
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
