package Models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;

import static android.arch.persistence.room.ForeignKey.CASCADE;

/**
 * Class for notes which can be added to an Entry
 */
@Entity(foreignKeys = @ForeignKey(
        entity = Entry.class,
        parentColumns = "eid",
        childColumns = "eid",
        onDelete = CASCADE
))
public class Note {
    @ColumnInfo
    private String note;
    @PrimaryKey
    private int eid;

    /**
     * Creator for the Note class
     * @param note, the note which to add
     * @param eid, the ID of the entry to add the note to
     */
    public Note(String note, int eid) {
        this.note = note;
        this.eid = eid;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getEid() {
        return eid;
    }

    public void setEid(int eid) {
        this.eid = eid;
    }
}
