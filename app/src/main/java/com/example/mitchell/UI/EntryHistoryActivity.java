package com.example.mitchell.UI;

import android.app.Activity;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import Controller.Controller;
import Controller.EntryWrapper;
import database.AppDatabase;
import Controller.TripWrapper;

/**
 * used to display a history of all entries that have been given
 */
public class EntryHistoryActivity extends AppCompatActivity {

    private EntryAdapter entryAdapter;
    private TripAdapter tripAdapter;
    private int carID;
    private RecyclerView historyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry_history);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        historyList = findViewById(R.id.history);
        entryAdapter = new EntryAdapter(this);
        tripAdapter = new TripAdapter(this);
        historyList.setAdapter(entryAdapter);
        historyList.setLayoutManager(new LinearLayoutManager(this));

        carID = getIntent().getIntExtra("carID", 0);

        BottomNavigationView navigation = findViewById(R.id.type_switcher);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        fillList(entryAdapter, tripAdapter, carID, getApplicationContext());
    }

    @Override
    public void onResume() {
        super.onResume();
        /*
         * means that whenever the page is reopened the list will be rebuilt.
         * This allows edits to flow back in.
         */
        fillList(entryAdapter, tripAdapter, carID, getApplicationContext());
    }

    /**
     * Used to update the page view when different options are selected on the bottom navigation view
     */
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_entry:
                    historyList.setAdapter(entryAdapter);
                    return true;
                case R.id.navigation_trip:
                    historyList.setAdapter(tripAdapter);
                    return true;
            }
            return false;
        }
    };

    /**
     * used to populate entries to the RecyclerView on the page.
     */
    private static void fillList(final EntryAdapter entryAdapter, final TripAdapter tripAdapter,
                                 final int carID, Context context) {
        final AppDatabase db = Room.databaseBuilder(context,
                AppDatabase.class, "database-name").build();

        new AsyncTask<Void, Void, List<EntryWrapper>>() {
            @Override
            protected List<EntryWrapper> doInBackground(Void... voids) {
                return Controller.getCurrentController().entryC.getAllEntriesOnCar(carID);
            }

            @Override
            protected void onPostExecute(List<EntryWrapper> entries) {
                entryAdapter.clear();
                entryAdapter.addAll(entries);
                entryAdapter.notifyDataSetChanged();

                tripAdapter.clear();
                tripAdapter.addAll(entries);
                tripAdapter.notifyDataSetChanged();
            }
        }.execute();
    }

    /**
     * used to handle the opening of a specific entry from the list
     * @param entry, the entry you wish to open
     */
    public void openEntry(EntryWrapper entry) {
        Intent intent = new Intent(this, EntryActivity.class);
        intent.putExtra("eid", entry.getEid());
        Log.d("R", String.valueOf(entry.getEid()));
        startActivity(intent);
    }

    /**
     * used to handle the opening of a specific trip from the list.
     * @param entry1, the first trip assosciated with the entry
     * @param entry2, the second trip assosciated with the entry
     */
    public void openTrip(int entry1, int entry2) {
        Intent intent = new Intent(this, TripActivity.class);
        intent.putExtra("entry1", entry1);
        intent.putExtra("entry2", entry2);
        startActivity(intent);
    }

    /**
     * The RecyclerView Adapter for handling Entry's
     */
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


    /**
     * The RecyclerView ViewHolder for handling Entry's
     */
    public class EntryViewHolder extends RecyclerView.ViewHolder {
        private DecimalFormat decimalFormat = new DecimalFormat("#.00");
        private View itemView;

        public EntryViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
        }

        public void build(final EntryWrapper entry) {
            TextView listDate = itemView.findViewById(R.id.listDate);
            TextView listEfficiency = itemView.findViewById(R.id.listEfficiency);
            TextView listNote = itemView.findViewById(R.id.list_note);

            listDate.setText(entry.getDateAsString());
            listEfficiency.setText(decimalFormat.format(entry.getEfficiency()));
            listNote.setText(entry.getNote());

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openEntry(entry);
                }
            });
        }
    }

    /**
     * The RecyclerView Adapter for handling Trips
     */
    public class TripAdapter extends RecyclerView.Adapter<TripViewHolder> {
        Activity activity;
        List<EntryWrapper> entries;

        public TripAdapter(Activity activity) {
            this.activity = activity;
            this.entries = new ArrayList<>();
        }

        public void clear() {
            entries.clear();
        }

        public void addAll(List<EntryWrapper> newEntries) {
            entries.addAll(newEntries);
        }

        @Override
        public TripViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(activity).inflate(R.layout.trip_list_item, parent, false);
            TripViewHolder tripVH = new TripViewHolder(v);
            return tripVH;
        }

        @Override
        public void onBindViewHolder(TripViewHolder holder, int position) {
            EntryWrapper entry1 = entries.get(position);
            EntryWrapper entry2 = entries.get(position+1);
            holder.build(entry1, entry2);
        }

        @Override
        public int getItemCount() {
            return entries.size()-1;
        }
    }

    /**
     * The RecyclerView ViewHolder for handling Trips
     */
    public class TripViewHolder extends RecyclerView.ViewHolder {
        private DecimalFormat decimalFormat = new DecimalFormat("#.00");
        private View itemView;
        private TripWrapper trip;
        private int entry1ID;
        private int entry2ID;

        public TripViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
        }

        public void build(EntryWrapper entry1, EntryWrapper entry2) {
            entry1ID = entry1.getEid();
            entry2ID = entry2.getEid();
            trip = new TripWrapper(entry1, entry2);
            TextView tripDate = itemView.findViewById(R.id.tripDate);
            TextView tripLength = itemView.findViewById(R.id.trip_length);
            TextView tripDays = itemView.findViewById(R.id.trip_days);

            tripDate.setText(trip.getStartDateAsString());
            tripDays.setText(String.valueOf(trip.getTrip().getDays()));
            tripLength.setText(decimalFormat.format(trip.getTrip().getDistance()));

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    openTrip(entry1ID, entry2ID);
                }
            });
        }
    }

}
