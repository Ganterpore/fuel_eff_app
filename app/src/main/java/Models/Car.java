package Models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

@Entity
public class Car {
    @PrimaryKey(autoGenerate = true)
    private int cid;
    @ColumnInfo
    private String licensePlate;
    @ColumnInfo
    private String model;
    @ColumnInfo
    private String make;
    @ColumnInfo
    private String name;

    public Car(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public int getCid() {
        return cid;
    }

    public void setCid(int cid) {
        this.cid = cid;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate = licensePlate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    //	public void addRefill(Refill refill) {
//		refills.add(refill);
//	}
//	public void removeRefill(Refill refill) {
//		refills.remove(refill);
//	}
//	public Refill getLatestRefill(Refill refill) {
//		return Collections.max(refills);
//	}
//	public ArrayList<Refill> getAllRefills() {
//		return (ArrayList<Refill>) this.refills.clone();
//	}

}

