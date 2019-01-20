package application;

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
        this.db.entryDao().insert(entry.entry);
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
    public List<EntryWrapper> getAllEntries(int cid, int tid) {
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

    public double getAverageEfficiency() {
        return db.entryDao().getAverageEfficiency();
    }

    public double getTotalDistance() {
        return db.entryDao().getTotalDistance();
    }

    public double getTotalCost() {
        return db.entryDao().getTotalCost();
    }

    public double getTotalLitres() {
        return db.entryDao().getTotalLitres();
    }
}
