package Controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import Models.EntryTag;
import Models.Trip;
import database.AppDatabase;

public class TripWrapper {
    private List<EntryTag> tags;
    private Trip trip;

    public TripWrapper(EntryWrapper entry1, EntryWrapper entry2) {
        this.trip = new Trip(entry1.entry, entry2.entry);
        this.tags = entry1.getTags(true);
        this.tags.addAll(entry2.getTags(false));
    }

//    public TripWrapper(int entry1id, int entry2id, AppDatabase db) {
//        EntryWrapper entry1 = new EntryWrapper(entry1id, db);
//        EntryWrapper entry2 = new EntryWrapper(entry2id, db);
//        new TripWrapper(entry1, entry2);
//    }

    public List<EntryTag> getTags() {
        return tags;
    }

    public Trip getTrip() {
        return trip;
    }

    public String getStartDateAsString() {
        return new SimpleDateFormat("dd/MM/yyyy").format(new Date(trip.getStartDate()));
    }
}
