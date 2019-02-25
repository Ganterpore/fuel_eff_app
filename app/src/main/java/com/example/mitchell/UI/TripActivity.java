package com.example.mitchell.UI;

import android.arch.persistence.room.Room;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.Arrays;

import Controller.TripWrapper;
import database.AppDatabase;

public class TripActivity extends AppCompatActivity {

    private final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);

        final TextView startDate = findViewById(R.id.trip_start_date);
        final TextView tripLength = findViewById(R.id.trip_length);
        final TextView tripEff = findViewById(R.id.trip_efficiency);
        final TextView tripDays = findViewById(R.id.trip_days);
        final TextView tripLitres = findViewById(R.id.trip_litres);
        final TextView tripTags = findViewById(R.id.trip_tags);

        if(getIntent().hasExtra("entry1")
                && getIntent().hasExtra("entry2")) {
            final int entry1 = getIntent().getIntExtra("entry1", 0);
            final int entry2 = getIntent().getIntExtra("entry2", 0);
            new AsyncTask<Void, Void, TripWrapper>() {

                @Override
                protected TripWrapper doInBackground(Void... voids) {
                    AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                            AppDatabase.class, "database-name").build();
                    return new TripWrapper(entry1, entry2, db);
                }

                @Override
                protected void onPostExecute(TripWrapper tripWrapper) {
                    startDate.setText(tripWrapper.getStartDateAsString());
                    tripLength.setText(DECIMAL_FORMAT.format(tripWrapper.getTrip().getDistance()));
                    tripEff.setText(DECIMAL_FORMAT.format(tripWrapper.getTrip().getEfficiency()));
                    tripDays.setText(String.valueOf(tripWrapper.getTrip().getDays()));
                    tripLitres.setText(DECIMAL_FORMAT.format(tripWrapper.getTrip().getLitres()));
                    tripTags.setText(Arrays.toString(tripWrapper.getTags().toArray()));
                }
            }.execute();
        }
    }
}
