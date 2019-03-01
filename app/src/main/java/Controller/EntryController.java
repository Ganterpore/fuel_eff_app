package Controller;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import Models.Entry;
import database.AppDatabase;

/**
 * Used to access and control data assosciated with entries in the database
 */
public class EntryController {
    private AppDatabase db;

    /**
     * creates a new Controller
     * @param db, the database to access information from.
     */
    public EntryController(AppDatabase db) {
        this.db = db;
    }

    /**
     * Creates a new Entry in the database
     * @return the new Entry, in the form of an EntryWrapper
     */
    public EntryWrapper newEntry(long date, double trip, double litres, double price, int car, int fuel) {
        //Creating the Entry
        EntryWrapper entry = new EntryWrapper(date, trip, litres, price, car, fuel, db);
        //adding the entry to the database and getting the new ID
        int eid = (int) this.db.entryDao().addEntry(entry.entry);
        //adding the ID to the EntryWrapper, and returning it
        entry.entry.setEid(eid);
        return entry;
    }

    /**
     * Gets a specific Entry
     * @param eid, the ID of the entry to get
     * @return the entry, in the form of an EntryWrapper
     */
    public EntryWrapper getEntry(int eid) {
        Entry entry = db.entryDao().getEntry(eid);
        if(entry==null) {
            return null;
        }
        return new EntryWrapper(entry, db);
    }

    /**
     * Gets all entries assosciated with a particular car
     * @param cid, the ID of the car
     * @return a List of Entries on a car, in the form of EntryWrappers
     */
    public List<EntryWrapper> getAllEntriesOnCar(int cid) {
        //getting all the entries on the car
        List<Entry> entries = db.entryDao().getAllEntriesOnCar(cid);
        //wrapping the entries before returning them
        List<EntryWrapper> wrappedEntries = new ArrayList<>();
        for(Entry entry : entries) {
            wrappedEntries.add(new EntryWrapper(entry, db));
        }
        return wrappedEntries;
    }

    /**
     * Gets all entries that contain a particular tag
     * @param cid, the ID of the car they are on
     * @param tid, the aID of the tag
     * @return a List of Entry's in the form of EntryWrappers
     */
    public List<EntryWrapper> getAllEntriesWithTag(int cid, int tid) {
        //getting all the entries with the tag
        List<Entry> entries = db.entryDao().getEntriesWithTag(cid, tid);
        //wrapping the entries before returning them
        List<EntryWrapper> wrappedEntries = new ArrayList<>();
        for(Entry entry : entries) {
            wrappedEntries.add(new EntryWrapper(entry, db));
        }
        return wrappedEntries;
    }

    /**
     * Gets all entries with a particular fuel percentage
     * @param percent, the percent of the fuel
     * @return a list of Entry's with that percent.
     */
    public List<Entry> getEntriesWithPercent(int percent) {
        return db.entryDao().getEntryWithPercent(percent);
    }

    /*************Getters for stats about a car***********************/

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
