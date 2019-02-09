package com.example.mitchell.UI;

import android.arch.persistence.room.Room;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import Controller.Controller;
import Controller.TripWrapper;
import database.AppDatabase;

public class TripActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);

        final TextView startDate = findViewById(R.id.trip_start_date);
        final TextView tripLength = findViewById(R.id.trip_length);
        final TextView tripEff = findViewById(R.id.trip_efficiency);
        final TextView tripDays = findViewById(R.id.trip_days);
        final TextView tripLitres = findViewById(R.id.trip_litres);

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
                    startDate.setText("Trip start: "+tripWrapper.getStartDateAsString());
                    tripLength.setText("Trip Length (distance): "+tripWrapper.getTrip().getDistance());
                    tripEff.setText("Trip Efficiency: "+tripWrapper.getTrip().getEfficiency());
                    tripDays.setText("Trip length (days): "+tripWrapper.getTrip().getDays());
                    tripLitres.setText("Fuel used: "+tripWrapper.getTrip().getLitres());
                }
            }.execute();
        }
    }
}
