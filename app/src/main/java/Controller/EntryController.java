package Controller;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import Models.Entry;
import database.AppDatabase;

public class EntryController {
    private AppDatabase db;

    public EntryController(AppDatabase db) {
        this.db = db;
    }

    public EntryWrapper getEntry(int eid) {
        Entry entry = db.entryDao().getEntry(eid);
        if(entry==null) {
            return null;
        }
        return new EntryWrapper(entry, db);
    }

    public EntryWrapper newEntry(long date, double trip, double litres, double price, int car, int fuel) {
        EntryWrapper entry = new EntryWrapper(date, trip, litres, price, car, fuel, db);
        int eid = (int) this.db.entryDao().addEntry(entry.entry);
        Log.d("A", "newEntry: Entry added");
        entry.entry.setEid(eid);
        return entry;
    }

    public List<EntryWrapper> getAllEntries(int cid) {
        List<Entry> entries = db.entryDao().getAllEntries(cid);
        List<EntryWrapper> wrappedEntries = new ArrayList<>();
        for(Entry entry : entries) {
            wrappedEntries.add(new EntryWrapper(entry, db));
        }
        return wrappedEntries;
    }
    public List<EntryWrapper> getAllEntriesWithTag(int cid, int tid) {
        List<Entry> entries = db.entryDao().getEntriesWithTag(cid, tid);
        List<EntryWrapper> wrappedEntries = new ArrayList<>();
        for(Entry entry : entries) {
            wrappedEntries.add(new EntryWrapper(entry, db));
        }
        return wrappedEntries;
    }

    public List<Entry> getEntriesWithPercent(int percent) {
        return db.entryDao().getEntryWithPercent(percent);
    }

    public double getAverageEfficiency(int cid) {
        return db.entryDao().getAverageEfficiency(cid);
    }

    public double getTotalDistance(int cid) {
        return db.entryDao().getTotalDistance(cid);
    }

    public double getTotalCost(int cid) {
        return db.entryDao().getTotalCost(cid);
    }

    public double getTotalLitres(int cid) {
        return db.entryDao().getTotalLitres(cid);
    }
}
