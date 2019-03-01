package Models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Contains all the information for a Car object.
 */
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

    public Car() {
    }

    /**
     * Creator for the Car object
     * @param cid, the ID of the car in the database
     * @param licensePlate, the license plate of said car
     * @param model, the Moddel of the car
     * @param make, the make of the car
     * @param name, the users name for the car
     */
    public Car(int cid, String licensePlate, String model, String make, String name) {
        this.cid = cid;
        this.licensePlate = licensePlate;
        this.model = model;
        this.make = make;
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        //if the object is not a car, it is not equal
        if(!(obj instanceof Car)) {
            return false;
        }
        //otherwise it is equal if it has the same ID
        Car carObj = (Car) obj;
        return carObj.cid==this.cid;
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
}

