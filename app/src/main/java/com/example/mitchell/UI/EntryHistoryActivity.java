package com.example.mitchell.UI;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.Arrays;
import java.util.List;

import Models.Entry;
import Controller.EntryWrapper;
import database.AppDatabase;

/**
 * used to display a history of all entries that have been given
 */
public class EntryHistoryActivity extends AppCompatActivity {

    private ArrayAdapter<Entry> entryAdapter;
    private int carID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_history);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        android.support.v7.app.ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        //building the list to be used to display history
        entryAdapter = new ArrayAdapter<Entry>(this,
                android.R.layout.simple_list_item_1);
        ListView historyList = findViewById(R.id.History);
        historyList.setAdapter(entryAdapter);

        carID = getIntent().getIntExtra("carID", 0);
        Log.d("R", "onCreate: "+carID);

        //sets up the ability to select items from the list
        historyList.setOnItemClickListener(
            new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Entry entry = (Entry) parent.getItemAtPosition(position);
                    Log.d("R", String.format("pos: %d, id: %d", position, id));
                    Log.d("R", String.valueOf(entry.getEid()));
//                    openEntry(entry);
//                    entryAdapter.notifyDataSetChanged();
                }
            }
        );

        fillList();


    }


    @Override
    /**
     * means that whenever the page is reoppenned the list will be rebuilt.
     * This allows edits to flow back in.
     */
    public void onResume() {
        super.onResume();
        fillList();
    }

    /**
     * used to add all the entries to the list
     */
    private void fillList() {
        final AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "database-name").build();

        new AsyncTask<Void, Void, List<Entry>>() {
            @Override
            /**
             * gets all the entries which will be added to the list
             * TODO again, remove database access from UI
             */
            protected List<Entry> doInBackground(Void... voids) {
                Log.d("R", "updating list");
                return db.entryDao().getAllEntries(carID);
            }
            @Override
            /**
             * adds all entries to the adapter
             */
            protected void onPostExecute(List<Entry> item) {
                Log.d("R", "updating adapter");
                Log.d("R", "onPostExecute: "+Arrays.toString(item.toArray()));
                entryAdapter.clear();
                entryAdapter.addAll(item);
                entryAdapter.notifyDataSetChanged();
            }
        }.execute();
    }

    /**
     * used to handle the opening of a specific entry from the list
     * @param entry
     */
//    public void openEntry(EntryWrapper entry) {
//        Intent intent = new Intent(this, EntryActivity.class);
//        intent.putExtra("eid", entry.getEid());
//        Log.d("R", String.valueOf(entry.getEid()));
//        startActivity(intent);
//    }

}
