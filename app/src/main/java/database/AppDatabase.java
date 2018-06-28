package database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import application.Entry;

/**
 * Created by Mitchell on 18/02/2018.
 */

@Database(entities = {Entry.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract EntryDao entryDao();
}
