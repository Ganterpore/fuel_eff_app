package Controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import Models.EntryTag;
import Models.Trip;
import database.AppDatabase;

/**
 * Class is used as a wrapper for Trip objects.
 * Allows the ability to access all assosciated data with a given trip.
 *
 * A trip, in this context, is the time between two entries, where a car refuelled at a fuel station.
 *
 * Trips are essentially virtual data points, as they are created from the data between two entries.
 * Therefore they are created by using two other entries, and are not actually stored in the
 * database on their own.
 */
public class TripWrapper {
    private List<EntryTag> tags;
    private Trip trip;

    /**
     * Creates a TripWrapper object from two already built entries. Therefore does not require any access
     * to the database (and therefore can be run on the main thread)
     * @param entry1, the first entry that starts the trip
     * @param entry2, the second entry,m, which finished the trip.
     */
    public TripWrapper(EntryWrapper entry1, EntryWrapper entry2) {
        this.trip = new Trip(entry1.entry, entry2.entry);
        this.tags = entry1.getTags(true);
        this.tags.addAll(entry2.getTags(false));
    }

    /**
     * Creates a tripwrapper object from two entry IDs. this needs to access the database to get
     * information from the entries, and therefore should not be run on the main thread.
     * @param entry1id, the ID for the entry which starts the trip
     * @param entry2id, the ID for the entry which ends the trip
     * @param db, the database from which to access the records from.
     */
    public TripWrapper(int entry1id, int entry2id, AppDatabase db) {
        this(new EntryWrapper(entry1id, db), new EntryWrapper(entry2id, db));
    }

    /**
     * Gets the tags assosciated with this trip. These are the tags for the next next trip of entry 1,
     * and the previous trip of entry 2.
     * @return, all the asssosciated trips
     */
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
