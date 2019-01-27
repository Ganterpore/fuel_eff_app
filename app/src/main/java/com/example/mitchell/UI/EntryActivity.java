package com.example.mitchell.UI;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import Controller.Controller;
import Controller.EntryWrapper;
import database.AppDatabase;

/**
 * used to display entries to the user
 */
public class EntryActivity extends AppCompatActivity {
//    public static final String PREVIOUS_ENTRY = "com.example.mitchell.test1.PREVIOUS_ENTRY";

    //entry being displayed
    private EntryWrapper entry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        android.support.v7.app.ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        final TextView efficiency = findViewById(R.id.displayEfficiency);
        final TextView litres = findViewById(R.id.displayLitresFilled);
        final TextView distance = findViewById(R.id.displayDistanceTravelled);
        final TextView cost = findViewById(R.id.displayCost);
        final TextView cpkm = findViewById(R.id.displayCentsPerKilometre);
        final TextView note = findViewById(R.id.displayNote);

        if(getIntent().hasExtra("eid")) {
            final int eid = getIntent().getIntExtra("eid", 0);
            new AsyncTask<Void, Void, EntryWrapper>() {

                @Override
                protected EntryWrapper doInBackground(Void... voids) {
                    return Controller.getCurrentController().entryC.getEntry(eid);
                }

                @Override
                protected void onPostExecute(EntryWrapper entryWrapper) {
                    entry = entryWrapper;
                    efficiency.setText(String.format("%3.2f L/100km", entry.getEfficiency()));
                    litres.setText(String.format("Volume: %3.2fL",entry.getLitres()));
                    distance.setText(String.format("Distance: %3.2fkm", entry.getTrip()));
                    cost.setText(String.format("Cost: $%3.2f", entry.getCost()));
                    cpkm.setText(String.format("cost/distance: %3.2fc/km", entry.getCPerKm()));
                    note.setText(String.format("Note:\n"+ entry.getNote()));
                }
            }.execute();
        } else {
            entry = null;
        }

        //display the entry fields onto the screen
        //TODO EntryWrapper should be given the type to convert to (Litres, gallons etc.) and convert
        //TODO stop using default Locale


    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_entry_activity, menu);
        return true;
    }

    //when an item from the menu is selected
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_entry:
                deleteEntry();
                finish();
                return true;
            case R.id.edit_entry:
                editEntry();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    /**
     * used to edit an entry.
     * sends current entry to the create entry editor
     */
    private void editEntry() {
//        Intent intent = new Intent(this, addEntry.class);
//        intent.putExtra("eid", entry.getEid());
//        startActivity(intent);
//        finish();
    }

    /**
     * removes entry from database
     * TODO remove database usage from UI
     */
    public void deleteEntry() {
        final AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "database-name").build();

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                Log.d("R", String.format("deleting entry: %d", entry.getEid()));
                Log.d("R", String.valueOf(db.entryDao().getCount()));
                db.entryDao().deleteId(entry.getEid());
                Log.d("R", String.valueOf(db.entryDao().getCount()));
                return null;
            }
            @Override
            protected void onPostExecute(Void item) {
                Log.d("R", "deletion successful");
            }
        }.execute();
    }

}
