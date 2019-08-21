package com.example.mitchell.UI;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.Arrays;

import Controller.Controller;
import Controller.TripWrapper;
import Models.PetrolType;
import database.AppDatabase;

/**
 * Activity used for displaying a Trip to the user
 */
public class TripActivity extends AppCompatActivity {
    private static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);

        //getting the Views
        final TextView startDate = findViewById(R.id.trip_start_date);
        final TextView tripLength = findViewById(R.id.trip_length);
        final TextView tripEff = findViewById(R.id.trip_efficiency);
        final TextView tripDays = findViewById(R.id.trip_days);
        final TextView tripLitres = findViewById(R.id.trip_litres);
        final TextView tripFuel = findViewById(R.id.trip_fuel);
        final TextView tripTags = findViewById(R.id.trip_tags);

        //updating the page based on the entries which made it
        if(    getIntent().hasExtra("entry1")
            && getIntent().hasExtra("entry2")) {
            final int entry1 = getIntent().getIntExtra("entry1", 0);
            final int entry2 = getIntent().getIntExtra("entry2", 0);
            updatePage(startDate, tripLength, tripEff, tripDays, tripLitres, tripFuel,
                    tripTags, entry1, entry2, getApplicationContext());
        }
    }

    /**
     * Used to update the details on the page.
     */
    private static void updatePage(final TextView startDate, final TextView tripLength,
                                   final TextView tripEff, final TextView tripDays,
                                   final TextView tripLitres, final TextView tripFuel,
                                   final TextView tripTags, final int entry1,
                                   final int entry2, final Context context) {
        new AsyncTask<Void, Void, TripWrapper>() {
            PetrolType fuel;
            @Override
            protected TripWrapper doInBackground(Void... voids) {
                //gets trip from database
                AppDatabase db = Room.databaseBuilder(context,
                        AppDatabase.class, "database-name").build();
                TripWrapper trip = new TripWrapper(entry1, entry2, db);
                fuel = Controller.getCurrentController().getFuel(trip.getTrip().getFuel());
                return trip;
            }

            @Override
            protected void onPostExecute(TripWrapper tripWrapper) {
                //update page based on trip
                startDate.setText(tripWrapper.getStartDateAsString());
                tripLength.setText(DECIMAL_FORMAT.format(tripWrapper.getTrip().getDistance()));
                tripEff.setText(DECIMAL_FORMAT.format(tripWrapper.getTrip().getEfficiency()));
                tripDays.setText(String.valueOf(tripWrapper.getTrip().getDays()));
                tripLitres.setText(DECIMAL_FORMAT.format(tripWrapper.getTrip().getLitres()));
                tripFuel.setText(fuel.toString());
                tripTags.setText(Arrays.toString(tripWrapper.getTags().toArray()));
            }
        }.execute();
    }
}
