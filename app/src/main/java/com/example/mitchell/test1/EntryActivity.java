package com.example.mitchell.test1;

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

public class EntryActivity extends AppCompatActivity {

    public static final String PREVIOUS_ENTRY = "com.example.mitchell.test1.PREVIOUS_ENTRY";

    private Entry entry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        android.support.v7.app.ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        entry   = intent.getParcelableExtra(addEntry.ENTRY);

        Log.d("R", String.valueOf(entry.getEid()));


        //display the entry fields onto the screen
        TextView efficiency = findViewById(R.id.displayEfficiency);
        efficiency.setText(String.format("%3.2f L/100km", entry.getEfficiency()));

        TextView litres = findViewById(R.id.displayLitresFilled);
        litres.setText(String.format("Litres: %3.2fL",entry.getLitres()));

        TextView distance = findViewById(R.id.displayDistanceTravelled);
        distance.setText(String.format("Distance: %3.2fkm", entry.getTrip()));

        TextView cost = findViewById(R.id.displayCost);
        cost.setText(String.format("Cost: $%3.2f", entry.getCost()));

        TextView cpkm = findViewById(R.id.displayCentsPerKilometre);
        cpkm.setText(String.format("cents/km: %3.2fc/km", entry.getCPerKm()));
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
            //if create entry is selected, create the entry
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

    private void editEntry() {
        Intent intent = new Intent(this, addEntry.class);
        intent.putExtra(PREVIOUS_ENTRY, entry);
        startActivity(intent);
        finish();
    }

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
