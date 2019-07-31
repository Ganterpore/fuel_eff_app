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
 * allows access and modification information on the fuel efficiency app database
 */
@Dao
public interface EntryDao {

    /***************************Entry Code********************************/
    @Insert
    long addEntry(Entry entry);

    @Query("SELECT * FROM entry ORDER BY date")
    List<Entry> getAllEntries();


    @Insert
    void insertEntries(Entry... entries);

    @Query("SELECT * FROM entry WHERE eid = :eid")
    Entry getEntry(long eid);

    @Query("SELECT * FROM entry")
    List<Entry> getAllEntries();

    @Query("SELECT * FROM entry WHERE car = :cid ORDER BY date")
    List<Entry> getAllEntriesOnCar(int cid);

    @Query("SELECT COUNT(*) FROM entry")
    int countEntries();

    @Delete
    void deleteEntry(Entry entry);

    @Delete
    void deleteEntries(Entry... entries);

    @Query("DELETE FROM entry WHERE eid = :entryId")
    void deleteEntryFromID(long entryId);

    //Tags

    @Insert
    long addTag(EntryTag tag);

    @Insert()
    long addTagToEntry(TagsOnEntry tag);

    @Insert()
    void addTagsToEntry(TagsOnEntry... tag);

    @Query("SELECT * FROM entrytag WHERE tid = :tid")
    EntryTag getTag(int tid);

    @Query("SELECT * FROM entrytag")
    List<EntryTag> getAllTags();

    @Query("SELECT * FROM entry WHERE car = :cid & eid IN " +
            "(SELECT eid FROM tagsonentry WHERE  tid = :tid)")
    List<Entry> getEntriesWithTag(int cid, int tid);

    @Query("SELECT * FROM entrytag WHERE tid IN " +
            "(SELECT tid FROM tagsonentry " +
            "WHERE eid = :eid AND nextTrip = :nextTrip)")
    List<EntryTag> getTagsOnEntry(long eid, boolean nextTrip);

    //Notes
    @Insert()
    void addNote(Note note);

    @Query("SELECT * FROM note WHERE eid = :eid")
    Note getNote(long eid);


    /**********************************Car Code*******************************************/
    @Insert
    long addCar(Car car);

    @Query("SELECT * FROM car WHERE cid = :cid")
    Car getCar(int cid);

    @Query("SELECT * FROM car")
    List<Car> getAllCars();

    @Delete
    void deleteCar(Car car);
    /*********************************get stats code*************************************/
    @Query("SELECT 100*(SUM(litres)/SUM(trip)) FROM entry WHERE car = :cid")
    double getAverageEfficiency(int cid);

    @Query("SELECT SUM(trip) FROM entry WHERE car = :cid")
    double getTotalDistance(int cid);

    @Query("SELECT SUM(litres*price)/100 FROM entry WHERE car = :cid")
    double getTotalCost(int cid);

    @Query("SELECT SUM(litres) FROM entry WHERE car = :cid")
    double getTotalLitres(int cid);


    /************************************** Fuel *************************************/
    @Insert
    long addFuel(PetrolType fuel);

    @Query("SELECT * FROM petroltype WHERE pid = :pid")
    PetrolType getFuel(long pid);

    @Query("SELECT * FROM petroltype")
    List<PetrolType> getAllFuels();

    @Query("SELECT * FROM entry WHERE fuel IN" +
            "(SELECT pid FROM petroltype WHERE percent = :percent)")
    List<Entry> getEntryWithPercent(int percent);

}
