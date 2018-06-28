package database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.database.Cursor;

import java.util.List;

import application.Entry;

/**
 * Created by Mitchell on 18/02/2018.
 */


/**
 * allows access and modification of entries from the entry database
 */
@Dao
public interface EntryDao {
    @Query("SELECT * FROM entry")
    List<Entry> getAll();

    @Query("SELECT * FROM entry")
    Cursor getAllCursor();

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

    @Query("SELECT 100*(SUM(litres)/SUM(trip)) FROM entry")
    double getAverageEfficiency();

    @Query("SELECT SUM(trip) FROM entry")
    double getTotalDistance();

    @Query("SELECT SUM(litres*price)/100 FROM entry")
    double getTotalCost();

    @Query("SELECT SUM(litres) FROM entry")
    double getTotalLitres();


}
