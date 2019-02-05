package com.example.mitchell.UI;

import android.app.Activity;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import Controller.Controller;
import Models.Entry;
import Controller.EntryWrapper;
import database.AppDatabase;

/**
 * used to display a history of all entries that have been given
 */
public class EntryHistoryActivity extends AppCompatActivity {

    private EntryAdapter entryAdapter;
    private int carID;
    private RecyclerView historyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_history);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        android.support.v7.app.ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);

        historyList = findViewById(R.id.history);
        entryAdapter = new EntryAdapter(this);
        historyList.setAdapter(entryAdapter);
        historyList.setLayoutManager(new LinearLayoutManager(this));

        carID = getIntent().getIntExtra("carID", 0);
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

        new AsyncTask<Void, Void, List<EntryWrapper>>() {
            @Override
            /**
             * gets all the entries which will be added to the list
             * TODO again, remove database access from UI
             */
            protected List<EntryWrapper> doInBackground(Void... voids) {
                return Controller.getCurrentController().entryC.getAllEntries(carID);
            }
            @Override
            /**
             * adds all entries to the adapter
             */
            protected void onPostExecute(List<EntryWrapper> entries) {
                entryAdapter.clear();
                entryAdapter.addAll(entries);
                entryAdapter.notifyDataSetChanged();
            }
        }.execute();
    }

    /**
     * used to handle the opening of a specific entry from the list
     * @param entry
     */
    public void openEntry(EntryWrapper entry) {
        Intent intent = new Intent(this, EntryActivity.class);
        intent.putExtra("eid", entry.getEid());
        Log.d("R", String.valueOf(entry.getEid()));
        startActivity(intent);
    }

    public class EntryAdapter extends RecyclerView.Adapter<EntryViewHolder> {
        private Activity activity;
        private List<EntryWrapper> entries;

        public EntryAdapter(Activity activity) {
            this.activity = activity;
            entries = new ArrayList<>();
        }

        public void clear() {
            entries.clear();
        }

        public void addAll(List<EntryWrapper> newEntries) {
            this.entries.addAll(newEntries);
        }

        @Override
        public EntryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(activity).inflate(R.layout.entry_list_item, parent, false);
            EntryViewHolder entryVH = new EntryViewHolder(v);
            return entryVH;
        }

        @Override
        public void onBindViewHolder(EntryViewHolder holder, int position) {
            holder.build(entries.get(position));
        }

        @Override
        public int getItemCount() {
            return entries.size();
        }
    }


    public class EntryViewHolder extends RecyclerView.ViewHolder {
        private View itemView;

        public EntryViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
        }

        public void build(final EntryWrapper entry) {
            TextView listDate = itemView.findViewById(R.id.listDate);
            TextView listEfficiency = itemView.findViewById(R.id.listEfficiency);

            listDate.setText(entry.getDateAsString());
            listEfficiency.setText(String.valueOf(entry.getEfficiency()));

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openEntry(entry);
                }
            });
        }
    }

}
