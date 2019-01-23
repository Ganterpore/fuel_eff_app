package database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.database.Cursor;

import java.util.List;

import Models.Car;
import Models.Entry;
import Models.EntryTag;
import Models.Note;
import Models.PetrolType;
import Models.TagsOnEntry;

/**
 * Created by Mitchell on 18/02/2018.
 */


/**
 * allows access and modification of entries from the entry database
 */
@Dao
public interface EntryDao {
    @Query("SELECT * FROM entry WHERE car = :cid")
    List<Entry> getAllEntries(int cid);

    @Query("SELECT * FROM entry")
    Cursor getAllCursor();

    @Query("SELECT * FROM entry WHERE eid = :eid")
    Entry getEntry(int eid);

    @Query("SELECT COUNT(*) FROM entry")
    int getCount();

    @Insert
    void insertAll(Entry... entries);

    @Insert
    void insert(Entry entry);

    @Delete
    void delete(Entry entry);

    @Delete
    void deleteAll(Entry... entries);

    @Query("DELETE FROM entry WHERE eid = :entryid")
    void deleteId(int entryid);

    @Query("SELECT 100*(SUM(litres)/SUM(trip)) FROM entry WHERE car = :cid")
    double getAverageEfficiency(int cid);

    @Query("SELECT SUM(trip) FROM entry WHERE car = :cid")
    double getTotalDistance(int cid);

    @Query("SELECT SUM(litres*price)/100 FROM entry WHERE car = :cid")
    double getTotalCost(int cid);

    @Query("SELECT SUM(litres) FROM entry WHERE car = :cid")
    double getTotalLitres(int cid);

    @Query("SELECT * FROM entry WHERE car = :cid & eid IN " +
            "(SELECT eid FROM tagsonentry WHERE  tid = :tid)")
    List<Entry> getEntriesWithTag(int cid, int tid);

    @Query("SELECT * FROM entrytag WHERE tid IN " +
            "(SELECT tid FROM tagsonentry " +
            "WHERE eid = :eid & nextTrip = :nextTrip)")
    List<EntryTag> getTagsOnEntry(int eid, boolean nextTrip);

    @Insert()
    void addTagToEntry(TagsOnEntry tag);

    @Insert()
    void addNote(Note note);

    @Query("SELECT * FROM note WHERE eid = :eid")
    Note getNote(int eid);

    @Query("SELECT * FROM entrytag WHERE tid = :tid")
    EntryTag getTag(int tid);

    @Query("SELECT * FROM petroltype WHERE pid = :pid")
    PetrolType getFuel(int pid);

    @Query("SELECT * FROM car WHERE cid = :cid")
    Car getCar(int cid);

    @Query("SELECT * FROM entrytag")
    List<EntryTag> getAllTags();

    @Query("SELECT * FROM entry WHERE fuel IN" +
            "(SELECT pid FROM petroltype WHERE percent = :percent)")
    List<Entry> getEntryWithPercent(int percent);

    @Query("SELECT * FROM car")
    List<Car> getAllCars();

    @Insert
    void addTag(EntryTag tag);

    @Insert
    void addFuel(PetrolType fuel);

    @Insert
    long addCar(Car car);

    @Query("SELECT eid FROM entry WHERE car = :cid ORDER BY date")
    List<Integer> getOrderedEid(int cid);
}
