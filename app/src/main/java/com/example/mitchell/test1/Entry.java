package com.example.mitchell.test1;

import android.app.Activity;
import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;
import android.content.Context;
import java.util.ArrayList;

import static android.support.v4.content.ContextCompat.startActivity;
import static com.example.mitchell.test1.addEntry.ENTRY;

/**
 * Created by Mitchell on 8/02/2018.
 */

@Entity
public class Entry implements Parcelable  {
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
    private String date;
    @ColumnInfo
    private double trip;
    @ColumnInfo
    private double litres;
    @ColumnInfo
    private double price;

//    private static ArrayList<Entry> entries = new ArrayList<>();

    public Entry(String date, double trip, double litres, double price) {
        this.date = date;
        this.trip = trip;
        this.litres = litres;
        this.price = price;



//        entries.add(this);
    }

    //TODO remove privacy leak
//    public static ArrayList<Entry> getEntries() {
//        return entries;
//    }

    public String getDate() {
        return date;
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
    public double getEfficiency() {
        return (litres*100)/trip;
    }
    public double getCPerKm() {
        return (this.getCost()*100)/trip;
    }
    //TODO
    public void getDollarsPerWeek() {
    }


    @Override
    public String toString() {
        return date + String.format(" %3.2fL", this.getLitres()) +
                String.format(" %3.2fL/100km", this.getEfficiency());
    }

    /******************Parcel funcitons************************/


    //creating Entry from Parcel
    public Entry(Parcel input) {
        String[] data = new String[5];

        input.readStringArray(data);
        this.eid    = Integer.parseInt(data[0]);
        this.date   = data[1];
        this.trip   = Double.parseDouble(data[2]);
        this.litres = Double.parseDouble(data[3]);
        this.price  = Double.parseDouble(data[4]);
    }

    public static final Creator<Entry> CREATOR = new Creator<Entry>() {
        @Override
        public Entry createFromParcel(Parcel in) {
            return new Entry(in);
        }

        @Override
        public Entry[] newArray(int size) {
            return new Entry[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeStringArray(new String[]{
                String.valueOf(this.eid),
                this.date,
                String.valueOf(this.trip),
                String.valueOf(this.litres),
                String.valueOf(this.price)
        });
    }


}
