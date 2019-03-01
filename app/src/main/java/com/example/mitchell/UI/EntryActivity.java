package com.example.mitchell.UI;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

import Controller.Controller;
import Controller.EntryWrapper;
import Models.Car;
import Models.EntryTag;
import Models.PetrolType;

/**
 * Activity for displaying a single Entry to the user
 */
public class EntryActivity extends AppCompatActivity implements DatabaseObserver {
    private EntryWrapper entry; //entry being displayed
    private List<Car> cars;
    private List<PetrolType> fuels;
    private List<EntryTag> tags;
    private Car currentCar;

    //Views on page
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
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //setting the views
        efficiency = findViewById(R.id.displayEfficiency);
        litres = findViewById(R.id.displayLitresFilled);
        distance = findViewById(R.id.displayDistanceTravelled);
        cost = findViewById(R.id.displayCost);
        cpkm = findViewById(R.id.displayCentsPerKilometre);
        note = findViewById(R.id.display_note);
        prevTags = findViewById(R.id.prev_tags);
        nextTags = findViewById(R.id.next_tags);

        //updates the page based on the given entryID
        if(getIntent().hasExtra("eid")) {
            final int eid = getIntent().getIntExtra("eid", 0);
            updatePage(this, eid);
        } else {
            entry = null;
        }

        //TODO EntryWrapper should be given the type to convert to (Litres, gallons etc.) and convert
        //TODO stop using default Locale
    }

    /**
     * Used for updating all the fields on the screen with information from the database
     */
    private static void updatePage(final EntryActivity activity, final int eid) {
        final DecimalFormat decimalFormat = new DecimalFormat("#.00");
        new AsyncTask<Void, Void, EntryWrapper>() {
            @Override
            protected EntryWrapper doInBackground(Void... voids) {
                //get the entry, and other information, from the database
                EntryWrapper entry = Controller.getCurrentController().entryC.getEntry(eid);

                activity.fuels = Controller.getCurrentController().getAllFuels();
                activity.cars = Controller.getCurrentController().getAllCars();
                activity.tags = Controller.getCurrentController().getAllTags();
                activity.currentCar = Controller.getCurrentController().getCar(entry.getCar());
                return entry;
            }

            @Override
            protected void onPostExecute(EntryWrapper entryWrapper) {
                activity.entry = entryWrapper;
                //update the views to reflect the data on the database
                activity.efficiency.setText(decimalFormat.format(entryWrapper.getEfficiency()));
                activity.litres.setText(decimalFormat.format(entryWrapper.getLitres()));
                activity.distance.setText(decimalFormat.format(entryWrapper.getTrip()));
                activity.cost.setText(decimalFormat.format(entryWrapper.getCost()));
                activity.cpkm.setText(decimalFormat.format(entryWrapper.getCPerKm()));
                activity.note.setText(entryWrapper.getNote());

                activity.prevTags.setText(Arrays.toString(entryWrapper.getTags(false).toArray()));
                activity.nextTags.setText(Arrays.toString(entryWrapper.getTags(true).toArray()));
            }
        }.execute();
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
                deleteEntry(entry);
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
     * sends current entry to the create entry editor, and deletes it's current form
     */
    private void editEntry() {
        NewEntryDialogueBuilder.newEntry(this, this, cars, fuels, tags, entry, currentCar);
        deleteEntry(entry);
    }

    /**
     * removes entry from database. Static to avoid leaks
     */
    public static void deleteEntry(final EntryWrapper entry) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                entry.deleteEntry();
                return null;
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
                updatePage(this, entry.getEid());
                break;
        }
    }
}
