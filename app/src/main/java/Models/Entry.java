package Models;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.PrimaryKey;
import android.util.Log;

import static android.arch.persistence.room.ForeignKey.CASCADE;
import static android.arch.persistence.room.ForeignKey.SET_NULL;

/**
 * Created by Mitchell on 8/02/2018.
 * class describes the refill entries when a ar is filled up
 */

@Entity(foreignKeys = {@ForeignKey(
        entity = Car.class,
        parentColumns = "cid",
        childColumns = "car",
        onDelete = CASCADE),
            @ForeignKey(
        entity = PetrolType.class,
        parentColumns = "pid",
        childColumns = "fuel",
        onDelete = SET_NULL
        )}
)
public class Entry {//implements Parcelable  {
    public int getEid() {
        return eid;
    }

    public void setEid(int eid) {
        Log.d("R", String.format("setEid: %d", eid));
        this.eid = eid;
    }

    @PrimaryKey(autoGenerate = true)
    private int eid;

    //keeps track of an entry in the system, and all its variables
    @ColumnInfo
    private long date;
    @ColumnInfo
    private double trip;
    @ColumnInfo
    private double litres;
    @ColumnInfo
    private double price;
    @ColumnInfo
    private int car;
    @ColumnInfo
    private int fuel;

    public Entry() {
    }

    public Entry(long date, double trip, double litres, double price, int car, int fuel) {
        this.date = date;
        this.trip = trip;
        this.litres = litres;
        this.price = price;
        this.car = car;
        this.fuel = fuel;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public double getTrip() {
        return trip;
    }
    public double getLitres() {
        return litres;
    }
    public double getPrice() {
        return price;
    }
    public double getCost() {
        return litres*(price/100);
    }
    //TODO differ this and others based on settings
    public double getEfficiency() {
        return (litres*100)/trip;
    }
    public double getCPerKm() {
        return (this.getCost()*100)/trip;
    }
    public int getCar() {
        return car;
    }
    public int getFuel() {
        return fuel;
    }

    public void setTrip(double trip) {
        this.trip = trip;
    }

    public void setLitres(double litres) {
        this.litres = litres;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setCar(int car) {
        this.car = car;
    }

    public void setFuel(int fuel) {
        this.fuel = fuel;
    }
    //    public List<EntryTag> getTags() {
//        //if tags not in memory, get from database
//
//        if(tags==null) {
//            tags = Controller.db.entryDao().getTagsOnEntry(this.eid);
//        }
//    }

    /******************Parcel funcitons************************/


//    //creating Entry from Parcel
//    public Entry(Parcel input) {
//        String[] data = new String[6];
//
//        input.readStringArray(data);
//        this.eid    = Integer.parseInt(data[0]);
//        this.date   = data[1];
//        this.trip   = Double.parseDouble(data[2]);
//        this.litres = Double.parseDouble(data[3]);
//        this.price  = Double.parseDouble(data[4]);
//        this.car    = Integer.parseInt(data[5]);
//    }
//
//    public static final Creator<Entry> CREATOR = new Creator<Entry>() {
//        @Override
//        public Entry createFromParcel(Parcel in) {
//            return new Entry(in);
//        }
//
//        @Override
//        public Entry[] newArray(int size) {
//            return new Entry[size];
//        }
//    };
//
//    @Override
//    public int describeContents() {
//        return 0;
//    }
//
//    @Override
//    public void writeToParcel(Parcel parcel, int i) {
//        parcel.writeStringArray(new String[]{
//                String.valueOf(this.eid),
//                this.date,
//                String.valueOf(this.trip),
//                String.valueOf(this.litres),
//                String.valueOf(this.price),
//                String.valueOf(this.car)
//        });
//    }


}
