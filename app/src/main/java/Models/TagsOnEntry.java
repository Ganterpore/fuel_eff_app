package Models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(primaryKeys = {"eid", "tid", "nextTrip"},
        foreignKeys = {
                @ForeignKey(entity = Entry.class,
                        parentColumns = "eid",
                        childColumns = "eid",
                        onDelete = CASCADE),
                @ForeignKey(entity = EntryTag.class,
                        parentColumns = "tid",
                        childColumns = "tid",
                        onDelete = CASCADE)
        })
public class TagsOnEntry {
    private int eid;
    private int tid;
    @ColumnInfo
    private boolean nextTrip;

    public TagsOnEntry(int eid, int tid, boolean nextTrip) {
        this.eid = eid;
        this.tid = tid;
        this.nextTrip = nextTrip;
    }

    public int getEid() {
        return eid;
    }

    public void setEid(int eid) {
        this.eid = eid;
    }

    public int getTid() {
        return tid;
    }

    public void setTid(int tid) {
        this.tid = tid;
    }

    public boolean isNextTrip() {
        return nextTrip;
    }

    public void setNextTrip(boolean nextTrip) {
        this.nextTrip = nextTrip;
    }
}
