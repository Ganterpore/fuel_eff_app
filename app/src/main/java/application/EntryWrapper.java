package application;

import java.util.List;

import Models.Entry;
import Models.EntryTag;
import Models.Note;
import Models.TagsOnEntry;
import database.AppDatabase;

public class EntryWrapper {
    Entry entry;
    private AppDatabase db;

    public EntryWrapper(int eid, AppDatabase db) {
        this.entry = db.entryDao().getEntry(eid);
        this.db = db;
    }

    public EntryWrapper(Entry entry, AppDatabase db) {
        this.entry = entry;
        this.db = db;
    }

    public EntryWrapper(long date, double trip, double litres, double price, int car, int fuel, AppDatabase db) {
        this.db = db;
        this.entry = new Entry(date, trip, litres, price, car, fuel);
    }

    public void addTags(boolean nextTrip, EntryTag... tags) {
        for(EntryTag tag : tags) {
            db.entryDao().addTagToEntry(new TagsOnEntry(entry.getEid(), tag.getTid(), nextTrip));
        }
    }

    public List<EntryTag> getTags(boolean nextTrip) {
        return db.entryDao().getTagsOnEntry(entry.getEid(), nextTrip);
    }

    public void addNote(String note) {
        db.entryDao().addNote(new Note(note, entry.getEid()));
    }

    public Note getNote() {
        return db.entryDao().getNote(entry.getEid());
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
