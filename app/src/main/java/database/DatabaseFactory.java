package database;

import android.arch.persistence.room.Room;
import android.content.Context;

public class DatabaseFactory {
    private static AppDatabase db = null;

    public static AppDatabase get(Context context) {
        if(db == null) {
            db = Room.databaseBuilder(
                    context,
                    AppDatabase.class,
                    "database-name"
            )
                    .build();
        }
        return db;
    }
}
