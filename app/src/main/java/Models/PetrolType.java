package Models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Class for a fuel type which can be used to fill up the car.
 */
@Entity
public class PetrolType {
    @PrimaryKey(autoGenerate = true)
    private int pid;
    @ColumnInfo
    private String name;
    @ColumnInfo
    private int percent;

    /**
     * Creator for the PetrolType class
     * @param name, the name for the fuel
     * @param percent, the percentage of the fuel
     */
    public PetrolType(String name, int percent) {
        this.name = name;
        this.percent = percent;
    }

    @Override
    public String toString() {
        return name+" - "+percent+"%";
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPercent() {
        return percent;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }
}
