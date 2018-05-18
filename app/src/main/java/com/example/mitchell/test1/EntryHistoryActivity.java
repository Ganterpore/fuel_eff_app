package com.example.mitchell.test1;

import android.arch.persistence.room.Room;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.util.List;

public class EntryHistoryActivity extends AppCompatActivity {

    private ArrayAdapter<Entry> entryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_history);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        android.support.v7.app.ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        entryAdapter = new ArrayAdapter<Entry>(this,
                android.R.layout.simple_list_item_1);



        ListView history = findViewById(R.id.History);
        history.setAdapter(entryAdapter);

        history.setOnItemClickListener(
            new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Entry entry = (Entry) parent.getItemAtPosition(position);
                    Log.d("R", String.format("pos: %d, id: %d", position, id));
                    Log.d("R", String.valueOf(entry.getEid()));
                    openEntry(entry);
//                    entryAdapter.notifyDataSetChanged();
                }
            }
        );

        fillList();


    }

    @Override
    public void onResume() {
        super.onResume();
        fillList();
    }

    private void fillList() {
        final AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "database-name").build();



        new AsyncTask<Void, Void, List<Entry>>() {
            @Override
            protected List<Entry> doInBackground(Void... voids) {
                Log.d("R", "updating list");
//                entryAdapter.addAll(
                return db.entryDao().getAll();
//                return null;
            }
            @Override
            protected void onPostExecute(List<Entry> item) {
                Log.d("R", "updating adapter");
                entryAdapter.clear();
                entryAdapter.addAll(item);
                entryAdapter.notifyDataSetChanged();
            }
        }.execute();
    }

    public void openEntry(Entry entry) {
        Intent intent = new Intent(this, EntryActivity.class);
        intent.putExtra(addEntry.ENTRY, entry);
        Log.d("R", String.valueOf(entry.getEid()));
        startActivity(intent);
    }

}
