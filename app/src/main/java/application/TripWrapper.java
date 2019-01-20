package application;

import java.util.List;

import Models.EntryTag;
import Models.Trip;
import database.AppDatabase;

public class TripWrapper {
    private List<EntryTag> tags;
    private Trip trip;

    public TripWrapper(int entry1id, int entry2id, AppDatabase db) {
        EntryWrapper entry1 = new EntryWrapper(entry1id, db);
        EntryWrapper entry2 = new EntryWrapper(entry2id, db);
        this.trip = new Trip(entry1.entry, entry2.entry);
        this.tags = entry1.getTags(true);
        this.tags.addAll(entry2.getTags(false));
    }

    public List<EntryTag> getTags() {
        return tags;
    }

    public Trip getTrip() {
        return trip;
    }
}
