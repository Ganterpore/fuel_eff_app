package database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import Models.Car;
import Models.Entry;
import Models.EntryTag;
import Models.Note;
import Models.PetrolType;
import Models.TagsOnEntry;

/**
 * Created by Mitchell on 18/02/2018.
 */

@Database(entities = {Entry.class, Car.class, PetrolType.class, TagsOnEntry.class, EntryTag.class,
        Note.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract EntryDao entryDao();
}
