package com.example.mitchell.UI;

import android.arch.persistence.room.Room;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

import Controller.Controller;
import Controller.EntryWrapper;
import Models.Car;
import Models.EntryTag;
import Models.PetrolType;
import database.AppDatabase;

/**
 * Activity for displaying a single Entry to the user
 */
public class EntryActivity extends AppCompatActivity implements DatabaseObserver {
    //entry being displayed
    private EntryWrapper entry;
    private List<Car> cars;
    private List<PetrolType> fuels;
    private List<EntryTag> tags;
    private Car currentCar;
    private TextView efficiency;
    private TextView litres;
    private TextView distance;
    private TextView cost;
    private TextView cpkm;
    private TextView note;
    private TextView prevTags;
    private TextView nextTags;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        efficiency = findViewById(R.id.displayEfficiency);
        litres = findViewById(R.id.displayLitresFilled);
        distance = findViewById(R.id.displayDistanceTravelled);
        cost = findViewById(R.id.displayCost);
        cpkm = findViewById(R.id.displayCentsPerKilometre);
        note = findViewById(R.id.display_note);
        prevTags = findViewById(R.id.prev_tags);
        nextTags = findViewById(R.id.next_tags);

        if(getIntent().hasExtra("eid")) {
            final int eid = getIntent().getIntExtra("eid", 0);
            new Updater(this, eid).execute();
        } else {
            entry = null;
        }

        //display the entry fields onto the screen
        //TODO EntryWrapper should be given the type to convert to (Litres, gallons etc.) and convert
        //TODO stop using default Locale
    }

    /**
     * Used for updating all the fields on teh screen with information from the database
     */
    private class Updater extends AsyncTask<Void, Void, EntryWrapper> {

        EntryActivity activity;
        int eid;

        public Updater(EntryActivity activity, int eid) {
            this.activity = activity;
            this.eid = eid;
        }

        @Override
        protected EntryWrapper doInBackground(Void... voids) {
            EntryWrapper entry = Controller.getCurrentController().entryC.getEntry(eid);

            activity.fuels = Controller.getCurrentController().getAllFuels();
            activity.cars = Controller.getCurrentController().getAllCars();
            activity.tags = Controller.getCurrentController().getAllTags();
            activity.currentCar = Controller.getCurrentController().getCar(entry.getCar());
            return entry;
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

            prevTags.setText("Prev trip:\n"+Arrays.toString(entry.getTags(false).toArray()));
            nextTags.setText("Next trip:\n"+Arrays.toString(entry.getTags(true).toArray()));
        }
    }

    @Override
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
        NewEntryDialogueBuilder.newEntry(this, this, cars, fuels, tags, entry, currentCar);
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                entry.deleteEntry();
                return null;
            }
        }.execute();
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
                db.entryDao().deleteEntryFromID(entry.getEid());
                return null;
            }
            @Override
            protected void onPostExecute(Void item) {
                Log.d("R", "deletion successful");
            }
        }.execute();
    }

    @Override
    public void notifyChange(Object object, String type) {
        switch (type) {
            case DatabaseObserver.FUEL:
                fuels.add((PetrolType) object);
                break;
            case DatabaseObserver.CAR:
                Car car = (Car) object;
                cars.add(car);
                break;
            case DatabaseObserver.TAG:
                tags.add((EntryTag) object);
                break;
            case DatabaseObserver.ENTRY:
                entry = (EntryWrapper) object;
                new Updater(this, entry.getEid()).execute();
        }
    }
}
