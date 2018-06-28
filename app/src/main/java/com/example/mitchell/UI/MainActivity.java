package com.example.mitchell.UI;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import database.AppDatabase;

/**
 * initial class created on startup
 */
public class MainActivity extends AppCompatActivity {
    //ID of message to be sent in intent
    public static final String EXTRA_MESSAGE = "com.example.mitchell.test1.MESSAGE";

    //database for the apps storage
    public AppDatabase db;

    /**
     * automatically called when activity created.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main); //name of view
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "database-name").build();

        //collecting text fields to be set for display
        final TextView efficiency = findViewById(R.id.AverageEfficiency);
        final TextView distance   = findViewById(R.id.TotalDistance);
        final TextView cost       = findViewById(R.id.TotalCost);
        final TextView litres     = findViewById(R.id.TotalLitres);

        //getting values for the text fields out from the app database
        new AsyncTask<Void, Void, Double[]>() {
            @Override
            protected Double[] doInBackground(Void... voids) {
                double  eff = db.entryDao().getAverageEfficiency();
                double dist = db.entryDao().getTotalDistance();
                double cost = db.entryDao().getTotalCost();
                double litr = db.entryDao().getTotalLitres();
                Double[] vals = {eff, dist, cost, litr};
                return vals;
            }
            //setting the values of the text fields
            @Override
            protected void onPostExecute(Double[] vals) {
                efficiency.setText(String.format("%3.2f", vals[0]));
                distance.setText(String.format("%3.2f", vals[1]));;
                cost.setText(String.format("%3.2f", vals[2]));;
                litres.setText(String.format("%3.2f", vals[3]));;

            }
        }.execute();

        //creating the add entry button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            //when clicked a new entry will be created
            public void onClick(View view) {
                newEntry(view);
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId(); //id of the option selected

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            //if settings is clicked... do nothing
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /* called when the Send button is pressed*/
//    @Override
    public void sendMessage(View view) {
        //action when send message buttion is pressed.
        //activity created
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        //creating message to be sent to next activity
        EditText editText = (EditText) findViewById(R.id.editText);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        //starting activity with intent
        startActivity(intent);
    }

    public void newEntry(View view) {
        //action when entry button is pressed
        Intent intent = new Intent(this, addEntry.class);
        startActivity(intent);
    }

    public void openHistory(View view) {
        //action when history button is pressed
        Intent intent = new Intent(this, EntryHistoryActivity.class);
        startActivity(intent);
    }

}
