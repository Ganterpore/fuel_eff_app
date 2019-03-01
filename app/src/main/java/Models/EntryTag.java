package Models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;

import static android.arch.persistence.room.ForeignKey.CASCADE;

/**
 * Class for a Tag which can be placmd on entries
 */
@Entity
public class EntryTag {
    @PrimaryKey(autoGenerate = true)
    private int tid;

    @ColumnInfo
    private String name;

    /**
     * Creator for an EntryTag
      * @param name, the name of the tag
     */
    public EntryTag(String name) {
        this.name = name;
    }

    public int getTid() {
        return tid;
    }

    public void setTid(int tid) {
        this.tid = tid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        //if the object is not an EntryTag, it is not equal
        if(!(obj instanceof EntryTag)) {
            return false;
        }
        //otherwise they are equal if they have the same ID
        EntryTag otherTag = (EntryTag) obj;
        return otherTag.tid == this.tid;
    }
}