package com.example.mitchell.test1;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.database.Cursor;

import java.util.List;

/**
 * Created by Mitchell on 18/02/2018.
 */

@Dao
public interface EntryDao {
    @Query("SELECT * FROM entry")
    public List<Entry> getAll();

    @Query("SELECT * FROM entry")
    Cursor getAllCursor();

    @Query("SELECT COUNT(*) FROM entry")
    int getCount();

    @Insert
    public void insertAll(Entry... entries);

    @Insert
    void insert(Entry entry);

    @Delete
    public void delete(Entry entry);

    @Delete
    public void deleteAll(Entry... entries);

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
