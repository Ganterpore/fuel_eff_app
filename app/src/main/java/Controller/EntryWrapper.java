package Controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import Models.Entry;
import Models.EntryTag;
import Models.Note;
import Models.TagsOnEntry;
import database.AppDatabase;

/**
 * Used to allow access to an Entry, and all assosciated information to it.
 */
public class EntryWrapper {
    Entry entry;
    private AppDatabase db;
    private List<EntryTag> nextTags;
    private List<EntryTag> prevTags;
    private String notes;

    /**
     * Creates an EntryWrapper from an ID and the database to get information from.
     * @param eid, the ID of the Entry you wish to access.
     * @param db, the database from which to access the information.
     */
    public EntryWrapper(int eid, AppDatabase db) {
        this.entry = db.entryDao().getEntry(eid);
        this.db = db;
        this.nextTags = db.entryDao().getTagsOnEntry(entry.getEid(), true);
        this.prevTags = db.entryDao().getTagsOnEntry(entry.getEid(), false);
        try {
            this.notes = db.entryDao().getNote(entry.getEid()).getNote();
        } catch (NullPointerException e) {
            this.notes = "";
        }
    }

    /**
     * Creates an EntryWrapper from an Entry and the database to get information from.
     * @param entry, the Entry you wish to interact with.
     * @param db, the database from which to access the information.
     */
    public EntryWrapper(Entry entry, AppDatabase db) {
        this.entry = entry;
        this.db = db;
        this.nextTags = db.entryDao().getTagsOnEntry(entry.getEid(), true);
        this.prevTags = db.entryDao().getTagsOnEntry(entry.getEid(), false);
        try {
            this.notes = db.entryDao().getNote(entry.getEid()).getNote();
        } catch (NullPointerException e) {
            this.notes = "";
        }
    }

    /**
     * Manually creates an EntryWrapper object that may not be in a database
     */
    public EntryWrapper(long date, double trip, double litres, double price, int car, int fuel, AppDatabase db) {
        this.db = db;
        this.entry = new Entry(date, trip, litres, price, car, fuel);
        this.prevTags = new ArrayList<>();
        this.nextTags = new ArrayList<>();
        this.notes = "";
    }

    /**
     * Used to add tags to the current entry.
     * @param nextTrip, whether the tags are for the next or previous trip
     * @param tags, the tags to add
     */
    public void addTags(boolean nextTrip, List<EntryTag> tags) {
        for(EntryTag tag : tags) {
            db.entryDao().addTagToEntry(new TagsOnEntry(entry.getEid(), tag.getTid(), nextTrip));
            if(nextTrip) {
                nextTags.add(tag);
            } else {
                prevTags.add(tag);
            }
        }
    }

    /**
     * Deletes the current Entry
     */
    public void deleteEntry() {
        db.entryDao().deleteEntry(entry);
    }

    /**
     * gets the tags currently assosciated with the entry
     * @param nextTrip, whether should be for the current or next trip
     * @return the assosciated tags.
     */
    public List<EntryTag> getTags(boolean nextTrip) {
        if(nextTrip) {
            return nextTags;
        } else {
            return prevTags;
        }
    }

    /**
     * add a note to the Entry
     * @param note, the note to add
     */
    public void addNote(String note) {
        db.entryDao().addNote(new Note(note, entry.getEid()));
        this.notes = note;
    }

    /**
     * gets the note from the current entry
     * @return the note
     */
    public String getNote() {
        return notes;
    }

    public String getDateAsString() {
        return new SimpleDateFormat("dd/MM/yyyy").format(new Date(getDate()));
    }

    public int getEid() {
        return entry.getEid();
    }
    public long getDate() {
        return entry.getDate();
    }
    public double getTrip() {
        return entry.getTrip();
    }
    public double getLitres() {
        return entry.getLitres();
    }
    public double getPrice() {
        return entry.getPrice();
    }
    public double getCost() {
        return entry.getCost();
    }

    public double getEfficiency() {
        return entry.getEfficiency();
    }
    public double getCPerKm() {
        return entry.getCPerKm();
    }
    public int getCar() {
        return entry.getCar();
    }
    public int getFuel() {
        return entry.getFuel();
    }

    public void setDate(long date) {
        entry.setDate(date);
    }

    public void setTrip(double trip) {
        entry.setTrip(trip);
    }

    public void setLitres(double litres) {
        entry.setLitres(litres);
    }

    public void setPrice(double price) {
        entry.setPrice(price);
    }

    public void setCar(int car) {
        entry.setCar(car);
    }

    public void setFuel(int fuel) {
        entry.setFuel(fuel);
    }
}
