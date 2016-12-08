package com.android.application.carmaintenanceapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ListViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ThirdScreen extends AppCompatActivity {

    public class fixListAdapter extends ArrayAdapter<MainActivity.LoadedPerson.Car.Fix> {
        public fixListAdapter(Context context, ArrayList<MainActivity.LoadedPerson.Car.Fix> listItems) {
            super(context, 0, listItems);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            MainActivity.LoadedPerson.Car.Fix fixItem = getItem(position);

            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.third_screen_list_layout, parent, false);
            }

            TextView company = (TextView) convertView.findViewById(R.id.CompanyOfFix);
            TextView location = (TextView) convertView.findViewById(R.id.LocationOfFix);
            TextView price = (TextView) convertView.findViewById(R.id.PriceOfFix);
            TextView distance = (TextView) convertView.findViewById(R.id.DistanceOfFix);

            company.setText(fixItem.getCompany());
            location.setText(fixItem.getLocation());
            price.setText(fixItem.getPrice());
            distance.setText(fixItem.getDistance());

            return convertView;
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third_screen);

        /*Intent i = getIntent();
        final ArrayList<String> fixCompany = i.getStringArrayListExtra("fixCompany");                           ****************COMMENTED - STRING ONLY IMPLEMENTATION*******************
        final ArrayList<String> fixLocation = i.getStringArrayListExtra("fixLocation");
        final ArrayList<String> fixPrice = i.getStringArrayListExtra("fixPrice");
        final ArrayList<String> fixDistance = i.getStringArrayListExtra("fixDistance");*/

        //******Structure: List of Cars: [Car1, Car2, Car3, ....., CarN]
        //                                             |
        //Each car has array of maintenance issues    [Issue1, Issue2, Issue3, ..., IssueN]
        //                                                       |
        //Each issue has an array of possible fixes            [PossibleFix1, PossibleFix2, ..., PossibleFixN]
        //                                                                        |
        //Each fix contains company, location, price, and distance             -CompanyOfFix
        //                                                                     -LocationOfFix
        //                                                                     -PriceOfFix
        //                                                                     -DistanceToFix
        Intent i = getIntent();
        int indexOfIssue = i.getIntExtra("indexOfIssue", 0);
        int indexOfClickedCar = i.getIntExtra("indexOfClickedCar", 0);
        MainActivity.LoadedPerson loadedPerson = (MainActivity.LoadedPerson) i.getSerializableExtra("loadedPerson");
        MainActivity.LoadedPerson.Car clickedCar = loadedPerson.getCars().get(indexOfClickedCar);
        String nameOfIssue = clickedCar.getUpcomingMaintenanceIssues().get(indexOfIssue).getNameOfIssue();         //get name of issue
        ArrayList<MainActivity.LoadedPerson.Car.Fix> fixArray = clickedCar.getUpcomingMaintenanceIssues().get(indexOfIssue).getListOfPossibleFixes();            //get array of possible fixes

        //Set title of screen to the name of the issue
        TextView titleOfScreen = (TextView) findViewById(R.id.selectedMaintenanceIssue);
        titleOfScreen.setText(nameOfIssue);

        //ListView adapter
        //array adapter stuff
        ListView fixList = (ListView) findViewById(R.id.upcomingIssuesList);
        fixListAdapter fix_list_adapter = new fixListAdapter(this, fixArray);
        fixList.setAdapter(fix_list_adapter);
        fix_list_adapter.notifyDataSetChanged();

    }
}
